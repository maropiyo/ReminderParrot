import Foundation
import Firebase
import FirebaseMessaging
import UserNotifications
import UIKit
import ComposeApp

@objc(FirebaseManager)
public class FirebaseManager: NSObject {
    
    @objc public static let shared = FirebaseManager()
    
    private var fcmToken: String?
    
    override init() {
        super.init()
        setupFirebase()
    }
    
    private func setupFirebase() {
        FirebaseApp.configure()
        Messaging.messaging().delegate = self
        
        // APNsトークンの自動取得を有効化
        UNUserNotificationCenter.current().delegate = self
        
        // 通知許可の自動要求
        requestNotificationPermission { granted in
            if granted {
                // APNsトークンの受信を待ってからFCMトークンを取得する
                print("リマインコ: 通知許可が付与されました")
            } else {
                print("リマインコ: 通知許可が拒否されました")
            }
        }
    }
    
    @objc public func requestNotificationPermission(completion: @escaping (Bool) -> Void) {
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
            if let error = error {
                print("リマインコ: 通知許可エラー - \(error.localizedDescription)")
            }
            
            if granted {
                DispatchQueue.main.async {
                    UIApplication.shared.registerForRemoteNotifications()
                }
            }
            completion(granted)
        }
    }
    
    @objc public func getFCMToken() -> String? {
        // 現在のトークンがない場合、UserDefaultsから取得を試行
        if fcmToken == nil {
            fcmToken = UserDefaults.standard.string(forKey: "FCMToken")
        }
        return fcmToken
    }
    
    @objc public func refreshFCMToken(completion: @escaping (String?) -> Void) {
        Messaging.messaging().token { token, error in
            if let error = error {
                print("リマインコ: FCMトークン取得エラー - \(error.localizedDescription)")
                completion(nil)
            } else if let token = token {
                print("リマインコ: FCMトークン取得成功")
                self.fcmToken = token
                
                // UserDefaultsに保存（KMPから読み取り可能）
                UserDefaults.standard.set(token, forKey: "FCMToken")
                UserDefaults.standard.synchronize()
                
                completion(token)
            } else {
                print("リマインコ: FCMトークンがnilです")
                completion(nil)
            }
        }
    }
}

// MARK: - MessagingDelegate
extension FirebaseManager: MessagingDelegate {
    public func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        print("Firebase registration token: \(String(describing: fcmToken))")
        self.fcmToken = fcmToken
        
        // UserDefaultsに保存（KMPから読み取り可能）
        if let token = fcmToken {
            UserDefaults.standard.set(token, forKey: "FCMToken")
            UserDefaults.standard.synchronize()
        }
        
        // KMPのNotificationServiceに通知
        NotificationCenter.default.post(
            name: Notification.Name("FCMTokenRefresh"),
            object: nil,
            userInfo: ["token": fcmToken ?? ""]
        )
    }
    
    public func messaging(_ messaging: Messaging, didReceiveRemoteMessage remoteMessage: [AnyHashable: Any]) {
        print("リマインコ: リモート通知受信")
        
        // リモートメッセージを処理
        if let messageData = remoteMessage as? [String: Any] {
            // 通知を作成して表示
            showNotificationFromRemoteMessage(messageData)
        }
    }
    
    private func showNotificationFromRemoteMessage(_ data: [String: Any]) {
        let content = UNMutableNotificationContent()
        
        if let title = data["title"] as? String {
            content.title = title
        }
        
        if let body = data["body"] as? String {
            content.body = body
        }
        
        content.sound = UNNotificationSound.default
        
        let request = UNNotificationRequest(
            identifier: "remote_notification_\(Date().timeIntervalSince1970)",
            content: content,
            trigger: nil // 即座に表示
        )
        
        UNUserNotificationCenter.current().add(request) { error in
            if let error = error {
                print("リマインコ: リモート通知表示エラー - \(error.localizedDescription)")
            }
        }
    }
}

// MARK: - UNUserNotificationCenterDelegate
extension FirebaseManager: UNUserNotificationCenterDelegate {
    public func userNotificationCenter(_ center: UNUserNotificationCenter,
                                      willPresent notification: UNNotification,
                                      withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        // アプリがフォアグラウンドでも通知を表示
        completionHandler([.banner, .sound, .badge])
    }
    
    public func userNotificationCenter(_ center: UNUserNotificationCenter,
                                      didReceive response: UNNotificationResponse,
                                      withCompletionHandler completionHandler: @escaping () -> Void) {
        // 通知タップ時の処理
        completionHandler()
    }
}