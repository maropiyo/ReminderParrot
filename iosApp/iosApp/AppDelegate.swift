//
//  AppDelegate.swift
//  iosApp
//
//  Created by Manato Takeishi on 2025/07/17.
//  Copyright © 2025 orgName. All rights reserved.
//
import Foundation
import SwiftUI
import GoogleMobileAds

class AppDelegate: UIResponder, UIApplicationDelegate {
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        MobileAds.shared.start(completionHandler: nil)
        return true
    }
}
