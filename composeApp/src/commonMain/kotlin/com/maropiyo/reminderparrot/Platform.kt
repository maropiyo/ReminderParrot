package com.maropiyo.reminderparrot

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
