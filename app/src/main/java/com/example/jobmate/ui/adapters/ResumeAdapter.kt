package com.example.jobmate.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jobmate.data.models.ResumeDTO
import com.example.jobmate.databinding.ItemResumeBinding

class ResumeAdapter(
    private val onEditClick: (ResumeDTO) -> Unit,
    private val onDeleteClick: (ResumeDTO) -> Unit,
    private val onSetDefaultClick: (ResumeDTO) -> Unit,
    private val onAnalyzeClick: (ResumeDTO) -> Unit
) : ListAdapter<ResumeDTO, ResumeAdapter.ResumeViewHolder>(ResumeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResumeViewHolder {
        val binding = ItemResumeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResumeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResumeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ResumeViewHolder(private val binding: ItemResumeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(resume: ResumeDTO) {
            binding.apply {
                tvResumeTitle.text = resume.title
                tvFullName.text = resume.fullName
                tvCreatedAt.text = resume.createdAt
                ivDefault.visibility = if (resume.isDefault) View.VISIBLE else View.GONE
                
                btnEdit.setOnClickListener { onEditClick(resume) }
                btnDelete.setOnClickListener { onDeleteClick(resume) }
                btnSetDefault.setOnClickListener { onSetDefaultClick(resume) }
                btnAnalyze.setOnClickListener { onAnalyzeClick(resume) }
            }
        }
    }

    class ResumeDiffCallback : DiffUtil.ItemCallback<ResumeDTO>() {
        override fun areItemsTheSame(oldItem: ResumeDTO, newItem: ResumeDTO) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ResumeDTO, newItem: ResumeDTO) = oldItem == newItem
    }
}
