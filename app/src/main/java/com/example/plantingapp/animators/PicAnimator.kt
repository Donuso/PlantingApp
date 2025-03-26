package com.example.plantingapp.animators

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import com.example.plantingapp.FullScreenActivity

class PicAnimator(
    private val context: Context,
    private val uriString: String,
    private val transitionName: String,
    private val animationDuration: Long = 300 // 默认动画时长 300ms
) {
    private lateinit var imageView: ImageView

    fun attachTo(imageView: ImageView) {
        this.imageView = imageView
        setupClickListener()
    }

    private fun setupClickListener() {
        imageView.setOnClickListener {
            val intent = Intent(context, FullScreenActivity::class.java).apply {
                putExtra("image_uri", uriString)
                putExtra("transition_name", transitionName)
                putExtra("animation_duration", animationDuration)
            }

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                context as Activity,
                imageView,
                transitionName
            )

            context.startActivity(intent, options.toBundle())
        }
    }
}