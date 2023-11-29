package com.example.storyapps.ui.story_list

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.storyapps.R
import com.example.storyapps.data.di.Injection
import com.example.storyapps.data.helper.AuthDataBundle
import com.example.storyapps.data.helper.AuthHelper
import com.example.storyapps.data.pref.UserAuthViewModel
import com.example.storyapps.data.pref.UserPreference
import com.example.storyapps.data.pref.dataStore
import com.example.storyapps.data.response.ListStoryItem
import com.example.storyapps.data.response.StoryResponse
import com.example.storyapps.databinding.ActivityStoryListBinding
import com.example.storyapps.ui.ViewModelFactory
import com.example.storyapps.ui.ViewModelFactoryStory
import com.example.storyapps.ui.adapter.LoadingAdapter
import com.example.storyapps.ui.addStory.AddStoryActivity
import com.example.storyapps.ui.maps.MapsActivity

class StoryListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryListBinding
    private lateinit var storyListViewModel: StoryListViewModel
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var tokenViewModel: UserAuthViewModel
    private var token: AuthDataBundle? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_story_list)

        binding = ActivityStoryListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storyAdapter = StoryAdapter()
        storyAdapter.withLoadStateFooter(footer = LoadingAdapter{
            storyAdapter.retry()
        })
        binding.listStory.adapter = storyAdapter

        val pref = UserPreference.getInstance(application.dataStore)
        tokenViewModel = ViewModelProvider(this, ViewModelFactory(pref))[UserAuthViewModel::class.java]
        token = if (Build.VERSION.SDK_INT >= 33) {
            intent.getSerializableExtra(TOKEN_INTENT_KEY, AuthDataBundle::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(TOKEN_INTENT_KEY) as AuthDataBundle
        }
        storyListViewModel = ViewModelProvider(this, ViewModelFactoryStory(this, token?.token))[StoryListViewModel::class.java]

        getData()

        val layoutManager = LinearLayoutManager(this)
        binding.listStory.layoutManager = layoutManager

        binding.addButton.setOnClickListener {
            val itn = Intent(this, AddStoryActivity::class.java )
            itn.putExtra(AddStoryActivity.ADD_STORY_KEY, AuthDataBundle(nama = token?.nama, userId = token?.userId, token = token?.token))
            getResult.launch(itn)
        }

        binding.srlRefresh.setOnRefreshListener {
            binding.srlRefresh.isRefreshing = true;
            getData()
            Toast.makeText(this, "List stories refreshed", Toast.LENGTH_SHORT).show()
        }
        logoutBtnFunction()
    }

    private fun getData(){
        storyListViewModel.storyPagging.observe(this){
            binding.srlRefresh.isRefreshing = false;
            if (it != null){
                Log.d("IS_ERROR", it.toString())
                setStory(it)

            } else {
            }
        }
    }
    private fun setStory(story: PagingData<ListStoryItem>){

        storyAdapter.submitData(lifecycle, story)

        storyAdapter.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallback{
            override fun onItemClicked(data: ListStoryItem) {

            }
        })
    }
    private fun logoutBtnFunction(){
        binding.appBarId.setOnMenuItemClickListener{
                menuitem ->
            when(menuitem.itemId){
                R.id.lgIcon -> {
                    AuthHelper.logOut(this, tokenViewModel = tokenViewModel )
                    true
                }
                R.id.mapsIcon -> {
                    val itn = Intent(this, MapsActivity::class.java)
                    itn.putExtra(MapsActivity.MAPS_ACTIVITY_INTENT_KEY, AuthDataBundle(nama = token?.nama, userId = token?.userId, token = token?.token))
                    startActivity(itn)

                    true
                }
                else -> false
            }
        }

    }

    override fun onResume() {
        super.onResume()
        storyListViewModel.getAll(token?.token ?: "")
    }
    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == Activity.RESULT_OK){
//                getData()
                storyAdapter.refresh()
                storyAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                        if (positionStart == 0) {
                            binding.listStory.scrollToPosition(0)
                        }
                    }
                })
            }
        }

    companion object{
        const val TOKEN_INTENT_KEY = "Token Intent Key"
    }
}

