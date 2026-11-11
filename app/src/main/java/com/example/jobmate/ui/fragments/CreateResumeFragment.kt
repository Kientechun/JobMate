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
import com.example.jobmate.data.models.CreateResumeRequestDTO
import com.example.jobmate.databinding.FragmentCreateResumeBinding
import com.example.jobmate.ui.viewmodel.ResumeViewModel
import com.example.jobmate.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateResumeFragment : Fragment() {

    private var _binding: FragmentCreateResumeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ResumeViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreateResumeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSaveResume.setOnClickListener {
            val request = CreateResumeRequestDTO(
                title = binding.etTitle.text.toString(),
                fullName = binding.etFullName.text.toString(),
                email = binding.etEmail.text.toString(),
                phoneNumber = binding.etPhone.text.toString(),
                address = binding.etAddress.text.toString(),
                summary = binding.etSummary.text.toString(),
                experiences = emptyList(),
                educations = emptyList(),
                skills = emptyList(),
                projects = emptyList(),
                certificates = emptyList(),
                languages = emptyList()
            )
            viewModel.createResume(request)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.createStatus.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> binding.btnSaveResume.isEnabled = false
                        is Resource.Success -> {
                            binding.btnSaveResume.isEnabled = true
                            Toast.makeText(requireContext(), "Tạo CV thành công", Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }
                        is Resource.Error -> {
                            binding.btnSaveResume.isEnabled = true
                            Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> {}
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
