package com.example.jobmate.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jobmate.data.models.CategoryDTO
import com.example.jobmate.databinding.ItemCategoryAdminBinding

class CategoryAdapter(
    private val onEditClick: (CategoryDTO) -> Unit,
    private val onDeleteClick: (CategoryDTO) -> Unit
) : ListAdapter<CategoryDTO, CategoryAdapter.CategoryViewHolder>(DiffCallback) {

    class CategoryViewHolder(private val binding: ItemCategoryAdminBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(category: CategoryDTO, onEditClick: (CategoryDTO) -> Unit, onDeleteClick: (CategoryDTO) -> Unit) {
            binding.tvName.text = category.name
            binding.btnEdit.setOnClickListener { onEditClick(category) }
            binding.btnDelete.setOnClickListener { onDeleteClick(category) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            ItemCategoryAdminBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position), onEditClick, onDeleteClick)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<CategoryDTO>() {
        override fun areItemsTheSame(oldItem: CategoryDTO, newItem: CategoryDTO) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: CategoryDTO, newItem: CategoryDTO) = oldItem == newItem
    }
}
