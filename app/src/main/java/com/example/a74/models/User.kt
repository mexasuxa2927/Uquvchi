package com.example.a74.models

import java.io.Serializable

class User(
    var name: String? = "",
    var uid: String? = "",
    var gmail: String? = "",
    var photoUrl: String? = "",
    var status: Boolean = false,
    var statusTime: String = ""
) :
    Serializable {

}