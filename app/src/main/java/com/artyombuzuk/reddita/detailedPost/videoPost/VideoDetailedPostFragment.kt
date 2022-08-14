package com.artyombuzuk.reddita.detailedPost.videoPost

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.artyombuzuk.reddita.R
import com.artyombuzuk.reddita.databinding.VideoDetailedPostFragmentBinding
import com.artyombuzuk.reddita.detailedPost.detailedPostRecyclerView.CommentClickListenerType
import com.artyombuzuk.reddita.detailedPost.detailedPostRecyclerView.DetailedCommentAdapter
import com.artyombuzuk.reddita.others.viewBinding.ViewBindingFragment
import com.artyombuzuk.reddita.others.shareWithOthers
import com.artyombuzuk.reddita.subreddit.subredditRecyclerView.RedditPost
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import kotlinx.coroutines.flow.collectLatest

class VideoDetailedPostFragment :
    ViewBindingFragment<VideoDetailedPostFragmentBinding>(VideoDetailedPostFragmentBinding::inflate) {
    private val args: VideoDetailedPostFragmentArgs by navArgs()
    private lateinit var item: RedditPost.VideoPost
    private val viewModel: VideoDetailedPostViewModel by viewModels()
    private var commentAdapter: DetailedCommentAdapter? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.commentsRecyclerView.isNestedScrollingEnabled = true
        item = args.item
        viewModel.setLink(item.url)
        setAttributes()
        initList()
        setOnTextChangedListener()

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.flowComments.collectLatest { pagingData ->
                commentAdapter?.submitData(pagingData)
            }
        }

        binding.sendCommentButton.setOnClickListener {
            viewModel.setSendAction(true)
            viewModel.sendComment(binding.commentEditText.text.toString(), item.postId)
        }

        binding.shareButton.setOnClickListener {
            shareWithOthers(item.url, requireContext())
        }

        viewModel.save.observe(viewLifecycleOwner, ::save)
        viewModel.unSave.observe(viewLifecycleOwner, ::unsave)
        viewModel.comment.observe(viewLifecycleOwner, ::commentResult)
    }

    private fun setAttributes() {
        binding.apply {
            Glide.with(requireContext())
                .load(item.avatar)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.placeholder)
                .into(avatarImageView)
            nicknameTextView.text = item.nickName
            timeTextView.text = item.time
            titleTextView.text = item.title
            commentsTextView.text = item.comments

            val mediaItem = MediaItem.Builder()
                .setUri(item.videoAudio)
                .build()

            binding.videoView.player = ExoPlayer.Builder(requireContext()).build()
            binding.videoView.player?.setMediaItem(mediaItem)
            binding.videoView.player?.prepare()
            binding.videoView.player?.play()
        }
    }

    private fun initList() {
        commentAdapter = DetailedCommentAdapter()
        with(binding.commentsRecyclerView) {
            adapter = commentAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            setHasFixedSize(false)
        }
        commentAdapter?.addLoadStateListener { state ->
            binding.commentsProgressBar.isVisible = state.refresh == LoadState.Loading
        }

        binding.commentsRecyclerView.itemAnimator = null

        commentAdapter?.itemClickListener = { item, position, vote, type ->
            when (type) {
                CommentClickListenerType.SAVE -> {
                    viewModel.saveComment(Pair(item.commentId, position))
                    viewModel.setSaveAction(true)
                }
                CommentClickListenerType.UNSAVE -> {
                    viewModel.unSaveComment(Pair(item.commentId, position))
                    viewModel.setSaveAction(true)
                }
            }
        }
    }

    private fun setOnTextChangedListener() {
        binding.commentEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.commentEditText.text?.isNotBlank() == true) {
                    binding.sendCommentButton.setColorFilter(Color.rgb(25, 118, 210))
                    binding.sendCommentButton.isEnabled = true
                } else {
                    binding.sendCommentButton.isEnabled = false
                    binding.sendCommentButton.setColorFilter(Color.rgb(189, 189, 189))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun unsave(pairValuePosition: Pair<Boolean, Int>) {
        if (pairValuePosition.first && viewModel.getSaveAction()) {
            commentAdapter?.snapshot()?.items!![pairValuePosition.second].isSaved = false
            commentAdapter?.notifyItemChanged(pairValuePosition.second)
            Toast.makeText(requireContext(), R.string.unsaved_successfully, Toast.LENGTH_SHORT)
                .show()
        } else {
            if (viewModel.getSaveAction()) {
                Toast.makeText(requireContext(), R.string.unable_to_unsave, Toast.LENGTH_SHORT)
                    .show()
            }
        }
        viewModel.setSaveAction(false)
    }

    private fun save(pairValuePosition: Pair<Boolean, Int>) {
        if (pairValuePosition.first && viewModel.getSaveAction()) {
            commentAdapter?.snapshot()?.items!![pairValuePosition.second].isSaved = true
            commentAdapter?.notifyItemChanged(pairValuePosition.second)
            Toast.makeText(requireContext(), R.string.saved_successfully, Toast.LENGTH_SHORT)
                .show()
        } else {
            if (viewModel.getSaveAction()) {
                Toast.makeText(requireContext(), R.string.unable_to_save, Toast.LENGTH_SHORT)
                    .show()
            }
        }
        viewModel.setSaveAction(false)
    }

    private fun commentResult(value: Boolean) {
        if (viewModel.getSendAction()) {
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.root.windowToken, 0)

            if (value) {
                binding.commentEditText.setText("")
                commentAdapter?.refresh()
                Toast.makeText(
                    requireContext(),
                    R.string.comment_has_been_added,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    R.string.error_comment_was_not_added,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        viewModel.setSendAction(false)
    }

    override fun onStop() {
        super.onStop()
        binding.videoView.player?.pause()
    }
}