package com.example.plantingapp.item

object DataExchange {
    var USERID: String? = "1"
    private var currentUsername: String? = null

    fun setCurrentUsername(username: String) {
        currentUsername = username
    }

    fun getCurrentUsername(): String? {
        return currentUsername
    }
}