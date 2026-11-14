package com.example.jobmate.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jobmate.databinding.ItemNotificationBinding
import com.example.jobmate.data.models.NotificationDTO

class NotificationAdapter(
    private val onNotificationClick: (NotificationDTO) -> Unit
) : ListAdapter<NotificationDTO, NotificationAdapter.NotificationViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NotificationViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: NotificationDTO) {
            binding.apply {
                tvTitle.text = notification.title
                tvMessage.text = notification.message
                tvTime.text = notification.createdAt // Should format this
                
                unreadIndicator.visibility = if (notification.isRead) View.GONE else View.VISIBLE
                container.setBackgroundColor(if (notification.isRead) Color.TRANSPARENT else Color.parseColor("#F0F7FF"))

                root.setOnClickListener {
                    onNotificationClick(notification)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<NotificationDTO>() {
        override fun areItemsTheSame(oldItem: NotificationDTO, newItem: NotificationDTO) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: NotificationDTO, newItem: NotificationDTO) = oldItem == newItem
    }
}
