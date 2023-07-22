package com.riviem.findmyphoneclap

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(

): ViewModel() {

    private val _state = MutableStateFlow<MainViewState>(MainViewState())
    val state: StateFlow<MainViewState> = _state

}

data class MainViewState(
    val isAdmin: Boolean = false
)