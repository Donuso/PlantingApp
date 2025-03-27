package com.example.plantingapp

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView

class FullScreenActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_full_screen)

        val ano = findViewById<SubsamplingScaleImageView>(R.id.another_full_screen)

        val imageUri = intent.getStringExtra("image_uri")
        val transitionName = intent.getStringExtra("transition_name")
        val duration = intent.getLongExtra("duration", 300L)

        ano.transitionName = transitionName
        ano.maxScale = 3f
        ano.setImage(ImageSource.uri(Uri.parse(imageUri)))

        ano.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupWindowAnimations(duration)
    }

    private fun setupWindowAnimations(duration: Long) {
        TransitionInflater.from(this).let { inflater ->
            val transition = inflater.inflateTransition(android.R.transition.move)
            transition.duration = duration
            window.enterTransition = transition
            window.returnTransition = transition
        }
    }
}