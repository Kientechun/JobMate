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
import com.example.jobmate.databinding.FragmentRegisterBinding
import com.example.jobmate.ui.viewmodel.RegisterViewModel
import com.example.jobmate.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegister.setOnClickListener {
            val name = binding.etFullName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()
            val confirmPass = binding.etConfirmPassword.text.toString().trim()
            val role = "Candidate" // Android app is only for candidates
            
            if (validateInput(name, email, pass, confirmPass)) {
                viewModel.register(email, pass, name, role)
            }
        }

        binding.tvLogin.setOnClickListener {
            findNavController().navigateUp()
        }

        observeState()
    }

    private fun validateInput(name: String, email: String, pass: String, confirmPass: String): Boolean {
        var isValid = true
        resetErrors()

        if (name.isEmpty()) {
            binding.tilFullName.error = getString(R.string.error_empty_fields)
            isValid = false
        }

        if (email.isEmpty()) {
            binding.tilEmail.error = getString(R.string.error_empty_fields)
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = getString(R.string.error_invalid_email)
            isValid = false
        }

        if (pass.isEmpty()) {
            binding.tilPassword.error = getString(R.string.error_empty_fields)
            isValid = false
        } else if (pass.length < 6) {
            binding.tilPassword.error = getString(R.string.error_short_password)
            isValid = false
        }

        if (confirmPass != pass) {
            binding.tilConfirmPassword.error = "Mật khẩu xác nhận không khớp"
            isValid = false
        }

        return isValid
    }

    private fun resetErrors() {
        binding.tilFullName.error = null
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        binding.tilConfirmPassword.error = null
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.registerState.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.btnRegister.isEnabled = false
                        }
                        is Resource.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.btnRegister.isEnabled = true
                            Toast.makeText(requireContext(), R.string.register_success, Toast.LENGTH_LONG).show()
                            findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
                        }
                        is Resource.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.btnRegister.isEnabled = true
                            val message = resource.message ?: getString(R.string.error_unknown)
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            binding.progressBar.visibility = View.GONE
                            binding.btnRegister.isEnabled = true
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
