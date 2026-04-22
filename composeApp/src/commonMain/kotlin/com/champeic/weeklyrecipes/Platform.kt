package com.champeic.weeklyrecipes

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform