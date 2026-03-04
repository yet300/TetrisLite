# Adaptive Architecture (Compose Multiplatform)

## Contents

1. Goal
2. Window-First Model
3. Breakpoints
4. Shared Adaptive State
5. App-Level vs Nested-Level Adaptation
6. State Continuity Rules
7. Anti-Patterns

## 1) Goal

Create one shared UI architecture that adapts to the current app window, not to device identity.
Use this model on Android, Desktop, and iOS targets, then enrich Android with form-factor-specific
signals (folding features, multi-window, connected display context).

## 2) Window-First Model

Never decide layout using "phone/tablet/foldable" checks. Use runtime window space.

Recommended layers:

- `WindowInfoProvider` (platform): returns window width/height in dp and optional posture signals.
- `AdaptivePolicy` (shared): maps window info to width/height classes and layout rules.
- `AppAdaptiveState` (shared): immutable state consumed by composables.

## 3) Breakpoints

Use the following width classes:

- Compact: `< 600dp`
- Medium: `600dp <= width < 840dp`
- Expanded: `840dp <= width < 1200dp`
- Large: `1200dp <= width < 1600dp`
- ExtraLarge: `>= 1600dp`

Use height classes:

- Compact: `< 480dp`
- Medium: `480dp <= height < 900dp`
- Expanded: `>= 900dp`

Practical rule: width class drives most layout decisions; height class guards against impractical
two-pane layouts in short windows (for example landscape phones/flippables).

## 4) Shared Adaptive State

Example shared model:

```kotlin
enum class WidthClass { Compact, Medium, Expanded, Large, ExtraLarge }
enum class HeightClass { Compact, Medium, Expanded }

enum class NavChrome { BottomBar, Rail, Drawer }
enum class ContentLayout { SinglePane, ListDetail, SupportingPane, FeedGrid }

data class AppAdaptiveState(
    val widthClass: WidthClass,
    val heightClass: HeightClass,
    val navChrome: NavChrome,
    val contentLayout: ContentLayout,
    val preferTwoPane: Boolean,
    val maxContentWidthDp: Int,
)
```

Policy example:

```kotlin
fun buildAdaptiveState(widthDp: Int, heightDp: Int): AppAdaptiveState {
    val widthClass = when {
        widthDp >= 1600 -> WidthClass.ExtraLarge
        widthDp >= 1200 -> WidthClass.Large
        widthDp >= 840 -> WidthClass.Expanded
        widthDp >= 600 -> WidthClass.Medium
        else -> WidthClass.Compact
    }
    val heightClass = when {
        heightDp >= 900 -> HeightClass.Expanded
        heightDp >= 480 -> HeightClass.Medium
        else -> HeightClass.Compact
    }

    val navChrome = when (widthClass) {
        WidthClass.Compact -> NavChrome.BottomBar
        WidthClass.Medium, WidthClass.Expanded -> NavChrome.Rail
        WidthClass.Large, WidthClass.ExtraLarge -> NavChrome.Drawer
    }

    val preferTwoPane = widthClass >= WidthClass.Medium && heightClass != HeightClass.Compact

    return AppAdaptiveState(
        widthClass = widthClass,
        heightClass = heightClass,
        navChrome = navChrome,
        contentLayout = if (preferTwoPane) ContentLayout.ListDetail else ContentLayout.SinglePane,
        preferTwoPane = preferTwoPane,
        maxContentWidthDp = if (widthClass >= WidthClass.Expanded) 1200 else Int.MAX_VALUE,
    )
}
```

## 5) App-Level vs Nested-Level Adaptation

Use window size classes for app-level or destination-level structural decisions:

- navigation chrome
- pane count
- route-level composition

Use local constraints for nested reusable components:

- `BoxWithConstraints` when rendering different content variants
- custom layouts/modifiers for responsive arrangement
- avoid reading global window metrics inside deeply nested leaf components

## 6) State Continuity Rules

Always keep enough state/data available to render any layout variant.

Do:

- hoist selected list item, sheet state, scroll anchors, and user input
- keep detail selection in shared state even if detail pane is hidden on compact width
- maintain playback and long-running tasks across resize/config changes

Do not:

- fetch extra data only when entering expanded mode
- couple network side effects to width class flips

## 7) Anti-Patterns

- Device allowlists (for example "enable large-screen mode only on known tablets")
- Layout decisions from physical screen or deprecated display APIs
- `fillMaxWidth` everywhere with no max-width constraints for readable content
- Fixed orientation/aspect-ratio assumptions for viewfinders and media surfaces
- Non-scrollable long forms in compact-height windows
