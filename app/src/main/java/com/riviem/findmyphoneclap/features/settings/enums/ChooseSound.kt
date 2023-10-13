package com.riviem.findmyphoneclap.features.settings.enums

import com.riviem.findmyphoneclap.R

enum class ChooseSound(val id: Int, val title: String, val index: Int) {
    SOUND_1(id = R.raw.ringtone2, "Classic", 1),
    SOUND_2(id = R.raw.birdwhistle, "Bird whistle", 2),
    SOUND_3(id = R.raw.ringtone3, "Direct Wave", 3),
    SOUND_4(id = R.raw.ringtonefaraway, "Far Away", 4),
    SOUND_5(id = R.raw.ringtonebells, "Bells", 5);

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