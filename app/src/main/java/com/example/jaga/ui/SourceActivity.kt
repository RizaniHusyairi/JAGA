package com.example.jaga.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.jaga.LoginViewModel
import com.example.jaga.R
import com.example.jaga.UserPreference
import com.example.jaga.ViewModelFactory
import com.example.jaga.databinding.ActivitySourceBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SourceActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySourceBinding
    private lateinit var mStorage:StorageReference
    private lateinit var sourceViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySourceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()

        supportActionBar?.hide()

        mStorage = FirebaseStorage.getInstance().reference

        sourceViewModel.getUser().observe(this){
            mStorage.child(it.id!!).child("Audio")
        }






    }
    private fun setupViewModel() {
        sourceViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]
    }

}