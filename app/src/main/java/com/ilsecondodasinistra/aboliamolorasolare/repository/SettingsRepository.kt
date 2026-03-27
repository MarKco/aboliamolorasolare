package com.ilsecondodasinistra.aboliamolorasolare.repository

interface SettingsRepository {
    suspend fun getX(): Int
    suspend fun getY(): Int
    suspend fun setX(x: Int)
    suspend fun setY(y: Int)
}

