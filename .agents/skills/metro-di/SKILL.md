---
name: metro-di
description: Implement, review, and refactor Metro DI in this repo. Use for @DependencyGraph, createGraph/createGraphFactory, AppScope, BindingContainer, ContributesTo, Binds, Provides, SingleIn, platform app graphs, Metro aggregation, or Metro plugin wiring in Kotlin/KMP modules.
---

# Metro DI

Use this skill for Metro dependency injection work in this repository.

This repo currently uses:

- Metro `0.12.0`
- the Gradle plugin `dev.zacsweers.metro`
- a shared `AppGraph` contract in `commonMain`
- final platform graphs in `shared/src/*Main`
- explicit `bindingContainers = [...]` lists on those final graphs
- provider-heavy `@BindingContainer` declarations contributed to `AppScope`

Default to the repo's current Metro style, not the broadest possible Metro API surface.

## Read These First

Open only the files relevant to the task.

Official Metro docs in this repo:

- `libs/metro-main/docs/installation.md`
- `libs/metro-main/docs/dependency-graphs.md`
- `libs/metro-main/docs/provides.md`
- `libs/metro-main/docs/bindings.md`
- `libs/metro-main/docs/scopes.md`
- `libs/metro-main/docs/multiplatform.md`
- `libs/metro-main/docs/aggregation.md`
- `libs/metro-main/docs/metro-intrinsics.md`
- `libs/metro-main/docs/validation-and-error-reporting.md`
- `libs/metro-main/docs/injection-types.md`
- `libs/metro-main/docs/generating-metro-code.md`
- `libs/metro-main/docs/features.md`

Project DI entry points:

- Common graph contract: `shared/src/commonMain/kotlin/com/yet/tetris/di/AppGraph.kt`
- Android final graph: `shared/src/androidMain/kotlin/com/yet/tetris/di/AndroidAppGraph.kt`
- Desktop final graph: `shared/src/desktopMain/kotlin/com/yet/tetris/di/DesktopAppGraph.kt`
- JS final graph: `shared/src/jsMain/kotlin/com/yet/tetris/di/JsAppGraph.kt`
- Native final graph: `shared/src/nativeMain/kotlin/com/yet/tetris/di/NativeAppGraph.kt`
- Shared domain/container providers: `shared/src/commonMain/kotlin/com/yet/tetris/di/DomainBindings.kt`

Representative binding containers:

- `core/common/src/commonMain/kotlin/com/app/common/di/CommonBindings.kt`
- `core/data/src/commonMain/kotlin/com/yet/tetris/data/di/DataBindings.kt`
- `core/database/src/commonMain/kotlin/com/yet/tetris/database/di/DatabaseBindings.kt`
- `core/database/src/androidMain/kotlin/com/yet/tetris/database/di/AndroidDatabaseBindings.kt`
- `feature/root/src/commonMain/kotlin/com/yet/tetris/feature/root/di/RootBindings.kt`
- `feature/home/src/commonMain/kotlin/com/yet/tetris/feature/home/di/HomeBindings.kt`
- `feature/game/src/commonMain/kotlin/com/yet/tetris/feature/game/di/GameBindings.kt`

Bootstrap usage:

- Android app graph bootstrap: `app/androidApp/src/main/kotlin/com/yet/tetris/TetrisApp.kt`
- Root component creation: `shared/src/commonMain/kotlin/com/yet/tetris/di/RootComponentFactory.kt`

## Repo Reality

Official Metro supports constructor injection, `@ContributesBinding`, graph extensions, multibindings, `@GraphPrivate`, assisted injection, MetroX Android, and more.

This repo currently uses a narrower subset:

- `@DependencyGraph`
- `createGraph()` and `createGraphFactory()`
- `@BindingContainer`
- `@ContributesTo(AppScope::class)`
- `@Binds`
- `@Provides`
- `@SingleIn(AppScope::class)`
- platform-specific final graphs for KMP

This repo currently does **not** use in production code:

- `@Inject`
- `@ContributesBinding`
- `@ContributesIntoSet`
- `@ContributesIntoMap`
- `@GraphExtension`
- `@GraphPrivate`
- `@Multibinds`
- `@Includes`
- MetroX Android

