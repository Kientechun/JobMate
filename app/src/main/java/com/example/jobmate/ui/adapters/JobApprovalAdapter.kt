package com.example.jobmate.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jobmate.data.models.JobDTO
import com.example.jobmate.databinding.ItemRecruiterJobBinding

class JobApprovalAdapter(
    private val onApproveClick: (JobDTO) -> Unit,
    private val onRejectClick: (JobDTO) -> Unit
) : ListAdapter<JobDTO, JobApprovalAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(private val binding: ItemRecruiterJobBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(job: JobDTO, onApproveClick: (JobDTO) -> Unit, onRejectClick: (JobDTO) -> Unit) {
            binding.tvTitle.text = job.title
            binding.tvStats.text = "Công ty: ${job.companyName} | Hạn: ${job.expiryDate}"
            binding.tvStatus.text = "Đang chờ duyệt"
            
            binding.btnApplications.text = "Xem chi tiết"
            binding.btnEdit.text = "Duyệt"
            binding.btnClose.text = "Từ chối"
            
            binding.btnEdit.setOnClickListener { onApproveClick(job) }
            binding.btnClose.setOnClickListener { onRejectClick(job) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemRecruiterJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onApproveClick, onRejectClick)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<JobDTO>() {
        override fun areItemsTheSame(oldItem: JobDTO, newItem: JobDTO) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: JobDTO, newItem: JobDTO) = oldItem == newItem
    }
}
