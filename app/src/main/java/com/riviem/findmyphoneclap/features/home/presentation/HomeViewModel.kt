package com.riviem.findmyphoneclap.features.home.presentation

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riviem.findmyphoneclap.core.data.repository.audioclassification.SettingsRepository
import com.riviem.findmyphoneclap.core.data.service.clapdetecting.AudioClassificationService
import com.riviem.findmyphoneclap.features.home.domain.usecase.HasMicrophonePermissionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val audioClassificationService: AudioClassificationService,
    private val settingsRepository: SettingsRepository,
    private val hasMicrophonePermissionUseCase: HasMicrophonePermissionUseCase
): ViewModel() {

    private val _state = MutableStateFlow<HomeViewState>(HomeViewState())
    val state: StateFlow<HomeViewState> = _state

    init {
        initSensitivity()
        initVolume()
        initService()
    }

    private fun initService() {
        viewModelScope.launch {
            val isServiceActivated = audioClassificationService.isServiceRunning()
            _state.update {
                it.copy(isServiceActivated = isServiceActivated)
            }
        }
    }

    private fun initVolume() {
        viewModelScope.launch {
            val volume = settingsRepository.getVolume()
            _state.update {
                it.copy(volume = volume)
            }
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

    fun onVolumeChange(newValue: Int) {
        viewModelScope.launch {
            settingsRepository.setVolume(newValue)
        }
        _state.update {
            it.copy(volume = newValue)
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

    fun configureService(activity: Activity) {
        viewModelScope.launch {
            val isServiceActivated = audioClassificationService.isServiceRunning()
            if(isServiceActivated) {
                stopService()
            } else {
                startService(activity = activity)
            }

        }
    }

    private suspend fun startService(activity: Activity) {
        if(!hasMicrophonePermissionUseCase.execute()) {
            _state.update {
                it.copy(shouldAskForMicrophonePermission = true)
            }
            return
        }
        audioClassificationService.startService()
        _state.update {
            it.copy(isServiceActivated = true)
        }
    }

    private suspend fun stopService() {
        audioClassificationService.stopService()
        _state.update {
            it.copy(isServiceActivated = false)
        }
    }

    fun onBypassDoNotDisturbClick() {
        viewModelScope.launch {
            settingsRepository.askForBypassDoNotDisturbPermission()
        }
    }

    fun resetShouldAskForMicrophonePermission() {
        _state.update {
            it.copy(shouldAskForMicrophonePermission = false)
        }
    }
}

data class HomeViewState(
    val sensitivity: Int = 0,
    val volume: Int = 0,
    val isServiceActivated: Boolean = false,
    val shouldAskForMicrophonePermission: Boolean = false,
)