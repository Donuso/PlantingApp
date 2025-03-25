package com.example.plantingapp.item

data class MeItem(
    val userId: Int,
    val username: String,
    val userAvatar: String? = null
) {
    companion object {
        const val FAILED_ALTER_USERNAME = 1
        const val FAILED_ALTER_USERAVATAR = 2
        const val SUCCESS = 0
    }
}
