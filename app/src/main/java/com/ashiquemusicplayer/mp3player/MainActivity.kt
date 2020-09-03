package com.ashiquemusicplayer.mp3player

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mp: MediaPlayer
    private var totalTime: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mp = MediaPlayer.create(this, R.raw.sample)
        mp.isLooping = true

        playPauseButton.setOnClickListener {
            if (mp.isPlaying) {
                mp.pause()
                playPauseButton.setBackgroundResource(R.drawable.play)
            } else {
                mp.start()
                playPauseButton.setBackgroundResource(R.drawable.pause)
            }
        }
    }
}