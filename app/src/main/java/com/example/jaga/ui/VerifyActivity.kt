package com.example.jaga.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.jaga.LoginViewModel
import com.example.jaga.R
import com.example.jaga.User
import com.example.jaga.UserPreference
import com.example.jaga.ViewModelFactory
import com.example.jaga.databinding.ActivityVerifyBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.log
import kotlin.streams.asSequence

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class VerifyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifyBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var verifyViewModel: LoginViewModel
    private var reSend = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyBinding.inflate(layoutInflater)
        setContentView(binding.root)


        var nPhone = intent.getStringExtra(NUMBER_PHONE).toString()
        val nNama = intent.getStringExtra(NAME_USER)
        val id = intent.getStringExtra(ID_USER).toString()
        Log.e("iduser",id)

        if (nPhone[0] == '0') {
            nPhone = nPhone.drop(1)
        }
        nPhone = "+62$nPhone"

        mAuth = Firebase.auth
        setupViewModel()

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:$credential")
            }

            override fun onVerificationFailed(e: FirebaseException) {
                binding.progressBar.visibility = View.GONE
                binding.btnVerifikasi.isEnabled = true
                Toast.makeText(this@VerifyActivity, "Error: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onCodeSent(

                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                binding.progressBar.visibility = View.GONE
                binding.btnVerifikasi.isEnabled = true

                storedVerificationId = verificationId
                resendToken = token
            }

        }



        sendOTP(nPhone)


        binding.angka1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!p0.toString().trim().isEmpty()) {
                    binding.angka2.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        binding.angka2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!p0.toString().trim().isEmpty()) {
                    binding.angka3.requestFocus()
                } else if (p0.toString().trim().isEmpty()) {
                    binding.angka1.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        binding.angka3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!p0.toString().trim().isEmpty()) {
                    binding.angka4.requestFocus()
                } else if (p0.toString().trim().isEmpty()) {
                    binding.angka2.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        binding.angka4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!p0.toString().trim().isEmpty()) {
                    binding.angka5.requestFocus()
                } else if (p0.toString().trim().isEmpty()) {
                    binding.angka3.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        binding.angka5.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!p0.toString().trim().isEmpty()) {
                    binding.angka6.requestFocus()
                } else if (p0.toString().trim().isEmpty()) {
                    binding.angka4.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        binding.angka6.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().trim().isEmpty()) {
                    binding.angka5.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })





        binding.btnVerifikasi.setOnClickListener {
            if (binding.angka1.text.toString().trim().isEmpty()
                || binding.angka2.text.toString().trim().isEmpty()
                || binding.angka3.text.toString().trim().isEmpty()
                || binding.angka4.text.toString().trim().isEmpty()
                || binding.angka5.text.toString().trim().isEmpty()
                || binding.angka6.text.toString().trim().isEmpty()
            ) {
                Toast.makeText(this, "Mohon Masukkan kode OTP dengan benar", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            val code: String =
                binding.angka1.text.toString() +
                        binding.angka2.text.toString() + binding.angka3.text.toString() +
                        binding.angka4.text.toString() + binding.angka5.text.toString() +
                        binding.angka6.text.toString()


            if (storedVerificationId != null) {
                val phoneAuthCredential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    storedVerificationId!!,
                    code
                )
                FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            db = Firebase.database
                            val userRef = db.reference.child(USERS)
                            Log.e("ref",userRef.toString())
                            var datauser: User
                            if (nNama?.isEmpty() == false) {

                                var iduser: String? = null
                                val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    iduser = Random().ints(10, 0, source.length)
                                        .asSequence()
                                        .map(source::get)
                                        .joinToString("")
                                }
                                datauser = User(
                                    iduser!!,
                                    nPhone,
                                    nNama,
                                    null,
                                    null,
                                    null
                                )
                                userRef.child(iduser).setValue(datauser)
                                verifyViewModel.login(datauser)

                            } else {
                                var name: String?
                                var tgl: String?
                                var tentang: String?
                                var foto:String? = null
                                db.reference.child("users").child(id).get()
                                    .addOnSuccessListener {

                                        if (it.exists()) {
                                            name = it.child("name").value.toString()
                                            tgl = it.child("tgl_lahir").value.toString()
                                            tentang = it.child("tentang").value.toString()
                                            foto = it.child("foto").value.toString()
                                            verifyViewModel.login(User(
                                                id,
                                                nPhone,
                                                name,
                                                tgl,
                                                tentang,
                                                foto
                                            )
                                            )
                                        }
                                    }.addOnFailureListener{ error ->
                                            Toast.makeText(this@VerifyActivity,"Error : ${error.message}",Toast.LENGTH_SHORT).show()
                                    }





                            }

                            val intent = Intent(this, MapsActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()

                        } else {
                            Toast.makeText(
                                this,
                                "Kode OTP yang dimasukkan tidak valid",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }



        binding.kirimUlang.setOnClickListener {
            if (reSend!!) {
                sendOTP(nPhone)
            }
        }


    }


    private fun sendOTP(no: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnVerifikasi.isEnabled = false
        Log.e("number", no)
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(no)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        updateTime()

    }

    private fun setupViewModel() {
        verifyViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]
    }

    private fun updateTime() {
        val duration: Long = TimeUnit.MINUTES.toMillis(1)

        object : CountDownTimer(duration, 1000) {
            @SuppressLint("ResourceAsColor")
            override fun onTick(p0: Long) {
                binding.kirimUlang.setTextColor(R.color.blue_200)
                reSend = true

                binding.time.text = "${p0 / 1000}"
            }


            @SuppressLint("ResourceAsColor")
            override fun onFinish() {
                reSend = false
                binding.kirimUlang.setTextColor(R.color.gray_100)

            }

        }.start()
    }

    companion object {
        const val NUMBER_PHONE = "number_phone"
        const val NAME_USER = "name_user"
        const val ID_USER = "id_user"

        private const val TAG = "PhoneAuthActivity"
        const val USERS = "users"
    }
}