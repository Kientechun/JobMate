package com.example.jobmate.ui.fragments.admin

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
import com.example.jobmate.databinding.FragmentAdminDashboardBinding
import com.example.jobmate.ui.viewmodel.AdminViewModel
import com.example.jobmate.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AdminDashboardFragment : Fragment() {

    private var _binding: FragmentAdminDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdminDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getDashboard()
        }

        binding.btnManageUsers.setOnClickListener {
            findNavController().navigate(R.id.action_adminDashboardFragment_to_userManagementFragment)
        }

        binding.btnApproveJobs.setOnClickListener {
            findNavController().navigate(R.id.action_adminDashboardFragment_to_jobApprovalFragment)
        }

        binding.btnManageCategories.setOnClickListener {
            findNavController().navigate(R.id.action_adminDashboardFragment_to_categoryManagementFragment)
        }

        viewModel.getDashboard()
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
                                binding.tvTotalUsers.text = data.totalUsers.toString()
                                binding.tvTotalJobs.text = data.totalJobs.toString()
                                binding.tvPendingJobs.text = data.pendingJobs.toString()
                                binding.tvTotalCompanies.text = data.totalCompanies.toString()
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