Do not introduce those advanced APIs by default just because Metro supports them. Add them only when the task explicitly requires them or there is a clear architectural reason.

## Non-Negotiable Rules

1. Keep the canonical graph contract in `commonMain` annotation-free.
2. Put the final annotated `@DependencyGraph` in the platform source set.
3. Keep `AppScope` as the app-level scope unless the task explicitly introduces a new lifecycle boundary.
4. `@Provides` functions must always have explicit return types.
5. Never override provider declarations. Use `replaces` / `excludes` if you are intentionally changing graph composition.
6. Keep `core/domain` free of Metro plugin usage and Metro annotations.
7. Do not add the Metro plugin to pure domain modules.
8. Prefer existing provider-centric binding containers over introducing constructor injection into unrelated code.
9. Keep graph creation at app bootstrap, not scattered through feature code.
10. Preserve the current explicit `bindingContainers = [...]` style on final graphs unless you are intentionally refactoring graph composition.

## Current Architecture Pattern

### 1) Common contract, platform-final graphs

This repo follows Metro's multiplatform guidance:

```kotlin
// commonMain
interface AppGraph {
    val rootComponentFactory: RootComponent.Factory
}

// androidMain
@DependencyGraph(
    scope = AppScope::class,
    bindingContainers = [
        CommonBindings::class,
        DatabaseBindings::class,
        AndroidDatabaseBindings::class,
        DataBindings::class,
        DomainBindings::class,
        RootBindings::class,
        SettingsBindings::class,
        HistoryBindings::class,
        HomeBindings::class,
        GameBindings::class,
    ],
)
internal interface AndroidAppGraph : AppGraph
```

Use this pattern whenever common code depends on platform-specific bindings.

Why:

- Metro docs require the final graph to live in platform code when common and platform contributions mix.
- This repo already does that for Android, Desktop, JS, and Native.

### 2) App graph creation

Use:

- `createGraph<PlatformGraph>()` when there are no runtime inputs
- `createGraphFactory<PlatformGraph.Factory>()` when runtime inputs are needed

In this repo:

- Android uses `createGraphFactory()` to bind `Context` via `@Provides`
- Desktop, JS, and Native use `createGraph()`

### 3) Binding containers

This repo organizes bindings into `@BindingContainer`s and then lists them explicitly in final graphs.

Use `object` containers for pure provider groups:

```kotlin
@ContributesTo(AppScope::class)
@BindingContainer
object CommonBindings {
    @SingleIn(AppScope::class)
    @Provides
    fun provideStoreFactory(): StoreFactory = DefaultStoreFactory()
}
```

Use `abstract class` containers when you need `@Binds` aliases:

```kotlin
@ContributesTo(AppScope::class)
@BindingContainer
abstract class RootBindings {
    @Binds
    internal abstract val DefaultRootComponentFactory.bindRootComponentFactory: RootComponent.Factory

    companion object {
        @Provides
        internal fun provideDefaultRootComponentFactory(
            homeComponentFactory: HomeComponent.Factory,
            gameComponentFactory: GameComponent.Factory,
        ): DefaultRootComponentFactory =
            DefaultRootComponentFactory(
                homeComponentFactory = homeComponentFactory,
                gameComponentFactory = gameComponentFactory,
            )
    }
}
```

Default rules:

- `object` for only-`@Provides`
- `abstract class` for `@Binds`, optionally with a `companion object` for `@Provides`
- keep provider groups close to the feature or module they assemble

## Repo Conventions

### Explicit graph registration

Metro can aggregate contributed binding containers automatically by scope.

This repo still explicitly lists binding containers on each final graph:

- it is easy to audit
- it makes platform composition obvious
- it avoids accidental graph-shape changes during unrelated edits

Preserve that style when adding a new binding container:

1. Create the new container in the owning module.
2. Annotate it with `@ContributesTo(AppScope::class)` and `@BindingContainer`.
3. Add it to every relevant final platform graph's `bindingContainers = [...]` list.

Do not "clean up" one side of this pattern unless you are deliberately standardizing the whole graph setup.

### Domain stays DI-free

`core/domain` does not apply the Metro plugin and does not contain Metro annotations.

Keep it that way:

- domain models, repositories, and use cases stay plain Kotlin
- wiring happens in `shared`, `core/data`, `core/database`, `feature/*/di`, and similar modules

