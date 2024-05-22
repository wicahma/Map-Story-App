package com.dicoding.androiddicodingsubmission_storyapp.ui.fragments

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.dicoding.androiddicodingsubmission_storyapp.data.Result
import com.dicoding.androiddicodingsubmission_storyapp.databinding.FragmentRegisterBinding
import com.dicoding.androiddicodingsubmission_storyapp.ui.RegisterViewModel
import com.dicoding.androiddicodingsubmission_storyapp.ui.ViewModelFactory
import com.dicoding.androiddicodingsubmission_storyapp.utils.Event
import com.dicoding.androiddicodingsubmission_storyapp.utils.validateEmail
import com.dicoding.androiddicodingsubmission_storyapp.utils.validatePassword
import com.google.android.material.snackbar.Snackbar

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        val registerStoryViewModel: RegisterViewModel by viewModels { factory }
        loginAnimation()

        binding.edRegisterEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.buttonRegister.isEnabled =
                    !binding.tilEmail.isErrorEnabled && !binding.tilPassword.isErrorEnabled
                showInputError("email", s?.validateEmail())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.edRegisterPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.buttonRegister.isEnabled =
                    !binding.tilEmail.isErrorEnabled && !binding.tilPassword.isErrorEnabled
                showInputError("password", s?.validatePassword())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.buttonRegister.setOnClickListener {
            registerStoryViewModel.register(
                binding.edRegisterName.text.toString(),
                binding.edRegisterEmail.text.toString(),
                binding.edRegisterPassword.text.toString()
            ).observe(viewLifecycleOwner) { result ->
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
                            setSnackBar(Event("Akun berhasil dibuat, silahkan login"))
                            view.findNavController()
                                .navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
                        }
                    }
                }
            }
        }

        binding.txToLogin.setOnClickListener {
            view.findNavController()
                .navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
        }
    }


    fun showInputError(inputType: String, isError: Boolean?) {
        when (inputType) {
            "email" -> {
                if (isError!!) {
                    binding.tilEmail.isErrorEnabled = true
                    binding.tilEmail.error = "Masukkan email dengan format yang benar"
                } else {
                    binding.tilEmail.isErrorEnabled = false
                    binding.tilEmail.error = null
                }
            }

            "password" -> {
                if (isError!!) {
                    binding.tilPassword.isErrorEnabled = true
                    binding.tilPassword.error = "Password minimal 8 karakter"
                } else {
                    binding.tilPassword.isErrorEnabled = false
                    binding.tilPassword.error = null
                }
            }
        }
    }

    private fun loginAnimation() {
        val title = ObjectAnimator.ofFloat(binding.tvTitleRegister, View.ALPHA, 1f).setDuration(400)
        val loginEmail = ObjectAnimator.ofFloat(binding.tilEmail, View.ALPHA, 1f).setDuration(400)
        val loginPass = ObjectAnimator.ofFloat(binding.tilPassword, View.ALPHA, 1f).setDuration(400)
        val loginUname = ObjectAnimator.ofFloat(binding.tilNama, View.ALPHA, 1f).setDuration(400)
        val btn = ObjectAnimator.ofFloat(binding.buttonRegister, View.ALPHA, 1f).setDuration(400)
        val ask = ObjectAnimator.ofFloat(binding.askLogin, View.ALPHA, 1f).setDuration(400)
        AnimatorSet().apply {
            playSequentially(title, loginUname, loginEmail, loginPass, btn, ask)
            start()
        }
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