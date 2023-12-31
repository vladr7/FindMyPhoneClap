package com.riviem.findmyphoneclap.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riviem.findmyphoneclap.features.home.data.models.BypassDNDState
import com.riviem.findmyphoneclap.features.home.domain.usecase.AskForBypassDNDPermissionUseCase
import com.riviem.findmyphoneclap.features.home.domain.usecase.GetSensitivityUseCase
import com.riviem.findmyphoneclap.features.home.domain.usecase.GetSongDurationUseCase
import com.riviem.findmyphoneclap.features.home.domain.usecase.GetVolumeUseCase
import com.riviem.findmyphoneclap.features.home.domain.usecase.HasBypassDNDPermissionUseCase
import com.riviem.findmyphoneclap.features.home.domain.usecase.HasMicrophonePermissionUseCase
import com.riviem.findmyphoneclap.features.home.domain.usecase.IsServiceRunningUseCase
import com.riviem.findmyphoneclap.features.home.domain.usecase.PauseServiceForDurationUseCase
import com.riviem.findmyphoneclap.features.home.domain.usecase.SetBypassDNDPermissionUseCase
import com.riviem.findmyphoneclap.features.home.domain.usecase.SetSensitivityUseCase
import com.riviem.findmyphoneclap.features.home.domain.usecase.SetSongDurationUseCase
import com.riviem.findmyphoneclap.features.home.domain.usecase.SetVolumeUseCase
import com.riviem.findmyphoneclap.features.home.domain.usecase.StartServiceUseCase
import com.riviem.findmyphoneclap.features.home.domain.usecase.StopServiceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val hasMicrophonePermissionUseCase: HasMicrophonePermissionUseCase,
    private val isServiceRunningUseCase: IsServiceRunningUseCase,
    private val getVolumeUseCase: GetVolumeUseCase,
    private val getSensitivityUseCase: GetSensitivityUseCase,
    private val setVolumeUseCase: SetVolumeUseCase,
    private val setSensitivityUseCase: SetSensitivityUseCase,
    private val startServiceUseCase: StartServiceUseCase,
    private val stopServiceUseCase: StopServiceUseCase,
    private val pauseServiceForDurationUseCase: PauseServiceForDurationUseCase
): ViewModel() {

    private val _state = MutableStateFlow<HomeViewState>(HomeViewState())
    val state: StateFlow<HomeViewState> = _state

    init {
        initSensitivity()
        initVolume()
        initServiceState()
    }

    private fun initServiceState() {
        viewModelScope.launch {
            val isServiceActivated = isServiceRunningUseCase.execute()
            _state.update {
                it.copy(isServiceActivated = isServiceActivated)
            }
        }
    }

    private fun initVolume() {
        viewModelScope.launch {
            val volume = getVolumeUseCase.execute()
            _state.update {
                it.copy(volume = volume)
            }
        }
    }

    private fun initSensitivity() {
        viewModelScope.launch {
            val sensitivity = getSensitivityUseCase.execute()
            _state.update {
                it.copy(sensitivity = sensitivity)
            }
        }
    }

    fun onVolumeChange(newValue: Int) {
        viewModelScope.launch {
            setVolumeUseCase.execute(newValue)
        }
        _state.update {
            it.copy(volume = newValue)
        }
    }

    fun onSensitivityChange(newValue: Int) {
        viewModelScope.launch {
            setSensitivityUseCase.execute(newValue)
        }
        _state.update {
            it.copy(sensitivity = newValue)
        }
    }

    fun configureService() {
        viewModelScope.launch {
            val isServiceActivated = isServiceRunningUseCase.execute()
            if(isServiceActivated) {
                stopService()
            } else {
                startService()
            }

        }
    }

    private suspend fun startService() {
        if(!hasMicrophonePermissionUseCase.execute()) {
            _state.update {
                it.copy(shouldAskForMicrophonePermission = true)
            }
            return
        }
        startServiceUseCase.execute()
        _state.update {
            it.copy(isServiceActivated = true)
        }
    }

    private fun stopService() {
        stopServiceUseCase.execute()
        _state.update {
            it.copy(isServiceActivated = false)
        }
    }

    fun resetShouldAskForMicrophonePermission() {
        _state.update {
            it.copy(shouldAskForMicrophonePermission = false)
        }
    }

    fun onPauseDurationChange(newValue: Int) {
        _state.update {
            it.copy(pauseDuration = newValue)
        }
        viewModelScope.launch {
            pauseServiceForDurationUseCase.execute(newValue)
        }
    }
}

data class HomeViewState(
    val sensitivity: Int = 0,
    val volume: Int = 0,
    val isServiceActivated: Boolean = false,
    val shouldAskForMicrophonePermission: Boolean = false,
    val pauseDuration: Int = 0
)