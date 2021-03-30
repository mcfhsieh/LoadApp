package com.udacity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class CheckDownloadStatusReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val download = intent.getStringExtra("download")
        val status = intent.getStringExtra("status")
        val detailIntent =Intent(context, DetailActivity::class.java)
            .putExtra("download", download)
            .putExtra("status", status)

        context.startActivity(detailIntent)
    }
}
