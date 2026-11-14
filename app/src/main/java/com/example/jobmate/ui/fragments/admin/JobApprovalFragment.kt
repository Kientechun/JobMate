package com.example.jobmate.ui.fragments.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobmate.R
import com.example.jobmate.databinding.FragmentRecruiterJobsBinding
import com.example.jobmate.ui.adapters.JobApprovalAdapter
import com.example.jobmate.ui.viewmodel.AdminViewModel
import com.example.jobmate.utils.Resource
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class JobApprovalFragment : Fragment() {

    private var _binding: FragmentRecruiterJobsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminViewModel by viewModels()
    private lateinit var adapter: JobApprovalAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRecruiterJobsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fabAddJob.isVisible = false
        setupRecyclerView()
        observeViewModel()

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getPendingJobs()
        }

        viewModel.getPendingJobs()
    }

    private fun setupRecyclerView() {
        adapter = JobApprovalAdapter(
            onApproveClick = { job ->
                viewModel.approveJob(job.id)
            },
            onRejectClick = { job ->
                showRejectDialog(job.id)
            }
        )
        binding.rvJobs.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@JobApprovalFragment.adapter
        }
    }

    private fun showRejectDialog(jobId: Int) {
        val input = EditText(requireContext())
        input.hint = getString(R.string.reject_reason_hint)
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.reject_reason_title)
            .setView(input)
            .setPositiveButton(R.string.reject) { _, _ ->
                val reason = input.text.toString()
                if (reason.isNotBlank()) {
                    viewModel.rejectJob(jobId, reason)
                } else {
                    Toast.makeText(requireContext(), "Vui lòng nhập lý do", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.pendingJobs.collectLatest { resource ->
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
                            binding.tvEmpty.text = "Không có tin nào đang chờ duyệt"
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
