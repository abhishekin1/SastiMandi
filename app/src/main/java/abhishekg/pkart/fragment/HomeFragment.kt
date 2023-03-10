package abhishekg.pkart.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import abhishekg.pkart.adapter.CategoryAdapter
import abhishekg.pkart.adapter.ProductAdapter
import abhishekg.pkart.databinding.FragmentHomeBinding
import abhishekg.pkart.model.AddProductModel
import abhishekg.pkart.model.CategoryModel
import abhishekg.pkart.roomdb.AppDatabase
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.load.engine.executor.GlideExecutor.UncaughtThrowableStrategy.LOG
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

var filter=0;
var etext=""
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
//    public lateinit var etext: String



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentHomeBinding.inflate(layoutInflater)
        etext="";


        getCategories()

        getSliderImage()
        getProducts()




        return binding.root
    }



    private fun getSliderImage() {
        Firebase.firestore.collection("slider")
            .document("item").get().addOnSuccessListener {
                val list = it.get("img") as ArrayList<String>

                val slideList = ArrayList<SlideModel>()
                for(data in list){
                    slideList.add(SlideModel(data, ScaleTypes.CENTER_CROP))
                }
                binding.imageSlider.setImageList(slideList)


            }.addOnFailureListener{
                Toast.makeText(requireContext(), "something went wrong", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getProducts() {
        val list = ArrayList<AddProductModel>()
        Firebase.firestore.collection("products")
            .get().addOnSuccessListener {
                list.clear()
                for(doc in it.documents){
                    val data = doc.toObject(AddProductModel::class.java)
                    list.add(data!!)
                }
                val dao = AppDatabase.getInstance(requireContext()).productDao()
                dao.getAllProducts().observe(requireActivity()){
                    Log.w("mypp", "1");
                    filterList(list);

                }
                binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                    androidx.appcompat.widget.SearchView.OnQueryTextListener {

                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        if(newText!=null){
                            etext=newText;
                        }
                        filterList(list)
                        return false
                    }
                })




            }
    }


    private fun filterList(list: ArrayList<AddProductModel>) {
        val filteredList = ArrayList<AddProductModel>()
        filteredList.clear()
        for(item in list){
             if(item.productName!!.lowercase().contains(etext!!.lowercase())){
//                 Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
                 filteredList.add(item)
             }
        }
        if(etext!=""){
            binding.imageSlider.visibility= View.GONE
            binding.catRL.visibility= View.GONE
            binding.categoryRecycler.visibility= View.GONE

        }else{
            binding.imageSlider.visibility= View.VISIBLE
            binding.catRL.visibility= View.VISIBLE
            binding.categoryRecycler.visibility= View.VISIBLE
        }


            productListShowRecyclear(filteredList)

    }
    private fun productListShowRecyclear(list: ArrayList<AddProductModel>) {
        binding.productRecycler.adapter = ProductAdapter(requireContext(), list)

    }


    private fun getCategories() {
        val list = ArrayList<CategoryModel>()
        Firebase.firestore.collection("categories")
            .get().addOnSuccessListener {
                list.clear()
                for(doc in it.documents){
                    val data = doc.toObject(CategoryModel::class.java)
                    list.add(data!!)
                }
                binding.categoryRecycler.adapter = CategoryAdapter(requireContext(), list)

            }
    }





}

