package com.example.plantingapp

class DataExchange {
    companion object {
        private var currentUsername: String? = null

        fun setCurrentUsername(username: String) {
            currentUsername = username
        }

        fun getCurrentUsername(): String? {
            return currentUsername
        }
    }
}