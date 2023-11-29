package com.example.storyapps.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.storyapps.data.response.ListStoryItem


@Database(
    entities = [ListStoryItem::class, RemoteKeys::class],
    version = 7,
    exportSchema = false
)
abstract class ListStoryDatabase : RoomDatabase()  {
    abstract fun listStoryDao(): ListStoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao
    companion object {
        @Volatile
        private var INSTANCE: ListStoryDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): ListStoryDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ListStoryDatabase::class.java, "story_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}