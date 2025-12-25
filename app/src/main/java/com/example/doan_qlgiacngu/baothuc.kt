package com.example.doan_qlgiacngu

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_STOP_ALARM = "com.example.doan_qlgiacngu.STOP_ALARM"

        // Sử dụng biến tĩnh để có thể dừng từ lần gọi sau
        private var ringtone: Ringtone? = null
        private var vibrator: Vibrator? = null
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    @SuppressLint("ServiceCast")
    override fun onReceive(context: Context, intent: Intent) {

        // --- TRƯỜNG HỢP 1: NGƯỜI DÙNG NHẤN TẮT BÁO THỨC ---
        if (intent.action == ACTION_STOP_ALARM) {
            stopAlarm() // Dừng nhạc và rung

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(1001) // Xóa thông báo

            // CHUYỂN SANG TRANG SleepDetails
            val intentSleepDetails = Intent(context, SleepDetails::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            context.startActivity(intentSleepDetails)
            return
        }

        // --- TRƯỜNG HỢP 2: BÁO THỨC BẮT ĐẦU NỔ ---
        val uriString = intent.getStringExtra("ALARM_SOUND_URI")
        val alarmMode = intent.getStringExtra("ALARM_MODE") ?: "BOTH"

        val alarmUri: Uri = if (!uriString.isNullOrEmpty()) {
            Uri.parse(uriString)
        } else {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        }

        // 1. XỬ LÝ ÂM THANH
        if (alarmMode == "SOUND" || alarmMode == "BOTH") {
            try {
                ringtone = RingtoneManager.getRingtone(context, alarmUri)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val aa = AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                    ringtone?.audioAttributes = aa
                }
                ringtone?.play()
            } catch (e: Exception) { e.printStackTrace() }
        }

        // 2. XỬ LÝ RUNG
        if (alarmMode == "VIBRATE" || alarmMode == "BOTH") {
            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            vibrator?.let {
                if (it.hasVibrator()) {
                    val pattern = longArrayOf(0, 1000, 1000)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        it.vibrate(VibrationEffect.createWaveform(pattern, 0))
                    } else {
                        @Suppress("DEPRECATION")
                        it.vibrate(pattern, 0)
                    }
                }
            }
        }

        // 3. HIỂN THỊ THÔNG BÁO
        showNotification(context)
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    private fun stopAlarm() {
        try {
            ringtone?.stop()
            vibrator?.cancel()
            ringtone = null
            vibrator = null
        } catch (e: Exception) {
            Log.e("ALARM_DEBUG", "Lỗi khi dừng báo thức: ${e.message}")
        }
    }

    private fun showNotification(context: Context) {
        val channelId = "ALARM_CHANNEL_ID"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Báo thức", NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setSound(null, null)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent gửi lệnh dừng về lại chính AlarmReceiver này
        val stopIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_STOP_ALARM
        }

        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("BÁO THỨC ĐANG KÊU")
            .setContentText("Nhấn để TẮT và xem chi tiết giấc ngủ")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setOngoing(true)
            .setContentIntent(stopPendingIntent)
            .setFullScreenIntent(stopPendingIntent, true)
            .build()

        notificationManager.notify(1001, notification)
    }
}
