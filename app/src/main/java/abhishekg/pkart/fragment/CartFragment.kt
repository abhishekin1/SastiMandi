package abhishekg.pkart.fragment

import abhishekg.pkart.MainActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import abhishekg.pkart.R
import abhishekg.pkart.activity.AddressActivity
import abhishekg.pkart.activity.CatagoryActivity
import abhishekg.pkart.adapter.CartAdapter
import abhishekg.pkart.adapter.FrequentlyBoughtAdapter
import abhishekg.pkart.adapter.ProductAdapter
import abhishekg.pkart.databinding.FragmentCartBinding
import abhishekg.pkart.databinding.FragmentHomeBinding
import abhishekg.pkart.model.AddProductModel
import abhishekg.pkart.roomdb.AppDatabase
import abhishekg.pkart.roomdb.ProductModel
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class CartFragment : Fragment() {
    private lateinit var builder: AlertDialog
    private lateinit var binding: FragmentCartBinding
    private lateinit var list: ArrayList<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentCartBinding.inflate(layoutInflater)

        val preference= requireContext().getSharedPreferences("info", AppCompatActivity.MODE_PRIVATE)
        val editor = preference.edit()
        editor.putBoolean("isCart", false)
        editor.apply()
//        getProducts()
        val dao = AppDatabase.getInstance(requireContext()).productDao()
        list=ArrayList()


        dao.getAllProducts().observe(requireActivity()){
            binding.cartRecycler.adapter= CartAdapter(requireContext(), it)
            //toFindcost below code
            list.clear()
            for(data in it){
                list.add(data.productId)
            }
            totalCost(it)
            if(dao.getSize()=="0"){
                showDialog()
            }
            
        }

        return binding.root
    }

/////unimplimemented frequently bought items///////////////////////////
//    private fun getProducts() {
//        val list = java.util.ArrayList<AddProductModel>()
//        Firebase.firestore.collection("products")
//            .get().addOnSuccessListener {
//                list.clear()
//                for(doc in it.documents){
//                    val data = doc.toObject(AddProductModel::class.java)
//                    list.add(data!!)
//                }
//                binding.recycler3.adapter = FrequentlyBoughtAdapter(requireContext(), list)
//                val dao = AppDatabase.getInstance(requireContext()).productDao()
//                dao.getAllProducts().observe(requireActivity()){
//                    binding.recycler3.adapter = FrequentlyBoughtAdapter(requireContext(), list)
//
//                }
//            }
//    }

    private fun totalCost(data: List<ProductModel>?) {
        var total=0
        var quan=0
        for(item in data!! ){
            total+= item.productSp!!.toInt() * item.productQuan!!.toInt()
            quan+= item.productQuan!!.toInt()
        }

        binding.textView12.text= "Total item in cart is ${quan}"
        binding.textView13.text= "Total cost : $total"
        binding.checkout.setOnClickListener {

            for(data in list){
                Log.i("cartfrag", data)

            }
            val intent= Intent(context, AddressActivity::class.java)
            intent.putExtra("totalCost", total.toString())
            intent.putExtra("productIds", list)
            startActivity(intent)
        }



    }
    private fun showDialog() {
        var view : View= LayoutInflater.from(requireContext()).inflate(R.layout.dialog_standered, null)
        val homeButton : Button = view.findViewById(R.id.buttonSingle)

        homeButton.setOnClickListener {
//            Toast.makeText(this,"dgd",Toast.LENGTH_SHORT).show()
            val intent =Intent(requireContext(), MainActivity::class.java)
            intent.putExtra("check","checkout")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent)

        }

        builder= AlertDialog.Builder(requireContext())
            .setView(view)
            .setCancelable(false)
            .create()
        builder.window?.setBackgroundDrawableResource(android.R.color.transparent)
        builder.show()
    }


}