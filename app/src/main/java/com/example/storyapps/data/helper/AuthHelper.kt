package com.example.storyapps.data.helper

import android.app.Activity
import android.content.Intent
import com.example.storyapps.data.pref.UserAuthViewModel
import com.example.storyapps.ui.login.LoginActivity

class AuthHelper {

    companion object {
        fun logOut(context: Activity, tokenViewModel: UserAuthViewModel){
            tokenViewModel.resetToken()
            val intentDetail = Intent(context, LoginActivity::class.java)
            intentDetail.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intentDetail)
        }
    }
}