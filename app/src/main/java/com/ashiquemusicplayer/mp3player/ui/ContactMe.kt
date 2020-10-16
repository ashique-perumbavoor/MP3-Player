package com.ashiquemusicplayer.mp3player.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ashiquemusicplayer.mp3player.R
import kotlinx.android.synthetic.main.activity_contact_me.*

class ContactMe : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_me)
        // Loading the url of the contact form
        webView.loadUrl("http://ashiquebava.online/contact.html")
    }
}