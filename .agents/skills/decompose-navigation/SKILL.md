---
name: decompose-navigation
description: This skill should be used when the user is implementing navigation with Decompose, mentions "ChildStack", "ChildSlot", "ChildPages", "ChildPanels", "ChildItems", "StackNavigation", "SlotNavigation", "PagesNavigation", "PanelsNavigation", "childStack", "childSlot", "childPages", "childPanels", asks which navigation model to use, asks about push/pop navigation, dialog navigation, pager navigation, tab navigation, master-detail layout, deep linking with Decompose, navigation configuration classes, or "bringToFront".
version: 1.0.0
---

You are helping implement navigation with Decompose. Choose the right model and follow these patterns exactly.

## Choosing a Navigation Model

| Scenario | Model |
|----------|-------|
| Screens pushed onto a stack (list → details, login → home) | **ChildStack** |
| One optional child at a time (dialog, modal, bottom sheet) | **ChildSlot** |
| Horizontal pager / tab-with-swipe (image gallery, onboarding) | **ChildPages** |
| Responsive master-detail layout (list + details side by side) | **ChildPanels** |
| Lazy list where each item is a live component | **ChildItems** |
| None of the above (carousel, custom state machine) | **Generic Navigation** |

Multiple navigation models in one component are fine — just use different `key` values.

## Configuration Requirements

```kotlin
@Serializable  // kotlinx-serialization plugin required
private sealed interface Config {
    @Serializable data object List : Config
    @Serializable data class Details(val itemId: Long) : Config
}
```

**Rules:** `@Serializable`, immutable `data class`/`data object`, `equals`/`hashCode` via data class, small size (<500KB on Android), unique within ChildStack.

## ChildStack — Screen Stack Navigation

```kotlin
interface RootComponent {
    val stack: Value<ChildStack<*, Child>>
    fun onBackClicked(toIndex: Int)

    sealed class Child {
        class ListChild(val component: ListComponent) : Child()
        class DetailsChild(val component: DetailsComponent) : Child()
    }
}

class DefaultRootComponent(componentContext: ComponentContext) : RootComponent, ComponentContext by componentContext {

    private val nav = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, RootComponent.Child>> =
        childStack(
            source = nav,
            serializer = Config.serializer(),
            initialConfiguration = Config.List,
            handleBackButton = true,
            childFactory = ::createChild,
        )

    private fun createChild(config: Config, ctx: ComponentContext): RootComponent.Child =
        when (config) {
            is Config.List -> ListChild(DefaultListComponent(ctx, onItemSelected = { nav.pushNew(Config.Details(it)) }))
            is Config.Details -> DetailsChild(DefaultDetailsComponent(ctx, config.itemId, onFinished = nav::pop))
        }

    override fun onBackClicked(toIndex: Int) = nav.popTo(index = toIndex)

    @Serializable private sealed interface Config {
        @Serializable data object List : Config
        @Serializable data class Details(val itemId: Long) : Config
    }
}
```

**Key operations:** `push`, `pushNew` (guard double-tap), `pushToFront`, `pop`, `popTo(index)`, `popWhile { }`, `replaceCurrent`, `replaceAll`, `bringToFront` (tabs only).

**Result delivery:**
```kotlin
nav.pop {  // onComplete runs after navigation
    (stack.active.instance as? ListChild)?.component?.onItemDeleted(itemId)
}
```

## ChildSlot — Single Optional Child (Dialogs/Modals)

```kotlin
class DefaultCounterComponent(componentContext: ComponentContext) : ComponentContext by componentContext {

    private val dialogNav = SlotNavigation<DialogConfig>()

    val dialogSlot: Value<ChildSlot<*, DialogComponent>> =
        childSlot(
            source = dialogNav,
            serializer = null,
            handleBackButton = true,
            childFactory = { config, _ -> DefaultDialogComponent(config.value, dialogNav::dismiss) },
        )

    fun onInfoClicked() = dialogNav.activate(DialogConfig(value = 42))

    @Serializable private data class DialogConfig(val value: Int)
}
```

**Operations:** `activate(config)`, `dismiss()`.
**In Compose:** `slot.child?.instance?.also { dialog -> ... }`

## ChildPages — Pager Navigation

```kotlin
class DefaultGalleryComponent(componentContext: ComponentContext) : ComponentContext by componentContext {

    private val nav = PagesNavigation<ImageId>()

    val pages: Value<ChildPages<*, ImageComponent>> =
        childPages(
            source = nav,
            serializer = ImageId.serializer(),
            initialPages = { Pages(items = ImageId.entries, selectedIndex = 0) },
            childFactory = { id, ctx -> DefaultImageComponent(ctx, id) },
        )

    fun selectPage(index: Int) = nav.select(index = index)
}
```

**Operations:** `select(index)`, `selectNext()`, `selectPrev()`, `selectFirst()`, `selectLast()`, `setItems(items, selectedIndex)`.

## ChildPanels — Multi-Pane Layout

```kotlin
class DefaultMultiPaneComponent(componentContext: ComponentContext) : ComponentContext by componentContext {

    private val nav = PanelsNavigation<Unit, DetailsConfig, ExtraConfig>()

    val panels = childPanels(
        source = nav,
        initialPanels = { Panels(main = Unit) },
        serializers = Triple(null, DetailsConfig.serializer(), ExtraConfig.serializer()),
        handleBackButton = true,
        mainFactory = { _, ctx -> MainChild(DefaultListComponent(ctx, onItemSelected = { id -> nav.navigate { it.copy(details = DetailsConfig(id)) } })) },
        detailsFactory = { config, ctx -> DetailsChild(DefaultDetailsComponent(ctx, config.itemId, onFinished = { nav.navigate { it.copy(details = null) } })) },
        extraFactory = { config, ctx -> ExtraChild(DefaultExtraComponent(ctx, config)) },
    )

    fun setMode(mode: ChildPanelsMode) = nav.navigate { it.copy(mode = mode) }
}
```

**Modes:** `SINGLE` (phone), `DUAL` (tablet), `TRIPLE` (large tablet/desktop). Drive from Compose via `BoxWithConstraints` + `LaunchedEffect`.

## Multiple Navigation Models

```kotlin
private val mainStack = childStack(source = mainNav, key = "MainStack", ...)
private val sideStack = childStack(source = sideNav, key = "SideStack", ...)
```

## Deep Linking

```kotlin
override val stack = childStack(
    source = nav,
    serializer = Config.serializer(),
    initialStack = { buildInitialStack(deepLinkUrl) },
    childFactory = ::createChild,
)
```

## Critical Warnings

- **Always navigate on the Main thread** — background navigation causes undefined behavior
- **ChildStack: configurations must be unique** — duplicates throw by default (set `DecomposeSettings.duplicateConfigurationsEnabled = true` to allow)
- **Use `bringToFront` for tabs, not `push`** — push creates duplicates
- **Use `pushNew` not `push`** to guard against double-taps
- **Android bundle size limit** — keep configs lean
- **Pass `serializer = null`** if you intentionally don't want state saved (e.g., transient dialogs)
- **Multiple stacks need unique `key`** — default `"default"` collides
