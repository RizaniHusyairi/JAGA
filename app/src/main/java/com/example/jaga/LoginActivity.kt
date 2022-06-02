package com.example.jaga

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.example.jaga.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        supportActionBar?.hide()
    }

    fun onClickLogin(v : View){
        val iLogin = Intent(this,MapsActivity::class.java)
        startActivity(iLogin)
    }

    fun regBtn(view: View) {
        val regis = Intent(this,RegisActivity::class.java)
        startActivity(regis)
    }


}