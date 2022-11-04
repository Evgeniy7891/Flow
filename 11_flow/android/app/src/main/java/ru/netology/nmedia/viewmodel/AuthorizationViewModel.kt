package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.dto.Token
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.AuthRepository

class AuthorizationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository()

    val data = MutableLiveData<Token>()

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState : LiveData<FeedModelState>
    get() = _dataState

    fun updateUser(login:String, password: String) {
        viewModelScope.launch {
            try {
                data.value = repository.updateUser(login, password)
                _dataState.value = FeedModelState()
            } catch (e:Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }
}