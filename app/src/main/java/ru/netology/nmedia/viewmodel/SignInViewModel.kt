package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nmedia.dto.auth.AppAuth
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.PostRepository
import javax.inject.Inject


@HiltViewModel
class SignInViewModel @Inject constructor(
    private val repository: PostRepository,
    auth: AppAuth
    ) : ViewModel() {
    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    val data = auth.authStateFlow
    val authenticated: Boolean
        get() = data.value.id != 0L

    fun signIn(login: String, pass: String) {

        viewModelScope.launch {

            try {
                repository.singIn(login, pass)
                _dataState.value = FeedModelState(authState = true)

            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

}