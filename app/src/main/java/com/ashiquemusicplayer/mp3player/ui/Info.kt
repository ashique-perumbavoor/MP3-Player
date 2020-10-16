package com.ashiquemusicplayer.mp3player.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ashiquemusicplayer.mp3player.R
import kotlinx.android.synthetic.main.activity_info.*

class Info : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        // Action listener for showing privacy and policy
        policy.setOnClickListener {
            startActivity(Intent(this, PrivacyAndPolicy::class.java))
        }

        // Action listener for showing developers information
        developerInfo.setOnClickListener {
            startActivity(Intent(this, DevelopersInfo::class.java))
        }

        // Action listener for showing contact form
        contact.setOnClickListener {
            startActivity(Intent(this, ContactMe::class.java))
        }
    }
}