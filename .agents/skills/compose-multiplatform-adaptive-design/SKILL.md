---
name: compose-multiplatform-adaptive-design
description: Build adaptive UI in Compose Multiplatform across phones, tablets, foldables, desktop windows, and connected displays. Use for window-size-class-driven layout decisions, canonical pane patterns, adaptive navigation, and state continuity across resize and configuration changes.
---

# Compose Multiplatform Adaptive Design

## Overview

Use this skill when building or reviewing adaptive UI in Compose Multiplatform.

The skill enforces one shared adaptive model for all targets, then adds platform-specific logic
only where required (especially Android large-screen, foldable, and multi-window behavior).

## When To Use This Skill

- Designing shared Compose UI that must work across phone, tablet, foldable, desktop windows
- Migrating from fixed phone-only layouts to adaptive patterns
- Implementing list-detail, supporting pane, or feed layouts
- Choosing adaptive navigation chrome (bottom bar, rail, drawer)
- Handling window resize, orientation changes, split-screen, desktop windowing
- Preserving state during folding/unfolding and window-size changes
- Reviewing adaptive correctness and anti-patterns

## Non-Negotiable Rules

1. Make layout decisions from app window size, not device type.
2. Keep adaptive decisions centralized in app-level state.
3. Hoist state and keep unidirectional data flow; do not load different data sets by size class.
4. Do not lock orientation or aspect ratio for adaptive UI.
5. Avoid deprecated Display APIs for sizing logic.
6. Build scrollable layouts so controls are always reachable.
7. Preserve user context across resize/config changes (selection, scroll, inputs, playback position).

## Workflow

### 1) Build a shared adaptive model first

- Define a shared `AppAdaptiveState` from current window width/height and optional posture info.
- Derive booleans and policies once (for example: `showNavRail`, `useTwoPane`, `showTopBar`).
- Pass this state downward as explicit parameters.

Read: `references/adaptive-architecture.md`

### 2) Pick app-level navigation by width class

- Compact width: bottom navigation or single-pane destination flow.
- Medium width: navigation rail is usually preferred.
- Expanded and above: rail + persistent pane layouts, or drawer when information density is high.

Read: `references/canonical-layouts.md`

### 3) Pick a canonical content layout

- List-detail for master-detail exploration.
- Supporting pane when secondary content only makes sense with main content.
- Feed for many equivalent content cards with adaptive grid spans.

Read: `references/canonical-layouts.md`

### 4) Handle nested composables with local constraints

- App/content-level: use window size classes.
- Nested components: use local constraints (`BoxWithConstraints`, custom layout, modifiers).
- Avoid coupling nested reusable components to global window state.

Read: `references/adaptive-architecture.md` and
`references/advanced-compose-layout-primitives.md`

### 5) Apply Android-specific behavior where needed

- For Android targets, align with API 36+ behavior for large screens.
- Handle multi-window, desktop windowing caption insets, foldables, connected displays.
- Prefer modern camera preview approaches on foldables.

Read: `references/android-large-screen-foldables.md`

### 6) Validate with adaptive test matrix

- Test compact/medium/expanded widths and compact/medium/expanded heights.
- Test live resize, split-screen, desktop windowing, fold/unfold, display changes.
- Verify no state loss and no inaccessible controls.

Read: `references/testing-playbook.md`

## Implementation Notes

- Prefer Material 3 Adaptive APIs where available for the target:
  `NavigationSuiteScaffold`, `ListDetailPaneScaffold`, `SupportingPaneScaffold`,
  `NavigableListDetailPaneScaffold`, `NavigableSupportingPaneScaffold`.
- Availability can differ by target and version. If a target cannot use these APIs, implement the
  same pane/navigation policy using shared adaptive state and regular Compose layouts.
- For Android predictive back with adaptive navigable scaffolds, enable
  `android:enableOnBackInvokedCallback="true"` on Android 15 and lower; Android 16+ enables
  predictive back by default.

## Quick Review Checklist

- [ ] Adaptive policy is derived once at the top level.
- [ ] No `isTablet` or physical-device heuristics drive app layout.
- [ ] Canonical layout choice matches task (list-detail/supporting/feed).
- [ ] Nested components use local constraints, not global window state.
- [ ] State continuity works across resize/fold/unfold/multi-window.
- [ ] Android-specific large-screen and foldable behaviors are handled where relevant.

## Reference Map

- Core adaptive architecture: `references/adaptive-architecture.md`
- Canonical layouts and pane navigation: `references/canonical-layouts.md`
- Android 16+, multi-window, foldables, connected displays: `references/android-large-screen-foldables.md`
- Test matrix and verification workflow: `references/testing-playbook.md`
- FlexBox, visibility tracking, alignment lines, intrinsics: `references/advanced-compose-layout-primitives.md`
