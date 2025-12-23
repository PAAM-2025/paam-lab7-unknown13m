package upt.paam.lab7

import android.Manifest
import android.app.Notification
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyForegroundService : Service() {

    private var isRunning = false
    private var counter = 0
    private val scope = CoroutineScope(Dispatchers.Default)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        isRunning = true
        counter = 0

        val notification = buildNotification("Service started", 0)
        startForeground(1, notification)

        startCounterLoop()

        return START_STICKY
    }

    private fun startCounterLoop() {
        scope.launch {
            while (isRunning) {
                delay(1000)
                counter++
                updateNotification("Running for $counter sec", counter)
            }
        }
    }

    private fun buildNotification(msg: String, progress: Int): Notification {

        
        return NotificationCompat.Builder(this, "service_channel")
            .setContentTitle("Foreground Service")
            .setContentText(msg)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()

        /*
        // sau mai merge asa cu notificare care deschide o activitate 
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, "service_channel")
            .setContentTitle("Foreground Service")
            .setContentText(msg)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
        */
    }

    private fun updateNotification(msg: String, progress: Int) {
        val manager = NotificationManagerCompat.from(this)
        val notification = buildNotification(msg, progress)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            manager.notify(1, notification)
        }
    }

    override fun onDestroy() {
        isRunning = false
        scope.cancel()
        super.onDestroy()
    }
}
