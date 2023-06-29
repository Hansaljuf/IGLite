package com.example.iglite.di

import android.app.Application
import androidx.room.Room
import com.example.iglite.data.local.dao.RemoteKeysDao
import com.example.iglite.data.local.dao.StoriesDao
import com.example.iglite.data.local.db.StoriesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): StoriesDatabase = Room.databaseBuilder(
        app, StoriesDatabase::class.java, "stories_db"
    ).build()

    @Provides
    @Singleton
    fun provideStoryDao(db: StoriesDatabase): StoriesDao = db.storiesDao()

    @Provides
    @Singleton
    fun provideRemoteKeysDao(db: StoriesDatabase): RemoteKeysDao = db.remoteKeysDao()

}