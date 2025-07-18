//
//  BannarAdView.swift
//  iosApp
//
//  Created by Manato Takeishi on 2025/07/17.
//  Copyright © 2025 orgName. All rights reserved.
//
import Foundation
import SwiftUI
import GoogleMobileAds
import ComposeApp

class BannerAdView: BannerAdViewFactory {
    func createBannerAdView() -> UIView {
        let banner = BannerView()
        banner.adUnitID = "ca-app-pub-3940256099942544/2435281174"
        
        // rootViewControllerを設定
        if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
           let rootViewController = windowScene.windows.first?.rootViewController {
            banner.rootViewController = rootViewController
        }
        
        banner.load(Request())
        return banner
    }
}
