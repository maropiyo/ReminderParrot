import SwiftUI
import FirebaseCore
import FirebaseMessaging
import GoogleMobileAds
import ComposeApp

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
 
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        // Firebase初期化
        _ = FirebaseManager.shared
        
        // Google Mobile Ads SDK初期化
        MobileAds.shared.start()
        return true
    }

    func application(_ application: UIApplication,
                     didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        // APNsトークンをFirebaseに転送
        Messaging.messaging().apnsToken = deviceToken

        // 少し待ってからFCMトークンを取得（APNsトークンが確実に設定されるまで）
        DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) {
            FirebaseManager.shared.refreshFCMToken { token in
                if token != nil {
                    print("リマインコ: APNs設定後のFCMトークン取得成功")
                } else {
                    print("リマインコ: APNs設定後のFCMトークン取得失敗")
                }
            }
        }
    }

    func application(_ application: UIApplication,
                     didFailToRegisterForRemoteNotificationsWithError error: Error) {
        print("リマインコ: APNs登録エラー - \(error.localizedDescription)")
    }

    func application(_ application: UIApplication,
                     didReceiveRemoteNotification userInfo: [AnyHashable: Any],
                     fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
        // Firebaseに通知を転送
        Messaging.messaging().appDidReceiveMessage(userInfo)

        completionHandler(.newData)
    }
}
