package com.example.jaga

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.example.jaga.databinding.ActivityVerifyBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class VerifyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifyBinding

    private lateinit var mAuth: FirebaseAuth

    private lateinit var db: FirebaseDatabase

    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = Firebase.auth

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:$credential")
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w(TAG, "onVerificationFailed", e)
                Toast.makeText(this@VerifyActivity,"Error: ${e.message}",Toast.LENGTH_SHORT).show()
            }
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token
            }

        }

        val nPhone = "+62${intent.getStringExtra(NUMBER_PHONE).toString()}"
        val nNama = intent.getStringExtra(NAME_USER)

        Log.d("nomor", nPhone)
        sendOTP(nPhone)


        binding.angka1.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(!p0.toString().trim().isEmpty()){
                    binding.angka2.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        binding.angka2.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(!p0.toString().trim().isEmpty()){
                    binding.angka3.requestFocus()
                }else if(p0.toString().trim().isEmpty()){
                    binding.angka1.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        binding.angka3.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(!p0.toString().trim().isEmpty()){
                    binding.angka4.requestFocus()
                }else if(p0.toString().trim().isEmpty()){
                    binding.angka2.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        binding.angka4.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(!p0.toString().trim().isEmpty()){
                    binding.angka5.requestFocus()
                }else if(p0.toString().trim().isEmpty()){
                    binding.angka3.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        binding.angka5.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(!p0.toString().trim().isEmpty()){
                    binding.angka6.requestFocus()
                }else if(p0.toString().trim().isEmpty()){
                    binding.angka4.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        binding.angka6.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0.toString().trim().isEmpty()){
                    binding.angka5.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })



        binding.btnVerifikasi.setOnClickListener {
            if(binding.angka1.text.toString().trim().isEmpty()
                || binding.angka2.text.toString().trim().isEmpty()
                || binding.angka3.text.toString().trim().isEmpty()
                || binding.angka4.text.toString().trim().isEmpty()
                || binding.angka5.text.toString().trim().isEmpty()
                || binding.angka6.text.toString().trim().isEmpty()){
                Toast.makeText(this,"Mohon Masukkan kode OTP dengan benar",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            var code:String =
                binding.angka1.text.toString() +
                        binding.angka2.text.toString() + binding.angka3.text.toString() +
                        binding.angka4.text.toString() + binding.angka5.text.toString() +
                        binding.angka6.text.toString()


            if (storedVerificationId != null){
                val phoneAuthCredential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    storedVerificationId!!,
                    code
                )
                FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                    .addOnCompleteListener {
                        if(it.isSuccessful){
                            if(nNama.toString() != "LOGIN_USER"){
                                db = Firebase.database

                                val userRef = db.reference.child(USERS)
                                val datauser = User(
                                    nPhone,
                                    nNama.toString()
                                )

                                userRef.child(nPhone).setValue(datauser)

                            }
                            val intent = Intent(this, MapsActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()

                        }else{
                            Toast.makeText(this, "Kode OTP yang dimasukkan tidak valid", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
        binding.kirimUlang.setOnClickListener {
            sendOTP(nPhone)

        }
    }

    private fun sendOTP(no : String){
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(no)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    companion object{
        const val NUMBER_PHONE = "number_phone"
        const val NAME_USER = "name_user"

        private const val TAG = "PhoneAuthActivity"
        const val USERS = "users"
    }
}