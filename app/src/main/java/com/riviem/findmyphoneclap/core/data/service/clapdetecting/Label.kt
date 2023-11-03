package com.riviem.findmyphoneclap.core.data.service.clapdetecting

enum class Label(val stringValue: String) {
    CLAPPING("Clapping"),
    HANDS("Hands"),
    WHISTLING("Whistling"),
    WHISTLE("Whistle"),
    FINGER_SNAPPING("Finger snapping"),
    SILENCE("Silence");
    companion object {
        fun fromString(stringValue: String): Label {
            return when (stringValue) {
                CLAPPING.stringValue -> CLAPPING
                HANDS.stringValue -> HANDS
                WHISTLING.stringValue -> WHISTLING
                WHISTLE.stringValue -> WHISTLE
                FINGER_SNAPPING.stringValue -> FINGER_SNAPPING
                SILENCE.stringValue -> SILENCE
                else -> SILENCE
            }
        }
    }
}