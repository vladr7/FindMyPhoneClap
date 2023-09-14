package com.riviem.findmyphoneclap.core.data.models

import com.riviem.findmyphoneclap.R

data class ServiceSettings(
    val sensitivity: Int = 0,
    val volume: Int = 0,
    val songDuration: Long = 0,
    val isBypassDNDPermissionEnabled: Boolean = false,
    val currentSoundId: Int = R.raw.birdwhistle,
)
