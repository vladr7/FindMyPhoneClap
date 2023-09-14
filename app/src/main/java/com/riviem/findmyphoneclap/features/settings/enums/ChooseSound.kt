package com.riviem.findmyphoneclap.features.settings.enums

import com.riviem.findmyphoneclap.R

enum class ChooseSound(val id: Int, val title: String, val index: Int) {
    SOUND_1(id = R.raw.birdwhistle, "Bird whistle", 1),
    SOUND_2(id = R.raw.ringtone1, "Simple", 2),
    SOUND_3(id = R.raw.ringtone2, "Classic", 3);

    companion object {
        fun findByIndex(index: Int): ChooseSound {
            for (sound in values()) {
                if (sound.index == index) {
                    return sound
                }
            }
            return SOUND_1
        }
        fun findById(id: Int): ChooseSound {
            for (sound in values()) {
                if (sound.id == id) {
                    return sound
                }
            }
            return SOUND_1
        }
    }
}