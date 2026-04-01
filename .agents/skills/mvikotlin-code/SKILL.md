---
name: mvikotlin-code
description: Write, review, and refactor MVIKotlin code in this repo. Use when the task mentions MVIKotlin, StoreFactory, Store<Intent, State, Label>, CoroutineExecutor, Reducer, Bootstrapper, Labels, MviView, LoggingStoreFactory, TimeTravelStoreFactory, or when implementing a feature store/component wired through Decompose.
---

# MVIKotlin Code

Use this skill for MVIKotlin work in this repository.

This repo uses:

- MVIKotlin `4.3.0`
- `mvikotlin-extensions-coroutines`
- Decompose components as the presentation boundary
- Metro DI with a global injected `StoreFactory`

Default to the repo's existing pattern, not the most generic MVIKotlin API surface.

## Read These First

Paths below are repo-root relative. Open only the files relevant to the task.

- Upstream store docs: `libs/MVIKotlin/docs/store.md`
- Upstream view docs: `libs/MVIKotlin/docs/view.md`
- Upstream binding docs: `libs/MVIKotlin/docs/binding_and_lifecycle.md`
- Upstream state preservation docs: `libs/MVIKotlin/docs/state_preservation.md`
- Upstream logging docs: `libs/MVIKotlin/docs/logging.md`
- Upstream time travel docs: `libs/MVIKotlin/docs/time_travel.md`
- Simple bootstrapped store: `feature/home/src/commonMain/kotlin/com/yet/tetris/feature/home/store/HomeStore.kt`
- Simple bootstrapped store impl: `feature/home/src/commonMain/kotlin/com/yet/tetris/feature/home/store/HomeStoreFactory.kt`
- Optimistic update plus async save: `feature/settings/src/commonMain/kotlin/com/yet/tetris/feature/settings/store/SettingsStoreFactory.kt`
- Complex long-lived executor: `feature/game/src/commonMain/kotlin/com/yet/tetris/feature/game/store/GameStoreFactory.kt`
- Store to component mapping: `feature/game/src/commonMain/kotlin/com/yet/tetris/feature/game/integration/Mappers.kt`
- Component integration with retained store: `feature/game/src/commonMain/kotlin/com/yet/tetris/feature/game/DefaultGameComponent.kt`
- Global production `StoreFactory`: `core/common/src/commonMain/kotlin/com/app/common/di/CommonBindings.kt`
- Store to Decompose `Value`: `core/common/src/commonMain/kotlin/com/app/common/decompose/asValue.kt`

## Repo Rules

1. Inject `StoreFactory` into every feature store factory. Never instantiate `DefaultStoreFactory()` inside production feature code.
2. Use `internal interface XxxStore : Store<XxxStore.Intent, XxxStore.State, XxxStore.Label>`.
3. Put feature business logic in `XxxStoreFactory`, not in Compose UI or Decompose components.
4. Use `CoroutineExecutor` by default. This repo does not use Reaktive for feature stores.
5. Keep `Reducer` pure and synchronous. No I/O, no coroutines, no navigation, no logging side effects.
6. Use `Label` only for one-off effects such as navigation, dialogs, errors, or external commands.
7. Keep long-lived or startup work in `Action` plus `Bootstrapper`, not in the component init block.
8. In Decompose components, retain stores with `instanceKeeper.getStore { factory.create() }`.
9. Expose UI state as `Value<Model>` via `store.asValue().map(stateToModel)`.
10. Keep `State -> Model` mapping in `integration/Mappers.kt`.
11. Wire feature dependencies through Metro bindings in the feature's `di/` package.
12. Do not default to `MviView` or `Binder` in this repo unless the task is specifically about platform views outside Compose/Decompose.

## Default File Layout

For a new feature, prefer this structure:

```text
feature/foo/src/commonMain/kotlin/com/yet/tetris/feature/foo/
  FooComponent.kt
  DefaultFooComponent.kt
  PreviewFooComponent.kt
  integration/Mappers.kt
  store/FooStore.kt
  store/FooStoreFactory.kt
  di/FooBindings.kt
```

## Store Pattern

Default to the dedicated store interface plus factory pattern.

```kotlin
internal interface FooStore : Store<FooStore.Intent, FooStore.State, FooStore.Label> {
    data class State(
        val isLoading: Boolean = false,
        val items: List<Item> = emptyList(),
    )

    sealed class Intent {
        data object Refresh : Intent()
        data class Delete(val id: String) : Intent()
    }

    sealed class Action {
        data object LoadStarted : Action()
    }

    sealed class Msg {
        data class LoadingChanged(val isLoading: Boolean) : Msg()
        data class Loaded(val items: List<Item>) : Msg()
        data class Deleted(val id: String) : Msg()
    }

    sealed class Label {
        data class ShowError(val message: String) : Label()
        data object NavigateBack : Label()
    }
}
```

