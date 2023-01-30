package abhishekg.pkart.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import abhishekg.pkart.R
import abhishekg.pkart.adapter.CategoryAdapter
import abhishekg.pkart.adapter.ProductAdapter
import abhishekg.pkart.databinding.FragmentHomeBinding
import abhishekg.pkart.model.AddProductModel
import abhishekg.pkart.model.CategoryModel
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat.getCategory
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.ArrayList


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentHomeBinding.inflate(layoutInflater)

//        val preference= requireContext().getSharedPreferences("info", AppCompatActivity.MODE_PRIVATE)
//        if(preference.getBoolean("isCart", false)){
////            findNavController().navigate(R.id.action_homeFragment_to_cartFragment)
//        }

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

//        Firebase.firestore.collection("slider").document("item")
//            .get().addOnSuccessListener {
//                Glide.with(requireContext()).load(it.get("img")).into(binding.sliderImage)
//            }
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
                binding.productRecycler.adapter = ProductAdapter(requireContext(), list)
            }
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