package com.example.jaga.ui

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TimePicker
import com.example.jaga.R
import com.example.jaga.databinding.ActivityQuisionerBinding
import com.google.firebase.database.collection.LLRBNode
import com.squareup.okhttp.internal.http.HttpDate.format
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class QuisionerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuisionerBinding

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuisionerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var t1Hour: Int? = 0
        var t1Minute: Int? = 0


        binding.btnBack.setOnClickListener {
            startActivity(Intent(this,MapsActivity::class.java))
            finish()
        }

        binding.selectTime.setOnClickListener {
            val timePickerDialog: TimePickerDialog = TimePickerDialog(this,
                { view, hourOfDay, minute ->
                    t1Hour = hourOfDay
                    t1Minute = minute

                    val calendar = Calendar.getInstance()

                    calendar.set(0, 0, 0, t1Hour!!, t1Minute!!)
                    val simpleDate = android.text.format.DateFormat.format("hh:mm aa", calendar)

                    binding.selectTime.text = simpleDate
                }, 12, 0, false
            )
            timePickerDialog.updateTime(t1Hour!!, t1Minute!!)
            timePickerDialog.show()
        }

    }
}