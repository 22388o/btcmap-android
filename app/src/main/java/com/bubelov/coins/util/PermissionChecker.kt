package com.bubelov.coins.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class PermissionChecker(
    private val context: Context
) {

    fun check(permission: String): CheckResult {
        return if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            CheckResult.GRANTED
        } else {
            CheckResult.DENIED
        }
    }

    enum class CheckResult { GRANTED, DENIED }
}