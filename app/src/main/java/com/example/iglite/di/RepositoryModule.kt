package com.example.iglite.di

import com.example.iglite.data.InstagramLiteRepository
import com.example.iglite.data.local.db.StoriesDatabase
import com.example.iglite.data.local.preference.PreferenceSession
import com.example.iglite.network.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideInstagramLiteRepository(
        apiService: ApiService,
        storiesDatabase: StoriesDatabase,
        preferenceSession: PreferenceSession
    ) = InstagramLiteRepository(apiService, storiesDatabase, preferenceSession)
}