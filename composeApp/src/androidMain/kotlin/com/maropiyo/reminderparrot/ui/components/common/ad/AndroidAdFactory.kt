package com.maropiyo.reminderparrot.ui.components.common.ad

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class AndroidAdFactory : AdFactory {
    private var nativeAdCache: NativeAdCache? = null

    @Composable
    override fun BannerAd(modifier: Modifier) {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                AdView(context).apply {
                    adUnitId = "ca-app-pub-3940256099942544/9214589741"
                    setAdSize(
                        AdSize(
                            AdSize.FULL_WIDTH,
                            50
                        )
                    )
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }

    @Composable
    override fun NativeAd(modifier: Modifier, adPosition: Int) {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        var nativeAd by remember(adPosition) { mutableStateOf<NativeAd?>(null) }

        // キャッシュを初期化
        if (nativeAdCache == null) {
            nativeAdCache = NativeAdCache(context, scope)
        }

        // キャッシュから広告を取得または事前読み込み
        LaunchedEffect(adPosition) {
            val cache = nativeAdCache ?: return@LaunchedEffect

            // キャッシュされた広告があるかチェック
            val cachedAd = cache.getAd(adPosition)
            if (cachedAd != null) {
                nativeAd = cachedAd
                println("📱 AndroidAdFactory: ✅ キャッシュから広告を取得 (position: $adPosition)")
            } else {
                println("📱 AndroidAdFactory: ❌ キャッシュなし、事前読み込み開始 (position: $adPosition)")
                // キャッシュにない場合は事前読み込み
                cache.preloadAd(adPosition)

                // 少し待ってから再度チェック
                kotlinx.coroutines.delay(1500)
                val newAd = cache.getAd(adPosition)
                if (newAd != null) {
                    nativeAd = newAd
                    println("📱 AndroidAdFactory: ✅ 事前読み込み完了 (position: $adPosition)")
                } else {
                    println("📱 AndroidAdFactory: ⚠️ 事前読み込み失敗、ダミー表示 (position: $adPosition)")
                    // フォールバック: ダミーデータで表示
                    // 実際の広告がない場合でも何かしら表示するためのダミー広告
                }
            }

            // 次の広告も事前読み込み
            cache.preloadAds(listOf(adPosition + 5, adPosition + 10))
        }

        AndroidView(
            modifier = modifier,
            factory = { context ->
                createNativeAdView(context, null)
            },
            update = { view ->
                (view as? NativeAdView)?.let { adView ->
                    if (nativeAd != null) {
                        val ad = nativeAd!!
                        // 実際の広告データを設定
                        adView.headlineView?.let { it as TextView }?.text = ad.headline ?: ""
                        adView.bodyView?.let { it as TextView }?.text = ad.body ?: ""
                        adView.callToActionView?.let { it as Button }?.text = ad.callToAction ?: ""

                        ad.icon?.let { icon ->
                            adView.iconView?.let { it as ImageView }?.setImageDrawable(icon.drawable)
                        }

                        adView.setNativeAd(ad)
                    } else {
                        // ダミーデータを表示
                        adView.headlineView?.let { it as TextView }?.text = "おしらせ"
                        adView.bodyView?.let { it as TextView }?.text = "広告を読み込み中..."
                        adView.callToActionView?.let { it as Button }?.text = "読み込み中"
                        
                        // アイコンを灰色のプレースホルダーに設定
                        adView.iconView?.let { iconView ->
                            iconView as ImageView
                            iconView.setImageDrawable(null)
                            iconView.setBackgroundColor(Color.LTGRAY)
                        }
                    }
                }
            }
        )
    }

    private fun createNativeAdView(context: Context, nativeAd: NativeAd?): NativeAdView {
        val adView = NativeAdView(context)

        // メインコンテナ
        val mainLayout =
            LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams =
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                setPadding(12, 12, 12, 12)
                gravity = Gravity.CENTER_VERTICAL
            }

        // 左側：アイコンと本文のコンテナ
        val leftLayout =
            LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams =
                    LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                    )
            }

        // 上段：アイコンと見出しの水平レイアウト
        val topLayout =
            LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams =
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                gravity = Gravity.CENTER_VERTICAL
            }

        // 広告のアイコン（左上）
        val iconView =
            ImageView(context).apply {
                layoutParams = ViewGroup.LayoutParams(40, 40)
                scaleType = ImageView.ScaleType.FIT_CENTER
            }

        // 広告の見出し
        val headlineView =
            TextView(context).apply {
                layoutParams =
                    LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                textSize = 14f
                setTextColor(Color.BLACK)
                maxLines = 1
                setPadding(8, 0, 0, 0)
            }

        // 広告の本文（アイコンの下）
        val bodyView =
            TextView(context).apply {
                layoutParams =
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                textSize = 12f
                setTextColor(Color.GRAY)
                maxLines = 1
                setPadding(0, 8, 0, 0)
            }

        // CTAボタン（右側、上下中央）
        val ctaButton =
            Button(context).apply {
                val density = context.resources.displayMetrics.density
                layoutParams =
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        (36 * density).toInt() // 36dpをpxに変換
                    )
                textSize = 11f
                val paddingPx = (16 * density).toInt() // 16dpをpxに変換
                val paddingVerticalPx = (6 * density).toInt() // 6dpをpxに変換
                setPadding(paddingPx, paddingVerticalPx, paddingPx, paddingVerticalPx)
                minHeight = (36 * density).toInt() // 36dpをpxに変換

                // Secondaryオレンジの色を適用
                val secondaryOrange = Color.parseColor("#E59F43")
                val cornerRadiusPx = (16 * density) // 16dpをpxに変換
                val buttonBackground = GradientDrawable().apply {
                    setColor(secondaryOrange)
                    cornerRadius = cornerRadiusPx
                }
                background = buttonBackground
                setTextColor(Color.WHITE) // テキストを白に設定

                // 影を完全に削除
                elevation = 0f
                stateListAnimator = null
            }

        // レイアウトに追加
        // 上段：アイコンと見出し
        topLayout.addView(iconView)
        topLayout.addView(headlineView)

        // 左側：上段と本文
        leftLayout.addView(topLayout)
        leftLayout.addView(bodyView)

        // メインレイアウト：左側とCTAボタン
        mainLayout.addView(leftLayout)
        mainLayout.addView(ctaButton)

        adView.addView(mainLayout)

        // NativeAdViewに各要素を設定
        adView.headlineView = headlineView
        adView.bodyView = bodyView
        adView.callToActionView = ctaButton
        adView.iconView = iconView

        // 初期状態ではダミーデータで表示
        headlineView.text = "おしらせ"
        bodyView.text = "広告を読み込み中..."
        ctaButton.text = "読み込み中"
        
        // アイコンを灰色のプレースホルダーに設定
        iconView.setBackgroundColor(Color.LTGRAY)

        return adView
    }
}
