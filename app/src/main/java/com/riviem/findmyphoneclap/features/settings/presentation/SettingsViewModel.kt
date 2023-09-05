package com.riviem.findmyphoneclap.features.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riviem.findmyphoneclap.features.home.data.models.BypassDNDState
import com.riviem.findmyphoneclap.features.home.domain.usecase.AskForBypassDNDPermissionUseCase
import com.riviem.findmyphoneclap.features.home.domain.usecase.GetSongDurationUseCase
import com.riviem.findmyphoneclap.features.home.domain.usecase.HasBypassDNDPermissionUseCase
import com.riviem.findmyphoneclap.features.home.domain.usecase.SetBypassDNDPermissionUseCase
import com.riviem.findmyphoneclap.features.home.domain.usecase.SetSongDurationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val hasBypassDNDPermissionUseCase: HasBypassDNDPermissionUseCase,
    private val setBypassDNDPermissionUseCase: SetBypassDNDPermissionUseCase,
    private val setSongDurationUseCase: SetSongDurationUseCase,
    private val getSongDurationUseCase: GetSongDurationUseCase,
    private val askForBypassDNDPermissionUseCase: AskForBypassDNDPermissionUseCase,
    ): ViewModel() {

    private val _state = MutableStateFlow<SettingsViewState>(SettingsViewState())
    val state: StateFlow<SettingsViewState> = _state

    init {
        initBypassDNDState()
        initSongDuration()
    }

    private fun initSongDuration() {
        viewModelScope.launch {
            val songDuration = getSongDurationUseCase.execute()
            _state.update {
                it.copy(songDuration = songDuration)
            }
        }
    }

    private fun initBypassDNDState() {
        viewModelScope.launch {
            when(hasBypassDNDPermissionUseCase.execute()) {
                BypassDNDState.ENABLED -> {
                    _state.update {
                        it.copy(isBypassDNDActive = true)
                    }
                }
                else -> {
                    _state.update {
                        it.copy(isBypassDNDActive = false)
                    }
                }
            }
        }
    }

    fun onBypassDoNotDisturbClick() {
        viewModelScope.launch {
            when(hasBypassDNDPermissionUseCase.execute()) {
                BypassDNDState.ENABLED -> {
                    setBypassDNDPermissionUseCase.execute(false)
                    _state.update {
                        it.copy(isBypassDNDActive = false)
                    }
                }
                BypassDNDState.DISABLED_FROM_LOCAL_STORAGE -> {
                    setBypassDNDPermissionUseCase.execute(true)
                    _state.update {
                        it.copy(isBypassDNDActive = true)
                    }
                }
                BypassDNDState.DISABLED_FROM_SYSTEM -> {
                    askForBypassDNDPermissionUseCase.execute()
                }
            }
        }
    }

    fun onSongDurationChange(newValue: Int) {
        viewModelScope.launch {
            setSongDurationUseCase.execute(newValue)
        }
        _state.update {
            it.copy(songDuration = newValue)
        }
    }
}


data class SettingsViewState(
    val songDuration: Int = 0,
    val shouldAskForMicrophonePermission: Boolean = false,
    val isBypassDNDActive: Boolean = false,
    val pauseDuration: Int = 0
)