package com.example.jobmate.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.jobmate.R
import com.example.jobmate.data.models.CreateApplicationRequestDTO
import com.example.jobmate.data.models.Job
import com.example.jobmate.data.models.ResumeDTO
import com.example.jobmate.databinding.DialogApplyJobBinding
import com.example.jobmate.databinding.FragmentJobDetailBinding
import com.example.jobmate.ui.viewmodel.JobDetailViewModel
import com.example.jobmate.ui.viewmodel.ResumeViewModel
import com.example.jobmate.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class JobDetailFragment : Fragment() {

    private var _binding: FragmentJobDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: JobDetailViewModel by viewModels()
    private val resumeViewModel: ResumeViewModel by viewModels()
    private val args: JobDetailFragmentArgs by navArgs()

    private var currentJob: Job? = null
    private var myResumes: List<ResumeDTO> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentJobDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getJobDetail(args.jobId)
        resumeViewModel.fetchResumes()

        binding.btnApply.setOnClickListener {
            showApplyDialog()
        }

        binding.btnFavorite.setOnClickListener {
            currentJob?.let { job ->
                if (job.isSaved) {
                    viewModel.unsaveJob(job.id)
                } else {
                    viewModel.saveJob(job.id)
                }
            }
        }

        observeViewModel()
    }

    private fun showApplyDialog() {
        if (myResumes.isEmpty()) {
            Toast.makeText(requireContext(), "Bạn cần tạo CV trước khi ứng tuyển", Toast.LENGTH_SHORT).show()
            return
        }

        val dialogBinding = DialogApplyJobBinding.inflate(layoutInflater)
        dialogBinding.tvJobTitle.text = currentJob?.title

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            myResumes.map { it.title }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogBinding.spinnerResumes.adapter = adapter

        // Select default resume if exists
        val defaultIndex = myResumes.indexOfFirst { it.isDefault }
        if (defaultIndex != -1) {
            dialogBinding.spinnerResumes.setSelection(defaultIndex)
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnSubmitApply.setOnClickListener {
            val selectedResume = myResumes[dialogBinding.spinnerResumes.selectedItemPosition]
            val coverLetter = dialogBinding.etCoverLetter.text.toString()
            
            viewModel.applyJob(CreateApplicationRequestDTO(
                jobId = args.jobId,
                resumeId = selectedResume.id,
                coverLetter = coverLetter
            ))
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.jobDetail.collectLatest { resource ->
                        when (resource) {
                            is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                            is Resource.Success -> {
                                binding.progressBar.visibility = View.GONE
                                val job = resource.data
                                currentJob = job?.copy(isSaved = job.isSaved) // Ensure we have the latest state
                                updateFavoriteIcon(job?.isSaved ?: false)
                                binding.apply {
                                    tvTitle.text = job?.title
                                    tvCompany.text = job?.companyName
                                    tvLocation.text = job?.location
                                    tvSalary.text = job?.let { getString(R.string.salary_format, it.salaryRange) }
                                    tvDescription.text = job?.description
                                    tvRequirements.text = job?.requirements ?: "Không có yêu cầu cụ thể"
                                    tvBenefits.text = job?.benefits ?: "Không có thông tin quyền lợi"
                                }
                            }
                            is Resource.Error -> {
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                            }
                            else -> {}
                        }
                    }
                }

                launch {
                    resumeViewModel.resumes.collectLatest { resource ->
                        if (resource is Resource.Success) {
                            myResumes = resource.data ?: emptyList()
                        }
                    }
                }

                launch {
                    viewModel.applyState.collectLatest { resource ->
                        when (resource) {
                            is Resource.Loading -> {
                                binding.btnApply.isEnabled = false
                                binding.progressBar.visibility = View.VISIBLE
                            }
                            is Resource.Success -> {
                                binding.btnApply.isEnabled = true
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(requireContext(), R.string.apply_success, Toast.LENGTH_SHORT).show()
                                viewModel.resetApplyState()
                                findNavController().navigateUp()
                            }
                            is Resource.Error -> {
                                binding.btnApply.isEnabled = true
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                                viewModel.resetApplyState()
                            }
                            else -> {
                                binding.progressBar.visibility = View.GONE
                            }
                        }
                    }
                }

                launch {
                    viewModel.saveJobStatus.collectLatest { resource ->
                        when (resource) {
                            is Resource.Success -> {
                                val isSaved = resource.data ?: false
                                currentJob = currentJob?.copy(isSaved = isSaved)
                                updateFavoriteIcon(isSaved)
                                val message = if (isSaved) "Đã lưu công việc" else "Đã bỏ lưu công việc"
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                                viewModel.resetSaveStatus()
                            }
                            is Resource.Error -> {
                                Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                                viewModel.resetSaveStatus()
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }

    private fun updateFavoriteIcon(isSaved: Boolean) {
        if (isSaved) {
            binding.btnFavorite.setImageResource(android.R.drawable.btn_star_big_on)
            binding.btnFavorite.setColorFilter(requireContext().getColor(R.color.primary))
        } else {
            binding.btnFavorite.setImageResource(android.R.drawable.btn_star_big_off)
            binding.btnFavorite.setColorFilter(requireContext().getColor(R.color.text_secondary))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
