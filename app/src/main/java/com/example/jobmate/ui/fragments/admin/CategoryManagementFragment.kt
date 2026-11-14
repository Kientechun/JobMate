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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobmate.R
import com.example.jobmate.databinding.FragmentAdminCategoriesBinding
import com.example.jobmate.ui.adapters.CategoryAdapter
import com.example.jobmate.ui.viewmodel.AdminViewModel
import com.example.jobmate.utils.Resource
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryManagementFragment : Fragment() {

    private var _binding: FragmentAdminCategoriesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminViewModel by viewModels()
    private lateinit var adapter: CategoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdminCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView()
        setupListeners()
        observeViewModel()

        viewModel.getCategories()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {
        adapter = CategoryAdapter(
            onEditClick = { category ->
                showCategoryDialog(category.id, category.name)
            },
            onDeleteClick = { category ->
                showDeleteConfirm(category.id)
            }
        )
        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@CategoryManagementFragment.adapter
        }
    }

    private fun setupListeners() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getCategories()
        }
        binding.fabAddCategory.setOnClickListener {
            showCategoryDialog()
        }
    }

    private fun showCategoryDialog(id: Int? = null, currentName: String? = null) {
        val input = EditText(requireContext())
        input.setText(currentName)
        input.hint = "Tên danh mục"
        
        val title = if (id == null) "Thêm danh mục mới" else "Cập nhật danh mục"
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setView(input)
            .setPositiveButton("Lưu") { _, _ ->
                val name = input.text.toString()
                if (name.isNotBlank()) {
                    if (id == null) viewModel.createCategory(name)
                    else viewModel.updateCategory(id, name)
                } else {
                    Toast.makeText(requireContext(), "Tên không được để trống", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showDeleteConfirm(id: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Xóa danh mục")
            .setMessage("Bạn có chắc chắn muốn xóa danh mục này? Hành động này không thể hoàn tác.")
            .setPositiveButton("Xóa") { _, _ ->
                viewModel.deleteCategory(id)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.categories.collectLatest { resource ->
                        binding.progressBar.isVisible = resource is Resource.Loading
                        binding.swipeRefresh.isRefreshing = false
                        when (resource) {
                            is Resource.Success -> {
                                val list = resource.data ?: emptyList()
                                adapter.submitList(list)
                                binding.tvEmpty.isVisible = list.isEmpty()
                            }
                            is Resource.Error -> {
                                Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                            }
                            else -> {}
                        }
                    }
                }

                launch {
                    viewModel.actionState.collectLatest { resource ->
                        if (resource is Resource.Success) {
                            Toast.makeText(requireContext(), "Thành công", Toast.LENGTH_SHORT).show()
                            viewModel.resetActionState()
                        } else if (resource is Resource.Error) {
                            Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                            viewModel.resetActionState()
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