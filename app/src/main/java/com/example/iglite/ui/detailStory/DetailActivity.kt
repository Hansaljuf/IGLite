package com.example.iglite.ui.detailStory

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.example.iglite.data.local.model.Story
import com.example.iglite.databinding.ActivityDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        playAnimation()

        val story = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_STORY, Story::class.java)
        } else {
            intent.getParcelableExtra(EXTRA_STORY)
        }

        binding?.apply {
            ivDetailPhoto.load(story?.photoUrl)
            tvDetailName.text = story?.name
            tvDetailDescription.text = story?.description
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding?.ivDetailPhoto, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 4000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val detailPhoto =
            ObjectAnimator.ofFloat(binding?.ivDetailPhoto, View.ALPHA, 1f).setDuration(500)
        val detailUsername =
            ObjectAnimator.ofFloat(binding?.tvDetailName, View.ALPHA, 1f).setDuration(500)
        val detailDescription =
            ObjectAnimator.ofFloat(binding?.tvDetailDescription, View.ALPHA, 1f).setDuration(500)
        val playTogether =
            AnimatorSet().apply { playTogether(detailPhoto, detailUsername, detailDescription) }
        playTogether.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}