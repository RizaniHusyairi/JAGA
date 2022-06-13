package com.example.jaga

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.jaga.databinding.ActivitySplashBinding
import com.example.jaga.ui.LoginActivity
import com.example.jaga.ui.MapsActivity


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var splashViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()

        supportActionBar?.hide()
        Handler(Looper.getMainLooper()).postDelayed({
            splashViewModel.getUser().observe(this) {
                if (it.id?.isNotEmpty() == true) {
                    val intent = Intent(this@SplashActivity, MapsActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()

                }
            }
        }, 3000)
    }

    private fun playAnimation() {
        val down = ObjectAnimator.ofFloat(binding.logo, View.SCALE_X, 0f).setDuration(1500)

        AnimatorSet().apply {
            playTogether(down)
        }.start()

    }

    private fun setupViewModel() {
        splashViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]
    }
}