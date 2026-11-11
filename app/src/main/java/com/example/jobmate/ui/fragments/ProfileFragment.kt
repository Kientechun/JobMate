package com.example.jobmate.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.jobmate.R
import com.example.jobmate.data.local.TokenManager
import com.example.jobmate.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

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
    }

    private fun setupUserInfo() {
        viewLifecycleOwner.lifecycleScope.launch {
            tokenManager.userName.collect { name ->
                binding.tvUserName.text = name ?: "Người dùng"
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            tokenManager.userRole.collect { role ->
                // Currently only displaying email/name, but could show role if needed
            }
        }
        // In a real app, email would also come from TokenManager
        // For now, I'll assume we can get it from there if we add it to TokenManager
        // TokenManager currently has userEmail? Let's check.
    }

    private fun setupMenuActions() {
        // Career Group
        setupRow(binding.itemPersonalInfo, R.drawable.ic_person, R.string.profile_personal_info, R.string.profile_personal_info_desc) {
            // Navigate to personal info
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
            // Navigate to favorites
        }
        setupRow(binding.itemAppliedJobs, R.drawable.ic_recent, R.string.profile_applied_jobs, R.string.profile_applied_jobs_desc) {
            findNavController().navigate(R.id.applicationsFragment)
        }

        // Account Group
        setupRow(binding.itemSettings, R.drawable.ic_work, R.string.profile_settings, R.string.profile_settings_desc) {
            // Navigate to settings
        }

        binding.btnLogout.setOnClickListener {
            showLogoutConfirmation()
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
                performLogout()
            }
            .show()
    }

    private fun performLogout() {
        lifecycleScope.launch {
            tokenManager.clear()
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}