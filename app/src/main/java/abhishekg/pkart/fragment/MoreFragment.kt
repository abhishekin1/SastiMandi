package abhishekg.pkart.fragment

import abhishekg.pkart.MainActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import abhishekg.pkart.R
import abhishekg.pkart.activity.AddressActivity
import abhishekg.pkart.adapter.AllOrderAdapter
import abhishekg.pkart.databinding.FragmentMoreBinding
import abhishekg.pkart.model.AllOrderModel
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MoreFragment : Fragment() {

    private lateinit var binding: FragmentMoreBinding
    private lateinit var list: ArrayList<AllOrderModel>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentMoreBinding.inflate(layoutInflater)

        list= ArrayList()

        loadOrders()
        loadUserInfo()
        binding.editIcon.setOnClickListener{
            val intent = Intent(requireContext(), AddressActivity::class.java)
            intent.putExtra("CameFromMore", "abd")
            startActivity(intent)
        }


        binding.btnSignOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent= Intent(requireActivity(), MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent)
        }

        return binding.root

    }

    private fun loadUserInfo() {

        val preferences= this.activity?.getSharedPreferences("userr", AppCompatActivity.MODE_PRIVATE)
        Firebase.firestore.collection("users")
            .document(preferences?.getString("numberr", "")!!)
            .get().addOnSuccessListener {

                binding.userName.setText(it.getString("userName"))
                binding.userMobile.setText(it.getString("userPhoneNumber"))
                binding.userAddress.setText("${it.getString("street")}, ${(it.getString("city"))}, ${it.getString("state")}, ${it.getString("pincode")}")
            }
    }

    private fun loadOrders() {
        val preferences= this.activity?.getSharedPreferences("userr", AppCompatActivity.MODE_PRIVATE)

        Firebase.firestore.collection("allOrders")
            .whereEqualTo(
                "userId",
                preferences?.getString("numberr", "")
            )
            .get().addOnSuccessListener {
                list.clear()
                for (doc in it){
                    val data = doc.toObject(AllOrderModel::class.java)
                    list.add(data)

                }
                binding.recyclerView.adapter= AllOrderAdapter(list, requireActivity())

            }

    }

}