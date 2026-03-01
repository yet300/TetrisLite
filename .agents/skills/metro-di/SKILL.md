---
name: metro-di
description: Design, implement, migrate, and audit Metro DI in Kotlin/KMP projects. Use for @DependencyGraph setup, Graph Extensions, scopes, aggregation (@Contributes*), Binding Containers, multibindings, optional bindings, and KMP source-set graph placement.
---

# Metro DI (Kotlin/KMP)

## Overview

Use this skill when implementing or reviewing Metro DI in multi-module Kotlin or Kotlin Multiplatform
projects.

This skill is optimized for:

- DI architecture design for KMP (common + platform source sets)
- Migration from Koin/Dagger/kotlin-inject patterns to Metro
- Build-safe Metro setup with aggregation (`@Contributes*`)
- Strict detection of Metro anti-patterns that cause compile/runtime issues

## When To Use This Skill

- Creating a new `@DependencyGraph` or graph factory
- Designing `@GraphExtension` child graphs
- Wiring multi-module bindings with `@ContributesTo` / `@ContributesBinding`
- Building set/map multibindings and optional bindings
- Solving KMP source-set visibility for platform-specific implementations
- Refactoring DI to keep domain layer free of DI framework knowledge

## Non-Negotiable Rules

1. `@Provides` must always have an explicit return type.
2. Never override provider declarations. Use `replaces` / `excludes`.
3. `@Includes` factory params are inputs only and cannot be injected from the graph.
4. Unscoped graph cannot request scoped bindings.
5. Scoped graph cannot request bindings with non-matching scope.
6. `@GraphPrivate` bindings cannot be exposed via accessors and are invisible to extensions.
7. Empty multibindings are compile errors unless declared with `@Multibinds(allowEmpty = true)`.
8. For mixed common + platform contributions in KMP, final `@DependencyGraph` must be in
   platform source sets (not commonMain).

## Primary Workflow

### 1) Pick Graph Placement (KMP First)

- If bindings are fully common: graph may stay in common.
- If any contributions are platform-specific: define only a plain contract interface in
  `commonMain`, then create final annotated graphs in each platform source set.

```kotlin
// commonMain
interface AppGraph {
  val userRepository: UserRepository
}

// androidMain
@DependencyGraph(AppScope::class)
interface AndroidAppGraph : AppGraph

// iosMain
@DependencyGraph(AppScope::class)
interface IosAppGraph : AppGraph
```

### 2) Create Graph + Factory Inputs Correctly

```kotlin
@DependencyGraph(AppScope::class)
interface AndroidAppGraph : AppGraph {
  val app: AppEntryPoint

  @DependencyGraph.Factory
  fun interface Factory {
    fun create(
      @Provides appConfig: AppConfig,
      @Includes platformBindings: PlatformBindings,
    ): AndroidAppGraph
  }
}
```

Use:

- `createGraph<T>()` for no runtime inputs.
- `createGraphFactory<T.Factory>()` when graph needs runtime inputs.

### 3) Prefer Constructor Injection + Contributed Bindings

```kotlin
@ContributesBinding(AppScope::class)
class UserRepositoryImpl(
  private val api: UserApi,
  private val db: UserDatabase,
) : UserRepository
```

Use explicit `binding = binding<...>()` if a class has multiple supertypes or needs a qualified
binding.

### 4) Use Binding Containers for Provider Groups

Use `@BindingContainer` when provider groups are easier to maintain outside graph interfaces.

```kotlin
@ContributesTo(AppScope::class)
@BindingContainer
object NetworkBindings {
  @Provides
  fun provideHttpClient(config: AppConfig): HttpClient = HttpClient(config.baseUrl)
}
```

Use `replaces` for test/fake variants:

```kotlin
@ContributesTo(AppScope::class, replaces = [NetworkBindings::class])
@BindingContainer
object FakeNetworkBindings {
  @Provides
  fun provideHttpClient(): HttpClient = FakeHttpClient()
}
```

### 5) Add Graph Extensions for Child Lifecycles

```kotlin
@GraphExtension(LoggedInScope::class)
interface LoggedInGraph {
  val profileInteractor: ProfileInteractor

  @ContributesTo(AppScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun create(@Provides userId: String): LoggedInGraph
  }
}

@DependencyGraph(AppScope::class)
interface AndroidAppGraph : LoggedInGraph.Factory
```

