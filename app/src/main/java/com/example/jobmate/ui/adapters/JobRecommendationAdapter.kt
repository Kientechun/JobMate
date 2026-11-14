package com.example.jobmate.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jobmate.data.models.JobRecommendationDTO
import com.example.jobmate.databinding.ItemJobRecommendationBinding

class JobRecommendationAdapter(
    private val onClick: (JobRecommendationDTO) -> Unit
) : ListAdapter<JobRecommendationDTO, JobRecommendationAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemJobRecommendationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemJobRecommendationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recommendation: JobRecommendationDTO) {
            binding.apply {
                tvJobTitle.text = recommendation.jobTitle
                tvCompanyName.text = recommendation.companyName
                tvMatchScore.text = "Độ phù hợp: ${(recommendation.matchScore * 100).toInt()}%"
                tvReason.text = recommendation.reason
                
                root.setOnClickListener { onClick(recommendation) }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<JobRecommendationDTO>() {
        override fun areItemsTheSame(oldItem: JobRecommendationDTO, newItem: JobRecommendationDTO) = oldItem.jobId == newItem.jobId
        override fun areContentsTheSame(oldItem: JobRecommendationDTO, newItem: JobRecommendationDTO) = oldItem == newItem
    }
}
