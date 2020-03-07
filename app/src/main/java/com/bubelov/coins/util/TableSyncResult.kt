package com.bubelov.coins.util

import org.joda.time.DateTime

data class TableSyncResult(
    val startDate: DateTime,
    val endDate: DateTime,
    val success: Boolean,
    val affectedRecords: Int
)