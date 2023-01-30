package abhishekg.pkart.activity

import abhishekg.pkart.MainActivity
import abhishekg.pkart.R
import abhishekg.pkart.databinding.ActivityProductDetailsBinding
import abhishekg.pkart.roomdb.AppDatabase
import abhishekg.pkart.roomdb.ProductDao
import abhishekg.pkart.roomdb.ProductModel
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductDetailsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProductDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProductDetailsBinding.inflate(layoutInflater)

        getProductDetails(intent.getStringExtra("id"))

        setContentView(binding.root)
    }

    private fun getProductDetails(proId: String?) {
        Firebase.firestore.collection("products")
            .document(proId!!).get().addOnSuccessListener {
                val list = it.get("productImages") as ArrayList<String>
                val name=it.getString("productName")
                val productSp= it.getString("productSp")
                val productDesc=it.getString("productDescription")
                binding.textView7.text = name
                binding.textView9.text = productDesc
                binding.textView8.text = productSp
                val slideList = ArrayList<SlideModel>()
                for(data in list){
                    slideList.add(SlideModel(data, ScaleTypes.CENTER_CROP))
                }
                binding.imageSlider.setImageList(slideList)

               cartAction(proId, name, productSp, it.getString("productCoverImg"))
            }.addOnFailureListener{
                Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cartAction(proId: String, name: String?, productSp: String?, coverImg: String?) {

        val productDao= AppDatabase.getInstance(this).productDao()
        if(productDao.isExit(proId) != null){
            binding.textView10.text="Go To Cart"
        }else{
            binding.textView10.text="AddTo Cart"
        }

        binding.textView10.setOnClickListener{
            if(productDao.isExit(proId) != null){
               openCart()
            }else{
               addToCart(productDao, proId, name, productSp, coverImg,"1")
            }
        }
    }

    private fun addToCart(productDao: ProductDao, proId: String, name: String?, productSp: String?, coverImg: String?, proQuan: String?) {
        val data = ProductModel(proId, name, coverImg, productSp, proQuan)
        lifecycleScope.launch(Dispatchers.IO){
            productDao.insertProduct(data)
            binding.textView10.text="Go To Cart"



        }
    }

    private fun openCart() {
//        val preference= this.getSharedPreferences("info", MODE_PRIVATE)
//        val editor = preference.edit()
//        editor.putBoolean("isCart", true)
//        editor.apply()

        val intent =Intent(this,MainActivity::class.java)
        intent.putExtra("cameFromPD","true")
        startActivity(intent)
//        startActivity(Intent(this, MainActivity ::class.java))
        finish()

    }
}