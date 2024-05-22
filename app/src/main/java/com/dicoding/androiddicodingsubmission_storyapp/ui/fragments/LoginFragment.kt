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
import androidx.navigation.fragment.findNavController
import com.dicoding.androiddicodingsubmission_storyapp.R
import com.dicoding.androiddicodingsubmission_storyapp.data.Result
import com.dicoding.androiddicodingsubmission_storyapp.databinding.FragmentLoginBinding
import com.dicoding.androiddicodingsubmission_storyapp.ui.LoginViewModel
import com.dicoding.androiddicodingsubmission_storyapp.ui.ViewModelFactory
import com.dicoding.androiddicodingsubmission_storyapp.utils.Event
import com.dicoding.androiddicodingsubmission_storyapp.utils.validateEmail
import com.dicoding.androiddicodingsubmission_storyapp.utils.validatePassword
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        val loginViewModel: LoginViewModel by viewModels { factory }
        loginAnimation()

        loginViewModel.getToken().observe(viewLifecycleOwner) { token: String? ->
            if (!token.isNullOrBlank() && findNavController().currentDestination?.id == R.id.loginFragment) view.findNavController()
                .navigate(LoginFragmentDirections.actionLoginFragmentToStoryFragment(loginViewModel.getUsername().toString()))
        }

        binding.edLoginEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.buttonLogin.isEnabled =
                    !binding.tilEmail.isErrorEnabled && !binding.tilPassword.isErrorEnabled
                showInputError("email", s?.validateEmail())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.edLoginPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.buttonLogin.isEnabled =
                    !binding.tilEmail.isErrorEnabled && !binding.tilPassword.isErrorEnabled
                showInputError("password", s?.validatePassword())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.buttonLogin.setOnClickListener {
            loginViewModel.login(
                binding.edLoginEmail.text.toString(), binding.edLoginPassword.text.toString()
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
                            if (!result.data.loginResult?.token.isNullOrBlank()) {
                                loginViewModel.setToken(result.data.loginResult?.token.toString())
                                loginViewModel.setName(result.data.loginResult?.name.toString())
                            }
                            view.findNavController()
                                .navigate(
                                    LoginFragmentDirections.actionLoginFragmentToStoryFragment(
                                        result.data.loginResult?.name.toString()
                                    )
                                )
                        }
                    }
                }
            }
        }

        binding.txToRegister.setOnClickListener {
            view.findNavController()
                .navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setSnackBar(e: Event<String>) {
        e.getContentIfNotHandled()?.let {
            Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun loginAnimation() {
        val title = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(400)
        val loginEmail = ObjectAnimator.ofFloat(binding.tilEmail, View.ALPHA, 1f).setDuration(400)
        val loginPass = ObjectAnimator.ofFloat(binding.tilPassword, View.ALPHA, 1f).setDuration(400)
        val btn = ObjectAnimator.ofFloat(binding.buttonLogin, View.ALPHA, 1f).setDuration(400)
        val ask = ObjectAnimator.ofFloat(binding.askAccount, View.ALPHA, 1f).setDuration(400)
        AnimatorSet().apply {
            playSequentially(title, loginEmail, loginPass, btn, ask)
            start()
        }
    }
}