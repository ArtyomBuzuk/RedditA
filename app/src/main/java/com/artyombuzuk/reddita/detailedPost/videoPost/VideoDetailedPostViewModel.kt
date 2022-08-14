package com.artyombuzuk.reddita.detailedPost.videoPost

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.artyombuzuk.reddita.detailedPost.detailedPostRecyclerView.DetailedPostComment
import com.artyombuzuk.reddita.detailedPost.detailedPostRecyclerView.DetailedPostPagination
import com.artyombuzuk.reddita.detailedPost.detailedPostRecyclerView.DetailedPostRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class VideoDetailedPostViewModel(state: SavedStateHandle) : ViewModel() {
    private val stateHandle = state
    private val repository = DetailedPostRepository()
    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, t ->
            Log.e("error", "$t")
        }
    private val scope = CoroutineScope(Dispatchers.IO + coroutineExceptionHandler)

    private val saveLiveData = MutableLiveData<Pair<Boolean, Int>>()
    val save: LiveData<Pair<Boolean, Int>> = saveLiveData

    private val commentLiveData = MutableLiveData<Boolean>()
    val comment: LiveData<Boolean> = commentLiveData

    private val unSaveLiveData = MutableLiveData<Pair<Boolean, Int>>()
    val unSave: LiveData<Pair<Boolean, Int>> = unSaveLiveData

    val flowComments: Flow<PagingData<DetailedPostComment>> =
        Pager(config = PagingConfig(pageSize = 20, prefetchDistance = 2),
            pagingSourceFactory = { DetailedPostPagination(getLink()!!) }
        ).flow.cachedIn(viewModelScope)


    fun setLink(link: String) {
        stateHandle.set<String>("LINK_KEY", link)
    }

    private fun getLink() = stateHandle.get<String>("LINK_KEY")

    fun setSaveAction(value: Boolean) {
        stateHandle.set<Boolean>("SAVE_KEY", value)
    }

    fun getSaveAction() = stateHandle.get<Boolean>("SAVE_KEY") ?: false

    fun setSendAction(value: Boolean) {
        stateHandle.set<Boolean>("SEND_KEY", value)
    }

    fun getSendAction() = stateHandle.get<Boolean>("SEND_KEY") ?: false

    fun vote(id: String, vote: Int) {
        scope.launch {
            repository.vote(id, vote)
        }
    }

    fun saveComment(id: Pair<String, Int>) {
        scope.launch {
            saveLiveData.postValue(repository.saveComment(id))
        }
    }

    fun unSaveComment(id: Pair<String, Int>) {
        scope.launch {
            unSaveLiveData.postValue(repository.unSaveComment(id))
        }
    }

    fun sendComment(text: String, postId: String) {
        scope.launch {
            commentLiveData.postValue(repository.sendComment(text, postId))
        }
    }
}