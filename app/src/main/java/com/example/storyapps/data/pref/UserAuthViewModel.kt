package com.example.storyapps.data.pref

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class UserAuthViewModel(private val preference: UserPreference) : ViewModel() {


    fun getToken(): LiveData<UserModel>{
        return preference.getSession().asLiveData()
    }

    fun saveToken(token: String, name: String, userId : String){
        viewModelScope.launch {
            preference.saveSession(UserModel(name = name, token = token, userID = userId))
        }
    }

    fun resetToken(){
        viewModelScope.launch {
            preference.saveSession(UserModel(name = "", token = "", userID = ""))
        }
    }
}