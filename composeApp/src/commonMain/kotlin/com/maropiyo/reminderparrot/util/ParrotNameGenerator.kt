package com.maropiyo.reminderparrot.util

import kotlin.random.Random

/**
 * インコの名前をランダムに生成するユーティリティ
 */
object ParrotNameGenerator {
    private val adjectives =
        listOf(
            // 性格・キャラクター系
            "おしゃべりな",
            "あまえんぼうな",
            "やさしい",
            "げんきな",
            "あかるい",
            "かわいい",
            "おだやかな",
            "たのしい",
            "しあわせな",
            "にこにこの",
            "ほんわかした",
            "おせっかいな",
            "しつこい",
            "やきもちやきな",
            "あわてんぼうな",
            "おっちょこちょいな",
            "なまけものな",
            "ねぼすけな",
            "くいしんぼうな",
            "めんどくさがりな",
            "わすれんぼうな",
            "かまってちゃんな",
            "あきっぽい",
            "いいかげんな",
            "ずぼらな",
            "てきとうな",
            "つんでれな",
            "きまぐれな",
            "のんきな",
            "せっかちな",
            "のろまな",
            "てんねんな",
            "ぼんやりした",
            "マイペースな",
            "じゆうきままな",
            "わがままな",
            "ひとりよがりな",
            "くせものな",
            "あまのじゃくな",
            "ひねくれた",
            "そっけない",
            "クールな",
            "ホットな",
            "ミステリアスな",
            "キュートな",
            "ラブリーな",
            "チャーミングな",
            "エレガントな",
            "ゴージャスな",
            "ユニークな",
            "オリジナルな",
            "クリエイティブな",
            "アーティスティックな",
            "フレッシュな",
            // 見た目系
            "ふわふわの",
            "まんまるな",
            "ちいさな",
            "ねぐせの",
            "ぽっちゃりした",
            "やせっぽちの",
            "のっぽの",
            "ちんちくりんな",
            "ぼさぼさの",
            "ひょろひょろの",
            "ぺちゃんこの",
            "むちむちの",
            "ほっそりした",
            "ずんぐりした",
            "ぷっくりした",
            "ちょこんとした",
            "つるつるの",
            "なめらかな",
            "きらきらの",
            "つやつやの",
            "ぴかぴかの",
            "もこもこの",
            "ふかふかの",
            "さらさらの",
            "しっとりした",
            "ぷるぷるの",
            "ぷにぷにの",
            "ぷよぷよの",
            // 動作・様子系
            "ねむそうな",
            "しゃきっとした",
            "いきいきした",
            "うきうきした",
            "そわそわした",
            "どきどきした",
            "わくわくした",
            "はらはらした",
            "おろおろした",
            "あたふたした",
            "ばたばたした",
            "せかせかした",
            "のそのそした",
            "ゆったりした",
            "のんびりした",
            "ぐうたらな",
            "だらだらした",
            "ぐずぐずした",
            "もたもたした",
            "ちんたらした",
            "ぼーっとした",
            "ぽかんとした",
            "きょとんとした",
            "きょろきょろした",
            "うろうろした",
            "ふらふらした",
            "ひらひらした",
            "くるくるした",
            "ぐるぐるした",
            // 個性系
            "ふしぎな",
            "へんてこな",
            "びみょうな",
            "ちゃっかりした",
            "ぬけめない",
            "ぬかりない",
            "ぬるい",
            "ゆるい",
            // 色・柄系
            "あかい",
            "あおい",
            "きいろい",
            "みどりの",
            "むらさきの",
            "オレンジの",
            "ピンクの",
            "しろい",
            "くろい",
            "はいいろの",
            "ちゃいろの",
            "きんいろの",
            "ぎんいろの",
            "にじいろの",
            "しましまの",
            "ぶちの",
            "まだらの",
            "みずたまの",
            "はながらの",
            "からふるな",
            "パステルな",
            "ビビッドな",
            "ネオンの",
            "メタリックな",
            "レインボーの",
            // 感情系
            "うれしい",
            "たのしい",
            "おもしろい",
            "びっくりした",
            "ゆうきのある",
            "しょんぼりした",
            "がっかりした",
            "ほっとした",
            "あんしんした",
            "しんぱいな",
            "ハッピーな",
            "ラッキーな"
        )

    /**
     * ランダムなインコの名前を生成する
     */
    fun generateRandomName(): String {
        val randomAdjective = adjectives[Random.nextInt(adjectives.size)]
        return randomAdjective + "インコ"
    }
}
