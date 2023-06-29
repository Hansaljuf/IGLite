package com.example.iglite.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.iglite.data.local.dao.RemoteKeysDao
import com.example.iglite.data.local.dao.StoriesDao
import com.example.iglite.data.local.model.RemoteKeys
import com.example.iglite.data.local.model.Story

@Database(entities = [Story::class, RemoteKeys::class], version = 1, exportSchema = false)
abstract class StoriesDatabase : RoomDatabase() {
    abstract fun storiesDao(): StoriesDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}