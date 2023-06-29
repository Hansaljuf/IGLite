package com.example.iglite.ui.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.iglite.databinding.ActivityMainBinding
import com.example.iglite.map.MapsActivity
import com.example.iglite.ui.adapter.InstagramLiteStoryAdapter
import com.example.iglite.ui.addStory.AddStoryActivity
import com.example.iglite.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        playAnimation()

        val adapter = InstagramLiteStoryAdapter()
        var switchValue = 0

        binding?.apply {
            rvInstagramLite.adapter = adapter
            rvInstagramLite.layoutManager = LinearLayoutManager(this@MainActivity)
            rvInstagramLite.setHasFixedSize(true)

            fabAddStoryButton.setOnClickListener {
                val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
                intent.putExtra(AddStoryActivity.TOKEN, token)
                startActivity(intent)
            }
            actionLogout.setOnClickListener {
                viewModel.clearUserToken()
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

            actionMap.setOnClickListener {
                val intent = Intent(this@MainActivity, MapsActivity::class.java)
                startActivity(intent)
            }
            actionLocation.apply {
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        viewModel.userToken.observe(this@MainActivity) {
                            Log.d("MainActivity", "token: $it")
                            if (it.isNotEmpty()) {
                                token = it
                                viewModel.instagramLiteGetStories(it, 1)
                                checkStoryResult(adapter)
                            }
                        }
                    } else {
                        viewModel.userToken.observe(this@MainActivity) {
                            Log.d("MainActivity", "token: $it")
                            if (it.isNotEmpty()) {
                                token = it
                                viewModel.instagramLiteGetStories(it)
                                checkStoryResult(adapter)
                            }
                        }
                    }
                }
            }
        }

        viewModel.getUserToken()
        viewModel.userToken.observe(this) {
            Log.d("MainActivity", "token: $it")
            if (it.isNotEmpty()) {
                token = it
                viewModel.instagramLiteGetStories(it)
                checkStoryResult(adapter)
            }
        }
    }

    private fun checkStoryResult(adapter: InstagramLiteStoryAdapter) {
        viewModel.storyResult.observe(this) {
            if (it != null) {
                adapter.submitData(lifecycle, it)
            }
        }
    }

    private fun playAnimation() {
        val instagramLiteRV =
            ObjectAnimator.ofFloat(binding?.rvInstagramLite, View.ALPHA, 1f).setDuration(500)
        val instagramLiteTV =
            ObjectAnimator.ofFloat(binding?.tvInstagramLite, View.ALPHA, 1f).setDuration(500)
        val actionMap = ObjectAnimator.ofFloat(binding?.actionMap, View.ALPHA, 1f).setDuration(500)
        val actionLogout =
            ObjectAnimator.ofFloat(binding?.actionLogout, View.ALPHA, 1f).setDuration(500)
        val locationText =
            ObjectAnimator.ofFloat(binding?.switchLocationText, View.ALPHA, 1f).setDuration(500)
        val locationSwitch =
            ObjectAnimator.ofFloat(binding?.actionLocation, View.ALPHA, 1f).setDuration(500)
        val fabButton =
            ObjectAnimator.ofFloat(binding?.fabAddStoryButton, View.ALPHA, 1f).setDuration(500)

        val playTogether = AnimatorSet().apply {
            playTogether(
                instagramLiteTV, actionLogout, actionMap, locationSwitch, locationText
            )
        }

        AnimatorSet().apply {
            playSequentially(playTogether, instagramLiteRV, fabButton)
            start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}