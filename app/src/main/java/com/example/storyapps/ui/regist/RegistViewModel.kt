package com.example.storyapps.ui.regist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyapps.data.response.AuthResponse
import com.example.storyapps.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistViewModel : ViewModel(){

    private val _auth = MutableLiveData<AuthResponse>()
    val auth: LiveData<AuthResponse> = _auth

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun register(registForm: RegistForm){
        _isLoading.value = true
        val client = ApiConfig.getApiService().register(email = registForm.email, password = registForm.password, name = registForm.name)
        client.enqueue(object : Callback<AuthResponse>{
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _auth.value = response.body()
                } else {
                    _auth.value = AuthResponse(error = true, message = response.message())
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                _isLoading.value = false
                if (t.message.toString().startsWith("failed to connect")){
                    _auth.value = AuthResponse(error = true, message = "Failed to connect API")
                }
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }

        })
    }

    companion object{
        private const val TAG = "REGIST_VIEW_MODEL"

        data class RegistForm(val name: String, val email: String, val password: String)
    }
}