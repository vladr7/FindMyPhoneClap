package com.riviem.findmyphoneclap.core.data.models

data class ServiceSettings(
    var sensitivity: Int = 0,
    var volume: Int = 0,
    var songDuration: Long = 0,
    var isBypassDNDPermissionEnabled: Boolean = false,
)
