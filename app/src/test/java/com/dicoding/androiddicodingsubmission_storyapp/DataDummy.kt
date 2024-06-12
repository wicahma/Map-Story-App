package com.dicoding.androiddicodingsubmission_storyapp

import com.dicoding.androiddicodingsubmission_storyapp.data.remote.response.StoryResponse

object DataDummy {
    fun generateDummyQuoteResponse(): List<StoryResponse> {
        val items: MutableList<StoryResponse> = arrayListOf()
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
        return items
    }
}