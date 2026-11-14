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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobmate.R
import com.example.jobmate.databinding.FragmentRecruiterApplicationsBinding
import com.example.jobmate.ui.adapters.ApplicationAdapter
import com.example.jobmate.ui.viewmodel.RecruiterViewModel
import com.example.jobmate.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecruiterApplicationsFragment : Fragment() {

    private var _binding: FragmentRecruiterApplicationsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RecruiterViewModel by viewModels()
    private val args: RecruiterApplicationsFragmentArgs by navArgs()
    private lateinit var adapter: ApplicationAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRecruiterApplicationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getApplications(if (args.jobId != -1) args.jobId else null)
        }

        viewModel.getApplications(if (args.jobId != -1) args.jobId else null)
    }

    private fun setupRecyclerView() {
        adapter = ApplicationAdapter { app ->
            val bundle = Bundle().apply { putInt("applicationId", app.id) }
            findNavController().navigate(R.id.action_recruiterApplicationsFragment_to_applicationDetailFragment, bundle)
        }
        binding.rvApplications.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@RecruiterApplicationsFragment.adapter
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.applications.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.progressBar.isVisible = true
                            binding.swipeRefresh.isRefreshing = false
                        }
                        is Resource.Success -> {
                            binding.progressBar.isVisible = false
                            binding.swipeRefresh.isRefreshing = false
                            val apps = resource.data?.items ?: emptyList()
                            adapter.submitList(apps)
                            binding.tvEmpty.isVisible = apps.isEmpty()
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
