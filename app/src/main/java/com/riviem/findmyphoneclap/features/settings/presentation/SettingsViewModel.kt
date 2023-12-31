package com.riviem.findmyphoneclap.features.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riviem.findmyphoneclap.core.data.service.clapdetecting.Label
import com.riviem.findmyphoneclap.features.home.data.models.BypassDNDState
import com.riviem.findmyphoneclap.features.home.domain.usecase.AskForBypassDNDPermissionUseCase
import com.riviem.findmyphoneclap.features.home.domain.usecase.ClearLabelsUseCase
import com.riviem.findmyphoneclap.features.home.domain.usecase.GetCurrentSoundUseCase
import com.riviem.findmyphoneclap.features.home.domain.usecase.GetLabelsUseCase
import com.riviem.findmyphoneclap.features.home.domain.usecase.GetSongDurationUseCase
import com.riviem.findmyphoneclap.features.home.domain.usecase.HasBypassDNDPermissionUseCase
import com.riviem.findmyphoneclap.features.home.domain.usecase.SetBypassDNDPermissionUseCase
import com.riviem.findmyphoneclap.features.home.domain.usecase.SetCurrentSoundUseCase
import com.riviem.findmyphoneclap.features.home.domain.usecase.SetLabelsUseCase
import com.riviem.findmyphoneclap.features.home.domain.usecase.SetSongDurationUseCase
import com.riviem.findmyphoneclap.features.settings.enums.ChooseSound
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
    private val getCurrentSoundUseCase: GetCurrentSoundUseCase,
    private val setCurrentSoundUseCase: SetCurrentSoundUseCase,
    private val getLabelsUseCase: GetLabelsUseCase,
    private val setLabelsUseCase: SetLabelsUseCase,
    private val clearLabelsUseCase: ClearLabelsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<SettingsViewState>(SettingsViewState())
    val state: StateFlow<SettingsViewState> = _state

    init {
        initBypassDNDState()
        initSongDuration()
        initCurrentSound()
        initLabels()
    }

    private fun initLabels() {
        viewModelScope.launch {
            val labels = getLabelsUseCase.execute()
            val whistleLabels = listOf<Label>(
                Label.WHISTLE,
                Label.WHISTLING,
            )
            _state.update {
                it.copy(
                    isWhistleActive = labels.containsAll(whistleLabels),
                    isClappingActive = labels.contains(Label.CLAPPING)
                )
            }
        }
    }

    private fun initCurrentSound() {
        viewModelScope.launch {
            val currentSoundId = getCurrentSoundUseCase.execute()
            _state.update {
                it.copy(currentSound = ChooseSound.findById(currentSoundId))
            }
        }
    }

    fun onCurrentSoundChange(newValue: Int) {
        val currentSound = ChooseSound.findByIndex(newValue)
        viewModelScope.launch {
            setCurrentSoundUseCase.execute(currentSound.id)
        }
        _state.update {
            it.copy(currentSound = currentSound)
        }
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
            when (hasBypassDNDPermissionUseCase.execute()) {
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
            when (hasBypassDNDPermissionUseCase.execute()) {
                BypassDNDState.ENABLED -> {
                    setBypassDNDPermissionUseCase.execute(false)
                    _state.update {
                        it.copy(isBypassDNDActive = false)
                    }
                }

                BypassDNDState.DISABLED_FROM_LOCAL_STORAGE -> {
                    setBypassDNDPermissionUseCase.execute(true)
                    _state.update {
                        it.copy(
                            isBypassDNDActive = true,
                            showBypassDNDToast = true
                        )
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

    fun onBypassDNDToastShown() {
        _state.update {
            it.copy(showBypassDNDToast = false)
        }
    }

    fun onWhistleClick(checked: Boolean) {
        viewModelScope.launch {
            val whistleLabels = setOf<Label>(
                Label.WHISTLE,
                Label.WHISTLING,
            )
            if(checked) {
                setLabelsUseCase.execute(whistleLabels)
            } else {
                if(!state.value.isClappingActive) {
                    setAtLeastOneSoundTypeMustBeActiveToastShown(true)
                    return@launch
                }
                clearLabelsUseCase.execute(whistleLabels)
            }
            _state.update {
                it.copy(isWhistleActive = checked)
            }
        }
    }

    fun onClappingClick(checked: Boolean) {
        viewModelScope.launch {
            val clappingLabel = setOf<Label>(
                Label.CLAPPING,
            )
            if(checked) {
                setLabelsUseCase.execute(clappingLabel)
            } else {
                if(!state.value.isWhistleActive) {
                   setAtLeastOneSoundTypeMustBeActiveToastShown(true)
                    return@launch
                }
                clearLabelsUseCase.execute(clappingLabel)
            }
            _state.update {
                it.copy(isClappingActive = checked)
            }
        }
    }

    fun setAtLeastOneSoundTypeMustBeActiveToastShown(show: Boolean) {
        _state.update {
            it.copy(showAtLeastOneSoundTypeMustBeActiveToast = show)
        }
    }
}


data class SettingsViewState(
    val songDuration: Int = 0,
    val shouldAskForMicrophonePermission: Boolean = false,
    val isBypassDNDActive: Boolean = false,
    val pauseDuration: Int = 0,
    val showBypassDNDToast: Boolean = false,
    val currentSound: ChooseSound = ChooseSound.SOUND_1,
    val isWhistleActive: Boolean = false,
    val isClappingActive: Boolean = false,
    val showAtLeastOneSoundTypeMustBeActiveToast: Boolean = false,
)