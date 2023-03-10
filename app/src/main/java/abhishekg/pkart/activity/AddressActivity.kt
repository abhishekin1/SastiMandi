package abhishekg.pkart.activity

import abhishekg.pkart.MainActivity
import abhishekg.pkart.R
import abhishekg.pkart.databinding.ActivityAddressBinding
import abhishekg.pkart.databinding.ActivityOtpactivityBinding
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddressBinding
    private lateinit var preferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(intent.hasExtra("CameFromMore")){
            binding.proceed.text="Update Details"
        }

        preferences=this.getSharedPreferences("userr", MODE_PRIVATE)
        loadUserInfo()


        binding.proceed.setOnClickListener{

            validateData(
                binding.userNumber.text.toString(),
                binding.userName.text.toString(),
                binding.userPincode.text.toString(),
                binding.userCity.text.toString(),
                binding.userState.text.toString(),
                binding.userStreet.text.toString()
                
            )
        }
    }

    private fun validateData(number: String, name: String, pincode: String, city: String, state: String, street: String) {
        if(number.isEmpty() || name.isEmpty() || pincode.isEmpty() || city.isEmpty() ||state.isEmpty() || street.isEmpty()){
            Toast.makeText(this,"Please Fill All the fields", Toast.LENGTH_SHORT).show()
        }else{
            storeData(name, number, pincode, city,state,street)
        }
    }

    private fun storeData(name: String, number: String, pincode: String, city: String, state: String, street: String) {
        val map= hashMapOf<String, Any>()
        map["street"]= street;
        map["state"]=state
        map["city"]=city
        map["pincode"]=pincode
        map["userName"]= name
        map["userPhoneNumber"]= number

        Firebase.firestore.collection("users")
            .document(preferences.getString("numberr", "")!!)
            .update(map).addOnSuccessListener {
                if(intent.hasExtra("CameFromMore")){
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("goToMore", "abd")
                    startActivity(intent)
                }else{
                    val list= intent.getStringArrayListExtra("productIds")
                    for(data in list!!){
                        Log.i("cartfrag", data)

                    }
                    val intent=(Intent(this, CheckoutActivity::class.java))
                    intent.putExtra("userName", name)
                    intent.putExtra("productIds", list )
                    intent.putExtra("totalCost", intent.getStringExtra("totalCost") )
                    intent.putExtra("addressFromAddressActivity", "${binding.userStreet.text}, ${binding.userCity.text}, ${binding.userState.text}, ${binding.userPincode.text}")
                    startActivity(intent)
                }



            }.addOnFailureListener{
                Toast.makeText(this,"STWR", Toast.LENGTH_SHORT).show()
            }

    }

    private fun loadUserInfo() {

        val preferences= this.getSharedPreferences("userr", MODE_PRIVATE)
        Log.i("morefrag",preferences.getString("namee", "hfn").toString())

        Firebase.firestore.collection("users")
            .document(preferences.getString("numberr", "")!!)
            .get().addOnSuccessListener {
//                binding.userName.setText(it.getString("userName"))
                binding.userName.setText(it.getString("userName"))
//                binding.userNumber.setText(it.getString("userPhoneNumber"))
                binding.userNumber.setText(preferences.getString("numberr", "null")!!)
                binding.userStreet.setText(it.getString("street"))
                binding.userState.setText(it.getString("state"))
                binding.userCity.setText(it.getString("city"))
                binding.userPincode.setText(it.getString("pincode"))
            }
    }

}