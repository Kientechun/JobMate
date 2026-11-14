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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobmate.databinding.FragmentInterviewsBinding
import com.example.jobmate.ui.adapters.InterviewAdapter
import com.example.jobmate.ui.viewmodel.InterviewViewModel
import com.example.jobmate.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InterviewsFragment : Fragment() {

    private var _binding: FragmentInterviewsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InterviewViewModel by viewModels()
    private lateinit var adapter: InterviewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInterviewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSwipeRefresh()
        observeViewModel()
        
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.fetchInterviews()
    }

    private fun setupRecyclerView() {
        adapter = InterviewAdapter { interview ->
            // Navigate to detail if needed
        }
        binding.rvInterviews.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@InterviewsFragment.adapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchInterviews()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.interviews.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> binding.swipeRefresh.isRefreshing = true
                        is Resource.Success -> {
                            binding.swipeRefresh.isRefreshing = false
                            adapter.submitList(resource.data)
                        }
                        is Resource.Error -> {
                            binding.swipeRefresh.isRefreshing = false
                            Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> binding.swipeRefresh.isRefreshing = false
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
