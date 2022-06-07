package com.example.jaga.ui

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContextWrapper
import android.content.Intent
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.jaga.databinding.ActivityRecordBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class   RecordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecordBinding
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var mStorage: StorageReference

    private lateinit var mProgress: ProgressDialog

    private var fileName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        mStorage = FirebaseStorage.getInstance().reference

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fileName = getRecordingFilePath()
        }

        mProgress = ProgressDialog(this)



        try {
            mediaRecorder = MediaRecorder()
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mediaRecorder.setOutputFile(fileName)
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

        uploadAudio()
        binding.textRecord.text = "Stop Merekam Suara"
        val backActiv = Intent(this, MapsActivity::class.java)
        backActiv.putExtra(MapsActivity.SUCCESS_RECORD, "Rekaman Suara Berhasil Disimpan")

        startActivity(backActiv)
    }

    @SuppressLint("NewApi")
    private fun uploadAudio() {
        mProgress.setMessage("Uploading Audio..")
        mProgress.show()
        val d = LocalDateTime.now()


        val s = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        val formatted = d.format(s)
        val filePath = mStorage.child("Audio").child("case_$formatted.mp3")
        val uri:Uri = Uri.fromFile(File(fileName))

        filePath.putFile(uri).addOnSuccessListener { mProgress.dismiss() }

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