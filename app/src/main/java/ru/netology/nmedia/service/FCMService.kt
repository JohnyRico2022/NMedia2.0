package ru.netology.nmedia.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import javax.inject.Inject
import kotlin.random.Random


class FCMService : FirebaseMessagingService() {
 //   private val action = "action"
    private val content = "content"
    private val channelId = "remote"
    private val gson = Gson()

@Inject
    lateinit var auth: AppAuth

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {

        val myId = auth.authStateFlow.value.id
        val recipientId = message.data["recipientId"]?.toLong()


        when (recipientId) {
            myId, null -> handleLike(gson.fromJson(message.data[content], PushMessage::class.java))
            else -> auth.sendPushToken()
        }

    }

    override fun onNewToken(token: String) {
        auth.sendPushToken(token)
    }

    @SuppressLint("StringFormatInvalid")
    private fun handleLike(content: PushMessage) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentText(
                getString(
                    R.string.notification_user_liked,
                    content.recipientId.toString(),
                    content.content
                )
            )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notify(notification)
    }

    private fun notify(notification: Notification) {
        if (
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(this)
                .notify(Random.nextInt(100_000), notification)
        }
    }
}

data class PushMessage(
    val recipientId: Long?,
    val content: String
)