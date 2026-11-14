package com.example.jobmate.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.jobmate.R
import com.example.jobmate.databinding.FragmentApplicationsBinding
import com.example.jobmate.ui.adapters.ApplicationAdapter
import com.example.jobmate.ui.viewmodel.ApplicationViewModel
import com.example.jobmate.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ApplicationsFragment : Fragment() {

    private var _binding: FragmentApplicationsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ApplicationViewModel by viewModels()
    private lateinit var adapter: ApplicationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApplicationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListeners()
        setupObservers()
        setupFilters()
    }

    private fun setupRecyclerView() {
        adapter = ApplicationAdapter { application ->
            val action = ApplicationsFragmentDirections.actionApplicationsFragmentToApplicationDetailFragment(application.id)
            findNavController().navigate(action)
        }
        binding.rvApplications.adapter = adapter
    }

    private fun setupListeners() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchApplications()
        }

        binding.btnExplore.setOnClickListener {
            // Navigate to HomeFragment (Job search tab)
            findNavController().navigate(R.id.homeFragment)
        }

        binding.btnRetry.setOnClickListener {
            viewModel.fetchApplications()
        }
    }

    private fun setupFilters() {
        binding.chipGroupStatus.setOnCheckedChangeListener { group, checkedId ->
            val status = when (checkedId) {
                R.id.chipPending -> "Chờ duyệt"
                R.id.chipApproved -> "Đã duyệt"
                R.id.chipRejected -> "Từ chối"
                R.id.chipUnderReview -> "Đang xem xét"
                else -> "Tất cả"
            }
            viewModel.applyFilter(status)
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.applications.collect { resource ->
                        handleLoadingState(resource is Resource.Loading)
                        
                        when (resource) {
                            is Resource.Success -> {
                                binding.layoutError.isVisible = false
                                if (resource.data.isNullOrEmpty()) {
                                    showEmptyState(true)
                                } else {
                                    binding.layoutEmpty.isVisible = false
                                }
                            }
                            is Resource.Error -> {
                                binding.layoutEmpty.isVisible = false
                                binding.layoutError.isVisible = true
                                binding.tvErrorMessage.text = resource.message
                            }
                            else -> {}
                        }
                    }
                }

                launch {
                    viewModel.filteredApplications.collect { list ->
                        adapter.submitList(list)
                        if (viewModel.applications.value is Resource.Success) {
                            val isSourceEmpty = (viewModel.applications.value as Resource.Success).data.isNullOrEmpty()
                            if (!isSourceEmpty) {
                                showEmptyFilterState(list.isEmpty())
                            }
                        }
                    }
                }
            }
        }
    }

    private fun handleLoadingState(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading && !binding.swipeRefresh.isRefreshing
        if (!isLoading) {
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun showEmptyState(isEmpty: Boolean) {
        binding.layoutEmpty.isVisible = isEmpty
        if (isEmpty) {
            binding.tvEmptyTitle.setText(R.string.empty_applications_title)
            binding.tvEmptyDesc.setText(R.string.empty_applications_desc)
            binding.btnExplore.isVisible = true
        }
    }

    private fun showEmptyFilterState(isEmpty: Boolean) {
        binding.layoutEmpty.isVisible = isEmpty
        if (isEmpty) {
            binding.tvEmptyTitle.setText(R.string.filter_empty_title)
            binding.tvEmptyDesc.setText(R.string.filter_empty_desc)
            binding.btnExplore.isVisible = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
