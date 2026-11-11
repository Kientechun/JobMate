package com.example.jobmate.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.jobmate.R
import com.example.jobmate.databinding.FragmentLoginBinding
import com.example.jobmate.ui.viewmodel.LoginViewModel
import com.example.jobmate.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()
            
            resetErrors()
            
            when {
                email.isEmpty() -> binding.tilEmail.error = getString(R.string.error_empty_fields)
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> binding.tilEmail.error = getString(R.string.error_invalid_email)
                pass.isEmpty() -> binding.tilPassword.error = getString(R.string.error_empty_fields)
                else -> viewModel.login(email, pass)
            }
        }

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        observeState()
    }

    private fun resetErrors() {
        binding.tilEmail.error = null
        binding.tilPassword.error = null
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginState.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.btnLogin.isEnabled = false
                        }
                        is Resource.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.btnLogin.isEnabled = true
                            Toast.makeText(requireContext(), R.string.login_success, Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                        }
                        is Resource.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.btnLogin.isEnabled = true
                            val message = resource.message ?: getString(R.string.error_unknown)
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            binding.progressBar.visibility = View.GONE
                            binding.btnLogin.isEnabled = true
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
