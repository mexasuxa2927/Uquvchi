package com.example.a74.models

data class Message(
    var message: String? = null,
    var time: String? = null,
    var fromUid: String? = null,
    var toUid: String? = null,
    var readStatus: Boolean? = false,
    var key: String = ""
)