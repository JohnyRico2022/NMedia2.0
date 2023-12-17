package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl


class SignInViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(context = application).postDao)

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    val data = AppAuth.getInstance().authStateFlow

    val authenticated: Boolean
        get() = data.value.id != 0L


    fun signIn(login: String, pass: String) {

        viewModelScope.launch {

            try {
                repository.singIn(login, pass)
                _dataState.value = FeedModelState(authState = true)

            } catch (e: Exception){
                _dataState.value = FeedModelState(error = true)
            }


        }

    }


}