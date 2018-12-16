/*
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <https://unlicense.org>
 */

package com.bubelov.coins.repository.currency

import com.bubelov.coins.api.coins.CoinsApi
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrenciesRepository @Inject constructor(
    private val api: CoinsApi,
    private val db: CurrenciesDb,
    private val assetsCache: CurrenciesAssetsCache
) {
    private var assetCacheUploaded = false

    init {
        GlobalScope.launch {
            if (db.count() == 0) {
                Timber.d("Uploading asset cache")
                db.insert(assetsCache.getCurrencies())
                Timber.d("Asset cache uploaded. Size: ${db.count()}")
            }

            assetCacheUploaded = true
        }
    }

    suspend fun syncWithApi() {
        Timber.d("syncWithApi()")

        return withContext(Dispatchers.IO) {
            while(!assetCacheUploaded) {
                Timber.d("Waiting fo asset cache to upload...")
                delay(100)
            }

            val currencies = api.getCurrencies().await()
            Timber.d("Got ${currencies.size} currencies from the API")
            Timber.d(currencies.toString())
            db.insert(currencies)
        }
    }
}