package com.example.storyapps.ui.regist

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.storyapps.databinding.ActivityRegistBinding
import com.example.storyapps.ui.login.LoginActivity

class RegistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistBinding
    private lateinit var registViewModel: RegistViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[RegistViewModel::class.java]
        checkIsNotEmpty()

        registViewModel.auth.observe(this){
            if (it.error == true){
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
            checkIsNotEmpty()
        }
         registViewModel.isLoading.observe(this){
             setButtonLoading(it)
         }

        binding.buttonToLogin.setOnClickListener {
            val intentDetail = Intent(this, LoginActivity::class.java)
            intentDetail.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intentDetail)
        }

        binding.nameEditTextLayout.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                checkIsNotEmpty()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
        binding.emailEditTextLayout.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                checkIsNotEmpty()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        binding.passwordEditTextLayout.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                checkIsNotEmpty()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        binding.signupButton.setOnClickListener {
            if (checkIsNotEmpty()){
                registViewModel.register(
                    RegistViewModel.Companion.RegistForm(
                        name = binding.nameEditTextLayout.text.toString(),
                        email = binding.emailEditTextLayout.text.toString(),
                        password = binding.passwordEditTextLayout.text.toString()
                    )
                )
            }
        }



    }

    private fun checkIsNotEmpty(): Boolean{
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+.[A-Za-z0-9.-]\$"
        if (!binding.nameEditTextLayout.text.isNullOrEmpty() && !binding.emailEditTextLayout.text.isNullOrEmpty() && !binding.passwordEditTextLayout.text.isNullOrEmpty() ) {
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
        binding.signupButton.setLoading(value)
    }

    private fun setButtonEnable(value: Boolean) {
        binding.signupButton.isEnabled = value
    }
}