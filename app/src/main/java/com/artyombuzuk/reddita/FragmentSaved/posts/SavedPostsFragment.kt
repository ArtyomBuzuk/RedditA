package com.artyombuzuk.reddita.FragmentSaved.posts

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.artyombuzuk.reddita.R
import com.artyombuzuk.reddita.databinding.SavedPostsFragmentBinding
import com.artyombuzuk.reddita.others.shareWithOthers
import com.artyombuzuk.reddita.others.viewBinding.ViewBindingFragment
import com.artyombuzuk.reddita.subreddit.subredditRecyclerView.ClickListenerType
import com.artyombuzuk.reddita.subreddit.subredditRecyclerView.RedditPost
import com.artyombuzuk.reddita.subreddit.subredditRecyclerView.SubredditAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SavedPostsFragment :
    ViewBindingFragment<SavedPostsFragmentBinding>(SavedPostsFragmentBinding::inflate) {
    private var savedPostsAdapter: SubredditAdapter? = null
    private val viewModel: SavedPostsViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.flowSavedPost.collectLatest { pagingData ->
                savedPostsAdapter?.submitData(pagingData)
            }
        }

        lifecycleScope.launch {
            savedPostsAdapter?.submitData(lifecycle, PagingData.empty())
            viewModel.flowSavedPost.collectLatest { pagingData ->
                savedPostsAdapter?.submitData(pagingData)
            }
        }

        viewModel.unsaveOnline.observe(viewLifecycleOwner, ::unsave)
        onRefreshListener()
    }

    private fun initList() {
        savedPostsAdapter = SubredditAdapter()
        with(binding.savedPostsRecyclerView) {
            adapter = savedPostsAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            setHasFixedSize(true)
        }
        savedPostsAdapter?.itemClickListener = { item, position, type, name ->
            onClickListener(item, type, position, name)
        }

        binding.savedPostsRecyclerView.itemAnimator = null

        savedPostsAdapter?.addLoadStateListener { state ->
            binding.savedPostsProgressBar.isVisible = state.refresh == LoadState.Loading
            if (state.refresh != LoadState.Loading && binding.refreshLayout.isRefreshing) {
                binding.savedPostsRecyclerView.smoothScrollToPosition(0)
                binding.refreshLayout.isRefreshing = false
            }
        }
    }

    private fun onRefreshListener() {
        binding.refreshLayout.setOnRefreshListener {
            lifecycleScope.launch {
                savedPostsAdapter?.refresh()
            }
        }
    }

    private fun onClickListener(
        item: RedditPost,
        type: ClickListenerType,
        position: Int,
        name: String,
    ) {
        when (type) {
            ClickListenerType.NAVIGATE -> unsavePost(item)
            ClickListenerType.SHARE -> shareWithOthers(item.url, requireContext())
            ClickListenerType.SAVE -> {}
            ClickListenerType.UNSAVE -> {
                viewModel.setActionState(true)
                viewModel.unsavePostOnline(item)
            }
        }
    }

    private fun unsavePost(item: RedditPost) {
        val deleteItemString = requireContext().getString(R.string.delete_item)
        val confirmString = requireContext().getString(R.string.confirm)
        val cancelString = requireContext().getString(R.string.cancel)
        if (item.isLocal) {
            viewModel.setActionState(true)
            viewModel.unsavePostLocal(item.postId)
            AlertDialog.Builder(requireContext())
                .setTitle(deleteItemString)
                .setPositiveButton(confirmString) { dialog, _ ->
                    unsave(true)
                    dialog.cancel()
                }
                .setNegativeButton(cancelString) { dialog, _ ->
                    dialog.cancel()
                }
                .create()
                .show()
        }
    }

    private fun unsave(value: Boolean) {
        if (viewModel.getActionState()) {
            if (value) {
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.unsaved_successfully),
                    Toast.LENGTH_SHORT
                ).show()
                savedPostsAdapter?.refresh()
            } else {
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.unable_to_unsave),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        viewModel.setActionState(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        savedPostsAdapter = null
    }
}