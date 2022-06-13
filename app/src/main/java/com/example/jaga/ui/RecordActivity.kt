package com.example.jaga.ui

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.jaga.*
import com.example.jaga.databinding.ActivityRecordBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storageMetadata
import com.squareup.okhttp.Dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class RecordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecordBinding
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var mStorage: StorageReference
    private lateinit var recordViewModel: LoginViewModel
    private lateinit var db: FirebaseDatabase
    private var dbStore = Firebase.firestore.collection("messages")

    private lateinit var mProgress: ProgressDialog

    private var fileName: String? = null

    var id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViewModel()
        supportActionBar?.hide()

        db = Firebase.database
        val lat:Double = intent.getDoubleExtra(LATITUDE, 0.0)
        val lng:Double = intent.getDoubleExtra(LONGITUDE, 0.0)
        Log.e("location","$lat , $lng")
        recordViewModel.getUser().observe(this) {user ->

            db.reference.child("users").child(MapsActivity.iduser!!).child("contact").get()
                .addOnSuccessListener {
                    it.children.forEach { kontak ->
                        sendMessages(
                            Message(
                                kontak.child("nomor").value.toString(),
                                "Heii ${kontak.child("nama").value.toString()}, teman atau kerabat mu ${user.name} Mungkin sedang dalam bahaya \n Berikut lokasi terakhirnya: https://www.google.com/maps/search/?api=1&query=$lat%2C$lng"
                            )
                        )
                    }
                }
        }

        recordViewModel.getUser().observe(this) {
            id = it.id
        }

        mStorage = FirebaseStorage.getInstance().reference

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fileName = getRecordingFilePath()
        }

        mProgress = ProgressDialog(this)



        try {
            mediaRecorder = MediaRecorder()
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mediaRecorder.setOutputFile(fileName)
            }
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            mediaRecorder.prepare()
            mediaRecorder.start()

        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    private fun sendMessages(message: Message) = CoroutineScope(Dispatchers.IO).launch {
        try {
            dbStore.add(message)
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@RecordActivity, "Error: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    fun btnStopPressed(v: View) {
        mediaRecorder.stop()
        mediaRecorder.release()

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
        val filePath = mStorage.child("$id/Audio").child("case_$formatted.mp3")
        val uri: Uri = Uri.fromFile(File(fileName))

        var metadata = storageMetadata {
            contentType = "audio/mpeg"
        }

        filePath.putFile(uri, metadata).addOnSuccessListener {
            mProgress.dismiss()
            val url: String = it.uploadSessionUri.toString()
            val name: String? = it.metadata?.name
            db.reference.child("users").child(id!!).child("record").push().setValue(
                Record(
                    name,
                    url

                )
            )

        }

    }

    private fun setupViewModel() {
        recordViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getRecordingFilePath(): String {
        val d = LocalDateTime.now()
        val s = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        val formatted = d.format(s)
        val contextWrapper: ContextWrapper = ContextWrapper(applicationContext)
        val musicDirectory: File? = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val file: File = File(musicDirectory, "case_$formatted.mp3")
        return file.path
    }

    companion object {
        const val LONGITUDE = "longitude"
        const val LATITUDE = "latitude"
    }
}