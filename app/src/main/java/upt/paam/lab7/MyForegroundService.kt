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
        // TODO 2: Create a NotificationCompat.Builder inside your service
        //   - Use "service_channel" as the channelId
        //   - Set a title, text, and small icon
        //   - Mark the notification as ongoing (persistent)`
        return TODO("Provide the return value")

        // TODO 3: Create a different notification with NotificationCompat.Builder inside your service
        // that opens a certain screen when you press on it. Comment one implementation when you're done
    }
    private fun updateNotification(msg: String, progress: Int) {
        val manager = NotificationManagerCompat.from(this)
        val notification = buildNotification(msg, progress)

        // Runtime permission check for Android 13+
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
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
