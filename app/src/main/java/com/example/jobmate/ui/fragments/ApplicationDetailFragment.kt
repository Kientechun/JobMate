package com.example.jobmate.ui.fragments

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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobmate.R
import com.example.jobmate.data.local.TokenManager
import com.example.jobmate.databinding.FragmentApplicationDetailBinding
import com.example.jobmate.ui.adapters.ApplicationHistoryAdapter
import com.example.jobmate.ui.viewmodel.ApplicationViewModel
import com.example.jobmate.ui.viewmodel.RecruiterViewModel
import com.example.jobmate.utils.Resource
import com.example.jobmate.data.models.ApplicationDTO
import com.example.jobmate.data.models.ApplicationHistoryDTO
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ApplicationDetailFragment : Fragment() {

    private var _binding: FragmentApplicationDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ApplicationViewModel by viewModels()
    private val recruiterViewModel: RecruiterViewModel by viewModels()
    private val args: ApplicationDetailFragmentArgs by navArgs()
    private lateinit var historyAdapter: ApplicationHistoryAdapter

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentApplicationDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupListeners()
        observeViewModel()
        
        viewModel.getApplicationDetail(args.applicationId)
        viewModel.getApplicationHistory(args.applicationId)
    }

    private fun setupRecyclerView() {
        historyAdapter = ApplicationHistoryAdapter()
        binding.rvHistory.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        
        binding.btnWithdraw.setOnClickListener {
            viewModel.withdrawApplication(args.applicationId)
        }

        binding.btnApprove.setOnClickListener {
            showStatusUpdateDialog(2) // Accepted
        }

        binding.btnReject.setOnClickListener {
            showStatusUpdateDialog(3) // Rejected
        }
    }

    private fun showStatusUpdateDialog(status: Int) {
        val input = EditText(requireContext())
        input.hint = getString(R.string.status_note_hint)
        val title = if (status == 2) "Chấp nhận ứng viên" else "Từ chối ứng viên"
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setView(input)
            .setPositiveButton("Xác nhận") { _, _ ->
                val note = input.text.toString()
                recruiterViewModel.updateApplicationStatus(args.applicationId, status, note)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            val role = tokenManager.userRole.first()
            val isRecruiter = role == "Recruiter"

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.applicationDetail.collectLatest { resource ->
                        binding.progressBar.isVisible = resource is Resource.Loading
                        when (resource) {
                            is Resource.Success<ApplicationDTO> -> {
                                resource.data?.let { app ->
                                    binding.apply {
                                        tvJobTitle.text = app.job?.title ?: "N/A"
                                        tvCompanyName.text = app.job?.companyName ?: "N/A"
                                        tvCoverLetter.text = app.coverLetter ?: "Không có thư giới thiệu"
                                        tvStatus.text = mapStatus(app.status)
                                        ivStatusIcon.setImageResource(mapStatusIcon(app.status))
                                        
                                        // Only allow withdraw if pending and is Candidate
                                        btnWithdraw.isVisible = !isRecruiter && app.status == 0
                                        
                                        // Recruiter actions visibility
                                        recruiterActionGroup.isVisible = isRecruiter && (app.status == 0 || app.status == 1)
                                    }
                                }
                            }
                            is Resource.Error<ApplicationDTO> -> {
                                Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                            }
                            else -> {}
                        }
                    }
                }

                launch {
                    viewModel.applicationHistory.collectLatest { resource ->
                        if (resource is Resource.Success<List<ApplicationHistoryDTO>>) {
                            historyAdapter.submitList(resource.data)
                        }
                    }
                }

                launch {
                    viewModel.withdrawStatus.collectLatest { resource ->
                        when (resource) {
                            is Resource.Loading<Boolean> -> binding.btnWithdraw.isEnabled = false
                            is Resource.Success<Boolean> -> {
                                Toast.makeText(requireContext(), "Rút hồ sơ thành công", Toast.LENGTH_SHORT).show()
                                findNavController().popBackStack()
                            }
                            is Resource.Error<Boolean> -> {
                                binding.btnWithdraw.isEnabled = true
                                Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                            }
                            else -> {}
                        }
                    }
                }

                launch {
                    recruiterViewModel.statusUpdateState.collectLatest { resource ->
                        when (resource) {
                            is Resource.Loading -> {
                                binding.btnApprove.isEnabled = false
                                binding.btnReject.isEnabled = false
                            }
                            is Resource.Success -> {
                                Toast.makeText(requireContext(), "Cập nhật trạng thái thành công", Toast.LENGTH_SHORT).show()
                                recruiterViewModel.resetStatusUpdateState()
                                viewModel.getApplicationDetail(args.applicationId)
                                viewModel.getApplicationHistory(args.applicationId)
                            }
                            is Resource.Error -> {
                                binding.btnApprove.isEnabled = true
                                binding.btnReject.isEnabled = true
                                Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                                recruiterViewModel.resetStatusUpdateState()
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }

    private fun mapStatus(status: Int): String = when (status) {
        0 -> "Đang chờ duyệt"
        1 -> "Đã xem"
        2 -> "Được chấp nhận"
        3 -> "Đã từ chối"
        4 -> "Đã rút hồ sơ"
        else -> "Không xác định"
    }

    private fun mapStatusIcon(status: Int): Int = when (status) {
        0 -> R.drawable.ic_work // Placeholder for ic_pending
        2 -> R.drawable.ic_work // Placeholder for ic_check_circle
        3 -> R.drawable.ic_work // Placeholder for ic_error
        else -> R.drawable.ic_work
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
