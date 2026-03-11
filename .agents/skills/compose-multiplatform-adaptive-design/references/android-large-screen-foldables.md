# Android Large Screens, Foldables, and Multi-Window

## Contents

1. API 36 Baseline Changes
2. Manifest and Runtime Rules
3. Multi-Window and Desktop Windowing
4. Connected Displays
5. Foldables and Posture Awareness
6. Camera Preview Guidance
7. High-Risk Pitfalls

## 1) API 36 Baseline Changes

For apps targeting Android 16 (API 36), on displays with smallest width `>= 600dp`:

- orientation restrictions are ignored
- aspect-ratio restrictions are ignored
- app resizability restrictions are ignored

This affects tablets, inner foldable displays, and desktop windowing scenarios.

Timeline:

- API 36: opt-out exists via manifest property.
- API 37+: opt-out is removed; restrictions are always ignored on `sw >= 600dp`.

## 2) Manifest and Runtime Rules

Do:

- keep activities resizable and adaptive
- remove orientation and aspect-ratio locks for UI apps
- use window metrics APIs and window size classes

Avoid:

- `android:resizeableActivity="false"` as a layout strategy
- hard `screenOrientation` locks for general app surfaces
- `minAspectRatio` / `maxAspectRatio` restrictions for adaptive screens

If temporary opt-out is required (API 36 only), use:

- `android.window.PROPERTY_COMPAT_ALLOW_RESTRICTED_RESIZABILITY`

## 3) Multi-Window and Desktop Windowing

Key rules:

- On modern large screens, multi-window is normal behavior.
- Window size can change frequently; preserve state and avoid layout flashes.
- Handle caption bar insets in desktop windowing (`captionBar` insets).
- In multi-resume environments, treat exclusive resources (camera/mic) carefully.

Useful APIs and flags:

- `isInMultiWindowMode()`
- `onMultiWindowModeChanged()`
- `FLAG_ACTIVITY_LAUNCH_ADJACENT` + `FLAG_ACTIVITY_NEW_TASK`
- `WindowManager#getCurrentWindowMetrics()`
- `WindowManager#getMaximumWindowMetrics()`

## 4) Connected Displays

Connected displays can change:

- display id
- density
- refresh and HDR characteristics
- available bounds

Rules:

- use activity/UI context (not application context) for display-dependent queries
- relayout and resource-scale when density changes
- avoid device allowlists for large-screen features
- support keyboard, mouse, trackpad, and external media peripherals

Launching on a target display:

```kotlin
val options = ActivityOptions.makeBasic()
options.setLaunchDisplayId(targetDisplayId)
startActivity(intent, options.toBundle())
```

## 5) Foldables and Posture Awareness

Use Jetpack WindowManager (`WindowInfoTracker`) to observe `WindowLayoutInfo` and
`FoldingFeature` values:

- `state`: `FLAT` or `HALF_OPENED`
- `orientation`: `HORIZONTAL` or `VERTICAL`
- `occlusionType`: `NONE` or `FULL`
- `isSeparating`: whether fold/hinge splits logical areas

Posture rules:

- Tabletop: `HALF_OPENED` + horizontal fold
- Book posture: `HALF_OPENED` + vertical fold
- Keep controls away from hinge/fold bounds when separation is active

Trifold/landscape-foldable considerations:

- do not assume natural orientation is portrait
- handle density config changes between outer and inner displays
- maintain continuity across fold/unfold and posture changes

## 6) Camera Preview Guidance

Preferred order:

1. CameraX + `PreviewView` (best default)
2. CameraViewfinder for Camera2 codebases
3. Manual Camera2 transforms only when necessary
4. Camera intents for basic capture flows

Never assume:

- sensor orientation matches app orientation
- aspect ratio changes imply rotation changes
- full-screen bounds equal available app window bounds

## 7) High-Risk Pitfalls

- UI stretched without max-width constraints
- non-scrollable layouts in compact-height windows
- camera preview distortion after fold/unfold or resize
- state loss during activity recreation on config changes
- fixed resource scaling after moving between displays with different density
