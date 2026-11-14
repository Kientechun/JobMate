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
import androidx.navigation.fragment.navArgs
import com.example.jobmate.databinding.FragmentResumeAnalysisBinding
import com.example.jobmate.ui.viewmodel.AiViewModel
import com.example.jobmate.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResumeAnalysisFragment : Fragment() {

    private var _binding: FragmentResumeAnalysisBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AiViewModel by viewModels()
    private val args: ResumeAnalysisFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentResumeAnalysisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeViewModel()
        viewModel.fetchAnalysis(args.resumeId)
    }

    private fun setupUI() {
        binding.btnViewRecommendations.setOnClickListener {
            val action = ResumeAnalysisFragmentDirections.actionResumeAnalysisFragmentToJobRecommendationsFragment(args.resumeId)
            findNavController().navigate(action)
        }
        
        binding.errorLayout.btnRetry.setOnClickListener {
            viewModel.fetchAnalysis(args.resumeId)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.analysis.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.errorLayout.layoutError.visibility = View.GONE
                        }
                        is Resource.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.errorLayout.layoutError.visibility = View.GONE
                            val analysis = resource.data
                            binding.apply {
                                tvOverview.text = analysis?.overview
                                tvStrengths.text = analysis?.strengths?.joinToString("\n• ", prefix = "• ")
                                tvImprovements.text = analysis?.improvements?.joinToString("\n• ", prefix = "• ")
                                tvSuggestions.text = analysis?.suggestions?.joinToString("\n• ", prefix = "• ")
                            }
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
