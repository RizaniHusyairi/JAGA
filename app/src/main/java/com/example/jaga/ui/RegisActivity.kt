package com.example.jaga.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.jaga.LoginViewModel
import com.example.jaga.UserPreference
import com.example.jaga.ViewModelFactory
import com.example.jaga.databinding.ActivityRegisBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegisActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisBinding
    private lateinit var db : FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
    }

    fun onClickRegis(view: View) {
        if (binding.noHp.editText?.text?.isEmpty() == true) {
            binding.noHp.error = "Nomor HP Belum Diisi"
        }else if(binding.nama.editText?.text?.isEmpty() == true){
            binding.nama.error = "Nama Belum Diisi"
        }
        else {
            db = Firebase.database
            var nomor = if (binding.noHp.editText?.text?.get(0) == '0'){
                binding.noHp.editText!!.text.drop(1)
            }else{
                binding.noHp.editText?.text.toString()
            }
            db.reference.child(VerifyActivity.USERS).orderByChild("number").equalTo("+62${nomor}").addListenerForSingleValueEvent(object:
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        binding.noHp.error = "Nomor ini sudah terdaftar"
                    }else{
                        val intent = Intent(this@RegisActivity, VerifyActivity::class.java)
                        intent.putExtra(VerifyActivity.NUMBER_PHONE, binding.noHp.editText?.text.toString())
                        intent.putExtra(VerifyActivity.NAME_USER, binding.nama.editText?.text.toString())
                        startActivity(intent)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@RegisActivity,"Error: $error",Toast.LENGTH_LONG).show()
                }

            })

        }

    }



    fun logBtn(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)


    }
}