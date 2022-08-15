package com.artyombuzuk.reddita.FragmentSaved.comments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.artyombuzuk.reddita.R
import com.artyombuzuk.reddita.databinding.SavedCommentsFragmentBinding
import com.artyombuzuk.reddita.detailedPost.detailedPostRecyclerView.DetailedCommentAdapter
import com.artyombuzuk.reddita.others.viewBinding.ViewBindingFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SavedCommentsFragment :
    ViewBindingFragment<SavedCommentsFragmentBinding>(SavedCommentsFragmentBinding::inflate) {
    private var savedCommentsAdapter: DetailedCommentAdapter? = null
    private val viewModel: SavedCommentsViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            if (viewModel.getIconState()) {
                viewModel.flowSavedComments.collectLatest { pagingData ->
                    savedCommentsAdapter?.submitData(pagingData)
                }
            } else {
                viewModel.flowMyComments.collectLatest { pagingData ->
                    savedCommentsAdapter?.submitData(pagingData)
                }
            }
        }

        if (viewModel.getIconState()) {
            binding.switchButton.setImageResource(R.drawable.ic_comment)
        } else {
            binding.switchButton.setImageResource(R.drawable.ic_save_comment)
        }

        binding.switchButton.setOnClickListener {
            changeList()
        }

        onRefreshListener()
    }

    private fun initList() {
        savedCommentsAdapter = DetailedCommentAdapter()
        with(binding.savedCommentsRecyclerView) {
            adapter = savedCommentsAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            setHasFixedSize(true)
        }
        savedCommentsAdapter?.addLoadStateListener { state ->
            binding.savedCommentsProgressBar.isVisible = state.refresh == LoadState.Loading
            if (state.refresh != LoadState.Loading && binding.refreshLayout.isRefreshing) {
                binding.savedCommentsRecyclerView.scrollToPosition(0)
                binding.refreshLayout.isRefreshing = false
            }
        }
    }

    private fun changeList() {
        if (viewModel.getIconState()) {
            savedCommentsAdapter?.submitData(lifecycle, PagingData.empty())
            lifecycleScope.launch {
                viewModel.flowMyComments.collectLatest { pagingData ->
                    savedCommentsAdapter?.submitData(pagingData)
                }
            }
            binding.savedCommentsProgressBar.visibility = View.VISIBLE
            viewModel.setIconState(false)
            binding.switchButton.setImageResource(R.drawable.ic_save_comment)
        } else {
            savedCommentsAdapter?.submitData(lifecycle, PagingData.empty())
            lifecycleScope.launch {
                viewModel.flowSavedComments.collectLatest { pagingData ->
                    savedCommentsAdapter?.submitData(pagingData)
                }
            }
            binding.savedCommentsProgressBar.visibility = View.VISIBLE
            viewModel.setIconState(true)
            binding.switchButton.setImageResource(R.drawable.ic_comment)
        }
        binding.savedCommentsProgressBar.visibility = View.GONE
    }

    private fun onRefreshListener() {
        binding.refreshLayout.setOnRefreshListener {
            savedCommentsAdapter?.refresh()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        savedCommentsAdapter = null
    }
}