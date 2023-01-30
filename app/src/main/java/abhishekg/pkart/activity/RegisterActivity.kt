package abhishekg.pkart.activity

import abhishekg.pkart.R
import abhishekg.pkart.databinding.ActivityRegisterBinding
import abhishekg.pkart.databinding.LayoutCartItemBinding
import abhishekg.pkart.model.UserModel
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firestore.v1.FirestoreGrpc.FirestoreImplBase
import java.util.concurrent.TimeUnit

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.button4.setOnClickListener{
            openLogin()
        }
        binding.button3.setOnClickListener{

            validateUser()

        }
    }

    private fun validateUser() {
        if(binding.userName.text!!.isEmpty() || binding.userNumber.text!!.isEmpty()){
            Toast.makeText(this, "Please Fill All The Fields 📱", Toast.LENGTH_SHORT).show()
        }else{
            isUserRegistered(binding.userNumber.text.toString())
        }
    }

    private fun isUserRegistered(number: String) {

        Firebase.firestore.collection("users").document(number)
            .get().addOnSuccessListener {
                if(it.exists()){
                    Toast.makeText(this, "User Already Registered Please Login", Toast.LENGTH_SHORT).show()
                }else{
                    sendOtp(binding.userNumber.text.toString())
                }

            }.addOnFailureListener {
                Toast.makeText(this,"signup first", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this@RegisterActivity,"Verifiv=cation completed", Toast.LENGTH_SHORT).show()


        }
        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(this@RegisterActivity,"Verifiv=cation failed", Toast.LENGTH_SHORT).show()
        }
        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            Toast.makeText(this@RegisterActivity,"code sent", Toast.LENGTH_SHORT).show()
            builder.dismiss()
            val intent=Intent(this@RegisterActivity, OTPActivity::class.java)
            intent.putExtra("verificationId", verificationId)
            intent.putExtra("number", binding.userNumber.text.toString())
            intent.putExtra("nameFromRA", binding.userName.text.toString())
            startActivity(intent)
        }
    }



    private fun storeData() {

        val builder= AlertDialog.Builder(this)
            .setTitle("Loading...")
            .setMessage("Please Wait 🧘🏻️")
            .setCancelable(false)
            .create()
        builder.show()



        val data= UserModel(userName = binding.userName.text.toString(), userPhoneNumber = binding.userNumber.text.toString() )


        Firebase.firestore.collection("users").document(binding.userNumber.text.toString())
            .set(data).addOnSuccessListener {
                Toast.makeText(this, "User Registered 😁", Toast.LENGTH_SHORT).show()
                builder.dismiss()
                openLogin()


            }
            .addOnFailureListener{
                Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show()
                builder.dismiss()
            }


    }

    private fun openLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}