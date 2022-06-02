package com.example.jaga

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.jaga.databinding.ActivityRegisBinding

class RegisActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
    }

    fun onClickRegis(view: View) {
        if (binding.noHp.text.isEmpty() || binding.nama.text.isEmpty()) {
            Toast.makeText(applicationContext,"ISI DULU SEMUANYA ANJINGG!!",Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(this, VerifyActivity::class.java)
            Log.e("nomor2",binding.noHp.text.toString())
            intent.putExtra(VerifyActivity.NUMBER_PHONE, binding.noHp.text.toString())
            startActivity(intent)
        }

    }
}