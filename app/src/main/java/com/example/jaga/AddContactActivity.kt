package com.example.jaga

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.jaga.databinding.ActivityAddContactBinding
import com.example.jaga.ui.ContactActivity
import com.example.jaga.ui.MapsActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AddContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddContactBinding
    private lateinit var db: FirebaseDatabase



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = Firebase.database
        binding.btnBack.setOnClickListener {
            val back = Intent(this,ContactActivity::class.java)
            startActivity(back)

        }

        binding.btnSaveContact.setOnClickListener {
            if (binding.namaKontak.editText?.text?.isEmpty() == true){
                binding.namaKontak.error = "Nama kontak kosong"
            }else if(binding.noHp.editText?.text?.isEmpty() == true){
                binding.noHp.error = "Nomor hp kontak kosong"
            }else{
                var nomor = if (binding.noHp.editText?.text?.get(0) == '0'){
                    binding.noHp.editText!!.text.drop(1)
                }else{
                    binding.noHp.editText?.text.toString()
                }
                db.reference.child("users").child(MapsActivity.iduser!!)
                    .child("contact")
                    .push()
                    .setValue(Contact(
                        nama = binding.namaKontak.editText?.text.toString(),
                        nomor = "+62$nomor",
                    )).addOnSuccessListener {
                        Toast.makeText(this, "Kontak Berhasil Disimpan", Toast.LENGTH_SHORT).show()
                        val toContact = Intent(this,ContactActivity::class.java)
                        startActivity(toContact)
                        finish()

                    }.addOnFailureListener {
                        Toast.makeText(this, "Kontak Gagal Disimpan", Toast.LENGTH_SHORT).show()

                    }




            }
        }
    }
}