package com.example.socialnet.feed

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.socialnet.R
import com.example.socialnet.data.db.MessageEntity
import com.example.socialnet.databinding.ItemPostBinding

class FeedAdapter(
    private val onLikeClicked: (Int) -> Unit
) : ListAdapter<MessageEntity, FeedAdapter.PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onLikeClicked)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PostViewHolder(
        private val binding: ItemPostBinding,
        private val onLikeClicked: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MessageEntity) {
            binding.tvUserId.text = "@${item.userId}"
            binding.tvUserName.text = item.userName.ifEmpty { "User ${item.userId}" }
            binding.tvBody.text = item.body

            binding.ivAvatar.backgroundTintList = ColorStateList.valueOf(item.userAvatarColor)

            if (item.isLiked) {
                binding.btnLike.setImageResource(R.drawable.ic_favorite)
            } else {
                binding.btnLike.setImageResource(R.drawable.ic_favorite_border)
            }

            binding.btnLike.setOnClickListener {
                onLikeClicked(item.id)
            }
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
