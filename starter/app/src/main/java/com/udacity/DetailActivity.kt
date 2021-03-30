package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    private lateinit var title:String
    private lateinit var status:String
    private var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        getIntentExtras()
        var notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelNotification(id)
        fileNameText.text = title
        statusText.text = status
        detailNavButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

     private fun getIntentExtras() {
         if (intent != null){
             title = intent.getStringExtra("download").toString()
             status = intent.getStringExtra("status").toString()
             id = intent.getIntExtra("id", 0)
         }else Toast.makeText(this, "Intent empty", Toast.LENGTH_SHORT).show()
    }

}
