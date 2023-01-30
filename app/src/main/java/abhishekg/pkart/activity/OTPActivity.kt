package abhishekg.pkart.activity

import abhishekg.pkart.MainActivity
import abhishekg.pkart.R
import abhishekg.pkart.databinding.ActivityLoginBinding
import abhishekg.pkart.databinding.ActivityOtpactivityBinding
import abhishekg.pkart.model.UserModel
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.common.base.Verify.verify
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OTPActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpactivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button2.setOnClickListener{
            if(binding.userOTP.text!!.isEmpty()){
                Toast.makeText(this, "Please Enter OTP", Toast.LENGTH_SHORT).show()
            }else{
                verifyUser(binding.userOTP.text.toString())
            }
        }
        binding.button6.setOnClickListener{
            verifyUser("000000")
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun verifyUser(otp: String) {

        val credential = PhoneAuthProvider.getCredential(intent.getStringExtra("verificationId")!!, otp)
        signInWithPhoneAuthCredential(credential)

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    if(cameFromRegistered()){
                        saveUserData()
                    }else{
                        setPreferenceNameNumber()
                    }

                    Toast.makeText(this,"success  in otp", Toast.LENGTH_SHORT).show()



                } else {
                    Toast.makeText(this,"something went wrong in otp", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserData() {
        val builder= AlertDialog.Builder(this)
            .setTitle("Loading...")
            .setMessage("Please Wait 🧘🏻️")
            .setCancelable(false)
            .create()
        builder.show()



        val data= UserModel(userName = intent.getStringExtra("nameFromRA"), userPhoneNumber = intent.getStringExtra("number"))
        Firebase.firestore.collection("users").document(intent.getStringExtra("number").toString())
            .set(data).addOnSuccessListener {
                Toast.makeText(this, "User Registered 😁", Toast.LENGTH_SHORT).show()
                setPreferenceNameNumber()
                builder.dismiss()



            }
            .addOnFailureListener{
                Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show()
                builder.dismiss()
            }
    }

    private fun setPreferenceNameNumber() {
        var nameeee="";
        val preferences= this.getSharedPreferences("userr", MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("numberr", intent.getStringExtra("number"))


        Firebase.firestore.collection("users")
            .document(intent.getStringExtra("number").toString())
            .get().addOnSuccessListener {
                nameeee=it.getString("userName").toString()
                editor.putString("namee", nameeee)
                editor.apply()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }.addOnFailureListener{
                Log.i("preferm0","failed")
            }
    }


    private fun cameFromRegistered(): Boolean {
        return intent.getStringExtra("nameFromRA") != null

    }
}