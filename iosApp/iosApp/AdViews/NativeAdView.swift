//
//  NativeAdView.swift
//  iosApp
//
//  Created by Manato Takeishi on 2025/07/17.
//  Copyright © 2025 orgName. All rights reserved.
//
import Foundation
import SwiftUI
import GoogleMobileAds
import ComposeApp

class NativeAdViewFactoryImpl: NativeAdViewFactory {
    func createNativeAdView() -> UIView {
        print("📱 NativeAd: ネイティブ広告ビューを作成開始")
        let wrapper = NativeAdViewWrapper()
        return wrapper
    }
}

class NativeAdViewWrapper: UIView {
    private var nativeAdView: NativeAdView!
    private var adLoader: AdLoader!
    private var delegate: NativeAdDelegate!
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupAdView()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setupAdView()
    }
    
    private func setupAdView() {
        print("📱 NativeAdWrapper: セットアップ開始")
        
        // NativeAdViewを作成
        nativeAdView = NativeAdView()
        nativeAdView.backgroundColor = UIColor.systemBackground
        nativeAdView.layer.cornerRadius = 8
        nativeAdView.layer.borderWidth = 1
        nativeAdView.layer.borderColor = UIColor.systemGray4.cgColor
        nativeAdView.translatesAutoresizingMaskIntoConstraints = false
        
        addSubview(nativeAdView)
        
        // 制約設定
        NSLayoutConstraint.activate([
            nativeAdView.topAnchor.constraint(equalTo: topAnchor),
            nativeAdView.leadingAnchor.constraint(equalTo: leadingAnchor),
            nativeAdView.trailingAnchor.constraint(equalTo: trailingAnchor),
            nativeAdView.bottomAnchor.constraint(equalTo: bottomAnchor),
            nativeAdView.heightAnchor.constraint(equalToConstant: 100)
        ])
        
        // AdLoaderを設定
        adLoader = AdLoader(adUnitID: "ca-app-pub-3940256099942544/3986624511",
                           rootViewController: getRootViewController(),
                           adTypes: [.native],
                           options: nil)
        
        delegate = NativeAdDelegate(nativeAdView: nativeAdView)
        adLoader.delegate = delegate
        
        print("📱 NativeAdWrapper: 広告読み込み開始")
        adLoader.load(Request())
    }
    
    private func getRootViewController() -> UIViewController? {
        if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
           let rootViewController = windowScene.windows.first?.rootViewController {
            return rootViewController
        }
        return nil
    }
}

// MARK: - Native Ad Delegate
class NativeAdDelegate: NSObject, AdLoaderDelegate, NativeAdLoaderDelegate {
    private let nativeAdView: NativeAdView
    
    init(nativeAdView: NativeAdView) {
        self.nativeAdView = nativeAdView
        super.init()
        setupNativeAdView()
    }
    
    private func setupNativeAdView() {
        // Native Adのレイアウトを設定
        nativeAdView.translatesAutoresizingMaskIntoConstraints = false
        
        // 基本的なスタイル設定
        nativeAdView.backgroundColor = UIColor.systemBackground
        nativeAdView.layer.cornerRadius = 8
        nativeAdView.layer.borderWidth = 1
        nativeAdView.layer.borderColor = UIColor.systemGray4.cgColor
        
        // サイズ制約を設定（Androidに合わせて100pt）
        NSLayoutConstraint.activate([
            nativeAdView.heightAnchor.constraint(equalToConstant: 100),
            nativeAdView.widthAnchor.constraint(greaterThanOrEqualToConstant: 250)
        ])
    }
    
    // MARK: - NativeAdLoaderDelegate
    func adLoader(_ adLoader: AdLoader, didReceive nativeAd: NativeAd) {
        print("📱 NativeAd: 広告読み込み成功！")
        
        // Native Adの各要素を設定
        setupNativeAdContent(nativeAd: nativeAd)
    }
    
    func adLoader(_ adLoader: AdLoader, didFailToReceiveAdWithError error: Error) {
        print("📱 NativeAd: 広告読み込み失敗 - \(error.localizedDescription)")
        
        // 失敗時はダミーデータで表示
        createSimpleLayout(nativeAd: nil)
    }
    
    private func setupNativeAdContent(nativeAd: NativeAd) {
        print("📱 NativeAd: 広告データを設定開始")
        print("📱 NativeAd: headline = \(nativeAd.headline ?? "nil")")
        print("📱 NativeAd: body = \(nativeAd.body ?? "nil")")
        print("📱 NativeAd: callToAction = \(nativeAd.callToAction ?? "nil")")
        print("📱 NativeAd: icon = \(nativeAd.icon != nil ? "あり" : "なし")")
        
        // Native Adをビューに関連付け
        nativeAdView.nativeAd = nativeAd
        
        // 既存のサブビューをクリア
        nativeAdView.subviews.forEach { $0.removeFromSuperview() }
        
        // シンプルなレイアウトを作成（Androidと同じ構造）
        createSimpleLayout(nativeAd: nativeAd)
    }
    
