package org.example.reproducer

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform