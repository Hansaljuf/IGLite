package com.example.iglite

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.iglite.data.InstagramLiteRepository
import com.example.iglite.data.local.model.Story
import com.example.iglite.ui.adapter.InstagramLiteStoryAdapter
import com.example.iglite.ui.main.MainViewModel
import com.example.iglite.utils.DataDummy
import com.example.iglite.utils.MainDispatcherRule
import com.example.iglite.utils.StoryPagingSourceTest
import com.example.iglite.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelUnitTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesTestRule = MainDispatcherRule()

    @Mock
    private lateinit var instagramLiteRepository: InstagramLiteRepository

    private val fakeToken = "fake_token"

    @Test
    fun `Get all stories successfully and compare the first data`() = runTest {
        val dummyStory = DataDummy.generateStoryResponse()
        val data: PagingData<Story> = StoryPagingSourceTest.snapshot(dummyStory)
        val expectedQuote = MutableLiveData<PagingData<Story>>()
        expectedQuote.value = data

        Mockito.`when`(instagramLiteRepository.getStoriesLiveData("Bearer $fakeToken"))
            .thenReturn(expectedQuote)

        val mainViewModel = MainViewModel(instagramLiteRepository)
        mainViewModel.instagramLiteGetStories(fakeToken)
        val actualQuote: PagingData<Story> = mainViewModel.storyResult.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = InstagramLiteStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualQuote)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Quote Empty Should Return No Data`() = runTest {
        val data: PagingData<Story> = PagingData.from(emptyList())
        val expectedQuote = MutableLiveData<PagingData<Story>>()
        expectedQuote.value = data
        Mockito.`when`(instagramLiteRepository.getStoriesLiveData("Bearer $fakeToken"))
            .thenReturn(expectedQuote)
        val mainViewModel = MainViewModel(instagramLiteRepository)
        mainViewModel.instagramLiteGetStories(fakeToken)
        val actualQuote: PagingData<Story> = mainViewModel.storyResult.getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = InstagramLiteStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualQuote)
        Assert.assertEquals(0, differ.snapshot().size)
    }


    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}