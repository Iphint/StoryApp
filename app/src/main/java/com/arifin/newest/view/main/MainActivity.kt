package com.arifin.newest.view.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.arifin.newest.databinding.ActivityMainBinding
import com.arifin.newest.paging.LoadingStateAdapter
import com.arifin.newest.view.ViewModelFactory
import com.arifin.newest.view.login.LoginActivity
import com.arifin.newest.view.maps.MapsActivity
import com.arifin.newest.view.tambah.TambahActivity
import com.arifin.newest.view.wellcome.WelcomeActivity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private val storiesAdapter = StoriesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = storiesAdapter

        binding.addStories.setOnClickListener {
            val i = Intent(this, TambahActivity::class.java)
            startActivity(i)
        }

        binding.maps.setOnClickListener {
            val i = Intent(this, MapsActivity::class.java)
            startActivity(i)
        }

        Log.d("MainActivity", "Fetching stories...")

        viewModel.story.observe(this@MainActivity) { pagingData ->
            storiesAdapter.submitData(lifecycle, pagingData)
            Log.d("periksa", "Observation triggered")
            binding.progressBar.visibility = View.INVISIBLE
        }

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        binding.recyclerView.adapter = storiesAdapter.withLoadStateHeaderAndFooter(
            header = LoadingStateAdapter { storiesAdapter.retry() },
            footer = LoadingStateAdapter { storiesAdapter.retry() }
        )

        storiesAdapter.addLoadStateListener { loadState ->
            binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
            val errorState = loadState.source.refresh as? LoadState.Error
            errorState?.let {
                Toast.makeText(this, it.error.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }

        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.logoutButton.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Konfirmasi Logout")
            alertDialogBuilder.setMessage("Anda yakin ingin logout?")
            alertDialogBuilder.setPositiveButton("Ya") { _, _ ->
                viewModel.logout()
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }

            alertDialogBuilder.setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val logout = ObjectAnimator.ofFloat(binding.logoutButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(logout)
            startDelay = 100
        }.start()
    }
}