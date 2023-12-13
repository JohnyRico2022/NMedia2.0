package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nmedia.R
import ru.netology.nmedia.util.RoundingNumbers
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post

interface OnInteractionListener {
    fun like(post: Post)
    fun remove(post: Post)
    fun edit(post: Post)
    fun share(post: Post)
    fun video(post: Post)
    fun actionOnFragment(post: Post)
    fun actionOnAttachmentFragment(post: Post)
}

class PostAdapter(
    private val onInteractionListener: OnInteractionListener,
) : ListAdapter<Post, PostViewHolder>(PostDiffCallBack()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }


    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(post: Post) {
        val baseUrl = "http://10.0.2.2:9999"

        binding.apply {
            author.text = post.author
            published.text = post.published.toString()
            content.text = post.content
            postLikes.isChecked = post.likedByMe
            postLikes.text = RoundingNumbers.scoreDisplay(post.likes)
            postLikes.setOnClickListener {
                onInteractionListener.like(post)
            }
            //           postShare.text = RoundingNumbers.scoreDisplay(post.share)
            postShare.setOnClickListener {
                onInteractionListener.share(post)
            }
            attachment.visibility = View.GONE

            if (post.attachment == null) {
                attachment.visibility = View.GONE
            } else {
                attachment.visibility = View.VISIBLE
            }

            Glide.with(avatar)
                .load("$baseUrl/avatars/${post.authorAvatar}")
                .error(R.drawable.ic_error_24)
                .placeholder(R.drawable.ic_downloading_24)
                .circleCrop()
                .timeout(10_000)
                .into(avatar)

            Glide.with(attachment)
                .load("$baseUrl/media/${post.attachment?.url}")
                .timeout(10_000)
                .into(attachment)


            menu.isVisible = post.ownedByMe
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.menu_options)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.remove(post)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.edit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }
            content.setOnClickListener {
                onInteractionListener.actionOnFragment(post)
            }
            author.setOnClickListener {
                onInteractionListener.actionOnFragment(post)
            }
            published.setOnClickListener {
                onInteractionListener.actionOnFragment(post)
            }
            attachment.setOnClickListener {
                onInteractionListener.actionOnAttachmentFragment(post)
            }
        }
    }
}
    fun onOptionsItemSelected() {}

    class PostDiffCallBack : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }
