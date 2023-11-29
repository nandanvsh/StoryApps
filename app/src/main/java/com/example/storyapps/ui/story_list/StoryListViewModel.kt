package com.example.storyapps.ui.story_list

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.RemoteMediator
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.example.storyapps.data.database.ListStoryDatabase
import com.example.storyapps.data.database.ListStoryRemoteMediator
import com.example.storyapps.data.di.Injection
import com.example.storyapps.data.pref.UserRepository
import com.example.storyapps.data.response.ListStoryItem
import com.example.storyapps.data.response.StoryResponse
import com.example.storyapps.data.retrofit.ApiConfig
import retrofit2.Callback
import retrofit2.Response

class StoryListViewModel(repository: UserRepository) : ViewModel() {

    private val _listStory = MutableLiveData<StoryResponse>()
    val listOurStory : LiveData<StoryResponse> = _listStory
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val storyPagging: LiveData<PagingData<ListStoryItem>> =
        repository.getStory().cachedIn(viewModelScope)

    fun getAll(token: String){
        _isLoading.value = true
        val client = ApiConfig.getApiService(token).getStories(location = 1)
        client.enqueue(object : retrofit2.Callback<StoryResponse> {
            override fun onResponse(call: retrofit2.Call<StoryResponse>, response: Response<StoryResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _listStory.value = response.body()
                } else {
                    _listStory.value = response.body()
                    if (response.code() == 401){
                        _listStory.value = StoryResponse(error = true, message = response.message() )
                    }
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<StoryResponse>, t: Throwable) {
                _isLoading.value = false
                _listStory.value = StoryResponse(error = true, message = t.message.toString())
                Log.e(TAG, "onFailure Fatal: ${t.message.toString()}")
            }
        })
    }




    companion object{
        private const val TAG = "USER_VIEW_MODEL"
    }
}

