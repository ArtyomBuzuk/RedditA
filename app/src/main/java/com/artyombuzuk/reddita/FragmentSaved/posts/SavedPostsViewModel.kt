package com.artyombuzuk.reddita.FragmentSaved.posts

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.artyombuzuk.reddita.subreddit.SubredditRepository
import com.artyombuzuk.reddita.subreddit.subredditRecyclerView.RedditPost
import com.google.android.exoplayer2.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SavedPostsViewModel(state: SavedStateHandle) : ViewModel() {
    private val savedStateHandle = state
    val flowSavedPost: Flow<PagingData<RedditPost>> =
        Pager(config = PagingConfig(pageSize = 20, prefetchDistance = 2),
            pagingSourceFactory = { SavedPostsPagination() }
        ).flow.cachedIn(viewModelScope)

    private val unfollowListLiveData = MutableLiveData<Int>()
    private val followListLiveData = MutableLiveData<Int>()
    private val unsaveOnlineLiveData = MutableLiveData<Boolean>()
    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            Log.e("error", "$throwable")
        }
    private val scope = CoroutineScope(Dispatchers.IO + coroutineExceptionHandler)
    private val repository = SubredditRepository()

    val unfollow: LiveData<Int> = unfollowListLiveData
    val follow: LiveData<Int> = followListLiveData
    val unsaveOnline: LiveData<Boolean> = unsaveOnlineLiveData

    fun unsavePostOnline(item: RedditPost) {
        scope.launch {
            unsaveOnlineLiveData.postValue(repository.unsavePostOnline(item))
        }
    }

    fun unsavePostLocal(postId: String) {
        scope.launch {
            repository.unsaveLocalPost(postId)
        }
    }

    fun setActionState(state: Boolean) {
        savedStateHandle.set(ACTION_KEY, state)
    }

    fun getActionState(): Boolean {
        return savedStateHandle.get(ACTION_KEY) ?: false
    }

    fun setPositionState(state: Int) {
        savedStateHandle.set(POSITION_KEY, state)
    }

    fun getPositionState(): Int {
        return savedStateHandle.get(POSITION_KEY) ?: 0
    }

    companion object {
        const val SUBREDDIT_NAME_KEY = "SUBREDDIT_NAME_KEY"
        const val PAGE_KEY = "PAGE_KEY"
        const val ACTION_KEY = "ACTION_KEY"
        private const val POSITION_KEY = "POSITION_KEY"
    }
}