package com.artyombuzuk.reddita.detailedPost.detailedPostRecyclerView

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.artyombuzuk.reddita.databinding.ItemCommentBinding

sealed class CommentHomeViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
    var itemClickListener: ((item: DetailedPostComment, position: Int, vote: Int, type: CommentClickListenerType) -> Unit)? =
        null

    class CommentViewHolder(private val binding: ItemCommentBinding) :
        CommentHomeViewHolder(binding) {
        fun bind(item: DetailedPostComment) {
            binding.apply {
                nicknameTextView.text = item.nickname
                timeTextView.text = item.time
                descriptionTextView.text = item.description
                upsTextView.text = "Лайков: " + item.ups.toString()

                if (!item.elementVisibility) {
                    saveButton.visibility = View.GONE
                    upsTextView.visibility = View.GONE
                }

                if (item.isSaved) {
                    saveButton.setColorFilter(Color.rgb(255, 0, 0))
                } else {
                    saveButton.setColorFilter(Color.rgb(189, 189, 189))
                }

                saveButton.setOnClickListener {
                    if (!item.isSaved) {
                        itemClickListener?.invoke(
                            item,
                            bindingAdapterPosition,
                            0,
                            CommentClickListenerType.SAVE
                        )
                    } else {
                        itemClickListener?.invoke(
                            item,
                            bindingAdapterPosition,
                            0,
                            CommentClickListenerType.UNSAVE
                        )
                    }
                }
            }
        }
    }
}