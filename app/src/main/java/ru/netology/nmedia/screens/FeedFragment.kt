package ru.netology.nmedia.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.observeOn
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class FeedFragment : Fragment() {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val viewModel: PostViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        val adapter = PostAdapter(object : OnInteractionListener {

            override fun like(post: Post) {
                if (authViewModel.authenticated) {
                    if (!post.likedByMe) {
                        viewModel.likeById(post.id)
                    } else {
                        viewModel.disLikeById(post.id)
                    }
                } else {
                    findNavController().navigate(R.id.authFragment)
                    Toast.makeText(context, "неоходимо войти в систему", Toast.LENGTH_SHORT).show()
                }
            }

            override fun remove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun edit(post: Post) {
                val text = post.content
                findNavController().navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = text
                    })
                viewModel.edit(post)
            }

            override fun share(post: Post) {}

            override fun video(post: Post) {}

            override fun actionOnFragment(post: Post) {
                findNavController()
                    .navigate(
                        R.id.action_feedFragment_to_detailFragment,
                        Bundle().apply {
                            textArg = post.id.toString()
                        })
            }

            override fun actionOnAttachmentFragment(post: Post) {
                findNavController().navigate(R.id.action_feedFragment_to_attachmentFragment,
                    Bundle().apply {
                        textArg = post.attachment?.url
                    })
            }
        })

        binding.recyclerView.adapter = adapter

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.swipeRefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.network_error, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.retry_loading) { viewModel.load() }
                    .show()
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest {
                adapter.submitData(it)
            }
        }



        /*viewModel.newerCount.observe(viewLifecycleOwner) { state ->
            if (state > 0) {
                binding.newerPosts.visibility = View.VISIBLE
            }
            println()
        }*/

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                binding.recyclerView.smoothScrollToPosition(0)
            }
        })

        /*  binding.newerPosts.setOnClickListener {
              viewModel.makePostShowed()
              viewModel.getUnreadPosts()
              binding.newerPosts.visibility = View.GONE

          }*/

        binding.addPostButton.setOnClickListener {
            if (authViewModel.authenticated) {
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
            } else {
                findNavController().navigate(R.id.authFragment)
                Toast.makeText(context, "неоходимо войти в систему", Toast.LENGTH_SHORT).show()
            }
        }

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest {
                binding.swipeRefresh.isRefreshing = it.refresh is LoadState.Loading
                        || it.append is LoadState.Loading
                        || it.prepend is LoadState.Loading
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            adapter.refresh()
        }


        return binding.root
    }

    companion object {
        var Bundle.textArg: String? by StringArg
    }
}