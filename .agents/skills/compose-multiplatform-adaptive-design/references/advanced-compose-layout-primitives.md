# Advanced Compose Layout Primitives for Adaptive UI

## Contents

1. FlexBox
2. Visibility Tracking
3. Alignment Lines
4. Intrinsic Measurements
5. Selection Guide

## 1) FlexBox

Use FlexBox for small groups of variably-sized items where wrapping and grow/shrink behavior are
important. Do not use it for very large lists because it does not provide lazy item loading.

Best use cases:

- adaptive toolbars
- filter chip groups
- mixed-size action clusters

Key controls:

- container: `direction`, `wrap`, `justifyContent`, `alignItems`, `alignContent`, `rowGap`, `columnGap`
- item: `basis`, `grow`, `shrink`, `alignSelf`, `order`

Rule of thumb:

- For large data collections, prefer `LazyColumn` / `LazyVerticalGrid`.
- For wrapping a small dynamic set, prefer FlexBox over legacy flow layouts.

## 2) Visibility Tracking

Use visibility tracking to trigger analytics, prefetching, autoplay, and lifecycle-like behavior for
scrolled content.

Main APIs:

- `Modifier.onVisibilityChanged(...)`
- `Modifier.onLayoutRectChanged(...)` (lower-level)

Key parameters for `onVisibilityChanged`:

- `minFractionVisible`: trigger only after enough visible area is present
- `minDurationMs`: trigger only after sustained visibility

Keep modifier ordering intentional; put visibility modifier before padding if visibility should
include padded area.

## 3) Alignment Lines

Use alignment lines when parent layouts need semantic alignment points from children (for example
baseline or custom chart markers).

Good use cases:

- baseline-aligned typography across mixed components
- aligning labels to custom data landmarks in charts

Implementation:

- define custom `AlignmentLine`
- publish values from custom `Layout` via `alignmentLines = mapOf(...)`
- parent reads with `placeable[MyAlignmentLine]`

## 4) Intrinsic Measurements

Intrinsics solve cases where parent constraints depend on child content characteristics.

Typical example:

- vertical divider between two texts should match tallest text
- use `Modifier.height(IntrinsicSize.Min)` on parent row

Rules:

- Intrinsic queries are not equivalent to measuring children twice.
- Use intrinsic APIs only where needed; they can be expensive if overused.
- For custom layout/modifier, override intrinsic methods when defaults are inaccurate.

## 5) Selection Guide

- Need small wrapped layout with grow/shrink: use FlexBox.
- Need event when element becomes visible: use `onVisibilityChanged`.
- Need semantic child alignment in custom parent: use alignment lines.
- Need parent size derived from child content before final measure: use intrinsics.
