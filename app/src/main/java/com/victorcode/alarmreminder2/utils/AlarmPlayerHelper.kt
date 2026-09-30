package com.victorcode.alarmreminder2.utils

import android.content.Context
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

// 📦 Versión 0.0.3
// 📄 Archivo: utils/AlarmPlayerHelper.kt

class AlarmPlayerManager {
    companion object {
        private var mediaPlayer: MediaPlayer? = null

        fun playAlarm(context: Context, customUriString: String? = null) {
        if (mediaPlayer?.isPlaying == true) return

        try {
            val alarmUri = if (customUriString != null) {
                android.net.Uri.parse(customUriString)
            } else {
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            }
            
            mediaPlayer = MediaPlayer.create(context, alarmUri)?.apply {
                isLooping = true
                start()
            }

            vibrate(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun vibrate(context: Context) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (vibrator.hasVibrator()) {
            val pattern = longArrayOf(0, 500, 500)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern, 0)
            }
        }
    }

    fun stopAlarm(context: Context) {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
            mediaPlayer = null

            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            vibrator.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    }
}
