# Canonical Layouts (List-Detail, Supporting Pane, Feed)

## Contents

1. Choose the Right Pattern
2. List-Detail Behavior
3. Supporting Pane Behavior
4. Feed Behavior
5. Navigation and Back Behavior
6. Material 3 Adaptive Integration
7. Multiplatform Fallback Strategy

## 1) Choose the Right Pattern

- Use **List-Detail** when detail content is meaningful on its own.
- Use **Supporting Pane** when secondary content depends on primary content.
- Use **Feed** when many equivalent content cards are scanned quickly.

## 2) List-Detail Behavior

Target behavior by width class:

- Expanded and above: list + detail visible together.
- Medium/Compact: single pane, navigate list -> detail.
- Back from detail on single-pane returns to list.

State continuity expectations:

- If two-pane shrinks to one-pane while detail is open, keep detail visible.
- If one-pane detail expands to two-pane, keep selected item highlighted in list.
- If one-pane list expands to two-pane, show placeholder detail.

## 3) Supporting Pane Behavior

Purpose: keep user focus on primary content with contextual secondary content.

Recommended proportions:

- Medium width: 50/50 split when both panes are meaningful.
- Expanded width and above: around 70/30 (main/supporting).
- Compact width: supporting content via bottom sheet or alternate route.

Unlike list-detail, the supporting pane is usually not valuable in isolation.

## 4) Feed Behavior

Implementation rules:

- Use lazy grids for large datasets.
- Set adaptive min item width and derive columns from available width.
- Use item spans for visual hierarchy.
- Use full-width spans for section headers/dividers (`maxLineSpan` where available).

Practical policy:

- Compact width behaves close to a single-column feed.
- Medium and above progressively increase columns.
- Enforce max content width to avoid very long unreadable lines.

## 5) Navigation and Back Behavior

For navigable pane scaffolds, choose back behavior intentionally:

- `PopUntilScaffoldValueChange`:
  best default; back follows pane-structure changes.
- `PopUntilContentChange`:
  back restores previously viewed item/content.
- `PopUntilCurrentDestinationChange`:
  preserves destination-focused semantics.
- `PopLatest`:
  strict stack behavior; can be unintuitive when form factor changes mid-flow.

## 6) Material 3 Adaptive Integration

Use when available for the current target/version:

```kotlin
implementation("androidx.compose.material3.adaptive:adaptive")
implementation("androidx.compose.material3.adaptive:adaptive-layout")
implementation("androidx.compose.material3.adaptive:adaptive-navigation")
```

Common adaptive building blocks:

- `NavigationSuiteScaffold`
- `ListDetailPaneScaffold` / `NavigableListDetailPaneScaffold`
- `SupportingPaneScaffold` / `NavigableSupportingPaneScaffold`
- `ThreePaneScaffoldNavigator`

Predictive back:

- Android 15 and lower: opt in with `android:enableOnBackInvokedCallback="true"`.
- Android 16 and above: enabled by default.

## 7) Multiplatform Fallback Strategy

If a target cannot use Material 3 Adaptive APIs directly:

1. Keep the same shared `AppAdaptiveState`.
2. Implement pane logic with regular Compose (`Row`, `Box`, `AnimatedContent`, navigation routes).
3. Keep behavior parity with canonical rules:
   - one-pane/two-pane transitions
   - selected item continuity
   - explicit back behavior semantics
