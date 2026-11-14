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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobmate.R
import com.example.jobmate.databinding.FragmentResumesBinding
import com.example.jobmate.ui.adapters.ResumeAdapter
import com.example.jobmate.ui.viewmodel.ResumeViewModel
import com.example.jobmate.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

@AndroidEntryPoint
class ResumesFragment : Fragment() {

    private var _binding: FragmentResumesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ResumeViewModel by viewModels()
    private lateinit var resumeAdapter: ResumeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentResumesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        
        binding.fabAddResume.setOnClickListener {
            findNavController().navigate(R.id.action_resumesFragment_to_createResumeFragment)
        }

        binding.fabUploadResume.setOnClickListener {
            pickPdf()
        }

        observeViewModel()
    }

    private val selectPdfLauncher = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    private val pickPdfLauncher = registerForActivityResult(selectPdfLauncher) { uri ->
        uri?.let { uploadPdf(it) }
    }

    private fun pickPdf() {
        pickPdfLauncher.launch("application/pdf")
    }

    private fun uploadPdf(uri: android.net.Uri) {
        val contentResolver = requireContext().contentResolver
        val inputStream = contentResolver.openInputStream(uri) ?: return
        val fileName = getFileName(uri) ?: "resume.pdf"
        
        // Check file size (5MB limit)
        val fileSize = getFileSize(uri)
        if (fileSize > 5 * 1024 * 1024) {
            Toast.makeText(requireContext(), "File quá lớn. Tối đa 5MB", Toast.LENGTH_SHORT).show()
            return
        }

        val mediaType = "application/pdf".toMediaTypeOrNull()
        val requestFile = inputStream.readBytes().toRequestBody(mediaType)
        val body = okhttp3.MultipartBody.Part.createFormData("file", fileName, requestFile)
        viewModel.uploadResume(body)
    }

    private fun getFileName(uri: android.net.Uri): String? {
        var name: String? = null
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) name = it.getString(nameIndex)
            }
        }
        return name
    }

    private fun getFileSize(uri: android.net.Uri): Long {
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        var size: Long = 0
        cursor?.use {
            if (it.moveToFirst()) {
                val sizeIndex = it.getColumnIndex(android.provider.OpenableColumns.SIZE)
                if (sizeIndex != -1) size = it.getLong(sizeIndex)
            }
        }
        return size
    }

    private fun setupRecyclerView() {
        resumeAdapter = ResumeAdapter(
            onEditClick = { resume ->
                val action = ResumesFragmentDirections.actionResumesFragmentToEditResumeFragment(resume.id)
                findNavController().navigate(action)
            },
            onDeleteClick = { resume ->
                viewModel.deleteResume(resume.id)
            },
            onSetDefaultClick = { resume ->
                viewModel.setDefaultResume(resume.id)
            },
            onAnalyzeClick = { resume ->
                val action = ResumesFragmentDirections.actionResumesFragmentToResumeAnalysisFragment(resume.id)
                findNavController().navigate(action)
            }
        )
        binding.rvResumes.apply {
            adapter = resumeAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.resumes.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                        is Resource.Success -> {
                            binding.progressBar.visibility = View.GONE
                            resumeAdapter.submitList(resource.data)
                        }
                        is Resource.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> {}
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uploadStatus.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                        is Resource.Success -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(requireContext(), "Tải lên CV thành công", Toast.LENGTH_SHORT).show()
                            viewModel.fetchResumes()
                            viewModel.resetUploadStatus()
                        }
                        is Resource.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                            viewModel.resetUploadStatus()
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
