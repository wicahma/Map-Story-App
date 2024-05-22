package com.dicoding.androiddicodingsubmission_storyapp.ui.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.dicoding.androiddicodingsubmission_storyapp.data.Result
import com.dicoding.androiddicodingsubmission_storyapp.databinding.FragmentUploadStoryBinding
import com.dicoding.androiddicodingsubmission_storyapp.ui.CameraActivity
import com.dicoding.androiddicodingsubmission_storyapp.ui.UploadStoryViewModel
import com.dicoding.androiddicodingsubmission_storyapp.ui.ViewModelFactory
import com.dicoding.androiddicodingsubmission_storyapp.utils.Event
import com.dicoding.androiddicodingsubmission_storyapp.utils.reduceFileImage
import com.dicoding.androiddicodingsubmission_storyapp.utils.uriToFile
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class UploadStoryFragment : Fragment() {

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }

    private var _binding: FragmentUploadStoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var uploadStoryViewModel: UploadStoryViewModel

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            uploadStoryViewModel.setImageUri(uri)
        } else {
            setSnackBar(Event("No media selected"))
        }
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CameraActivity.CAMERAX_RESULT) {
            it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
                ?.let { uri -> uploadStoryViewModel.setImageUri(uri) }
        }
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        requireContext(), REQUIRED_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            setSnackBar(Event("Permission request granted"))
        } else {
            setSnackBar(Event("Permission request denied"))
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUploadStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        uploadStoryViewModel = ViewModelProvider(this, factory)[UploadStoryViewModel::class.java]

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.buttonSelectImage.setOnClickListener {
            startGallery()
        }

        binding.buttonCaptureImage.setOnClickListener {
            startCameraX()
        }

        uploadStoryViewModel.imageUri.observe(viewLifecycleOwner) {
            if (!it.path.isNullOrBlank()) {
                binding.imagePreview.setImageURI(it)
            }
        }

        binding.buttonAdd.setOnClickListener {
            if (binding.edAddDescription.text.length == 0) setSnackBar(Event("Tambahkan deskripsi terlebih dahulu"))
            else uploadStory(
                binding.edAddDescription.text.toString(), uploadStoryViewModel.imageUri.value
            )
        }
    }

    private fun uploadStory(desc: String, imageUri: Uri?) {
        imageUri?.let {
            val imageFile = uriToFile(it, requireContext()).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val requestBodyDescription = desc.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo", imageFile.name, requestImageFile
            )
            uploadStoryViewModel.uploadStory(requestBodyDescription, multipartBody)
                .observe(viewLifecycleOwner) { result ->
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
                                view?.findNavController()?.navigateUp()
                            }
                        }
                    }
                }
        } ?: setSnackBar(Event("Masukkan gambar terlebiih dahulu"))
    }

    private fun startGallery() {
        launcherIntentGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun startCameraX() {
        val intent = Intent(requireContext(), CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun setSnackBar(e: Event<String>) {
        e.getContentIfNotHandled()?.let {
            Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show()
        }
    }
}