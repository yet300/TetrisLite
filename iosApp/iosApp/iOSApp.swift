#if os(iOS)
import SwiftUI
import Shared

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self)
    var appDelegate: AppDelegate

    var body: some Scene {
        WindowGroup {
            ContentView(rootComponent: appDelegate.root)
                .ignoresSafeArea(.all, edges: .all)
        }
    }
}

class AppDelegate: NSObject, UIApplicationDelegate {
    private var stateKeeper = StateKeeperDispatcherKt.StateKeeperDispatcher(savedState: nil)
    private let appGraph: AppGraph = NativeAppGraphKt.createNativeAppGraph()

    lazy var root: RootComponent = RootComponentFactoryKt.createRootComponent(
        componentContext: DefaultComponentContext(
            lifecycle: ApplicationLifecycle(),
            stateKeeper: stateKeeper,
            instanceKeeper: nil,
            backHandler: nil
        ),
        graph: appGraph
    )
    
    func application(_ application: UIApplication, shouldSaveSecureApplicationState coder: NSCoder) -> Bool {
        StateKeeperUtilsKt.save(coder: coder, state: stateKeeper.save())
        return true
    }
    
    func application(_ application: UIApplication, shouldRestoreSecureApplicationState coder: NSCoder) -> Bool {
        stateKeeper = StateKeeperDispatcherKt.StateKeeperDispatcher(savedState: StateKeeperUtilsKt.restore(coder: coder))
        return true
    }
}
#endif
