// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.example.chat.model

import com.azure.android.communication.common.CommunicationUserIdentifier
import com.github.kittinunf.fuel.core.ResponseDeserializable
import org.json.JSONArray
import org.json.JSONObject


internal data class User(val id: String, val name: String, val communicationIdentifier: CommunicationUserIdentifier, var token: String? = null) {
    class DeserializerList : ResponseDeserializable<List<User>> {
        override fun deserialize(content: String): List<User> {

            val result = mutableListOf<User>()

            val array = JSONArray(content)
            for (i in 0 until array.length()) {
                result.add(deserializeUser(array.getJSONObject(i)))
            }
            return result
        }
    }

    class Deserializer : ResponseDeserializable<User> {
        override fun deserialize(content: String): User {
            val json = JSONObject(content)

            return deserializeUser(json)
        }
    }

    companion object {
        fun deserializeUser(json: JSONObject): User {
            val id = json.getString("id")
            val name = json.getString("name")
            val communicationUserIdentifierJson = json.getJSONObject("communicationIdentity")
            val communicationUserId = communicationUserIdentifierJson.getString("communicationUserId")
            val token = if (json.has("token")) json.getString("token") else null

            return User(id, name, CommunicationUserIdentifier(communicationUserId), token)
        }
    }

    override fun toString(): String {
        return name
    }
}