package com.ilsecondodasinistra.aboliamolorasolare.usecase

import com.ilsecondodasinistra.aboliamolorasolare.repository.SettingsRepository

class GetSettingsUseCase(private val repo: SettingsRepository) {
    suspend fun getX(): Int = repo.getX()
    suspend fun getY(): Int = repo.getY()
}

class SetSettingsUseCase(private val repo: SettingsRepository) {
    suspend fun setX(x: Int) = repo.setX(x)
    suspend fun setY(y: Int) = repo.setY(y)
}

