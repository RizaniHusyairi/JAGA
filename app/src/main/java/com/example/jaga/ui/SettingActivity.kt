package com.example.jaga.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.jaga.R
import com.example.jaga.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)


        
    }


}