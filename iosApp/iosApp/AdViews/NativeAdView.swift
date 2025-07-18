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
    // 読み込み済み広告を保持するマップ
    private static var loadedAds: [Int: NativeAd] = [:]
    
    // メモリリーク防止: 最大保持数制限
    private static let maxCachedAds = 20
    
    func createNativeAdView() -> UIView {
        print("📱 NativeAd: ネイティブ広告ビューを作成開始")
        let wrapper = NativeAdViewWrapper()
        return wrapper
    }
    
    func createNativeAdView(adPosition: Int32) -> UIView {
        print("📱 NativeAd: ポジション\(adPosition)の広告ビューを作成開始")
        let wrapper = NativeAdViewWrapper(adPosition: Int(adPosition))
        return wrapper
    }
    
    static func getLoadedAd(for position: Int) -> NativeAd? {
        return loadedAds[position]
    }
    
    static func setLoadedAd(_ ad: NativeAd, for position: Int) {
        // メモリリーク防止のクリーンアップ
        cleanupOldAds(currentPosition: position)
        
        loadedAds[position] = ad
        print("📱 NativeAdFactory: 広告を保存 (position: \(position), total: \(loadedAds.count))")
    }
    
    /**
     * 古い広告をクリーンアップしてメモリリークを防ぐ
     */
    private static func cleanupOldAds(currentPosition: Int) {
        if loadedAds.count >= maxCachedAds {
            // 現在位置から離れた古い広告を削除
            let positionsToRemove = loadedAds.keys.filter { position in
                abs(position - currentPosition) > 10
            }.sorted { abs($0 - currentPosition) > abs($1 - currentPosition) }
            
            // 最も離れた位置から削除
            let removeCount = loadedAds.count - maxCachedAds + 1
            for position in positionsToRemove.prefix(removeCount) {
                loadedAds.removeValue(forKey: position)
                print("📱 NativeAdFactory: 古い広告を削除 (position: \(position))")
            }
        }
    }
}

class NativeAdViewWrapper: UIView {
    private var nativeAdView: NativeAdView!
    private var adLoader: AdLoader!
    private var delegate: NativeAdDelegate!
    private let adPosition: Int
    
    init(adPosition: Int = 0) {
        self.adPosition = adPosition
        super.init(frame: .zero)
        setupAdView()
    }
    
    override init(frame: CGRect) {
        self.adPosition = 0
        super.init(frame: frame)
        setupAdView()
    }
    
    required init?(coder: NSCoder) {
        self.adPosition = 0
        super.init(coder: coder)
        setupAdView()
    }
    
    private func setupAdView() {
        print("📱 NativeAdWrapper: セットアップ開始 (position: \(adPosition))")
        
        // NativeAdViewを作成
        nativeAdView = NativeAdView()
        nativeAdView.backgroundColor = UIColor.clear
        nativeAdView.translatesAutoresizingMaskIntoConstraints = false
        
        addSubview(nativeAdView)
        
        // 制約設定
        NSLayoutConstraint.activate([
            nativeAdView.topAnchor.constraint(equalTo: topAnchor),
            nativeAdView.leadingAnchor.constraint(equalTo: leadingAnchor),
            nativeAdView.trailingAnchor.constraint(equalTo: trailingAnchor),
            nativeAdView.bottomAnchor.constraint(equalTo: bottomAnchor)
        ])
        
        // 読み込み済み広告をチェック
        if let loadedAd = NativeAdViewFactoryImpl.getLoadedAd(for: adPosition) {
            print("📱 NativeAdWrapper: ✅ 読み込み済み広告を取得 (position: \(adPosition))")
            delegate = NativeAdDelegate(nativeAdView: nativeAdView, adPosition: adPosition)
            delegate.setupNativeAdContent(nativeAd: loadedAd)
        } else {
            print("📱 NativeAdWrapper: ❌ 広告なし、読み込み開始 (position: \(adPosition))")
            // 先に読み込み中表示を表示
            delegate = NativeAdDelegate(nativeAdView: nativeAdView, adPosition: adPosition)
            delegate.createSimpleLayout(nativeAd: nil)
            
            // 新しい広告を読み込み
            loadAdDirectly()
        }
    }
    
