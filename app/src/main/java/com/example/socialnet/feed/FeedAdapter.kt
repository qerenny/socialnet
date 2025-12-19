package com.example.socialnet.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.socialnet.data.db.MessageEntity
import com.example.socialnet.databinding.ItemPostBinding

class FeedAdapter : ListAdapter<MessageEntity, FeedAdapter.PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PostViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MessageEntity) {
            binding.tvUserId.text = "User ID: ${item.userId}"
            binding.tvBody.text = item.body
        }
    }

    class PostDiffCallback : DiffUtil.ItemCallback<MessageEntity>() {
        override fun areItemsTheSame(oldItem: MessageEntity, newItem: MessageEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MessageEntity, newItem: MessageEntity): Boolean {
            return oldItem == newItem
        }
    }
}
