package com.ashiquemusicplayer.mp3player

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_privacy_and_policy.*

class PrivacyAndPolicy : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_and_policy)

        webView.loadUrl("http://ashiquebava.online/MP3PlayerPrivacyandPolicy.html")
    }
}