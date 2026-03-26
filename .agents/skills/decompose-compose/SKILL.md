---
name: decompose-compose
description: This skill should be used when the user is integrating Decompose with Jetpack or Multiplatform Compose UI, mentions "subscribeAsState", the "Children" composable from Decompose, "stackAnimation", "predictiveBackAnimation", "ChildPages" composable, "ChildPanels" composable, rendering a ChildSlot in Compose, tab navigation with a bottom bar using Decompose, "LifecycleController" for desktop, Compose previews for Decompose components, or back handling in Compose using Decompose's BackHandler.
version: 1.0.0
---

You are helping wire Decompose components to Compose UI. Follow these patterns exactly.

## Observing State

Convert `Value<T>` to Compose `State<T>` with `subscribeAsState()`.

```kotlin
import com.arkivanov.decompose.extensions.compose.subscribeAsState

@Composable
fun CounterContent(component: CounterComponent, modifier: Modifier = Modifier) {
    val model by component.model.subscribeAsState()  // auto-subscribes and unsubscribes

    Column(modifier = modifier) {
        Text(text = model.count.toString())
        Button(onClick = component::onIncrementClicked) { Text("Increment") }
    }
}
```

**Rules:** Always use `by` delegation. Never subscribe manually. Expose a `Model` data class, not raw mutable state.

## Rendering Child Stack

```kotlin
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation

@Composable
fun RootContent(component: RootComponent, modifier: Modifier = Modifier) {
    Children(
        stack = component.stack,
        modifier = modifier,
        animation = stackAnimation(fade() + scale()),
    ) {
        when (val child = it.instance) {
            is RootComponent.Child.ListChild -> ListContent(child.component)
            is RootComponent.Child.DetailsChild -> DetailsContent(child.component)
        }
    }
}
```

Animations (combinable with `+`): `fade()`, `scale()`, `slide()`, or fully custom `stackAnimation { ... }`.

## Predictive Back Gesture (Android 13+)

```kotlin
@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun RootContent(component: RootComponent, modifier: Modifier = Modifier) {
    Children(
        stack = component.stack,
        modifier = modifier,
        animation = predictiveBackAnimation(
            backHandler = component.backHandler,
            fallbackAnimation = stackAnimation(fade() + scale()),
            onBack = component::onBackClicked,
        ),
    ) { /* content */ }
}
```

Interface must extend `BackHandlerOwner`: `interface RootComponent : BackHandlerOwner { ... }`

## Rendering Child Slot (Dialogs/Modals)

```kotlin
val dialogSlot by component.dialogSlot.subscribeAsState()
dialogSlot.child?.instance?.also { dialog ->
    AlertDialog(
        onDismissRequest = dialog::onDismissClicked,
        text = { Text(dialog.message) },
        confirmButton = { Button(onClick = dialog::onDismissClicked) { Text("OK") } },
    )
}
```

## Rendering Child Pages (Pager)

```kotlin
import com.arkivanov.decompose.extensions.compose.pages.ChildPages
import com.arkivanov.decompose.extensions.compose.pages.PagesScrollAnimation

ChildPages(
    pages = component.pages,
    onPageSelected = component::selectPage,
    modifier = modifier,
    scrollAnimation = PagesScrollAnimation.Default,
) { _, page ->
    ImageContent(component = page, modifier = Modifier.fillMaxSize())
}
```

## Rendering Child Panels (Multi-Pane)

```kotlin
BoxWithConstraints(modifier = modifier) {
    val mode = when {
        maxWidth >= 1200.dp -> ChildPanelsMode.TRIPLE
        maxWidth >= 800.dp -> ChildPanelsMode.DUAL
        else -> ChildPanelsMode.SINGLE
    }
    LaunchedEffect(mode) {
        component.setMode(mode)
    }
    ChildPanels(
        panels = panels,
        mainChild = { ArticleListContent(it.instance) },
        detailsChild = { ArticleDetailsContent(it.instance) },
        layout = HorizontalChildPanelsLayout(dualWeights = Pair(0.4f, 0.6f), tripleWeights = Triple(0.3f, 0.4f, 0.3f)),
    )
}
```

## Tab Navigation with Bottom Bar

```kotlin
@Composable
fun TabsContent(component: TabsComponent, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Children(stack = component.stack, modifier = Modifier.weight(1f)) {
            when (val child = it.instance) {
                is TabsComponent.Child.HomeChild -> HomeContent(child.component)
                is TabsComponent.Child.ProfileChild -> ProfileContent(child.component)
            }
        }
        val stack by component.stack.subscribeAsState()
        val active = stack.active.instance
        NavigationBar {
            NavigationBarItem(selected = active is TabsComponent.Child.HomeChild, onClick = component::onHomeTabClicked, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") })
            NavigationBarItem(selected = active is TabsComponent.Child.ProfileChild, onClick = component::onProfileTabClicked, icon = { Icon(Icons.Default.Person, null) }, label = { Text("Profile") })
        }
    }
}
```

Component uses `bringToFront` for tab switching: `fun onHomeTabClicked() = nav.bringToFront(Config.Home)`

## Desktop: LifecycleController

```kotlin
application {
    val windowState = rememberWindowState()
    LifecycleController(lifecycle, windowState)   // required on Desktop
    Window(onCloseRequest = ::exitApplication, state = windowState) { RootContent(root) }
}
```

## Compose Previews

```kotlin
class PreviewCounterComponent : CounterComponent {
    override val model: Value<CounterComponent.Model> = MutableValue(CounterComponent.Model(count = 42))
    override val dialogSlot: Value<ChildSlot<*, DialogComponent>> = MutableValue(ChildSlot())
    override fun onIncrementClicked() {}
    override fun onInfoClicked() {}
}

@Preview @Composable
fun CounterContentPreview() { CounterContent(PreviewCounterComponent()) }
```

For `ComponentContext` in previews: `val PreviewContext = DefaultComponentContext(LifecycleRegistry())`

## Back Handling in Compose

```kotlin
DisposableEffect(component.backHandler, hasChanges) {
    val callback = BackCallback(isEnabled = hasChanges) { component.onBackClicked() }
    component.backHandler.register(callback)
    onDispose { component.backHandler.unregister(callback) }
}
```

Avoid Jetpack's `BackHandler {}` composable — it bypasses the component hierarchy.

## Critical Warnings

- **Never create root component inside `@Composable`** — may run on background thread
- **`subscribeAsState()` handles subscription lifecycle** — don't call `subscribe()`/`unsubscribe()` manually
- **`Children()` manages `SaveableStateHolder`** — don't add `rememberSaveableStateHolder` yourself
- **Desktop requires `LifecycleController`** — without it components won't receive lifecycle events
- **Use `bringToFront` not `push` for tabs**

## Dependencies

```kotlin
commonMain.dependencies {
    implementation("com.arkivanov.decompose:decompose:$decomposeVersion")
    implementation("com.arkivanov.decompose:extensions-compose:$decomposeVersion")
}
```
