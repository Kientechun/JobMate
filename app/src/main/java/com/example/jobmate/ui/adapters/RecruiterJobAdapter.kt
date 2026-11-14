package com.example.jobmate.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jobmate.R
import com.example.jobmate.data.models.JobDTO
import com.example.jobmate.databinding.ItemRecruiterJobBinding

class RecruiterJobAdapter(
    private val onApplicationsClick: (JobDTO) -> Unit,
    private val onEditClick: (JobDTO) -> Unit,
    private val onCloseClick: (JobDTO) -> Unit
) : ListAdapter<JobDTO, RecruiterJobAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(private val binding: ItemRecruiterJobBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            job: JobDTO,
            onApplicationsClick: (JobDTO) -> Unit,
            onEditClick: (JobDTO) -> Unit,
            onCloseClick: (JobDTO) -> Unit
        ) {
            binding.tvTitle.text = job.title
            
            val statusText = when (job.status) {
                0 -> "Chờ duyệt"
                1 -> "Đang tuyển"
                2 -> "Đã đóng"
                3 -> "Bị từ chối"
                else -> "Không xác định"
            }
            binding.tvStatus.text = statusText
            
            val statusBg = when (job.status) {
                0 -> R.drawable.bg_status_pending
                1 -> R.drawable.bg_status_success
                2 -> R.drawable.bg_status_closed
                3 -> R.drawable.bg_status_rejected
                else -> R.drawable.bg_status_pending
            }
            binding.tvStatus.setBackgroundResource(statusBg)
            
            val statusColor = when (job.status) {
                0 -> R.color.warning
                1 -> R.color.success
                2 -> R.color.text_secondary
                3 -> R.color.error
                else -> R.color.text_secondary
            }
            binding.tvStatus.setTextColor(ContextCompat.getColor(binding.root.context, statusColor))

            binding.tvStats.text = "Hạn nộp: ${job.expiryDate}"

            binding.btnApplications.setOnClickListener { onApplicationsClick(job) }
            binding.btnEdit.setOnClickListener { onEditClick(job) }
            binding.btnClose.setOnClickListener { onCloseClick(job) }
            
            // Disable buttons if job is closed or rejected
            binding.btnEdit.isEnabled = job.status != 2 && job.status != 3
            binding.btnClose.isEnabled = job.status == 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemRecruiterJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onApplicationsClick, onEditClick, onCloseClick)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<JobDTO>() {
        override fun areItemsTheSame(oldItem: JobDTO, newItem: JobDTO) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: JobDTO, newItem: JobDTO) = oldItem == newItem
    }
}
