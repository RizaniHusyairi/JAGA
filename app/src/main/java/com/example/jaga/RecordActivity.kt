package com.example.jaga

import android.Manifest
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.jaga.databinding.ActivityMapsBinding
import com.example.jaga.databinding.ActivityRecordBinding
import java.io.File

class RecordActivity : AppCompatActivity() {

    private val MICROPHONE_PERMISSION_CODE = 200
    private lateinit var binding: ActivityRecordBinding
    private lateinit var mediaRecorder: MediaRecorder


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        if (isMicrophonePresent()){
            getMicrophonePermission()
        }

        try {
            mediaRecorder = MediaRecorder()
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mediaRecorder.setOutputFile(getRecordingFilePath())
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mediaRecorder.prepare()
            mediaRecorder.start()

            Toast.makeText(this,"Recording is started", Toast.LENGTH_LONG).show()
        }catch (e: Exception){
            e.printStackTrace()
        }




    }

    private fun isMicrophonePresent(): Boolean {
        return this.packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)
    }
    private fun getMicrophonePermission(){
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.RECORD_AUDIO)
            == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,  Array<String>(3) { Manifest.permission.RECORD_AUDIO },MICROPHONE_PERMISSION_CODE)
        }
    }

    private fun getRecordingFilePath(): String {
        val contextWrapper:ContextWrapper = ContextWrapper(applicationContext)
        val musicDirectory: File? = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val file:File = File(musicDirectory,"testRecordingFile"+".mp3")
        return file.path
    }
}