If factory is contributed, instantiate via contribution/factory API from the parent graph.

### 6) Use Scopes Deliberately

- Graph scope implies equivalent `@SingleIn(scope)` behavior.
- Scope every long-lived singleton-like binding.
- Avoid cross-graph leakage of same-typed scoped instances; use `@GraphPrivate` when needed.

```kotlin
@DependencyGraph(AppScope::class)
interface AppGraph {
  @GraphPrivate
  @Provides
  @SingleIn(AppScope::class)
  fun provideAppCoroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob())
}
```

### 7) Implement Multibindings and Optional Bindings Safely

Set/map multibindings:

```kotlin
@ContributesIntoMap(AppScope::class, binding = binding<@StringKey("remote") SyncSource>())
class RemoteSyncSource : SyncSource
```

Optional accessors must include defaults:

```kotlin
@DependencyGraph
interface OptionalGraph {
  @OptionalBinding
  val logger: Logger? get() = null
}
```

Optional parameters use Kotlin default values:

```kotlin
@Provides
fun provideFeatureToggle(remote: RemoteToggles? = null): FeatureToggle =
  FeatureToggle(remote)
```

### 8) Keep Domain Clean (SOLID)

- Domain module should not declare Metro graph/container annotations.
- Domain classes may use plain constructor dependencies only.
- Bind interfaces to implementations in shared/data/di modules via `@ContributesBinding` or
  `@Binds`.

## Metro Audit Checklist

### Graph correctness

- [ ] Every graph has clear roots (accessors and `inject()` members).
- [ ] Factory params are explicitly marked `@Provides` or `@Includes`.
- [ ] No provider overrides exist.
- [ ] Graph scopes match requested scoped bindings.

### Aggregation correctness

- [ ] Scope markers are consistent across contributed bindings/containers.
- [ ] Test replacements use `replaces = [...]` instead of overrides.
- [ ] Graph exclusions use `excludes = [...]` only where intended.

### Graph extension correctness

- [ ] Extension is only accessible through parent graph.
- [ ] Contributed extension factory is not an abstract class.
- [ ] Parent graph has visibility to all needed contributions at merge point.

### Binding quality

- [ ] `@Binds` is used for aliases; provider getters are not misused as alias replacements.
- [ ] Qualifiers are applied consistently at request and provision points.
- [ ] Nullable and non-nullable bindings are treated as different keys.
- [ ] Empty multibindings are explicitly opted in via `@Multibinds(allowEmpty = true)`.

### KMP correctness

- [ ] Final annotated graphs live in platform source sets when platform contributions exist.
- [ ] Common graph contracts stay annotation-free if they are only contracts.
- [ ] JS limitations are considered for top-level function injection and aggregation support.

## Anti-Patterns (Always Reject)

1. `@DependencyGraph` in `commonMain` as the only final graph while injecting
   platform-specific contributed types.
2. Overriding `@Provides` in test graph types.
3. Exposing a `@GraphPrivate` binding as public graph API.
4. Relying on empty `Set<T>` or `Map<K, V>` without `@Multibinds(allowEmpty = true)`.
5. Using `Lazy<Provider<T>>` (unsupported pattern).
6. Injecting `@Includes` params from inside the graph.
7. Enabling function providers `() -> T` without checking for existing function-valued bindings.

## Migration Notes (Koin -> Metro)

1. Convert Koin definitions to constructor injection where possible.
2. Replace module wiring with `@ContributesBinding` / `@ContributesTo`.
3. Keep runtime values in graph factories (`@Provides` params), not global singletons.
4. Model feature/session lifecycles with `@GraphExtension`.
5. Replace test overrides with `replaces` + `excludes` strategy.

## Quick Snippets

### Create graph directly

```kotlin
val graph = createGraph<AndroidAppGraph>()
```

### Create graph via factory

```kotlin
val graph = createGraphFactory<AndroidAppGraph.Factory>()
  .create(appConfig, platformBindings)
```

### Dynamic test graph

```kotlin
val testGraph = createDynamicGraph<AppGraph>(FakeBindings)
```

## Output Style For This Skill

When this skill is used for reviews or migrations, provide output in this order:

1. Critical Metro violations
2. Major architecture/maintainability risks
3. Minor improvements
4. Suggested refactor diffs/snippets
5. Final readiness verdict
