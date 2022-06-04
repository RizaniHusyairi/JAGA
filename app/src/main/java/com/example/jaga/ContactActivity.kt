package com.example.jaga

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jaga.databinding.ActivityContactBinding

class ContactActivity : AppCompatActivity() {

    private lateinit var binding : ActivityContactBinding
    private val list = ArrayList<Contact>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvContact.setHasFixedSize(true)
        supportActionBar?.hide()

        checkPermission()

    }

    private fun checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.READ_CONTACTS),100)
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getContactList()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("Range")
    private fun getContactList() {
        val uri: Uri = ContactsContract.Contacts.CONTENT_URI
        val sort:String = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASD"

        val cursor : Cursor? = contentResolver.query(
            uri, null, null  ,null,sort
        )

        if (cursor != null) {
            if (cursor.count>0){
                while (cursor.moveToNext()){
                    val id:String = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts._ID
                    ))
                    val name:String = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME
                    ))
                    val uriPhone: Uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                    val selection: String = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?"

                    val phoneCursor: Cursor? = contentResolver.query(
                        uriPhone,null,selection, arrayOf(id),null
                    )

                    if(phoneCursor!!.moveToNext()){
                        val number: String = phoneCursor.getString(phoneCursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                        ))
                        val model: Contact = Contact()

                        model.nama= name
                        model.nomor=number
                        list.add(model)

                        phoneCursor.close()


                    }


                }
            }
            cursor.close()
        }
        binding.rvContact.layoutManager = LinearLayoutManager(this)
        val adapter = ListContactAdapter(list)
        binding.rvContact.adapter = adapter

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 100 && grantResults.size>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getContactList()
            }
        }else{
            Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show()
            checkPermission()
        }


    }




}