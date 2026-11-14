package com.example.jobmate.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jobmate.R
import com.example.jobmate.data.models.UserManagementDTO
import com.example.jobmate.databinding.ItemAdminUserBinding

class UserManagementAdapter(
    private val onLockClick: (UserManagementDTO) -> Unit
) : ListAdapter<UserManagementDTO, UserManagementAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(private val binding: ItemAdminUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserManagementDTO, onLockClick: (UserManagementDTO) -> Unit) {
            binding.tvName.text = user.fullName
            binding.tvEmail.text = user.email
            binding.tvRole.text = user.roles.joinToString(", ")

            if (user.isLocked) {
                binding.btnLock.text = binding.root.context.getString(R.string.unlock_account)
                binding.btnLock.setTextColor(ContextCompat.getColor(binding.root.context, R.color.success))
            } else {
                binding.btnLock.text = binding.root.context.getString(R.string.lock_account)
                binding.btnLock.setTextColor(ContextCompat.getColor(binding.root.context, R.color.error))
            }

            binding.btnLock.setOnClickListener { onLockClick(user) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAdminUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onLockClick)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<UserManagementDTO>() {
        override fun areItemsTheSame(oldItem: UserManagementDTO, newItem: UserManagementDTO) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: UserManagementDTO, newItem: UserManagementDTO) = oldItem == newItem
    }
}
