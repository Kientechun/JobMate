package com.example.jobmate.ui.fragments.recruiter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobmate.R
import com.example.jobmate.databinding.FragmentRecruiterJobsBinding
import com.example.jobmate.ui.adapters.RecruiterJobAdapter
import com.example.jobmate.ui.viewmodel.RecruiterViewModel
import com.example.jobmate.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManageJobsFragment : Fragment() {

    private var _binding: FragmentRecruiterJobsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RecruiterViewModel by viewModels()
    private lateinit var adapter: RecruiterJobAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRecruiterJobsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getMyJobs()
        }

        binding.fabAddJob.setOnClickListener {
            findNavController().navigate(R.id.action_manageJobsFragment_to_createJobFragment)
        }

        viewModel.getMyJobs()
    }

    private fun setupRecyclerView() {
        adapter = RecruiterJobAdapter(
            onApplicationsClick = { job ->
                val bundle = Bundle().apply { putInt("jobId", job.id) }
                findNavController().navigate(R.id.action_manageJobsFragment_to_recruiterApplicationsFragment, bundle)
            },
            onEditClick = { job ->
                val bundle = Bundle().apply { putInt("jobId", job.id) }
                findNavController().navigate(R.id.action_manageJobsFragment_to_createJobFragment, bundle)
            },
            onCloseClick = { job ->
                viewModel.closeJob(job.id)
            }
        )
        binding.rvJobs.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ManageJobsFragment.adapter
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.myJobs.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.progressBar.isVisible = true
                            binding.swipeRefresh.isRefreshing = false
                        }
                        is Resource.Success -> {
                            binding.progressBar.isVisible = false
                            binding.swipeRefresh.isRefreshing = false
                            val jobs = resource.data?.items ?: emptyList()
                            adapter.submitList(jobs)
                            binding.tvEmpty.isVisible = jobs.isEmpty()
                        }
                        is Resource.Error -> {
                            binding.progressBar.isVisible = false
                            binding.swipeRefresh.isRefreshing = false
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
