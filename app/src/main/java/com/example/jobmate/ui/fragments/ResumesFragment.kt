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

        observeViewModel()
    }

    private fun setupRecyclerView() {
        resumeAdapter = ResumeAdapter(
            onEditClick = { resume ->
                // findNavController().navigate(ResumesFragmentDirections.actionResumesFragmentToEditResumeFragment(resume.id))
            },
            onDeleteClick = { resume ->
                viewModel.deleteResume(resume.id)
            },
            onSetDefaultClick = { resume ->
                viewModel.setDefaultResume(resume.id)
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
