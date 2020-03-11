package com.bubelov.coins.logs

import androidx.lifecycle.ViewModel
import com.bubelov.coins.repository.synclogs.LogsRepository

class LogsViewModel(
    private val logsRepository: LogsRepository
): ViewModel() {
    fun getAll() = logsRepository.getAll()
}