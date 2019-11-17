package com.android.artificialintelligence

import android.media.MediaPlayer
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener, MediaPlayer.OnCompletionListener {

    private lateinit var mMediaController: MediaController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        play_button.setOnClickListener(this)

        mMediaController = MediaController(this)
        var uri: Uri =
            Uri.parse("https://artificial-intelligence-learning.oss-cn-shanghai.aliyuncs.com/test_video.mp4")
        video_view.setOnCompletionListener(this)
        video_view.setMediaController(mMediaController)
        video_view.setVideoURI(uri)
    }

    override fun onClick(v: View?) {
        video_view.start()
    }

    override fun onCompletion(mp: MediaPlayer?) {

    }
}
