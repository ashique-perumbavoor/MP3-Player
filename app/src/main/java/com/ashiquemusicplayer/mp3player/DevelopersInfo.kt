package com.ashiquemusicplayer.mp3player

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_developers_info.*

class DevelopersInfo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developers_info)

        webView.loadUrl("http://ashiquebava.online/portfolio.html")
    }
}