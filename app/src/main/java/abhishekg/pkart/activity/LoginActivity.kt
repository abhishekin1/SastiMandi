package abhishekg.pkart.activity

import abhishekg.pkart.R
import abhishekg.pkart.databinding.ActivityLoginBinding
import abhishekg.pkart.databinding.ActivityRegisterBinding
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button4.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
        binding.button3.setOnClickListener{
            if(binding.userNumber.text!!.isEmpty()){
                Toast.makeText(this,"Please Provide Number", Toast.LENGTH_SHORT).show()
            }else{

                Firebase.firestore.collection("users").document(binding.userNumber.text.toString())
                    .get().addOnSuccessListener {
                        if(it.exists())
                        sendOtp(binding.userNumber.text.toString())
                        else
                            Toast.makeText(this,"signup first", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(this,"signup first", Toast.LENGTH_SHORT).show()
                    }

            }
        }
    }

    private lateinit var builder: AlertDialog
    private fun sendOtp(number: String) {

        builder= AlertDialog.Builder(this)
            .setTitle("Loading...")
            .setMessage("Please Wait 🧘🏻️")
            .setCancelable(false)
            .create()
        builder.show()

        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber("+91$number")       // Phone number to verify
            .setTimeout(20L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Toast.makeText(this@LoginActivity,"Verifiv=cation completed", Toast.LENGTH_SHORT).show()


        }

        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(this@LoginActivity,"Verifiv=cation failed", Toast.LENGTH_SHORT).show()
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            Toast.makeText(this@LoginActivity,"code sent", Toast.LENGTH_SHORT).show()
            builder.dismiss()
            val intent=Intent(this@LoginActivity, OTPActivity::class.java)
            intent.putExtra("verificationId", verificationId)
            intent.putExtra("number", binding.userNumber.text.toString())
            startActivity(intent)
        }
    }
}