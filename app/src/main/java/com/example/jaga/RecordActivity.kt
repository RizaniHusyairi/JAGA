package com.example.jaga

import android.Manifest
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.jaga.databinding.ActivityMapsBinding
import com.example.jaga.databinding.ActivityRecordBinding
import java.io.File

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
            mediaRecorder.setOutputFile(getRecordingFilePath())
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
    }

    private fun getRecordingFilePath(): String {
        val contextWrapper:ContextWrapper = ContextWrapper(applicationContext)
        val musicDirectory: File? = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val file:File = File(musicDirectory,"testRecordingFile"+".mp3")
        return file.path
    }

}