package com.example.jaga.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.jaga.LoginViewModel
import com.example.jaga.UserPreference
import com.example.jaga.ViewModelFactory
import com.example.jaga.databinding.ActivityLoginBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var db: FirebaseDatabase
    private lateinit var loginViewModel: LoginViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setupViewModel()


        supportActionBar?.hide()
    }

    fun onClickLogin(v: View) {
        if (binding.noHp.text.isEmpty()) {
            binding.noHp.error = "Masukkan nomor HP terlebih dahulu!"
        } else {

            db = Firebase.database
            var nomor = if (binding.noHp.text[0] == '0') {
                binding.noHp.text.drop(1)
            } else {
                binding.noHp.text
            }
            loginViewModel.getUser().observe(this) {
                if ( it.number == "+62${nomor}") {
                    val iLogin = Intent(this@LoginActivity, MapsActivity::class.java)
                    startActivity(iLogin)
                    finish()

                }else{
                    db.reference.child(VerifyActivity.USERS).orderByChild("number").equalTo("+62${nomor}")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    val iLogin = Intent(this@LoginActivity, VerifyActivity::class.java)

                                    iLogin.putExtra(
                                        VerifyActivity.NUMBER_PHONE,
                                        binding.noHp.text.toString()
                                    )
                                    iLogin.putExtra(
                                        VerifyActivity.ID_USER,
                                        snapshot.child("id").toString()
                                    )
                                    startActivity(iLogin)
                                } else {
                                    binding.noHp.error = "Nomor belum terdaftar"
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(this@LoginActivity, "Error: $error", Toast.LENGTH_LONG)
                                    .show()
                            }

                        })
                }
            }


        }

    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]
    }


    fun regBtn(view: View) {
        val regis = Intent(this, RegisActivity::class.java)
        startActivity(regis)
    }


}