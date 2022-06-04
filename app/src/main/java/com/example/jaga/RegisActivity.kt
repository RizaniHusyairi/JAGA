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
        if (binding.noHp.editText?.text?.isEmpty() == true || binding.nama.text.isEmpty()) {
            Toast.makeText(applicationContext,"Mohon isi semua data",Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(this, VerifyActivity::class.java)
            Log.e("nomor2", binding.noHp.editText?.text.toString())
            intent.putExtra(VerifyActivity.NUMBER_PHONE, binding.noHp.editText?.text.toString())
            intent.putExtra(VerifyActivity.NAME_USER, binding.nama.text.toString())
            startActivity(intent)
        }

    }

    fun logBtn(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)


    }
}