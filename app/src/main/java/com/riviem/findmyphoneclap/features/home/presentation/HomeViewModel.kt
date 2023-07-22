package com.riviem.findmyphoneclap.features.home.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(

): ViewModel() {

    private val _state = MutableStateFlow<HomeViewState>(HomeViewState())
    val state: StateFlow<HomeViewState> = _state

    fun onSensitivityChange(newValue: Int) {
        _state.update {
            it.copy(sensitivity = newValue)
        }
    }
}

data class HomeViewState(
    val sensitivity: Int = 50
)