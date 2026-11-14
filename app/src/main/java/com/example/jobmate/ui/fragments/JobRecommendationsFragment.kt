package com.example.jobmate.ui.fragments

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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobmate.databinding.FragmentJobRecommendationsBinding
import com.example.jobmate.ui.adapters.JobRecommendationAdapter
import com.example.jobmate.ui.viewmodel.AiViewModel
import com.example.jobmate.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class JobRecommendationsFragment : Fragment() {

    private var _binding: FragmentJobRecommendationsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AiViewModel by viewModels()
    private val args: JobRecommendationsFragmentArgs by navArgs()
    private lateinit var recommendationAdapter: JobRecommendationAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentJobRecommendationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        viewModel.fetchRecommendations(if (args.resumeId != -1) args.resumeId else null)
    }

    private fun setupRecyclerView() {
        recommendationAdapter = JobRecommendationAdapter { recommendation ->
            val action = JobRecommendationsFragmentDirections.actionJobRecommendationsFragmentToJobDetailFragment(recommendation.jobId)
            findNavController().navigate(action)
        }
        binding.rvRecommendations.apply {
            adapter = recommendationAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.errorLayout.btnRetry.setOnClickListener {
            viewModel.fetchRecommendations(if (args.resumeId != -1) args.resumeId else null)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.recommendations.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                        is Resource.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.errorLayout.layoutError.visibility = View.GONE
                            recommendationAdapter.submitList(resource.data)
                        }
                        is Resource.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.errorLayout.layoutError.visibility = View.VISIBLE
                            binding.errorLayout.tvErrorMessage.text = resource.message
                        }
                        else -> {
                            binding.errorLayout.layoutError.visibility = View.GONE
                        }
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
