package com.riviem.findmyphoneclap.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riviem.findmyphoneclap.core.data.repository.audioclassification.SettingsRepository
import com.riviem.findmyphoneclap.core.data.service.clapdetecting.AudioClassificationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val audioClassificationService: AudioClassificationService,
    private val settingsRepository: SettingsRepository
): ViewModel() {

    private val _state = MutableStateFlow<HomeViewState>(HomeViewState())
    val state: StateFlow<HomeViewState> = _state

    init {
        initSensitivity()
        checkAndInitIsServiceActivated()
    }

    private fun checkAndInitIsServiceActivated() {
        viewModelScope.launch {
            val isServiceActivated = settingsRepository.getServiceActivated()
            _state.update {
                it.copy(isServiceActivated = isServiceActivated)
            }
            if(isServiceActivated) audioClassificationService.startService()
        }
    }

    private fun initSensitivity() {
        viewModelScope.launch {
            val sensitivity = settingsRepository.getSensitivity()
            _state.update {
                it.copy(sensitivity = sensitivity)
            }
        }
    }

    fun onSensitivityChange(newValue: Int) {
        viewModelScope.launch {
            settingsRepository.setSensitivity(newValue)
        }
        _state.update {
            it.copy(sensitivity = newValue)
        }
    }

    fun configureService() {
        viewModelScope.launch {
            val isServiceActivated = settingsRepository.getServiceActivated()
            if(isServiceActivated) {
                audioClassificationService.stopService()
                settingsRepository.setServiceActivated(false)
                _state.update {
                    it.copy(isServiceActivated = false)
                }
            } else {
                audioClassificationService.startService()
                settingsRepository.setServiceActivated(true)
                _state.update {
                    it.copy(isServiceActivated = true)
                }
            }

        }
    }
}

data class HomeViewState(
    val sensitivity: Int = 0,
    val isServiceActivated: Boolean = false
)