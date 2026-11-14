package com.example.jobmate.ui.fragments.recruiter

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
import com.example.jobmate.databinding.FragmentRecruiterDashboardBinding
import com.example.jobmate.ui.adapters.ApplicationAdapter
import com.example.jobmate.ui.viewmodel.RecruiterViewModel
import com.example.jobmate.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecruiterDashboardFragment : Fragment() {

    private var _binding: FragmentRecruiterDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RecruiterViewModel by viewModels()
    private lateinit var adapter: ApplicationAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRecruiterDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getDashboard()
        }

        binding.btnViewAllJobs.setOnClickListener {
            findNavController().navigate(R.id.action_recruiterDashboardFragment_to_manageJobsFragment)
        }

        binding.btnCreateJob.setOnClickListener {
            findNavController().navigate(R.id.action_recruiterDashboardFragment_to_createJobFragment)
        }

        viewModel.getDashboard()
    }

    private fun setupRecyclerView() {
        adapter = ApplicationAdapter { app ->
             val bundle = Bundle().apply { putInt("applicationId", app.id) }
             findNavController().navigate(R.id.action_recruiterDashboardFragment_to_applicationDetailFragment, bundle)
        }
        binding.rvRecentApps.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@RecruiterDashboardFragment.adapter
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dashboard.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> binding.swipeRefresh.isRefreshing = true
                        is Resource.Success -> {
                            binding.swipeRefresh.isRefreshing = false
                            resource.data?.let { data ->
                                binding.tvActiveJobsCount.text = data.activeJobs.toString()
                                binding.tvTotalAppsCount.text = data.totalApplications.toString()
                                binding.tvPendingAppsCount.text = data.pendingApplications.toString()
                                adapter.submitList(data.recentApplications)
                            }
                        }
                        is Resource.Error -> {
                            binding.swipeRefresh.isRefreshing = false
                            Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> binding.swipeRefresh.isRefreshing = false
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
