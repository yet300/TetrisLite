# Adaptive Testing Playbook

## Contents

1. Test Matrix
2. Core Transition Scenarios
3. Canonical Layout Checks
4. Multi-Window and Desktop Checks
5. Foldable and Display Checks
6. Automation Guidance
7. Exit Criteria

## 1) Test Matrix

Test all key width classes:

- Compact (`<600dp`)
- Medium (`600..839dp`)
- Expanded (`840..1199dp`)

Also sample Large/ExtraLarge if desktop or external displays are in scope.

Test height constraints:

- Compact height (`<480dp`) in landscape and short windows
- Medium/Expanded height variants

## 2) Core Transition Scenarios

Verify behavior while app is running:

- portrait <-> landscape rotations
- live window resize (split-screen divider and desktop edges)
- one-pane <-> two-pane transitions
- fold <-> unfold transitions
- move between internal/external displays

For each transition, validate:

- no crash
- no inaccessible controls
- no critical visual overlap
- state continuity (selection, inputs, scroll, playback)

## 3) Canonical Layout Checks

List-detail:

- Compact/Medium: list -> detail navigation works
- Back from detail returns to list in single-pane
- Expanded: list + detail visible together with selected state continuity

Supporting pane:

- Compact: supporting content reachable (route/sheet/button)
- Medium: balanced split and readable content
- Expanded: primary content remains visually dominant

Feed:

- columns adapt correctly to width
- grid remains scroll-performant
- emphasized items and full-width headers render correctly

## 4) Multi-Window and Desktop Checks

- Enter and exit split-screen and desktop windowing
- Rapid resize loops do not crash or leak
- UI responds quickly after resize
- Caption/header insets do not hide top controls
- Keyboard and mouse interactions remain functional

## 5) Foldable and Display Checks

- No important content under hinge/fold bounds
- Tabletop/book posture adaptations trigger correctly (if supported)
- Density/resource changes do not blur or mis-scale UI
- Camera preview remains correctly oriented/aspect-correct across transitions

## 6) Automation Guidance

Use:

- Compose UI tests for pane visibility and navigation transitions
- screenshot/golden tests for representative width classes
- regression tests for state restoration after activity recreation

Android-specific:

- emulator/device matrix including tablet and foldable profiles
- API 36 behavior validation for `sw >= 600dp`

## 7) Exit Criteria

Adaptive work is done when:

- all critical flows pass compact/medium/expanded variants
- resize/fold/multi-window transitions preserve state
- no orientation/aspect-ratio lock workarounds remain for general UI
- no deprecated window-size APIs drive layout behavior