```kotlin
internal class FooStoreFactory(
    private val storeFactory: StoreFactory,
    private val repository: FooRepository,
) {
    fun create(): FooStore =
        object : FooStore,
            Store<FooStore.Intent, FooStore.State, FooStore.Label> by storeFactory.create(
                name = "FooStore",
                initialState = FooStore.State(),
                bootstrapper = SimpleBootstrapper(FooStore.Action.LoadStarted),
                executorFactory = ::ExecutorImpl,
                reducer = ReducerImpl,
            ) {}

    private object ReducerImpl : Reducer<FooStore.State, FooStore.Msg> {
        override fun FooStore.State.reduce(msg: FooStore.Msg): FooStore.State =
            when (msg) {
                is FooStore.Msg.LoadingChanged -> copy(isLoading = msg.isLoading)
                is FooStore.Msg.Loaded -> copy(isLoading = false, items = msg.items)
                is FooStore.Msg.Deleted -> copy(items = items.filterNot { it.id == msg.id })
            }
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<FooStore.Intent, FooStore.Action, FooStore.State, FooStore.Msg, FooStore.Label>() {

        override fun executeAction(action: FooStore.Action) {
            when (action) {
                FooStore.Action.LoadStarted -> load()
            }
        }

        override fun executeIntent(intent: FooStore.Intent) {
            when (intent) {
                FooStore.Intent.Refresh -> load()
                is FooStore.Intent.Delete -> delete(intent.id)
            }
        }

        private fun load() {
            scope.launch {
                try {
                    dispatch(FooStore.Msg.LoadingChanged(true))
                    dispatch(FooStore.Msg.Loaded(repository.getAll()))
                } catch (e: Exception) {
                    dispatch(FooStore.Msg.LoadingChanged(false))
                    publish(FooStore.Label.ShowError(e.message ?: "Failed to load"))
                }
            }
        }

        private fun delete(id: String) {
            scope.launch {
                try {
                    repository.delete(id)
                    dispatch(FooStore.Msg.Deleted(id))
                } catch (e: Exception) {
                    publish(FooStore.Label.ShowError(e.message ?: "Failed to delete"))
                }
            }
        }
    }
}
```

## Intent, Action, Msg, Label

Use these roles consistently:

- `Intent`: input from UI or component API.
- `Action`: startup or internally forwarded trigger, especially bootstrap work.
- `Msg`: internal state transition payload consumed by the reducer.
- `Label`: one-shot effect that should not live in state.

Prefer:

- `Intent` for button taps, field changes, gesture events, retry actions.
- `Action` for `FeatureLoadStarted`, resubscribe triggers, or forwarded work.
- `Msg` for every state mutation.
- `Label` for navigation, snackbars, dialogs, external callbacks, analytics triggers only when those are not better handled elsewhere.

Do not:

- mutate state directly from the component
- put one-shot navigation in `State`
- do repository or use-case work in the reducer
- skip `Msg` and update state ad hoc from the executor

## Executor Rules

Upstream MVIKotlin guarantees that:

- `accept(Intent)` must be called on the main thread
- `init()` and `dispose()` must be called on the main thread
- states and labels are emitted on the main thread
- `Executor` and `Bootstrapper` are stateful and must not be singletons

Apply those rules here:

- `executorFactory = ::ExecutorImpl` must create a fresh executor instance every time
- keep `ReducerImpl` as an `object` only because it is pure and stateless
- use `scope.launch {}` for async work
- switch to background dispatchers only around actual I/O or CPU-heavy work
- call `dispatch`, `publish`, and `forward` from the main-thread executor context

When an intent needs a consistent snapshot, capture `val state = state()` once and pass it down.
When it needs the latest value later, call `state()` again instead of keeping stale local copies.

## Bootstrapper Rules

For this repo:

- use `SimpleBootstrapper(Action.LoadStarted)` for standard load-on-create flows
- use a custom `CoroutineBootstrapper` only when bootstrap work itself is asynchronous and materially more complex
- never use an `object` bootstrapper

Remember that `StoreFactory.create(...)` auto-initializes stores by default, so do not manually call `store.init()` unless you deliberately disabled `autoInit`.

## Decompose Integration

Default component integration pattern:

```kotlin
internal class DefaultFooComponent(
    componentContext: ComponentContext,
    private val fooStoreFactory: FooStoreFactory,
    private val navigateBack: () -> Unit,
) : ComponentContext by componentContext, FooComponent {

    private val store = instanceKeeper.getStore { fooStoreFactory.create() }

    init {
        coroutineScope().launch {
            store.labels.collect { label ->
                when (label) {
                    FooStore.Label.NavigateBack -> navigateBack()
                    is FooStore.Label.ShowError -> Unit
                }
            }
        }
    }

    override val model: Value<FooComponent.Model> =
        store.asValue().map(stateToModel)
}
```

