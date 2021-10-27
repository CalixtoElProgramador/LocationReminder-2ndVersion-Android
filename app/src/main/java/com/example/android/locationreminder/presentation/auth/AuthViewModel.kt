package com.example.android.locationreminder.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AuthViewModel: ViewModel() {

    private val _navigationToLogin = MutableLiveData<Boolean>()
    val navigationToLogin: LiveData<Boolean>
        get() = _navigationToLogin

    init {
        _navigationToLogin.value = false
    }

    fun onLoginClick() {
        _navigationToLogin.value = true
    }

    fun onListRemindersNavigated() {
        _navigationToLogin.value = false
    }

}