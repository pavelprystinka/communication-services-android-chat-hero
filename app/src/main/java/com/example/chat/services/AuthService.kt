// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.example.chat.services

internal interface AuthService {
    suspend fun getToken(userId: String): String
}
