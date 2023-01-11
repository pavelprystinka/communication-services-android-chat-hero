// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.example.chat.services

import com.example.chat.BuildConfig
import com.example.chat.model.User
import com.github.kittinunf.fuel.httpGet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class AuthServiceImpl : AuthService {

    override suspend fun getToken(userId: String): String {

        return withContext(Dispatchers.IO) {
            val request = "${BuildConfig.SERVICE_HERO_URL}/users/$userId/token/call-with-chat".httpGet()

            val jsonArray = request.responseObject(User.Deserializer())
            return@withContext jsonArray.third.get().token!!
        }
    }
}
