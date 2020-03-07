package com.bubelov.coins.util

import android.widget.SeekBar

abstract class OnSeekBarChangeAdapter : SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        // Nothing to do here
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        // Nothing to do here
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        // Nothing to do here
    }
}