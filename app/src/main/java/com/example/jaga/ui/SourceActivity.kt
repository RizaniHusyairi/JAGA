package com.example.jaga.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jaga.*
import com.example.jaga.databinding.ActivitySourceBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SourceActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySourceBinding
    private lateinit var mStorage:StorageReference
    private lateinit var sourceViewModel: LoginViewModel
    private lateinit var db: FirebaseDatabase
    private val list = ArrayList<Record>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySourceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()

        supportActionBar?.hide()
        db = Firebase.database

//
//        mStorage = FirebaseStorage.getInstance().reference
//
//        sourceViewModel.getUser().observe(this){
//            Log.e("sourceRecord",mStorage.child(it.id!!).child("Audio").toString())
//        }
        getRecordList()
        binding.btnBack.setOnClickListener {

            startActivity(Intent(this,MapsActivity::class.java))
            finish()
        }
    }
    private fun getRecordList() {
        db.reference.child("users").child(MapsActivity.iduser!!).child("record")
            .get().addOnSuccessListener {
                it.children.forEach { dataContact ->
                    val temp = Record(
                        dataContact.child("name").value.toString(),
                        dataContact.child("url").value.toString(),
                    )
                    Log.e("kontak", dataContact.value.toString())
                    list.add(temp)

                }

            }.addOnCompleteListener {

                if (list.isEmpty()) {
                    binding.isTextNull.visibility = View.VISIBLE
                } else {
                    binding.isTextNull.visibility = View.INVISIBLE

                    val contactAdabter = ListRecordAdapter(list)
                    binding.rvRecord.apply {
                        layoutManager = LinearLayoutManager(this@SourceActivity)
                        setHasFixedSize(true)
                        adapter = contactAdabter
                    }

                }
            }


    }


    private fun setupViewModel() {
        sourceViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]
    }

}