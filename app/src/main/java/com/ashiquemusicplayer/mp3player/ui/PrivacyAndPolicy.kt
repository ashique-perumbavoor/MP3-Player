package com.ashiquemusicplayer.mp3player.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ashiquemusicplayer.mp3player.R
import kotlinx.android.synthetic.main.activity_privacy_and_policy.*

class PrivacyAndPolicy : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_and_policy)

        // Loading the url of the privacy and policy of the application
        webView.loadUrl("http://ashiquebava.online/MP3PlayerPrivacyandPolicy.html")
    }
}