package abhishekg.pkart.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import abhishekg.pkart.R
import abhishekg.pkart.activity.AddressActivity
import abhishekg.pkart.activity.CatagoryActivity
import abhishekg.pkart.adapter.CartAdapter
import abhishekg.pkart.databinding.FragmentCartBinding
import abhishekg.pkart.databinding.FragmentHomeBinding
import abhishekg.pkart.roomdb.AppDatabase
import abhishekg.pkart.roomdb.ProductModel
import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity


class CartFragment : Fragment() {
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
            
        }

        return binding.root
    }

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


}