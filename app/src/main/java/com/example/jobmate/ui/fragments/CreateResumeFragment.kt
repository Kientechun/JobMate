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
import androidx.navigation.fragment.navArgs
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
    private val args: CreateResumeFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreateResumeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.resumeId != -1) {
            viewModel.fetchResumeDetail(args.resumeId)
        }

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
                languages = emptyList(),
                fileUrl = viewModel.resumeDetail.value?.data?.fileUrl
            )
            
            if (args.resumeId == -1) {
                viewModel.createResume(request)
            } else {
                viewModel.updateResume(args.resumeId, request)
            }
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.createStatus.collectLatest { resource ->
                        when (resource) {
                            is Resource.Loading -> binding.btnSaveResume.isEnabled = false
                            is Resource.Success -> {
                                binding.btnSaveResume.isEnabled = true
                                val msg = if (args.resumeId == -1) "Tạo CV thành công" else "Cập nhật CV thành công"
                                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
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

                launch {
                    viewModel.resumeDetail.collectLatest { resource ->
                        if (resource is Resource.Success) {
                            val resume = resource.data
                            binding.apply {
                                etTitle.setText(resume?.title)
                                etFullName.setText(resume?.fullName)
                                etEmail.setText(resume?.email)
                                etPhone.setText(resume?.phoneNumber)
                                etAddress.setText(resume?.address)
                                etSummary.setText(resume?.summary)
                            }
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
