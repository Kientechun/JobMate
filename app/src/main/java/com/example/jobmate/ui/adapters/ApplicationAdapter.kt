package com.example.jobmate.ui.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jobmate.R
import com.example.jobmate.data.models.ApplicationDTO
import com.example.jobmate.databinding.ItemApplicationBinding

class ApplicationAdapter(private val onDetailClick: (ApplicationDTO) -> Unit) :
    ListAdapter<ApplicationDTO, ApplicationAdapter.ApplicationViewHolder>(DiffCallback) {

    class ApplicationViewHolder(private val binding: ItemApplicationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(application: ApplicationDTO, onDetailClick: (ApplicationDTO) -> Unit) {
            val context = binding.root.context
            binding.tvJobTitle.text = application.job?.title ?: "N/A"
            binding.tvCompanyName.text = application.job?.companyName ?: "N/A"
            binding.tvAppliedDate.text = application.createdAt
            binding.tvStatus.text = mapStatus(application.status)
            
            // Set status background based on state
            val statusBg = when (application.status) {
                1 -> R.drawable.bg_status_success // Approved
                0 -> R.drawable.bg_status_pending // Pending
                2 -> R.drawable.bg_status_error   // Rejected
                3 -> R.drawable.bg_status_warning // UnderReview
                else -> R.drawable.bg_status_pending
            }
            binding.tvStatus.setBackgroundResource(statusBg)
            
            // Text color matching semantic state
            val textColor = when (application.status) {
                1 -> R.color.success
                0 -> R.color.text_secondary
                2 -> R.color.error
                3 -> R.color.warning
                else -> R.color.text_secondary
            }
            binding.tvStatus.setTextColor(ContextCompat.getColor(context, textColor))

            binding.btnDetail.setOnClickListener { onDetailClick(application) }
            binding.root.setOnClickListener { onDetailClick(application) }
        }

        private fun mapStatus(status: Int): String = when (status) {
            0 -> "Chờ duyệt"
            1 -> "Đã chấp nhận"
            2 -> "Đã từ chối"
            3 -> "Đang xem xét"
            else -> "Không xác định"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicationViewHolder {
        return ApplicationViewHolder(
            ItemApplicationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ApplicationViewHolder, position: Int) {
        holder.bind(getItem(position), onDetailClick)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ApplicationDTO>() {
        override fun areItemsTheSame(oldItem: ApplicationDTO, newItem: ApplicationDTO) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ApplicationDTO, newItem: ApplicationDTO) = oldItem == newItem
    }
}
