package com.dicoding.androiddicodingsubmission_storyapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.dicoding.androiddicodingsubmission_storyapp.databinding.FragmentDetailStoryBinding
import com.dicoding.androiddicodingsubmission_storyapp.ui.DetailStoryViewModel
import com.dicoding.androiddicodingsubmission_storyapp.ui.ViewModelFactory

class DetailStoryFragment : Fragment() {

    private var _binding: FragmentDetailStoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var detailStoryViewModel: DetailStoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailStoryBinding.inflate(inflater, container, false)
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        sharedElementReturnTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        val name = arguments?.getString("userName")
        val image = arguments?.getString("imageUrl")
        val date = arguments?.getString("datePost")
        val desc = arguments?.getString("desc")

        binding.imgDiscordCard.transitionName

        Glide.with(view).load(image).into(binding.imgDiscordCard)
        binding.tvDesc.text = desc
        binding.tvDatepost.text = date
        binding.tvUsername.text = name
        detailStoryViewModel = ViewModelProvider(this, factory)[DetailStoryViewModel::class.java]
    }

}