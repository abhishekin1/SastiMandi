package abhishekg.pkart.activity

import abhishekg.pkart.MainActivity
import abhishekg.pkart.R
import abhishekg.pkart.databinding.ActivityCheckoutBinding
import abhishekg.pkart.roomdb.AppDatabase
import abhishekg.pkart.roomdb.ProductModel
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CheckoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckoutBinding
    private var total=0
    private var curOrderId=1
    private lateinit var pnlist: ArrayList<Any>
    private lateinit var pqlist: ArrayList<Any>
    private lateinit var pplist: ArrayList<Any>
    private lateinit var pilist: ArrayList<Any>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

    //////////////setting data to card//////////////////////////////////////////////////
        pnlist=ArrayList()
        pqlist=ArrayList()
        pplist=ArrayList()
        pilist=ArrayList()
        val dao = AppDatabase.getInstance(this).productDao()
        dao.getAllProducts().observe(this){
            pnlist.clear()
            pqlist.clear()
            pplist.clear()
            for(data in it){
                pilist.add(data.productId)
                pnlist.add(data.productName.toString())
                pqlist.add(data.productQuan.toString())
                pplist.add(data.productSp.toString())

                binding.name.append("${data.productName.toString()} \n")
                binding.quan.append("${data.productQuan.toString()} \n")
                binding.price.append("${data.productSp.toString()} \n")
                Log.i("listc", data.productName.toString() )
                total+= data.productSp!!.toInt() * data.productQuan!!.toInt()
            }
            binding.NetPrice.text=("Net Payable Amount\n------------------------\n       Rs. ${total.toString()}")
        }

        ///////////////////////////////////////////////////////////////////////////////

        ///////////confirm order button////////////////
        binding.confirmOrderButton.setOnClickListener{
            if(binding.COD.isChecked())
            {
               // Toast.makeText(this, "You selected cod", Toast.LENGTH_SHORT).show()
                changeOrderId()
//                addOrder(pnlist, pqlist,pplist)
                //uploadData()
            }
            else
            {                Toast.makeText(this, "Please select COD. Online payment is currently facing issues", Toast.LENGTH_LONG).show()

            }
        }
        ///////////////////////


    }

    private fun changeOrderId() {
        //get

        Firebase.firestore.collection("universalOrderId")
            .document("1")
            .get().addOnSuccessListener {
                Log.i("prefermget",it.getString("id").toString())
                curOrderId=it.getString("id")!!.toInt()
                curOrderId++;
                Toast.makeText(this, "got order id 😁", Toast.LENGTH_SHORT).show()

                //set insede get
                var data= hashMapOf<String,Any>()
                data["id"]= curOrderId.toString()
                Firebase.firestore.collection("universalOrderId").document("1")
                    .set(data).addOnSuccessListener {
                        //adding order
                        addOrder(pnlist, pqlist,pplist)
                        Log.i("prefermset",curOrderId.toString())
                        Toast.makeText(this, "order id updated 😁", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener{
                        Toast.makeText(this, "problem in order id", Toast.LENGTH_SHORT).show()
                    }
            }.addOnFailureListener{
                Log.i("preferm0","failed")
            }


    }



    private lateinit var builder: AlertDialog
    private fun addOrder(pnlist: ArrayList<Any>, pqlist: ArrayList<Any>, pplist: ArrayList<Any>) {
        val data= hashMapOf<String, Any>()
        val preferences= this.getSharedPreferences("userr", MODE_PRIVATE)
        val currentDate = SimpleDateFormat("dd/M/yyyy HH:mm:ss").format(Date()) //date time fetch


        data["dateTime"]= currentDate
        data["pNameList"]= pnlist
        data["pQuanList"]= pqlist
        data["pPriceList"]= pplist
        data["totalMoney"]= total.toString()
        data["status"]="Ordered"
        data["userId"]= preferences.getString("numberr","")!!
        data["userName"]= preferences.getString("namee", "hi")!!
        val firestore = Firebase.firestore.collection("allOrders")
        //val key= firestore.document().id
        data["orderId"] = curOrderId.toString()
        data["address"] = intent.getStringExtra("addressFromAddressActivity").toString()
        data["paymentMode"] = "   |   💵 Pay on delivery"

        firestore.document(curOrderId.toString()).set(data).addOnSuccessListener {
            showDialog()
            builder.window?.setBackgroundDrawableResource(android.R.color.transparent)
            builder.show()

            deleteFromCart()
            Toast.makeText(this,"We got your order", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(this,"kuch to gdbd h", Toast.LENGTH_SHORT).show()
        }


    }

    private fun showDialog() {
        var view : View= LayoutInflater.from(this).inflate(R.layout.dialog, null)
        val homeButton :Button = view.findViewById(R.id.homeButton)
        val myOrdersButton :Button = view.findViewById(R.id.myOrdersButton)
        val oderNoTV :TextView = view.findViewById(R.id.orderNoTV)
        oderNoTV.text= ("Your order no. is ${curOrderId}")
        homeButton.setOnClickListener {
//            Toast.makeText(this,"dgd",Toast.LENGTH_SHORT).show()
            val intent =Intent(this,MainActivity::class.java)
            intent.putExtra("check","checkout")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent)

        }
        myOrdersButton.setOnClickListener {
//            Toast.makeText(this,"dgd",Toast.LENGTH_SHORT).show()
            val intent =Intent(this,MainActivity::class.java)
            intent.putExtra("goToMore","checkout")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent)

        }

        builder= AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(false)
            .create()
        builder.window?.setBackgroundDrawableResource(android.R.color.transparent)
        builder.show()
    }

    private fun deleteFromCart() {
        val dao = AppDatabase.getInstance(this).productDao()
        for(data in pilist){
            lifecycleScope.launch {
                dao.deleteProduct(ProductModel(data.toString()))

            }

        }
    }


}