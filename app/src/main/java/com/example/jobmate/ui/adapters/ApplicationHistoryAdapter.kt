package com.example.jobmate.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jobmate.data.models.ApplicationHistoryDTO
import com.example.jobmate.databinding.ItemApplicationHistoryBinding

class ApplicationHistoryAdapter : ListAdapter<ApplicationHistoryDTO, ApplicationHistoryAdapter.HistoryViewHolder>(HistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemApplicationHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position), position == 0, position == itemCount - 1)
    }

    inner class HistoryViewHolder(private val binding: ItemApplicationHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(history: ApplicationHistoryDTO, isFirst: Boolean, isLast: Boolean) {
            binding.apply {
                tvStatus.text = mapStatus(history.status)
                tvNote.text = history.note
                tvDate.text = history.createdAt
                
                lineTop.visibility = if (isFirst) View.INVISIBLE else View.VISIBLE
                lineBottom.visibility = if (isLast) View.INVISIBLE else View.VISIBLE
            }
        }

        private fun mapStatus(status: Int): String = when (status) {
            0 -> "Đã nộp hồ sơ"
            1 -> "Nhà tuyển dụng đã xem"
            2 -> "Được chấp nhận"
            3 -> "Đã từ chối"
            4 -> "Đã rút hồ sơ"
            else -> "Không xác định"
        }
    }

    class HistoryDiffCallback : DiffUtil.ItemCallback<ApplicationHistoryDTO>() {
        override fun areItemsTheSame(oldItem: ApplicationHistoryDTO, newItem: ApplicationHistoryDTO) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ApplicationHistoryDTO, newItem: ApplicationHistoryDTO) = oldItem == newItem
    }
}
