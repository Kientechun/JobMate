package com.example.jobmate.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jobmate.databinding.ItemInterviewBinding
import com.example.jobmate.data.models.InterviewDTO

class InterviewAdapter(
    private val onInterviewClick: (InterviewDTO) -> Unit
) : ListAdapter<InterviewDTO, InterviewAdapter.InterviewViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InterviewViewHolder {
        val binding = ItemInterviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InterviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InterviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class InterviewViewHolder(private val binding: ItemInterviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(interview: InterviewDTO) {
            binding.apply {
                tvJobTitle.text = interview.jobTitle
                tvCompany.text = interview.companyName
                tvDateTime.text = interview.scheduledAt
                tvLocation.text = interview.location
                tvStatus.text = interview.status

                root.setOnClickListener {
                    onInterviewClick(interview)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<InterviewDTO>() {
        override fun areItemsTheSame(oldItem: InterviewDTO, newItem: InterviewDTO) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: InterviewDTO, newItem: InterviewDTO) = oldItem == newItem
    }
}
