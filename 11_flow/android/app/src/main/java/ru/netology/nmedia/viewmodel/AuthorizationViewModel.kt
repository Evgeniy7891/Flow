package ru.netology.nmedia.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nmedia.dto.Token
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class AuthorizationViewModel @Inject constructor(private val repository:  AuthRepository) : ViewModel() {

   // private val repository = AuthRepository()

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