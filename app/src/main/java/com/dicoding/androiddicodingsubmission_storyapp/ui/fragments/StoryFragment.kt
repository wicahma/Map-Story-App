package com.dicoding.androiddicodingsubmission_storyapp.ui.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.dicoding.androiddicodingsubmission_storyapp.data.Result
import com.dicoding.androiddicodingsubmission_storyapp.data.remote.response.StoryResponse
import com.dicoding.androiddicodingsubmission_storyapp.databinding.CardStoryBinding
import com.dicoding.androiddicodingsubmission_storyapp.databinding.FragmentStoryBinding
import com.dicoding.androiddicodingsubmission_storyapp.ui.StoryViewModel
import com.dicoding.androiddicodingsubmission_storyapp.ui.ViewModelFactory
import com.dicoding.androiddicodingsubmission_storyapp.ui.adapters.StoryListAdapter
import com.dicoding.androiddicodingsubmission_storyapp.utils.Event
import com.dicoding.androiddicodingsubmission_storyapp.utils.relativeDateFormatter
import com.google.android.material.snackbar.Snackbar

class StoryFragment : Fragment() {

    private var _binding: FragmentStoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        val storyViewModel: StoryViewModel by viewModels { factory }

        val nama = arguments?.getString("namaUser")

        binding.txUsername.text = nama

        storyViewModel.getUsername().observe(viewLifecycleOwner) { username ->
            binding.txUsername.text = username
        }

        storyViewModel.getAllStory().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        setSnackBar(Event(result.error))
                    }

                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        setStoryListAdapter(result.data, storyViewModel)
                    }
                }
            }
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
    }


    private fun setStoryListAdapter(
        storyResponse: List<StoryResponse?>, viewModel: StoryViewModel
    ) {
        val adapter = StoryListAdapter(this)
        adapter.submitList(storyResponse)
        binding.rvStoryList.adapter = adapter

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
                        data.photoUrl.toString(),
                        data.name.toString(),
                        data.createdAt?.relativeDateFormatter()!!,
                        data.description.toString()
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