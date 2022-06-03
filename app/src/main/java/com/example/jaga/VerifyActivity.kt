package com.example.jaga

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.core.widget.addTextChangedListener
import com.example.jaga.databinding.ActivityVerifyBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class VerifyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifyBinding

    private lateinit var mAuth: FirebaseAuth

    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var mCurrentUser: FirebaseUser
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

        val nPhone = intent.getStringExtra(NUMBER_PHONE)
        Log.d("nomor", nPhone.toString())
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber("+62${nPhone.toString()}")       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)


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




    }

    companion object{
        const val NUMBER_PHONE = "number_phone"
        private const val TAG = "PhoneAuthActivity"
    }
}