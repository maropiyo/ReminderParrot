//
//  NativeAdCache.swift
//  iosApp
//
//  Created by Manato Takeishi on 2025/07/18.
//  Copyright © 2025 orgName. All rights reserved.
//

import Foundation
import GoogleMobileAds

/**
 * iOS用ネイティブ広告キャッシュシステム
 * 事前読み込みと再利用でユーザー体験を向上
 */
class NativeAdCache {
    static let shared = NativeAdCache()
    
    private var adCache: [Int: NativeAd] = [:]
    private var loadingPositions: Set<Int> = []
    private let queue = DispatchQueue(label: "NativeAdCache", attributes: .concurrent)
    
    private let adUnitID = "ca-app-pub-3940256099942544/3986624511" // テスト用ID
    
    private init() {}
    
    /**
     * 指定されたポジションの広告を事前読み込み
     */
    func preloadAd(position: Int) {
        queue.async(flags: .barrier) {
            // 既にキャッシュされている、または読み込み中の場合はスキップ
            if self.adCache[position] != nil || self.loadingPositions.contains(position) {
                return
            }
            
            self.loadingPositions.insert(position)
        }
        
        DispatchQueue.main.async {
            let adLoader = AdLoader(
                adUnitID: self.adUnitID,
                rootViewController: self.getRootViewController(),
                adTypes: [.native],
                options: nil
            )
            
            let delegate = CacheAdDelegate(cache: self, position: position)
            adLoader.delegate = delegate
            adLoader.load(Request())
        }
    }
    
    /**
     * キャッシュされた広告を取得
     */
    func getAd(position: Int) -> NativeAd? {
        return queue.sync {
            return adCache[position]
        }
    }
    
    /**
     * 複数のポジションの広告を事前読み込み
     */
    func preloadAds(positions: [Int]) {
        positions.forEach { preloadAd(position: $0) }
    }
    
    /**
     * 広告をキャッシュに保存
     */
    func cacheAd(_ ad: NativeAd, for position: Int) {
        queue.async(flags: .barrier) {
            self.adCache[position] = ad
            self.loadingPositions.remove(position)
            print("📱 NativeAdCache: 広告をキャッシュしました (position: \(position))")
        }
    }
    
    /**
     * 読み込み失敗時の処理
     */
    func failedToLoad(position: Int, error: Error) {
        queue.async(flags: .barrier) {
            self.loadingPositions.remove(position)
            print("📱 NativeAdCache: 広告読み込み失敗 (position: \(position), error: \(error.localizedDescription))")
        }
    }
    
    /**
     * 古い広告をクリーンアップ
     */
    func cleanup(currentVisiblePositions: [Int]) {
        queue.async(flags: .barrier) {
            let positionsToRemove = self.adCache.keys.filter { position in
                // 現在表示中のポジションから大きく離れた広告は削除
                !currentVisiblePositions.contains { abs($0 - position) <= 10 }
            }
            
            positionsToRemove.forEach { position in
                self.adCache.removeValue(forKey: position)
                print("📱 NativeAdCache: 古い広告を削除しました (position: \(position))")
            }
        }
    }
    
    /**
     * 全ての広告をクリーンアップ
     */
    func clearAll() {
        queue.async(flags: .barrier) {
            self.adCache.removeAll()
            self.loadingPositions.removeAll()
            print("📱 NativeAdCache: 全ての広告をクリアしました")
        }
    }
    
    /**
     * キャッシュ状態の確認
     */
    func getCacheInfo() -> String {
        return queue.sync {
            return "キャッシュ済み: \(adCache.count)件, 読み込み中: \(loadingPositions.count)件"
        }
    }
    
    private func getRootViewController() -> UIViewController? {
        if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
           let rootViewController = windowScene.windows.first?.rootViewController {
            return rootViewController
        }
        return nil
    }
}

// MARK: - キャッシュ用デリゲート
class CacheAdDelegate: NSObject, AdLoaderDelegate, NativeAdLoaderDelegate {
    private weak var cache: NativeAdCache?
    private let position: Int
    
    init(cache: NativeAdCache, position: Int) {
        self.cache = cache
        self.position = position
        super.init()
    }
    
    func adLoader(_ adLoader: AdLoader, didReceive nativeAd: NativeAd) {
        cache?.cacheAd(nativeAd, for: position)
    }
    
    func adLoader(_ adLoader: AdLoader, didFailToReceiveAdWithError error: Error) {
        cache?.failedToLoad(position: position, error: error)
    }
}