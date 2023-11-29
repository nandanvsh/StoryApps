package com.example.storyapps.ui.login

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyapps.data.response.AuthResponse
import com.example.storyapps.data.retrofit.ApiConfig
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {

    companion object{
        private const val TAG = "LOGIN_VIEW_MODEL"
        data class LoginForm(val email: String, val password: String)
    }
    private val _auth = MutableLiveData<AuthResponse>()
    val auth: LiveData<AuthResponse> = _auth

    private val _isloading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isloading

    fun login(loginForm: LoginForm){
        _isloading.value = true
        val client = ApiConfig.getApiService().login(email = loginForm.email, password = loginForm.password)
        client.enqueue(object : Callback<AuthResponse>{
            override fun onResponse(call: retrofit2.Call<AuthResponse>, response: Response<AuthResponse>) {
                _isloading.value = false
                if (response.isSuccessful){
                    _auth.value = response.body()
                }else{
                    if (response.code() == 401){
                        _auth.value = AuthResponse(error = true, message = "Incorrect password")
                    } else if (response.code() == 400) {

                        _auth.value = AuthResponse(error = true, message = "Incorrect password or email")
                    } else {
                        _auth.value = AuthResponse(error = true, message = response.message())
                    }
                    Log.e(ContentValues.TAG, "onFailure ${response.message()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<AuthResponse>, t: Throwable) {
                _isloading.value = false
                if (t.message.toString().startsWith("failed to connect")){
                    _auth.value = AuthResponse(error = true, message = "Failed to connect API")
                }else{
                    _auth.value = AuthResponse(error = true, message = t.message)
                }
                Log.e(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }

}