// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.example.chat.services


import com.example.chat.BuildConfig
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.jackson.responseObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal data class AcsInfo(val endpoint: String)

internal class AcsInfoService {

    suspend fun getAcsInfo(): AcsInfo {
        return withContext(Dispatchers.IO) {
            val request = "${BuildConfig.SERVICE_HERO_URL}/acs_info".httpGet()

            val responseResult = request.responseObject<AcsInfo>()
            return@withContext responseResult.third.get()
        }
    }
}
