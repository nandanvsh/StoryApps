package com.example.storyapps.data.pref

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapps.data.database.ListStoryDatabase
import com.example.storyapps.data.database.ListStoryRemoteMediator
import com.example.storyapps.data.pagging.StoryPagingSource
import com.example.storyapps.data.response.ListStoryItem
import com.example.storyapps.data.response.StoryResponse
import com.example.storyapps.data.retrofit.ApiService
import kotlinx.coroutines.flow.Flow

class UserRepository (
   private val storyDatabase: ListStoryDatabase, private  val apiService: ApiService
) {
    @OptIn(ExperimentalPagingApi::class)
    fun getStory(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = ListStoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
//                StoryPagingSource(apiService)
                storyDatabase.listStoryDao().getAllStory()
            }
        ).liveData
    }

}