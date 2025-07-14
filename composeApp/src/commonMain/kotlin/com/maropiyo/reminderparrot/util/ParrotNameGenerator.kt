package com.maropiyo.reminderparrot.util

import kotlin.random.Random

/**
 * インコの名前をランダムに生成するユーティリティ
 */
object ParrotNameGenerator {
    private val adjectives =
        listOf(
            "おしゃべりな",
            "やさしい",
            "げんきな",
            "あかるい",
            "かわいい",
            "おだやかな",
            "おせっかいな",
            "しつこい",
            "あわてんぼうな",
            "おっちょこちょいな",
            "なまけものな",
            "ねぼすけな",
            "めんどくさがりな",
            "わすれんぼうな",
            "かまってちゃんな",
            "あきっぽい",
            "いいかげんな",
            "ずぼらな",
            "てきとうな",
            "きまぐれな",
            "のんきな",
            "せっかちな",
            "のろまな",
            "てんねんな",
            "ぼんやりした",
            "マイペースな",
            "じゆうきままな",
            "わがままな",
            "ちゃっかりした",
            "ぬけめない",
            "ぬかりない",
            "ぬるい",
            "ゆるい",
            "しんぱいな",
        )

    /**
     * ランダムなインコの名前を生成する
     */
    fun generateRandomName(): String {
        val randomAdjective = adjectives[Random.nextInt(adjectives.size)]
        return randomAdjective + "インコ"
    }
}