Rules:

- retain the store with `instanceKeeper.getStore`
- collect labels in a lifecycle-bound coroutine scope
- send UI events with `store.accept(...)`
- keep component methods thin and delegate logic to the store
- do not duplicate business rules in component init blocks

If a component owns child navigation, labels may drive that navigation. Keep the navigation handling in the component, not in the store.

## View and Binder Guidance

Official MVIKotlin supports `MviView`, `BaseMviView`, and `Binder`, but this repo usually does not need them because Compose + Decompose can observe store state directly.

Default here:

- use `store.asValue()` plus `map(stateToModel)` for Decompose-facing state
- keep separate UI `Model` types when a component contract already exists
- use `Binder` only if you are integrating with a non-Compose platform view layer that already follows MVIKotlin view/event binding

Do not introduce `MviView` abstractions into a feature that already exposes a Decompose component contract unless the task explicitly requires it.

## State Preservation and Retention

Use the right tool for the right lifetime:

- `instanceKeeper.getStore { ... }`: retain the whole store across Decompose scope recreation and config changes
- `StateKeeper`: preserve serializable state for process death restoration

Current repo baseline:

- features already retain stores with `instanceKeeper.getStore`
- features do not currently restore store state via `StateKeeper`

If the task requires process-death restoration:

1. Thread `StateKeeper` from the component context into store creation.
2. Seed `initialState` from `stateKeeper.consume(...)`.
3. Register sanitized state with `stateKeeper.register(...)`.
4. Reset transient fields such as loading flags before saving.

Be careful not to retain or save heavyweight dependencies. Preserve only serializable state.

## Logging and Time Travel

MVIKotlin logging and time travel wrap the global `StoreFactory`.

In this repo, change the app-level binding, not every feature:

```kotlin
@Provides
fun provideStoreFactory(): StoreFactory = DefaultStoreFactory()
```

If enabling debugging wrappers, prefer swapping the single app-wide binding in `core/common/.../CommonBindings.kt`:

```kotlin
LoggingStoreFactory(DefaultStoreFactory())
LoggingStoreFactory(TimeTravelStoreFactory())
```

Rules:

- keep feature store factories accepting plain `StoreFactory`
- do not hardcode logging or time travel inside a feature module
- treat both as debug tooling, not production defaults
- if time travel is enabled, remember the server setup is platform-specific

Do not add `JvmSerializable` everywhere by default. Only add extra serialization markers when the specific debugging or export scenario needs them.

## DI Rules

Wire stores through Metro bindings in the feature module:

```kotlin
@Provides
internal fun provideFooStoreFactory(
    storeFactory: StoreFactory,
    repository: FooRepository,
): FooStoreFactory =
    FooStoreFactory(
        storeFactory = storeFactory,
        repository = repository,
    )
```

Rules:

- inject domain repositories and use cases into the store factory
- inject store factories into component factories
- keep DI annotations out of the store implementation itself

## Testing Rules

Follow the existing store test style in this repo:

- create stores with `DefaultStoreFactory()`
- disable MVIKotlin main-thread assertions in tests when needed
- set `Dispatchers.Main` to `StandardTestDispatcher`
- use `runTest` and `advanceUntilIdle()`
- assert `store.state` directly for reducer outcomes
- collect `store.labels` for one-off effect assertions

For new tests:

- prefer module-local helpers already present in that feature test package
- keep store tests focused on state transitions and labels
- keep component tests focused on navigation and child wiring

## Decision Guide

Use the dedicated `XxxStore` interface pattern when:

- the feature already has a component contract
- the store is non-trivial
- the store needs labels, bootstrap, or significant async logic

Consider the MVIKotlin DSL only when:

- the store is very small
- the user explicitly asks for DSL style
- you are writing an isolated example rather than extending this repo's established feature structure

Even then, prefer the repo's existing dedicated interface + factory style unless there is a strong reason not to.

## Anti-Patterns

Reject these by default:

1. Creating `DefaultStoreFactory()` inside feature code.
2. Putting repository calls in a reducer.
3. Updating component state separately from store state.
4. Emitting navigation as persistent state.
5. Using `object` executors or custom bootstrappers.
6. Introducing `MviView` where a Decompose component already owns the feature boundary.
7. Moving domain logic into Compose or SwiftUI views.
8. Wrapping only one feature with logging or time travel while others still use a different global store factory.

## Output Expectations

When implementing MVIKotlin code in this repo:

1. Match existing feature naming and file layout.
2. Keep business logic in the store factory and reducer.
3. Keep UI mapping in `integration/Mappers.kt`.
4. Keep components thin and lifecycle-aware.
5. Add or update focused tests for state and labels.
