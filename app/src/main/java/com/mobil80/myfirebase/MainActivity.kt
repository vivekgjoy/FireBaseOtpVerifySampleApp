package com.mobil80.myfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.datatransport.cct.internal.LogEvent
import com.google.android.play.integrity.internal.t
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.mobil80.myfirebase.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private var mAuth : FirebaseAuth? = null
    private var etdPhone : EditText? = null
    private var etdOTP : EditText? = null

    private var verifyOTPBtn : Button? = null
    private var generateOTPBtn : Button? = null

    private var verificationId: String? = null

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth?.firebaseAuthSettings?.setAppVerificationDisabledForTesting(true);
        mAuth = FirebaseAuth.getInstance()

        etdPhone = findViewById(R.id.idEditPhoneNumber)
        etdOTP = findViewById(R.id.idEditOtp)
        verifyOTPBtn = findViewById(R.id.idBtnVerify)
        generateOTPBtn = findViewById(R.id.idBtnGetOtp)

//        val crashButton = Button(this)
//        crashButton.text = "Test Crash"
//        crashButton.setOnClickListener {
//            throw RuntimeException("Test Crash") // Force a crash
//        }
//        addContentView(crashButton, ViewGroup.LayoutParams(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT))

        binding.idBtnGetOtp.setOnClickListener {
            if (binding.idEditPhoneNumber.text.isEmpty()){
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
            }else {
                val phone = "+91${binding.idEditPhoneNumber.text}"
                sendVerificationCode(phone)
            }
        }

        binding.idBtnVerify.setOnClickListener {
            if (binding.idEditOtp.text.isEmpty()){
                Toast.makeText(this, "Please enter OTP", Toast.LENGTH_SHORT).show()
            }else {
                verifyCode(binding.idEditOtp.text.toString())
            }
        }
    }

    private fun signInWithCredential(credential: PhoneAuthCredential){
        mAuth?.signInWithCredential(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun sendVerificationCode(number: String){
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(number)
            .setTimeout(40L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallBack)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val mCallBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(p0, p1)
            verificationId = p0
        }

        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
            val code = phoneAuthCredential.smsCode

            if (code != null){
                binding.idEditOtp.setText(code)

                verifyCode(code)
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
        }
    }
    private fun verifyCode(code : String){
        val credential = verificationId?.let { PhoneAuthProvider.getCredential(it, code) }

        if (credential != null) {
            signInWithCredential(credential)
            Toast.makeText(this@MainActivity,"main", Toast.LENGTH_SHORT).show()
            Log.e("TAGOTP", "verifyCode: $credential")
        }
    }
}