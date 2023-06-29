package com.example.iglite.data.local

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.iglite.data.local.db.StoriesDatabase
import com.example.iglite.data.local.model.RemoteKeys
import com.example.iglite.data.local.model.Story
import com.example.iglite.network.ApiService

@OptIn(ExperimentalPagingApi::class)
class StoriesRemoteMediator(
    private val token: String,
    private val location: Int,
    private val apiService: ApiService,
    private val db: StoriesDatabase
) : RemoteMediator<Int, Story>() {

    private companion object {
        const val STARTING_PAGE_INDEX = 1
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, Story>
    ): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE_INDEX
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                prevKey
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                nextKey
            }
        }

        try {
            val responseData =
                apiService.instagramLiteGetStories(token, page, state.config.pageSize, location)

            val story = responseData.listStory!!
            val endOfPaginationReached = story.isEmpty()

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.remoteKeysDao().deleteRemoteKeys()
                    db.storiesDao().deleteAllStories()
                }

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = story.map {
                    RemoteKeys(id = it!!.id, prevKey = prevKey, nextKey = nextKey)
                }

                db.remoteKeysDao().insertAll(keys)
                db.storiesDao().insertStories(story as List<Story>)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Story>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            db.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Story>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            db.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Story>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                db.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }
}