    private func loadAdDirectly() {
        print("📱 NativeAdWrapper: 広告読み込み開始 (position: \(adPosition))")
        adLoader = AdLoader(adUnitID: "ca-app-pub-3940256099942544/3986624511",
                           rootViewController: getRootViewController(),
                           adTypes: [.native],
                           options: nil)
        
        delegate = NativeAdDelegate(nativeAdView: nativeAdView, adPosition: adPosition)
        adLoader.delegate = delegate
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
    private let adPosition: Int
    
    init(nativeAdView: NativeAdView, adPosition: Int = 0) {
        self.nativeAdView = nativeAdView
        self.adPosition = adPosition
        super.init()
        setupNativeAdView()
    }
    
    private func setupNativeAdView() {
        // Native Adのレイアウトを設定
        nativeAdView.translatesAutoresizingMaskIntoConstraints = false
        
        // 基本的なスタイル設定（Androidに合わせて枠線なし）
        nativeAdView.backgroundColor = UIColor.clear
        
        // サイズ制約を設定（Androidに合わせて100pt）
        NSLayoutConstraint.activate([
            nativeAdView.heightAnchor.constraint(equalToConstant: 100),
            nativeAdView.widthAnchor.constraint(greaterThanOrEqualToConstant: 250)
        ])
    }
    
    // MARK: - NativeAdLoaderDelegate
    func adLoader(_ adLoader: AdLoader, didReceive nativeAd: NativeAd) {
        print("📱 NativeAd: 広告読み込み成功！(position: \(adPosition))")
        
        // 読み込み済み広告として保存
        NativeAdViewFactoryImpl.setLoadedAd(nativeAd, for: adPosition)
        
        // Native Adの各要素を設定
        setupNativeAdContent(nativeAd: nativeAd)
    }
    
    func adLoader(_ adLoader: AdLoader, didFailToReceiveAdWithError error: Error) {
        print("📱 NativeAd: ⚠️ 広告読み込み失敗、読み込み中表示維持 (position: \(adPosition)) - \(error.localizedDescription)")
        
        // 失敗時は読み込み中表示を維持（ダミーデータで表示）
        createSimpleLayout(nativeAd: nil)
    }
    
    func setupNativeAdContent(nativeAd: NativeAd) {
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
    
    func createSimpleLayout(nativeAd: NativeAd?) {
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
        
        // アイコン画像（見出しと同じ高さに調整）
        let iconImageView = UIImageView()
        iconImageView.contentMode = .scaleAspectFit
        iconImageView.translatesAutoresizingMaskIntoConstraints = false
        
        if let icon = nativeAd?.icon {
            iconImageView.image = icon.image
            print("📱 NativeAd: アイコン画像を設定")
        } else {
            // ダミーアイコンを設定（Androidと同じ色）
            iconImageView.backgroundColor = UIColor.systemGray4
            iconImageView.layer.cornerRadius = 8 // 16x16のサイズに合わせて調整
            print("📱 NativeAd: ダミーアイコンを設定")
        }
        
        // 見出しラベル
        let headlineLabel = UILabel()
        headlineLabel.font = UIFont.systemFont(ofSize: 14, weight: .medium)
        headlineLabel.textColor = UIColor.black
        headlineLabel.numberOfLines = 1
        headlineLabel.text = nativeAd?.headline ?? "おしらせ"
        headlineLabel.translatesAutoresizingMaskIntoConstraints = false
        
        // 本文ラベル
        let bodyLabel = UILabel()
        bodyLabel.font = UIFont.systemFont(ofSize: 12)
        bodyLabel.textColor = UIColor.gray
        bodyLabel.numberOfLines = 1
        bodyLabel.text = nativeAd?.body ?? "広告を読み込み中..."
        bodyLabel.translatesAutoresizingMaskIntoConstraints = false
        
        // CTAボタン
        let ctaButton = UIButton(type: .system)
        ctaButton.setTitle(nativeAd?.callToAction ?? "読み込み中", for: .normal)
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
        
        // 制約設定（Androidと同じ12ptの余白）
        NSLayoutConstraint.activate([
            mainStackView.topAnchor.constraint(equalTo: nativeAdView.topAnchor, constant: 8),
            mainStackView.leadingAnchor.constraint(equalTo: nativeAdView.leadingAnchor, constant: 8),
            mainStackView.trailingAnchor.constraint(equalTo: nativeAdView.trailingAnchor, constant: -8),
            mainStackView.bottomAnchor.constraint(equalTo: nativeAdView.bottomAnchor, constant: -8),
            
            // アイコンを見出しテキストの高さに合わせる（適切なサイズに制限）
            iconImageView.heightAnchor.constraint(equalToConstant: 16),
            iconImageView.widthAnchor.constraint(equalToConstant: 16)
        ])
        
        // GADNativeAdViewに各要素を設定
        nativeAdView.iconView = iconImageView
        nativeAdView.headlineView = headlineLabel
        nativeAdView.bodyView = bodyLabel
        nativeAdView.callToActionView = ctaButton
        
        print("📱 NativeAd: レイアウト設定完了")
    }
}
