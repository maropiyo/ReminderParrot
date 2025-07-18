package com.maropiyo.reminderparrot.ui.components.common.ad

import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * ネイティブ広告のキャッシュシステム
 * 事前読み込みと再利用でユーザー体験を向上
 */
class NativeAdCache(
    private val context: Context,
    private val scope: CoroutineScope
) {
    private val adCache = mutableMapOf<Int, NativeAd>()
    private val loadingPositions = mutableSetOf<Int>()
    private val mutex = Mutex()

    private val adUnitId = "ca-app-pub-3940256099942544/2247696110" // テスト用ID

    /**
     * 指定されたポジションの広告を事前読み込み
     */
    fun preloadAd(position: Int) {
        scope.launch(Dispatchers.Main) {
            mutex.withLock {
                // 既にキャッシュされている、または読み込み中の場合はスキップ
                if (adCache.containsKey(position) || loadingPositions.contains(position)) {
                    return@withLock
                }

                loadingPositions.add(position)
            }

            val adLoader = AdLoader.Builder(context, adUnitId)
                .forNativeAd { nativeAd ->
                    scope.launch {
                        mutex.withLock {
                            adCache[position] = nativeAd
                            loadingPositions.remove(position)
                        }
                        println("📱 NativeAdCache: 広告をキャッシュしました (position: $position)")
                    }
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        scope.launch {
                            mutex.withLock {
                                loadingPositions.remove(position)
                            }
                        }
                        println("📱 NativeAdCache: 広告読み込み失敗 (position: $position, error: ${adError.message})")
                    }
                })
                .withNativeAdOptions(
                    NativeAdOptions.Builder()
                        .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                        .build()
                )
                .build()

            adLoader.loadAd(AdRequest.Builder().build())
        }
    }

    /**
     * キャッシュされた広告を取得
     */
    suspend fun getAd(position: Int): NativeAd? {
        return mutex.withLock {
            adCache[position]
        }
    }

    /**
     * 複数のポジションの広告を事前読み込み
     */
    fun preloadAds(positions: List<Int>) {
        positions.forEach { preloadAd(it) }
    }

    /**
     * 古い広告をクリーンアップ
     */
    fun cleanup(currentVisiblePositions: List<Int>) {
        scope.launch {
            mutex.withLock {
                val positionsToRemove = adCache.keys.filter { position ->
                    // 現在表示中のポジションから大きく離れた広告は削除
                    currentVisiblePositions.none { abs(it - position) <= 10 }
                }

                positionsToRemove.forEach { position ->
                    adCache[position]?.destroy()
                    adCache.remove(position)
                    println("📱 NativeAdCache: 古い広告を削除しました (position: $position)")
                }
            }
        }
    }

    /**
     * 全ての広告をクリーンアップ
     */
    fun clearAll() {
        scope.launch {
            mutex.withLock {
                adCache.values.forEach { it.destroy() }
                adCache.clear()
                loadingPositions.clear()
                println("📱 NativeAdCache: 全ての広告をクリアしました")
            }
        }
    }

    /**
     * キャッシュ状態の確認
     */
    suspend fun getCacheInfo(): String {
        return mutex.withLock {
            "キャッシュ済み: ${adCache.size}件, 読み込み中: ${loadingPositions.size}件"
        }
    }
}

private fun abs(value: Int): Int = if (value < 0) -value else value
