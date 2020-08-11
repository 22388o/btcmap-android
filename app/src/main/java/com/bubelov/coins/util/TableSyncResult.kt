package com.bubelov.coins.util

import java.time.LocalDateTime

data class TableSyncResult(
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val success: Boolean,
    val affectedRecords: Int
)