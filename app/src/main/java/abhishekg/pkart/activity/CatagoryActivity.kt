package abhishekg.pkart.activity

import abhishekg.pkart.R
import abhishekg.pkart.adapter.CategoryProductAdapter
import abhishekg.pkart.adapter.ProductAdapter
import abhishekg.pkart.model.AddProductModel
import abhishekg.pkart.roomdb.AppDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.ArrayList

class CatagoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catagory)

        getProducts(intent.getStringExtra("cat" ))
    }

    private fun getProducts(category: String?) {
        val list = ArrayList<AddProductModel>()
        Firebase.firestore.collection("products").whereEqualTo("productCategory",category)
            .get().addOnSuccessListener {
                list.clear()
                for(doc in it.documents){
                    val data = doc.toObject(AddProductModel::class.java)
                    list.add(data!!)

                }
                Log.i("hh",list.size.toString())
                val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
                recyclerView.adapter = ProductAdapter(this, list)
                val dao = AppDatabase.getInstance(this).productDao()
                dao.getAllProducts().observe(this){
                    recyclerView.adapter = ProductAdapter(this, list)

                }
            }
    }
}