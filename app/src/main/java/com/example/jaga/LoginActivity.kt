package com.example.jaga

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.example.jaga.databinding.ActivityLoginBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var db : FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        supportActionBar?.hide()
    }

    fun onClickLogin(v : View){
//        db = Firebase.database
//
//        val userRef = db.reference.child(VerifyActivity.USERS)
//        val numberPhones = userRef.orderByKey()
//        numberPhones.addChildEventListener(object : ChildEventListener{
//            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//            }
//
//            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//            }
//
//            override fun onChildRemoved(snapshot: DataSnapshot) {
//            }
//
//            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//            }
//
//        })

        val iLogin = Intent(this,VerifyActivity::class.java)
        iLogin.putExtra(VerifyActivity.NUMBER_PHONE,binding.noHp.text)
        iLogin.putExtra(VerifyActivity.NAME_USER,"LOGIN_USER")
        startActivity(iLogin)
    }

    fun regBtn(view: View) {
        val regis = Intent(this,RegisActivity::class.java)
        startActivity(regis)
    }


}