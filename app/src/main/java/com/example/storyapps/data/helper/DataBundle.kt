package com.example.storyapps.data.helper

import java.io.Serializable

data class AuthDataBundle(var userId: String? = null, var nama: String? = null, var token: String? = null) :
    Serializable

data class DetailData(val nama: String, val image: String, val description: String ) :
    Serializable