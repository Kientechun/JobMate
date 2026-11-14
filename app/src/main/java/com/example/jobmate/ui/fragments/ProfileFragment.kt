package com.example.jobmate.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.jobmate.R
import com.example.jobmate.data.local.TokenManager
import com.example.jobmate.databinding.FragmentProfileBinding
import com.example.jobmate.ui.viewmodel.ProfileViewModel
import com.example.jobmate.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUserInfo()
        setupMenuActions()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.logoutState.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            // Show loading if needed
                        }
                        is Resource.Success -> {
                            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
                        }
                        is Resource.Error -> {
                            // Dù lỗi server vẫn cho logout ở app
                            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun setupUserInfo() {
        viewLifecycleOwner.lifecycleScope.launch {
            tokenManager.userName.collect { name ->
                binding.tvUserName.text = name ?: "Người dùng"
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            tokenManager.userEmail.collect { email ->
                binding.tvUserEmail.text = email
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            tokenManager.userRole.collect { role ->
                updateUIBasedOnRole(role)
            }
        }
    }

    private fun updateUIBasedOnRole(role: String?) {
        when (role) {
            "Recruiter" -> {
                binding.candidateGroup.visibility = View.GONE
                binding.managementGroup.visibility = View.VISIBLE
                setupRow(binding.itemDashboard, R.drawable.ic_work, R.string.recruiter_dashboard, R.string.recruiter_dashboard_desc) {
                    findNavController().navigate(R.id.action_profileFragment_to_recruiterDashboardFragment)
                }
            }
            "Admin" -> {
                binding.candidateGroup.visibility = View.GONE
                binding.managementGroup.visibility = View.VISIBLE
                setupRow(binding.itemDashboard, R.drawable.ic_work, R.string.admin_dashboard, R.string.admin_dashboard_desc) {
                    findNavController().navigate(R.id.action_profileFragment_to_adminDashboardFragment)
                }
            }
            else -> {
                binding.candidateGroup.visibility = View.VISIBLE
                binding.managementGroup.visibility = View.GONE
            }
        }
    }

    private fun setupMenuActions() {
        // Career Group
        setupRow(binding.itemPersonalInfo, R.drawable.ic_person, R.string.profile_personal_info, R.string.profile_personal_info_desc) {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }
        setupRow(binding.itemMyCv, R.drawable.ic_work, R.string.profile_my_cv, R.string.profile_my_cv_desc) {
            findNavController().navigate(R.id.action_profileFragment_to_resumesFragment)
        }
        setupRow(binding.itemSkills, R.drawable.ic_work, R.string.profile_skills, R.string.profile_skills_desc) {
            // Navigate to skills
        }
        setupRow(binding.itemEducation, R.drawable.ic_work, R.string.profile_education, R.string.profile_education_desc) {
            // Navigate to education
        }
        setupRow(binding.itemExperience, R.drawable.ic_work, R.string.profile_experience, R.string.profile_experience_desc) {
            // Navigate to experience
        }

        // Activity Group
        setupRow(binding.itemFavoriteJobs, R.drawable.ic_work, R.string.profile_favorite_jobs, R.string.profile_favorite_jobs_desc) {
            findNavController().navigate(R.id.action_profileFragment_to_savedJobsFragment)
        }
        setupRow(binding.itemAppliedJobs, R.drawable.ic_recent, R.string.profile_applied_jobs, R.string.profile_applied_jobs_desc) {
            findNavController().navigate(R.id.applicationsFragment)
        }
        setupRow(binding.itemInterviews, R.drawable.ic_recent, R.string.profile_interviews, R.string.profile_interviews_desc) {
            findNavController().navigate(R.id.interviewsFragment)
        }

        // Account Group
        setupRow(binding.itemSettings, R.drawable.ic_work, R.string.profile_settings, R.string.profile_settings_desc) {
            // Navigate to settings
        }

        binding.btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }

        binding.btnNotifications.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_notificationsFragment)
        }

        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }
    }

    private fun setupRow(
        rowBinding: com.example.jobmate.databinding.ItemProfileRowBinding,
        iconRes: Int,
        titleRes: Int,
        descRes: Int,
        onClick: () -> Unit
    ) {
        rowBinding.apply {
            ivIcon.setImageResource(iconRes)
            tvTitle.setText(titleRes)
            tvDescription.setText(descRes)
            root.setOnClickListener { onClick() }
        }
    }

    private fun showLogoutConfirmation() {
        com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.logout_confirm_title)
            .setMessage(R.string.logout_confirm_msg)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.logout_confirm_yes) { _, _ ->
                viewModel.logout()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}