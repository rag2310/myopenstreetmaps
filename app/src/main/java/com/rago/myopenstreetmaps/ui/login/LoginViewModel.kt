package com.rago.myopenstreetmaps.ui.login

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    val username = MutableStateFlow("rag2310")
    val password = MutableStateFlow("123")

    private val _onLogin = MutableStateFlow(false)
    val onLogin: StateFlow<Boolean>
        get() = _onLogin

    fun login() {
        if (username.value == "rag2310" && password.value == "123") {
            _onLogin.value = true
        }
    }
}