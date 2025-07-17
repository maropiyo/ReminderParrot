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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView

class AndroidAdFactory : AdFactory {
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
    override fun NativeAd(modifier: Modifier) {
        val context = LocalContext.current
        var nativeAd by remember { mutableStateOf<NativeAd?>(null) }

        DisposableEffect(Unit) {
            val adLoader =
                AdLoader
                    .Builder(context, "ca-app-pub-3940256099942544/2247696110")
                    .forNativeAd { ad ->
                        nativeAd = ad
                    }.withNativeAdOptions(
                        NativeAdOptions
                            .Builder()
                            .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                            .build()
                    ).build()

            adLoader.loadAd(AdRequest.Builder().build())

            onDispose {
                nativeAd?.destroy()
            }
        }

        AndroidView(
            modifier = modifier,
            factory = { context ->
                createNativeAdView(context, null)
            },
            update = { view ->
                nativeAd?.let { ad ->
                    (view as? NativeAdView)?.let { adView ->
                        // 各ビューを更新
                        adView.headlineView?.let { it as TextView }?.text = ad.headline
                        adView.bodyView?.let { it as TextView }?.text = ad.body
                        adView.callToActionView?.let { it as Button }?.text = ad.callToAction

                        ad.icon?.let { icon ->
                            adView.iconView?.let { it as ImageView }?.setImageDrawable(icon.drawable)
                        }

                        adView.setNativeAd(ad)
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

        // 広告のアイコン（左側）
        val iconView =
            ImageView(context).apply {
                layoutParams = ViewGroup.LayoutParams(40, 40)
                scaleType = ImageView.ScaleType.FIT_CENTER
            }

        // テキストコンテナ（中央）
        val textLayout =
            LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams =
                    LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                setPadding(8, 0, 8, 0)
            }

        // 広告の見出し
        val headlineView =
            TextView(context).apply {
                layoutParams =
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                textSize = 14f
                setTextColor(Color.BLACK)
                maxLines = 1
            }

        // 広告の本文
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
            }

        // CTAボタン（右側）
        val ctaButton =
            Button(context).apply {
                layoutParams =
                    LinearLayout
                        .LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        ).apply {
                            gravity = Gravity.CENTER_VERTICAL
                        }
                textSize = 11f
                setPadding(16, 6, 16, 6)
                minHeight = 36

                // Secondaryオレンジの色を適用
                val secondaryOrange = Color.parseColor("#E59F43")
                val density = context.resources.displayMetrics.density
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
        textLayout.addView(headlineView)
        textLayout.addView(bodyView)

        mainLayout.addView(iconView)
        mainLayout.addView(textLayout)
        mainLayout.addView(ctaButton)

        adView.addView(mainLayout)

        // NativeAdViewに各要素を設定
        adView.headlineView = headlineView
        adView.bodyView = bodyView
        adView.callToActionView = ctaButton
        adView.iconView = iconView

        // 初期状態では空の状態で表示
        headlineView.text = ""
        bodyView.text = ""
        ctaButton.text = ""

        return adView
    }
}