### Provider-centric style

Metro docs recommend constructor injection for many cases, but this repo currently favors explicit providers.

Default here:

- construct repositories, use cases, store factories, component factories, and platform services in `@Provides`
- use `@Binds` only to alias a concrete factory to a public interface type

Do **not** introduce `@Inject` + `@ContributesBinding` in a random module unless:

- the task explicitly asks for constructor injection or contributed bindings
- the refactor is local and coherent
- it will not create style drift across the surrounding code

### Inline simple construction is allowed

This repo sometimes constructs simple collaborators directly inside a provider instead of separately providing every leaf type.

Example pattern:

```kotlin
@Provides
internal fun provideHomeStoreFactory(
    storeFactory: StoreFactory,
    gameHistoryRepository: GameHistoryRepository,
    gameSettingsRepository: GameSettingsRepository,
    gameStateRepository: GameStateRepository,
): HomeStoreFactory =
    HomeStoreFactory(
        storeFactory = storeFactory,
        gameHistoryRepository = gameHistoryRepository,
        gameSettingsRepository = gameSettingsRepository,
        gameStateRepository = gameStateRepository,
        calculateProgressionSummaryUseCase = CalculateProgressionSummaryUseCase(),
    )
```

Preserve this level of granularity unless there is a strong reason to extract more providers.

## Scoping Rules

Metro rules that matter here:

- graphs with `scope = AppScope::class` implicitly support `@SingleIn(AppScope::class)` bindings
- unscoped graphs cannot request scoped bindings
- scoped graphs cannot request differently scoped bindings

Apply these rules here:

- app-lifetime shared objects get `@SingleIn(AppScope::class)`
- short-lived pure objects can remain unscoped
- avoid inventing extra scopes unless the feature really has a different lifecycle boundary

## Runtime Inputs

Use `@Provides` on graph factory parameters for runtime values.

Repo example:

```kotlin
@DependencyGraph.Factory
fun interface Factory {
    fun create(
        @Provides appContext: Context,
    ): AndroidAppGraph
}
```

Default rule:

- prefer `@Provides` factory parameters for plain runtime instances
- only use `@Includes` when you need to import another graph or accessor-bearing object into the graph

This repo does not currently use `@Includes`, so do not introduce it casually.

## Plugin and Module Rules

In this repo, DI-bearing modules apply `alias(libs.plugins.metro)` and `core/domain` does not.

When adding a new module, apply Metro only if the module declares Metro annotations or needs Metro-generated code.

## What To Do When Adding DI

### Adding a new feature factory binding

1. Put DI code in `feature/<name>/src/commonMain/kotlin/.../di/<Name>Bindings.kt`.
2. Use an `abstract class` container if you need `@Binds` for the public factory interface.
3. Add `@Provides` methods in a companion object for concrete factory construction.
4. Add the new binding container to each final platform graph in `shared/src/*Main/...AppGraph.kt`.

### Adding a new shared service or repository

1. Prefer `core/common`, `core/data`, or `core/database` `di/` packages based on ownership.
2. Use `object` binding containers when only provider functions are needed.
3. Scope app-wide singletons with `@SingleIn(AppScope::class)`.
4. Add the binding container to final platform graphs if it is new.

### Adding a platform-specific binding

1. Put it in the relevant source set, e.g. `androidMain`, `desktopMain`, `jsMain`, `nativeMain`.
2. Use a platform-specific binding container, e.g. `AndroidDatabaseBindings`.
3. Keep the common graph contract unchanged.
4. Reference that binding container from the corresponding platform final graph(s) only.

## Advanced Metro Features

Only use these when the task explicitly requires them.

### `@Inject` and constructor injection

Metro supports it and generally recommends it, but this repo does not currently use it in app code.
If you introduce it, keep it local and intentional and do not annotate domain-layer classes.

### `@ContributesBinding` / `@ContributesIntoSet` / `@ContributesIntoMap`

Official Metro supports them, including implicit injectability.

This repo does not currently use them. Do not switch a feature from explicit binding containers to contributed bindings unless the task is specifically about aggregation refactoring.

### `@GraphExtension`

Metro supports child graphs and scoped extensions.

