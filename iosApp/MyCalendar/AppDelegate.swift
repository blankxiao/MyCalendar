import UIKit
import composeApp

@main
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        // 初始化 Koin（必须在任何 Compose UI 之前）
        KoinInitializerKt.initKoin()

        window = UIWindow(frame: UIScreen.main.bounds)
        window?.rootViewController = MainViewControllerKt.MainViewController()
        window?.makeKeyAndVisible()

        return true
    }
}
