package com.example.jobmate.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jobmate.R
import com.example.jobmate.data.models.Job
import com.example.jobmate.databinding.ItemJobBinding

class JobAdapter(private val onClick: (Job) -> Unit) :
    ListAdapter<Job, JobAdapter.JobViewHolder>(DiffCallback) {

    class JobViewHolder(private val binding: ItemJobBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(job: Job, onClick: (Job) -> Unit) {
            val context = binding.root.context
            binding.tvTitle.text = job.title
            binding.tvCompany.text = job.companyName
            binding.tvLocation.text = job.location
            binding.tvSalary.text = context.getString(R.string.salary_format, job.salary)
            binding.root.setOnClickListener { onClick(job) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        return JobViewHolder(
            ItemJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        holder.bind(getItem(position), onClick)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Job>() {
        override fun areItemsTheSame(oldItem: Job, newItem: Job) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Job, newItem: Job) = oldItem == newItem
    }
}