This repo does not currently use graph extensions. Do not add them for ordinary feature wiring. Reach for them only if the user is explicitly introducing a real nested graph lifecycle like authenticated session graphs.

### `@GraphPrivate`

Useful for keeping bindings hidden from child graphs, but there are no graph extensions here right now. Avoid introducing it unless graph-extension work requires it.

### Multibindings

Metro supports `Set`/`Map` multibindings and requires explicit `@Multibinds(allowEmpty = true)` when empty collections are expected.
This repo does not currently use multibindings. If you add one, define empty multibindings explicitly and keep the API strongly typed with qualifiers or map keys.

### MetroX Android

MetroX Android is for `AppComponentFactory`-based Android component injection.

This repo does not use it. Do not introduce MetroX Android for normal app-graph bootstrapping. Current Android startup simply creates `appGraph` in `Application`.

## Validation and Debugging

Metro validates graphs at compile time and reports good dependency traces.

Important repo implication:

- many common binding issues will surface when compiling the final platform graph in `shared/src/*Main`, not necessarily at the file where you wrote the provider

When debugging Metro errors in this repo:

1. Find which final graph consumes the failing binding.
2. Check that the relevant binding container is listed in that graph's `bindingContainers`.
3. Check scope compatibility with `AppScope`.
4. Check `@Provides` explicit return types.
5. Check missing runtime inputs like Android `Context`.
6. Check for accidental provider override attempts.

## Review Checklist

- [ ] Common graph contract remains annotation-free in `shared/src/commonMain`.
- [ ] Final `@DependencyGraph` is in the right platform source set.
- [ ] New binding containers are annotated with `@ContributesTo(AppScope::class)` and `@BindingContainer`.
- [ ] New binding containers are added to the relevant final graph's `bindingContainers = [...]`.
- [ ] `@Provides` methods declare explicit return types.
- [ ] `@Binds` declarations are abstract and used only for aliases.
- [ ] App-wide lifetimes use `@SingleIn(AppScope::class)` where appropriate.
- [ ] No Metro annotations leaked into `core/domain`.
- [ ] No casual introduction of advanced Metro APIs unused elsewhere in the repo.

## Anti-Patterns

Reject these by default:

1. Annotating the common `AppGraph` contract with `@DependencyGraph`.
2. Defining the only final graph in `commonMain` when platform bindings exist.
3. Adding Metro to `core/domain`.
4. Overriding `@Provides` declarations instead of using graph composition tools.
5. Replacing explicit graph composition with aggregation-only shortcuts in a one-off edit.
6. Introducing `@Inject` or `@ContributesBinding` as a style pivot during unrelated work.
7. Forgetting to update all relevant platform final graphs after adding a new binding container.
8. Using `@Includes` when a simple `@Provides` graph factory input is enough.

## Default Snippets

### New platform graph with runtime input

```kotlin
@DependencyGraph(
    scope = AppScope::class,
    bindingContainers = [
        CommonBindings::class,
        FeatureBindings::class,
    ],
)
internal interface AndroidFeatureGraph : FeatureGraph {
    @DependencyGraph.Factory
    fun interface Factory {
        fun create(
            @Provides appContext: Context,
        ): AndroidFeatureGraph
    }
}
```

### New provider-only binding container

```kotlin
@ContributesTo(AppScope::class)
@BindingContainer
object FeatureBindings {
    @SingleIn(AppScope::class)
    @Provides
    fun provideFeatureService(
        dependency: Dependency,
    ): FeatureService = FeatureService(dependency)
}
```

### New alias + factory binding container

```kotlin
@ContributesTo(AppScope::class)
@BindingContainer
abstract class FeatureBindings {
    @Binds
    internal abstract val DefaultFeatureFactory.bindFeatureFactory: Feature.Factory

    companion object {
        @Provides
        internal fun provideDefaultFeatureFactory(
            dependency: Dependency,
        ): DefaultFeatureFactory = DefaultFeatureFactory(dependency)
    }
}
```

## Output Expectations

When changing Metro DI in this repo:

1. Follow the existing graph/container structure first.
2. Keep changes local to the owning module plus the final platform graph registration.
3. Preserve multiplatform graph correctness.
4. Prefer explicitness over clever Metro features the codebase is not already using.
5. If you use an advanced Metro feature, explain why the existing simpler repo pattern is insufficient.
