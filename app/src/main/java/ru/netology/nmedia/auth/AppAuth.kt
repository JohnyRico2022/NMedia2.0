package ru.netology.nmedia.auth

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.lang.IllegalStateException

class AppAuth private constructor(context: Context) {

    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    val keyId ="id"
    val keyToken = "token"

    private val _authStateFlow: MutableStateFlow<AuthState>
    init {
        val id = prefs.getLong(keyId, 0)
        val token = prefs.getString(keyToken, null)

        if (id == 0L || token == null) {
            _authStateFlow = MutableStateFlow(AuthState())
            with(prefs.edit()) {
                clear()
                apply()
            }
        } else {
            _authStateFlow = MutableStateFlow(AuthState(id, token))
        }
    }

    val authStateFlow: StateFlow<AuthState> = _authStateFlow.asStateFlow()

    @Synchronized
    fun setAuth(id: Long, token: String) {
        _authStateFlow.value = AuthState(id, token)
        with(prefs.edit()) {
            putLong(keyId, id)
            putString(keyToken, token)
            commit()
        }
    }

    @Synchronized
    fun removeAuth() {
        _authStateFlow.value = AuthState()
        with(prefs.edit()) {
            clear()
            commit()
        }
    }


    companion object {

        @Volatile
        private var instance: AppAuth? = null

        fun getInstance() = synchronized(this) {
            instance
                ?: throw IllegalStateException("getInstance should be called only after initAuth")
        }

        fun initAuth(context: Context) = instance ?: synchronized(this) {
            instance ?: AppAuth(context).also { instance = it }
        }
    }
}

data class AuthState(
    val id: Long = 0L,
    val token: String? = null
)