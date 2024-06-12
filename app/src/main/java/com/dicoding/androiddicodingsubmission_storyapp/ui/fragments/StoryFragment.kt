package com.dicoding.androiddicodingsubmission_storyapp.ui.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.dicoding.androiddicodingsubmission_storyapp.data.remote.response.StoryResponse
import com.dicoding.androiddicodingsubmission_storyapp.databinding.CardStoryBinding
import com.dicoding.androiddicodingsubmission_storyapp.databinding.FragmentStoryBinding
import com.dicoding.androiddicodingsubmission_storyapp.ui.StoryViewModel
import com.dicoding.androiddicodingsubmission_storyapp.ui.ViewModelFactory
import com.dicoding.androiddicodingsubmission_storyapp.ui.adapters.LoadingStateAdapter
import com.dicoding.androiddicodingsubmission_storyapp.ui.adapters.StoryListAdapter
import com.dicoding.androiddicodingsubmission_storyapp.utils.Event
import com.dicoding.androiddicodingsubmission_storyapp.utils.relativeDateFormatter
import com.google.android.material.snackbar.Snackbar

class StoryFragment : Fragment() {

    private var _binding: FragmentStoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var storyViewModel: StoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setStoryListAdapter()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        storyViewModel = ViewModelProvider(this, factory)[StoryViewModel::class.java]

        val nama = arguments?.getString("namaUser")


        binding.txUsername.text = nama

        storyViewModel.getUsername().observe(viewLifecycleOwner) { username ->
            binding.txUsername.text = username
        }

        binding.actionLogout.setOnClickListener {
            storyViewModel.logoutUser()
            view.findNavController()
                .navigate(StoryFragmentDirections.actionStoryFragmentToLoginFragment())
        }

        binding.fabAddPost.setOnClickListener {
            view.findNavController()
                .navigate(StoryFragmentDirections.actionStoryFragmentToUploadStoryFragment())
        }

        binding.actionToMap.setOnClickListener {
            view.findNavController()
                .navigate(StoryFragmentDirections.actionStoryFragmentToMapsActivity())
        }
    }


    private fun setStoryListAdapter(
    ) {
        val adapter = StoryListAdapter(this)
        binding.rvStoryList.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        storyViewModel.getAllStory.observe(viewLifecycleOwner) { result ->
            adapter.submitData(lifecycle, result)
        }

        adapter.setOnItemClickCallback(object : StoryListAdapter.OnItemClickCallback {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onItemClicked(
                data: StoryResponse, view: View, cardBinding: CardStoryBinding
            ) {
                val extras = FragmentNavigatorExtras(
                    Pair(cardBinding.imgDiscordCard, "image_post"),
                    Pair(cardBinding.tvItemName, "post_user"),
                    Pair(cardBinding.tvDate, "post_date"),
                    Pair(cardBinding.cardPost, "post_card"),
                )
                view.findNavController().navigate(
                    StoryFragmentDirections.actionStoryFragmentToDetailStoryFragment(
                        data.photoUrl,
                        data.name,
                        data.createdAt.relativeDateFormatter(),
                        data.description
                    ),
                    extras,
                )
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setSnackBar(e: Event<String>) {
        e.getContentIfNotHandled()?.let {
            Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show()
        }
    }
}