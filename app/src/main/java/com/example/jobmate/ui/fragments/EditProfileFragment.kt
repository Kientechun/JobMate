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
import com.example.jobmate.databinding.FragmentEditProfileBinding
import com.example.jobmate.ui.viewmodel.ProfileViewModel
import com.example.jobmate.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel.fetchCurrentUser()
        
        binding.btnSave.setOnClickListener {
            val fullName = binding.etFullName.text.toString()
            val phone = binding.etPhone.text.toString()
            
            if (fullName.isBlank()) {
                binding.tilFullName.error = "Vui lòng nhập họ tên"
                return@setOnClickListener
            }
            
            viewModel.updateProfile(fullName, phone)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.userProfile.collectLatest { resource ->
                        when (resource) {
                            is Resource.Success -> {
                                val user = resource.data
                                binding.etFullName.setText(user?.fullName)
                                binding.etPhone.setText(user?.phoneNumber)
                            }
                            else -> {}
                        }
                    }
                }

                launch {
                    viewModel.updateState.collectLatest { resource ->
                        when (resource) {
                            is Resource.Loading -> {
                                binding.btnSave.isEnabled = false
                                binding.progressBar.visibility = View.VISIBLE
                            }
                            is Resource.Success -> {
                                binding.btnSave.isEnabled = true
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                                findNavController().popBackStack()
                            }
                            is Resource.Error -> {
                                binding.btnSave.isEnabled = true
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                            }
                            else -> {}
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
