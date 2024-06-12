package com.dicoding.androiddicodingsubmission_storyapp.data.local.db

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dicoding.androiddicodingsubmission_storyapp.data.remote.response.DefaultResponse
import com.dicoding.androiddicodingsubmission_storyapp.data.remote.response.LoginResponse
import com.dicoding.androiddicodingsubmission_storyapp.data.remote.response.StoriesResponse
import com.dicoding.androiddicodingsubmission_storyapp.data.remote.response.StoryResponse
import com.dicoding.androiddicodingsubmission_storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Call

@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class StoryRemoteMediatorTest {

    private var mockApi: ApiService = FakeApiService()
    private var mockDb: StoryDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        StoryDatabase::class.java
    ).allowMainThreadQueries().build()

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runTest {
        val remoteMediator = StoryRemoteMediator(
            mockDb,
            mockApi,
        )
        val pagingState = PagingState<Int, StoryResponse>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @After
    fun tearDown() {
        mockDb.clearAllTables()
    }

}
class FakeApiService : ApiService {

    override fun createUser(name: String, email: String, password: String): Call<DefaultResponse> {
        TODO("Not yet implemented")
    }

    override fun loginUser(email: String, password: String): Call<LoginResponse> {
        TODO("Not yet implemented")
    }

    override fun addNewStory(
        photo: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): Call<DefaultResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllStories(page: Int, size: Int, getWithLoc: Int): StoriesResponse {
        val items: MutableList<StoryResponse> = arrayListOf()
        val error = false
        val message = "Success"

        for (i in 0..100) {
            val quote = StoryResponse(
                id = i.toString(),
                createdAt = "created at $i",
                name = "name $i",
                lat = i.toFloat() + 0.3f,
                lon = i.toFloat() + 0.5f,
                description = "description $i",
                photoUrl = "photo url $i"
            )
            items.add(quote)
        }

        val startIndex = (page - 1) * size
        val endIndex = minOf((page - 1) * size + size, items.size)
        val sublist = if (startIndex < endIndex) items.subList(startIndex, endIndex) else emptyList()

        return StoriesResponse(sublist, error, message)
    }

    override fun getStoriesWithLocation(getWithLoc: Int): Call<StoriesResponse> {
        TODO("Not yet implemented")
    }

    override fun getDetailStory(username: String): Call<StoryResponse> {
        TODO("Not yet implemented")
    }
}