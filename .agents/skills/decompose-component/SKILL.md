---
name: decompose-component
description: This skill should be used when the user is creating or modifying a Decompose component, mentions "ComponentContext", "retainedInstance", "stateKeeper", "instanceKeeper", "InstanceKeeper", "MutableValue", "Value<", component lifecycle, state preservation, instance retaining, back button handling in a component, or when writing a class that implements or extends a Decompose component interface. Also applies when the user asks how to structure a component, how to keep state across config changes, how to implement a ViewModel equivalent with Decompose, or how to handle the back button in a component.
version: 1.0.0
---

You are helping write a Decompose component. Follow these patterns exactly.

## Component Structure

Always use `interface + DefaultXxxComponent`. Never extend a library base class.

```kotlin
interface CounterComponent {
    val model: Value<Model>
    fun onIncrementClicked()

    data class Model(val count: Int = 0)
}

class DefaultCounterComponent(
    componentContext: ComponentContext,
    private val onFinished: () -> Unit,          // callbacks to parent go via constructor
) : CounterComponent, ComponentContext by componentContext {   // <-- delegation, not inheritance

    private val _model = MutableValue(CounterComponent.Model())
    override val model: Value<CounterComponent.Model> = _model

    override fun onIncrementClicked() {
        _model.update { it.copy(count = it.count + 1) }
    }
}
```

**Rules:**
- `ComponentContext by componentContext` — always delegate, never extend
- Constructor receives `componentContext: ComponentContext` as first parameter
- Parent callbacks (navigation, results) come via constructor lambdas
- Expose `Value<T>` (immutable), hold `MutableValue<T>` privately
- `MutableValue.update { }` for state mutations — call only on the main thread

## Value vs Other State Holders

`Value<T>` is Decompose's multiplatform observable. Prefer it for cross-platform components.

```kotlin
// Good — works on all platforms, observable in Compose/SwiftUI/React
val state: Value<State> = _state

// Also acceptable if you're coroutines-only (Android/JVM):
val state: StateFlow<State>
```

`Value` is NOT a coroutine — no `collect`, use `subscribe`/`subscribeAsState()` in Compose.

## Lifecycle

Components get lifecycle automatically. Subscribe only in `init` or lifecycle callbacks.

```kotlin
class DefaultSomeComponent(componentContext: ComponentContext) : ComponentContext by componentContext {
    init {
        lifecycle.doOnStart { /* start polling */ }
        lifecycle.doOnStop { /* stop polling */ }
        lifecycle.doOnDestroy { /* cleanup */ }
    }
}
```

Lifecycle states: `INITIALIZED → CREATED → STARTED → RESUMED → STOPPED → DESTROYED`
- Active component is `RESUMED`
- Back-stack components are `CREATED` (still alive, stopped)

## State Preservation (survives process death + config changes)

```kotlin
@Serializable
private data class State(val query: String = "", val selectedId: Long? = null)

class DefaultSearchComponent(componentContext: ComponentContext) : ComponentContext by componentContext {
    private var state: State by saveable(serializer = State.serializer(), init = ::State)
}
```

Manual version:
```kotlin
private var state = stateKeeper.consume("STATE", State.serializer()) ?: State()
init {
    stateKeeper.register("STATE", State.serializer()) { state }
}
```

**Rules:** `@Serializable` required, keep state small (<500KB on Android), `consume()` only once.

## Instance Retaining (survives config changes, like ViewModel)

```kotlin
class DefaultTimerComponent(componentContext: ComponentContext) : ComponentContext by componentContext {

    private val timer = retainedInstance { Timer() }

    private class Timer : InstanceKeeper.Instance {
        override fun onDestroy() {}
    }
}
```

**Rules:** NOT `inner` class, no `Activity`/`Context`/`View` references, implement `onDestroy()`.

## Combining state preservation + instance retaining

```kotlin
class DefaultComponent(componentContext: ComponentContext) : ComponentContext by componentContext {

    private val logic by saveable(serializer = Logic.State.serializer(), state = { it.state }) { savedState ->
        retainedInstance { Logic(savedState) }
    }

    private class Logic(savedState: Logic.State?) : InstanceKeeper.Instance {
        var state = savedState ?: Logic.State()
            private set
        @Serializable data class State(val items: List<String> = emptyList())
        override fun onDestroy() {}
    }
}
```

## Back Button Handling

```kotlin
class DefaultEditorComponent(componentContext: ComponentContext) : ComponentContext by componentContext {

    private val backCallback = BackCallback(isEnabled = false) {
        showDiscardDialog()
    }

    init { backHandler.register(backCallback) }

    fun onFormChanged() { backCallback.isEnabled = true }
}
```

Priority: last-registered wins. Use `priority = Int.MAX_VALUE` to always intercept first.

## Preview / Test Doubles

```kotlin
class PreviewCounterComponent : CounterComponent {
    override val model: Value<Model> = MutableValue(Model(count = 42))
    override fun onIncrementClicked() {}
}

internal val PreviewContext: ComponentContext = DefaultComponentContext(LifecycleRegistry())
```

## Critical Warnings

- **Root component must be created on the UI/Main thread, NEVER inside a `@Composable` function**
- **`defaultComponentContext()` must be called only once per Activity/Fragment lifetime**
- **`retainedComponent()` has the same restriction** — call only once in `onCreate`
- **Navigate only on Main thread** — background navigation causes crashes
- **`MutableValue`: subscribe and update only on the main thread** — off-thread calls cause race conditions
- **Don't make retained inner classes** — captures outer component → memory leak

## Quick Reference

| Need | API |
|------|-----|
| Component context delegation | `class Foo(ctx: ComponentContext) : ComponentContext by ctx` |
| Observable state | `MutableValue<T>` / `Value<T>` |
| Update state | `_state.update { it.copy(...) }` |
| Survive config change | `retainedInstance { }` |
| Survive process death | `saveable(serializer = ...)` or `stateKeeper` |
| Lifecycle events | `lifecycle.doOnStart/Stop/Destroy { }` |
| Custom back handling | `backHandler.register(BackCallback { })` |
| Auto back in navigation | `handleBackButton = true` in `childStack()`/`childSlot()` |
