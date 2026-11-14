package com.example.jobmate.ui.fragments.recruiter

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.jobmate.R
import com.example.jobmate.data.models.CategoryDTO
import com.example.jobmate.data.models.CreateJobRequestDTO
import com.example.jobmate.databinding.FragmentCreateJobBinding
import com.example.jobmate.ui.viewmodel.RecruiterViewModel
import com.example.jobmate.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CreateEditJobFragment : Fragment() {

    private var _binding: FragmentCreateJobBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RecruiterViewModel by viewModels()
    private val args: CreateEditJobFragmentArgs by navArgs()

    private var selectedDate = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private var categories = listOf<CategoryDTO>()
    private var selectedCategoryId: Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreateJobBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeViewModel()
        
        if (args.jobId != -1) {
            // In a real app, we'd fetch job detail here to pre-fill
            // For now, assume it's create mode if not pre-filled by other means
        }
    }

    private fun setupUI() {
        val jobTypes = arrayOf("Full-time", "Part-time", "Contract", "Freelance", "Internship")
        val typeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, jobTypes)
        binding.spinnerJobType.setAdapter(typeAdapter)

        binding.etExpiryDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnSave.setOnClickListener {
            validateAndSave()
        }
        
        // Mocking categories for now, usually fetched from API
        categories = listOf(
            CategoryDTO(1, "IT / Phần mềm"),
            CategoryDTO(2, "Marketing"),
            CategoryDTO(3, "Kế toán"),
            CategoryDTO(4, "Thiết kế")
        )
        val catAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories.map { it.name })
        binding.spinnerCategory.setAdapter(catAdapter)
        binding.spinnerCategory.setOnItemClickListener { _, _, position, _ ->
            selectedCategoryId = categories[position].id
        }
    }

    private fun showDatePicker() {
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                selectedDate.set(year, month, dayOfMonth)
                binding.etExpiryDate.setText(dateFormatter.format(selectedDate.time))
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun validateAndSave() {
        val title = binding.etTitle.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        val salary = binding.etSalary.text.toString().toDoubleOrNull() ?: 0.0
        val type = binding.spinnerJobType.text.toString()
        val experience = binding.etExperience.text.toString().trim()
        val expiryDate = binding.etExpiryDate.text.toString()
        val description = binding.etDescription.text.toString().trim()
        val requirements = binding.etRequirements.text.toString().trim()
        val benefits = binding.etBenefits.text.toString().trim()

        if (title.isEmpty() || location.isEmpty() || salary <= 0 || type.isEmpty() || 
            experience.isEmpty() || expiryDate.isEmpty() || selectedCategoryId == -1 || description.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        val request = CreateJobRequestDTO(
            title = title,
            description = description,
            requirements = requirements,
            benefits = benefits,
            location = location,
            salaryRange = salary,
            jobType = type,
            experience = experience,
            expiryDate = expiryDate,
            categoryId = selectedCategoryId
        )

        if (args.jobId == -1) {
            viewModel.createJob(request)
        } else {
            viewModel.updateJob(args.jobId, request)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.jobActionState.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> binding.btnSave.isEnabled = false
                        is Resource.Success -> {
                            Toast.makeText(requireContext(), "Đã lưu tin tuyển dụng thành công. Đang chờ Admin duyệt.", Toast.LENGTH_LONG).show()
                            findNavController().popBackStack()
                        }
                        is Resource.Error -> {
                            binding.btnSave.isEnabled = true
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
        viewModel.resetJobActionState()
        _binding = null
    }
}