    private func createSimpleLayout(nativeAd: NativeAd?) {
        // メインスタックビュー（水平レイアウト）
        let mainStackView = UIStackView()
        mainStackView.axis = .horizontal
        mainStackView.spacing = 8
        mainStackView.alignment = .center
        mainStackView.translatesAutoresizingMaskIntoConstraints = false
        
        // 左側のコンテンツスタックビュー（垂直レイアウト）
        let leftStackView = UIStackView()
        leftStackView.axis = .vertical
        leftStackView.spacing = 8
        leftStackView.alignment = .leading
        leftStackView.translatesAutoresizingMaskIntoConstraints = false
        
        // 上段のスタックビュー（アイコン + 見出し）
        let topStackView = UIStackView()
        topStackView.axis = .horizontal
        topStackView.spacing = 8
        topStackView.alignment = .center
        topStackView.translatesAutoresizingMaskIntoConstraints = false
        
        // アイコン画像
        let iconImageView = UIImageView()
        iconImageView.contentMode = .scaleAspectFit
        iconImageView.translatesAutoresizingMaskIntoConstraints = false
        iconImageView.widthAnchor.constraint(equalToConstant: 40).isActive = true
        iconImageView.heightAnchor.constraint(equalToConstant: 40).isActive = true
        
        if let icon = nativeAd?.icon {
            iconImageView.image = icon.image
            print("📱 NativeAd: アイコン画像を設定")
        } else {
            // ダミーアイコンを設定
            iconImageView.backgroundColor = UIColor.systemGray4
            iconImageView.layer.cornerRadius = 20
            print("📱 NativeAd: ダミーアイコンを設定")
        }
        
        // 見出しラベル
        let headlineLabel = UILabel()
        headlineLabel.font = UIFont.systemFont(ofSize: 14, weight: .medium)
        headlineLabel.textColor = UIColor.black
        headlineLabel.numberOfLines = 1
        headlineLabel.text = nativeAd?.headline ?? "テスト見出し"
        headlineLabel.translatesAutoresizingMaskIntoConstraints = false
        
        // 本文ラベル
        let bodyLabel = UILabel()
        bodyLabel.font = UIFont.systemFont(ofSize: 12)
        bodyLabel.textColor = UIColor.gray
        bodyLabel.numberOfLines = 1
        bodyLabel.text = nativeAd?.body ?? "テスト本文"
        bodyLabel.translatesAutoresizingMaskIntoConstraints = false
        
        // CTAボタン
        let ctaButton = UIButton(type: .system)
        ctaButton.setTitle(nativeAd?.callToAction ?? "テスト", for: .normal)
        ctaButton.backgroundColor = UIColor(red: 0.898, green: 0.624, blue: 0.263, alpha: 1.0) // #E59F43
        ctaButton.setTitleColor(UIColor.white, for: .normal)
        ctaButton.layer.cornerRadius = 16
        ctaButton.titleLabel?.font = UIFont.systemFont(ofSize: 11)
        ctaButton.contentEdgeInsets = UIEdgeInsets(top: 6, left: 16, bottom: 6, right: 16)
        ctaButton.translatesAutoresizingMaskIntoConstraints = false
        ctaButton.heightAnchor.constraint(equalToConstant: 36).isActive = true
        
        // レイアウト構築
        topStackView.addArrangedSubview(iconImageView)
        topStackView.addArrangedSubview(headlineLabel)
        
        leftStackView.addArrangedSubview(topStackView)
        leftStackView.addArrangedSubview(bodyLabel)
        
        mainStackView.addArrangedSubview(leftStackView)
        mainStackView.addArrangedSubview(ctaButton)
        
        nativeAdView.addSubview(mainStackView)
        
        // 制約設定
        NSLayoutConstraint.activate([
            mainStackView.topAnchor.constraint(equalTo: nativeAdView.topAnchor, constant: 12),
            mainStackView.leadingAnchor.constraint(equalTo: nativeAdView.leadingAnchor, constant: 12),
            mainStackView.trailingAnchor.constraint(equalTo: nativeAdView.trailingAnchor, constant: -12),
            mainStackView.bottomAnchor.constraint(equalTo: nativeAdView.bottomAnchor, constant: -12)
        ])
        
        // GADNativeAdViewに各要素を設定
        nativeAdView.iconView = iconImageView
        nativeAdView.headlineView = headlineLabel
        nativeAdView.bodyView = bodyLabel
        nativeAdView.callToActionView = ctaButton
        
        print("📱 NativeAd: レイアウト設定完了")
    }
}
