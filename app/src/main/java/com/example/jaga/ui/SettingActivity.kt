package com.example.jaga.ui

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.jaga.*
import com.example.jaga.databinding.ActivitySettingBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    private lateinit var settingViewModel: LoginViewModel
    private lateinit var storageReference: StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        setupViewModel()

        settingViewModel.getUser().observe(this) {
            id_user = it.id
            storageReference = FirebaseStorage.getInstance().getReference(it.id!!)

            if (it.foto == null) {
                Glide.with(this)
                    .load(R.drawable.foto_default)
                    .circleCrop()
                    .into(binding.imgProfile)
            } else {
                Log.e("nama_foto" , it.foto!!)
                val progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Sedang Menyiapkan gambar...")
                progressDialog.setCancelable(false)
                progressDialog.show()

                val cekfoto = storageReference.child("images/${it.foto}")
                try {
                    val localTemp = File.createTempFile("tempfile",".jpg")
                    cekfoto.getFile(localTemp).addOnSuccessListener{
                        if(progressDialog.isShowing)
                            progressDialog.dismiss()

                        val bitmap = BitmapFactory.decodeFile(localTemp.absolutePath)


                        Glide.with(this)
                            .load(cekfoto.child("${id_user}/images/${bitmap}"))
                            .circleCrop()
                            .into(binding.imgProfile)
                    }.addOnFailureListener{error ->
                        if(progressDialog.isShowing)
                            progressDialog.dismiss()

                        Toast.makeText(this, "Gambar Error: ${error.message}", Toast.LENGTH_SHORT).show()

                    }

                }catch (e: IOException){
                    e.printStackTrace()
                }


            }

            binding.apply {
                nameUser.text = it.name
                if (tglLahir.text == null) {
                    imageView4.visibility = View.INVISIBLE
                    tglLahir.text = ""
                } else {
                    imageView4.visibility = View.VISIBLE

                    tglLahir.text = it.tgl_lahir
                }
                if (tentang.text != null) {
                    tentang.text = it.tentang
                }
            }
        }


    }

    private fun setupViewModel() {
        settingViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]
    }

    fun btn_back(v: View) {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }

    fun btn_logOut(v: View) {
        AlertDialog.Builder(this).apply {
            setTitle("Log-Out")
            setMessage("Apa kamu yakin ingin Log-Out dari akun ini?")
            setPositiveButton("Yes") { _, _ ->
                settingViewModel.logout()
                val intent = Intent(this@SettingActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            setNegativeButton("No") { _, _ ->

            }
            create()
            show()
        }
    }

    companion object {
        var id_user: String? = null
    }


}