package com.dicoding.androiddicodingsubmission_storyapp.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.androiddicodingsubmission_storyapp.DataDummy
import com.dicoding.androiddicodingsubmission_storyapp.MainDispatcherRule
import com.dicoding.androiddicodingsubmission_storyapp.data.StoryRepository
import com.dicoding.androiddicodingsubmission_storyapp.data.remote.response.StoryResponse
import com.dicoding.androiddicodingsubmission_storyapp.getOrAwaitValue
import com.dicoding.androiddicodingsubmission_storyapp.ui.StoryViewModel
import com.dicoding.androiddicodingsubmission_storyapp.ui.adapters.StoryListAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Test
    fun `when Get Quote Should Not Null`() = runTest {
        val dummyStory = DataDummy.generateDummyQuoteResponse()
        val data: PagingData<StoryResponse> = QuotePagingSource.snapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<StoryResponse>>()
        expectedStory.value = data

        Mockito.`when`(storyRepository.getAllStory()).thenReturn(expectedStory)
        val storyViewModel = StoryViewModel(storyRepository)
        val actualStory: PagingData<StoryResponse> = storyViewModel.getAllStory.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)
    }

    @Test
    fun `when Get Quote Should Not Null and Return Data`() = runTest {
        val dummyStory = DataDummy.generateDummyQuoteResponse()
        val data: PagingData<StoryResponse> = QuotePagingSource.snapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<StoryResponse>>()
        expectedStory.value = data
        Mockito.`when`(storyRepository.getAllStory()).thenReturn(expectedStory)

        val storyViewModel = StoryViewModel(storyRepository)
        val actualQuote: PagingData<StoryResponse> = storyViewModel.getAllStory.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualQuote)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Quote Empty Should Return No Data`() = runTest {
        val data: PagingData<StoryResponse> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<StoryResponse>>()
        expectedStory.value = data
        Mockito.`when`(storyRepository.getAllStory()).thenReturn(expectedStory)
        val storyViewModel = StoryViewModel(storyRepository)
        val actualQuote: PagingData<StoryResponse> = storyViewModel.getAllStory.getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualQuote)
        assertEquals(0, differ.snapshot().size)
    }
}

class QuotePagingSource : PagingSource<Int, LiveData<List<StoryResponse>>>() {
    companion object {
        fun snapshot(items: List<StoryResponse>): PagingData<StoryResponse> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoryResponse>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoryResponse>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }

}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}