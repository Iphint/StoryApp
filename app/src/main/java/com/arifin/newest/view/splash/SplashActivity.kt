package com.arifin.newest.view.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.arifin.newest.R
import com.arifin.newest.databinding.ActivitySplashBinding
import com.arifin.newest.view.main.MainActivity
import kotlinx.coroutines.delay

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startBounceAnimation()
    }

    private fun startBounceAnimation() {
        val imageView = binding.imageSplashScreen
        val bounceDuration = 1000L

        fun bounce() {
            imageView.animate()
                .scaleX(1.1f)
                .scaleY(1.1f)
                .setDuration(bounceDuration)
                .withEndAction {
                    imageView.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(bounceDuration)
                        .withEndAction {
                            bounce()
                        }
                }
        }
        bounce()
        lifecycleScope.launchWhenCreated {
            delay(4000)
            imageView.clearAnimation()
            determiningDirection()
        }
    }

    private fun determiningDirection() {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
        finish()
    }
}