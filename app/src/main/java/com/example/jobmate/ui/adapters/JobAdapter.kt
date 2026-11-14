package com.example.jobmate.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jobmate.R
import com.example.jobmate.data.models.Job
import com.example.jobmate.databinding.ItemJobBinding

class JobAdapter(
    private val onClick: (Job) -> Unit,
    private val onFavoriteClick: ((Job) -> Unit)? = null
) : ListAdapter<Job, JobAdapter.JobViewHolder>(DiffCallback) {

    class JobViewHolder(private val binding: ItemJobBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(job: Job, onClick: (Job) -> Unit, onFavoriteClick: ((Job) -> Unit)?) {
            val context = binding.root.context
            binding.tvTitle.text = job.title
            binding.tvCompany.text = job.companyName
            binding.tvLocation.text = job.location
            binding.tvSalary.text = context.getString(R.string.salary_format, job.salaryRange)
            binding.tvJobType.text = job.type
            
            if (job.isSaved) {
                binding.ivFavorite.setImageResource(android.R.drawable.btn_star_big_on)
                binding.ivFavorite.setColorFilter(context.getColor(R.color.primary))
            } else {
                binding.ivFavorite.setImageResource(android.R.drawable.btn_star_big_off)
                binding.ivFavorite.setColorFilter(context.getColor(R.color.text_secondary))
            }

            binding.root.setOnClickListener { onClick(job) }
            
            binding.ivFavorite.setOnClickListener {
                onFavoriteClick?.invoke(job)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        return JobViewHolder(
            ItemJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        holder.bind(getItem(position), onClick, onFavoriteClick)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Job>() {
        override fun areItemsTheSame(oldItem: Job, newItem: Job) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Job, newItem: Job) = oldItem == newItem
    }
}
