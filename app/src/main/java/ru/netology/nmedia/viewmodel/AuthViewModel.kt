package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.netology.nmedia.dto.auth.AppAuth
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: AppAuth
): ViewModel() {

    val data = auth.authStateFlow

    val authenticated: Boolean
        get() = auth.authStateFlow.value.id != 0L

}