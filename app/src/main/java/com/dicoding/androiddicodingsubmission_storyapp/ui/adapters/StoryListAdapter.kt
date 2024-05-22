package com.dicoding.androiddicodingsubmission_storyapp.ui.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.androiddicodingsubmission_storyapp.data.remote.response.StoryResponse
import com.dicoding.androiddicodingsubmission_storyapp.databinding.CardStoryBinding
import com.dicoding.androiddicodingsubmission_storyapp.utils.relativeDateFormatter

class StoryListAdapter(private val fragment: Fragment) :
    ListAdapter<StoryResponse, StoryListAdapter.StoryViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class StoryViewHolder(
        private val binding: CardStoryBinding,
        private val fragment: Fragment,
        private val onItemClickCallback: OnItemClickCallback
    ) : RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.N)
        fun bind(story: StoryResponse) {
            binding.tvItemName.text = story.name
            binding.tvDate.text = story.createdAt?.relativeDateFormatter()
            Glide.with(fragment).load(story.photoUrl).into(binding.imgDiscordCard)
            binding.root.setOnClickListener { view ->
                onItemClickCallback.onItemClicked(
                    story, view, binding
                )
            }

        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): StoryViewHolder {
        val binding = CardStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding, fragment, onItemClickCallback)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryResponse>() {
            override fun areItemsTheSame(oldItem: StoryResponse, newItem: StoryResponse): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: StoryResponse, newItem: StoryResponse
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: StoryResponse, view: View, binding: CardStoryBinding)
    }
}