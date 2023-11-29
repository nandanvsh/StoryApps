package com.example.storyapps.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapps.data.pref.UserAuthViewModel
import com.example.storyapps.data.pref.UserPreference

class ViewModelFactory(private val preference: UserPreference) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(UserAuthViewModel::class.java)){
            return UserAuthViewModel(preference) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }


}