package com.ashiquemusicplayer.mp3player

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_info.*

class Info : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        policy.setOnClickListener {
            startActivity(Intent(this, PrivacyAndPolicy::class.java))
        }

        developerInfo.setOnClickListener {
            startActivity(Intent(this, DevelopersInfo::class.java))
        }

        contact.setOnClickListener {
            startActivity(Intent(this, ContactMe::class.java))
        }
    }
}