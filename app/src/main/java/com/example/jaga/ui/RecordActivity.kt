package com.example.jaga.ui

import android.content.ContextWrapper
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.jaga.databinding.ActivityRecordBinding
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RecordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecordBinding
    private lateinit var mediaRecorder: MediaRecorder


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()


        try {
            mediaRecorder = MediaRecorder()
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mediaRecorder.setOutputFile(getRecordingFilePath())
            }
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mediaRecorder.prepare()
            mediaRecorder.start()

        }catch (e: Exception){
            e.printStackTrace()
        }




    }
    fun btnStopPressed(v:View){
        mediaRecorder.stop()
        mediaRecorder.release()
//        mediaRecorder = null


        binding.textRecord.text = "Stop Merekam Suara"
        val backActiv = Intent(this, MapsActivity::class.java)
        backActiv.putExtra(MapsActivity.SUCCESS_RECORD, "Rekaman Suara Berhasil Disimpan")

        startActivity(backActiv)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getRecordingFilePath(): String {
        val d = LocalDateTime.now()
        val s = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        val formatted = d.format(s)
        val contextWrapper:ContextWrapper = ContextWrapper(applicationContext)
        val musicDirectory: File? = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val file:File = File(musicDirectory, "case_$formatted.mp3")
        return file.path
    }

}