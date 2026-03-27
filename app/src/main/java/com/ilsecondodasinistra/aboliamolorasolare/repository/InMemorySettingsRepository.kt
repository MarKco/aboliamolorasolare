package com.ilsecondodasinistra.aboliamolorasolare.repository

/**
 * Implementazione fake in memoria per TDD/test
 */
class InMemorySettingsRepository : SettingsRepository {
    private var x: Int = 5
    private var y: Int = 10
    override suspend fun getX(): Int = x
    override suspend fun getY(): Int = y
    override suspend fun setX(x: Int) { this.x = x }
    override suspend fun setY(y: Int) { this.y = y }
}

