package com.example.jobmate.ui.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobmate.R
import com.example.jobmate.databinding.FragmentHomeBinding
import com.example.jobmate.ui.adapters.JobAdapter
import com.example.jobmate.ui.viewmodel.JobViewModel
import com.example.jobmate.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: JobViewModel by viewModels()
    private lateinit var jobAdapter: JobAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        
        binding.btnRetry.setOnClickListener {
            viewModel.fetchJobs(1)
        }

        viewModel.fetchJobs(1)
    }

    private fun setupRecyclerView() {
        jobAdapter = JobAdapter { job ->
            val action = HomeFragmentDirections.actionHomeFragmentToJobDetailFragment(job.id)
            findNavController().navigate(action)
        }
        binding.rvJobs.apply {
            adapter = jobAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.jobsState.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.tvEmpty.visibility = View.GONE
                            binding.btnRetry.visibility = View.GONE
                            binding.rvJobs.visibility = View.GONE
                        }
                        is Resource.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.tvEmpty.visibility = View.GONE
                            binding.btnRetry.visibility = View.GONE
                            binding.rvJobs.visibility = View.VISIBLE
                            jobAdapter.submitList(resource.data)
                        }
                        is Resource.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.tvEmpty.visibility = View.VISIBLE
                            binding.tvEmpty.text = resource.message
                            binding.btnRetry.visibility = View.VISIBLE
                            binding.rvJobs.visibility = View.GONE
                            Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                        }
                        is Resource.Empty -> {
                            binding.progressBar.visibility = View.GONE
                            binding.tvEmpty.visibility = View.VISIBLE
                            binding.tvEmpty.setText(R.string.empty_jobs)
                            binding.btnRetry.visibility = View.GONE
                            binding.rvJobs.visibility = View.GONE
                            jobAdapter.submitList(emptyList())
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.fetchJobs(1, query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    viewModel.fetchJobs(1)
                }
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
