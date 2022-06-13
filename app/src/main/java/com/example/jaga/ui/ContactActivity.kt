package com.example.jaga.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jaga.*
import com.example.jaga.databinding.ActivityContactBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContactBinding
    private val list = ArrayList<Contact>()
    private lateinit var db: FirebaseDatabase
    private lateinit var contactViewModel: LoginViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvContact.setHasFixedSize(true)
        supportActionBar?.hide()
        db = Firebase.database
        setupViewModel()



        binding.btnAdd.setOnClickListener {
            val a = Intent(this, AddContactActivity::class.java)
            startActivity(a)
        }

        binding.btnBack.setOnClickListener {
            val back = Intent(this, MapsActivity::class.java)
            startActivity(back)
            finish()
        }
        getContactList()
    }

    private fun getContactList() {
        db.reference.child("users").child(MapsActivity.iduser!!).child("contact")
            .get().addOnSuccessListener {
                it.children.forEach { dataContact ->
                    val temp = Contact(
                        dataContact.key.toString(),
                        dataContact.child("nama").value.toString(),
                        dataContact.child("nomor").value.toString()
                    )
                    Log.e("kontak", dataContact.value.toString())
                    list.add(temp)

                }

            }.addOnCompleteListener {

                if (list.isEmpty()) {
                    binding.isTextNull.visibility = View.VISIBLE
                } else {
                    binding.isTextNull.visibility = View.INVISIBLE

                    val contactAdabter = ListContactAdapter(list)
                    contactAdabter.setOnItemClickCallback(object :
                        ListContactAdapter.OnItemClickCallback {
                        override fun onItemClicked(data: Contact) {
                            btn_deleted(data)
                        }

                        override fun onUpdateClicked(data: Contact) {

                        }

                    })
                    binding.rvContact.apply {
                        layoutManager = LinearLayoutManager(this@ContactActivity)
                        setHasFixedSize(true)
                        adapter = contactAdabter
                    }

                }
            }


    }

    fun btn_deleted(data: Contact) {
        AlertDialog.Builder(this).apply {
            setTitle("Hapus Kontak")
            setMessage("Apa kamu yakin menghapus kotak ini?")
            setPositiveButton("Yes") { _, _ ->
                Log.e("data-id", data.toString())
                db.reference.child("users").child(MapsActivity.iduser!!).child("contact")
                    .child(data.id!!).removeValue().addOnSuccessListener {
                    Toast.makeText(
                        this@ContactActivity,
                        "Kontak Berhasil Dihapus",
                        Toast.LENGTH_SHORT
                    )
                }
                getContactList()
            }
            setNegativeButton("No") { _, _ ->

            }
            create()
            show()
        }
    }

    private fun setupViewModel() {
        contactViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]
    }


}