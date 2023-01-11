// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.example.chat.model

internal class ChatThreadInfoViewModel(val id: String, val topic: String) {
    override fun toString(): String {
        return topic
    }
}
