package com.mitimiti.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
