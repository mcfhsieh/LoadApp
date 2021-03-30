package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.net.URL


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var url: String = ""
    private lateinit var title: String
    private lateinit var description: String


    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var cursor: Cursor

    companion object {
        private const val UDACITY_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GLIDE_URL =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val RETROFIT_URL =
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"
        private const val CHANNEL_ID = "channelId"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        downloadSelectionButtons.setOnCheckedChangeListener { _, checkedId ->
            url = when (checkedId) {
                R.id.glide_button -> GLIDE_URL
                R.id.udacity_button -> UDACITY_URL
                else -> RETROFIT_URL
            }
        }
        createChannel(
            getString(
                R.string.download_notification_channel_id
            ),
            getString(R.string.download_channel)
        )
        custom_button.setOnClickListener {
            if (url.isNullOrEmpty()) {
                Toast.makeText(this, "please select the file to download", Toast.LENGTH_SHORT).show()
            } else download(url)
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(false)
            }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description =
                getString(R.string.download_notification_channel_description)

            val notificationManager = this.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadID) {
                notificationManager.sendNotification(description, title, context)
            }
        }
    }

    private fun download(url: String) {

        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager

        title = when (url) {
            GLIDE_URL -> this.getString(R.string.Glide)
            UDACITY_URL -> this.getString(R.string.Udacity)
            RETROFIT_URL -> this.getString(R.string.Retrofit)
            else -> "empty url"
        }

        description = when (url) {
            GLIDE_URL -> "Glide repo downloaded"
            UDACITY_URL -> "The Project 3 repo is downloaded"
            RETROFIT_URL -> "Retrofit repo downloaded"
            else -> "empty url"
        }

        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(title)
                .setDescription(description)
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setDestinationInExternalFilesDir(
                    this,
                    Environment.DIRECTORY_DOWNLOADS,
                    "test download"
                )

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        var tempID = downloadManager.enqueue(request)

        runBlocking {
            delay(3000)
            downloadID = tempID
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}
