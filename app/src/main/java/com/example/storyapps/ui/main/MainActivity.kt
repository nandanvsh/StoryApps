package com.example.storyapps.ui.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import com.example.storyapps.data.helper.AuthDataBundle
import com.example.storyapps.data.pref.UserAuthViewModel
import com.example.storyapps.data.pref.UserPreference
import com.example.storyapps.data.pref.dataStore
import com.example.storyapps.databinding.ActivityMainBinding
import com.example.storyapps.ui.ViewModelFactory
import com.example.storyapps.ui.story_list.StoryListActivity
import com.example.storyapps.ui.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler().postDelayed({

            val pref = UserPreference.getInstance(application.dataStore, )
            var tokenViewModel = ViewModelProvider(this, ViewModelFactory(pref))[UserAuthViewModel::class.java]

            tokenViewModel.getToken().observe(this){

                if (it.token != "" && it.token?.isNotEmpty() == true){
                    val intentDetail = Intent(this, StoryListActivity::class.java)
                    intentDetail.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intentDetail.putExtra(StoryListActivity.TOKEN_INTENT_KEY, AuthDataBundle(token = it.token, nama = it.name, userId = it.userID))
                    startActivity(intentDetail)
                } else {
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                            Pair(binding.imageView2, "image")
                            )
                    startActivity(Intent(this, WelcomeActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }, optionsCompat.toBundle())
                    finish()
                }
            }

        }, 2000)

    }

    private fun setupView(){
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
}