package com.dicoding.androiddicodingsubmission_storyapp.ui.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.dicoding.androiddicodingsubmission_storyapp.R
import com.dicoding.androiddicodingsubmission_storyapp.data.Result
import com.dicoding.androiddicodingsubmission_storyapp.databinding.FragmentUploadStoryBinding
import com.dicoding.androiddicodingsubmission_storyapp.ui.UploadStoryViewModel
import com.dicoding.androiddicodingsubmission_storyapp.ui.ViewModelFactory
import com.dicoding.androiddicodingsubmission_storyapp.ui.activity.CameraActivity
import com.dicoding.androiddicodingsubmission_storyapp.utils.Event
import com.dicoding.androiddicodingsubmission_storyapp.utils.reduceFileImage
import com.dicoding.androiddicodingsubmission_storyapp.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class UploadStoryFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentUploadStoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var uploadStoryViewModel: UploadStoryViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private var lat: Float? = null
    private var lng: Float? = null

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
        mapFragment = childFragmentManager.findFragmentById(R.id.map_upload) as SupportMapFragment
        mapFragment.getMapAsync(this@UploadStoryFragment)
        mapFragment.view?.visibility = View.GONE
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())


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
            if (binding.edAddDescription.text.isEmpty()) setSnackBar(Event("Tambahkan deskripsi terlebih dahulu"))
            else uploadStory(
                binding.edAddDescription.text.toString(), uploadStoryViewModel.imageUri.value
            )
        }

        binding.checkBoxAddLocation.setOnClickListener {
            if (binding.checkBoxAddLocation.isChecked) {
                getMyLocation()
            } else {
                mapFragment.view?.visibility = View.GONE
            }
        }
    }

    private fun uploadStory(desc: String, imageUri: Uri?) {
        imageUri?.let {
            val imageFile = uriToFile(it, requireContext()).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val requestBodyDescription = desc.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val requestBodyLat = lat?.toString()?.toRequestBody("text/plain".toMediaType())
            val requestBodyLng = lng?.toString()?.toRequestBody("text/plain".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo", imageFile.name, requestImageFile
            )

            uploadStoryViewModel.uploadStory(
                requestBodyDescription,
                multipartBody,
                requestBodyLat,
                requestBodyLng
            )
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
                                view?.findNavController()?.navigate(R.id.action_uploadStoryFragment_to_storyFragment)
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

    private val requestPermissionLauncherLocation =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            mapFragment.view?.visibility = View.VISIBLE
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lat = location.latitude.toFloat()
                    lng = location.longitude.toFloat()
                } else {
                    Toast.makeText(
                        context,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncherLocation.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onMapReady(gMap: GoogleMap) {
        mMap = gMap
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}
