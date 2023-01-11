// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.example.chat.services

import com.example.chat.BuildConfig
import com.example.chat.model.User
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class UserService {
    suspend fun getUsers(): List<User> {
        return withContext(Dispatchers.IO) {
            val request = "${BuildConfig.SERVICE_HERO_URL}/users".httpGet()

            val responseResult = request.responseObject(User.DeserializerList())
            return@withContext responseResult.third.get()
        }
    }

    suspend fun createUser(name: String): User {
        return withContext(Dispatchers.IO) {
            val request = "${BuildConfig.SERVICE_HERO_URL}/users".httpPost()
                .header(mapOf("Content-Type" to "application/json"))
                .body("{\"name\": \"$name\"}")

            val responseResult = request.responseObject(User.Deserializer())
            return@withContext responseResult.third.get()
        }
    }
}
