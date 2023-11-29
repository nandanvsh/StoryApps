package com.example.storyapps.ui.login

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.storyapps.R
import com.example.storyapps.data.helper.AuthDataBundle
import com.example.storyapps.data.helper.requestPermissionLauncher
import com.example.storyapps.data.pref.UserAuthViewModel
import com.example.storyapps.data.pref.UserPreference
import com.example.storyapps.data.pref.dataStore
import com.example.storyapps.databinding.ActivityLoginBinding
import com.example.storyapps.ui.ViewModelFactory
import com.example.storyapps.ui.decorations.EmailEditText
import com.example.storyapps.ui.decorations.PasswordEditText
import com.example.storyapps.ui.regist.RegistActivity
import com.example.storyapps.ui.story_list.StoryListActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var PasswordEditText: PasswordEditText
    private lateinit var EmailEditText: EmailEditText
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding


    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.INTERNET
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermissionLauncher(this, REQUIRED_PERMISSION)
        val pref = UserPreference.getInstance(application.dataStore)

        EmailEditText = findViewById(R.id.emailEditTextLayout)
        PasswordEditText = findViewById(R.id.passwordEditTextLayout)
        val registerButton = binding.buttonToRegist

        var userToken = ViewModelProvider(this, ViewModelFactory(pref))[UserAuthViewModel::class.java]
        loginViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[LoginViewModel::class.java]

        checkIsNotEmpty()

        loginViewModel.auth.observe(this){
            if (it.error == true){
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }else if (it.error == false){
                userToken.saveToken(token = it.loginResult!!.token!!, name = it.loginResult!!.name!!, userId = it.loginResult!!.userId!! )
                userToken.getToken().observe(this){
                    if (it.token != "" && it.token?.isNotEmpty() == true){
                        val intentDetail = Intent(this, StoryListActivity::class.java)
                        intentDetail.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intentDetail.putExtra(StoryListActivity.TOKEN_INTENT_KEY, AuthDataBundle(token = it.token, nama = it.name, userId = it.userID))
                        startActivity(intentDetail)
                    }
                }
                checkIsNotEmpty()
            }else {
                Toast.makeText(this, "Unknown Error", Toast.LENGTH_SHORT).show()
            }
        }
        loginViewModel.isLoading.observe(this){
            setButtonLoading(it)
        }

        registerButton.setOnClickListener {
            val intentDetail = Intent(this, RegistActivity::class.java)
            startActivity(intentDetail)
        }

        binding.emailEditTextLayout.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                checkIsNotEmpty()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        binding.passwordEditTextLayout.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                checkIsNotEmpty()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        binding.loginButton.setOnClickListener {
            if(checkIsNotEmpty()){
                loginViewModel.login(LoginViewModel.Companion.LoginForm(email = binding.emailEditTextLayout.text.toString(), password = binding.passwordEditTextLayout.text.toString()))
            } else{
                Toast.makeText(this, "Make sure you enter the data correctly", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun checkIsNotEmpty(): Boolean{
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+.[A-Za-z0-9.-]\$"
        if (!binding.emailEditTextLayout.text.isNullOrEmpty() && !binding.passwordEditTextLayout.text.isNullOrEmpty() ) {
            if (binding.passwordEditTextLayout.text!!.length >= 8 && binding.emailEditTextLayout.text!!.matches(emailRegex.toRegex())){
                setButtonEnable(true)
                return true
            } else {

                setButtonEnable(false)
            }
        } else {
            setButtonEnable(false)
        }

        return false
    }

    private fun setButtonLoading(value: Boolean){
        setButtonEnable(false)
        binding.loginButton.setLoading(value)
        }

    private fun setButtonEnable(value: Boolean) {
        binding.loginButton.isEnabled = value
    }


}