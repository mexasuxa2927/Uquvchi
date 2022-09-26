package com.example.a74.models

import java.io.Serializable

class GroupModel : Serializable {
    var key:String?=null
    var name: String? =null
    var users: ArrayList<User>? = null
    var groupPhoto: String?= null

    constructor(
        key: String,
        name: String,
        users: ArrayList<User>,
        groupPhoto: String
    ) {
        this.key = key
        this.name = name
        this.users = users
        this.groupPhoto = groupPhoto
    }
    constructor()


}