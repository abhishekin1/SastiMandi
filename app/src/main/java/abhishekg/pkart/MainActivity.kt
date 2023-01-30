package abhishekg.pkart

import abhishekg.pkart.activity.LoginActivity
import abhishekg.pkart.databinding.ActivityMainBinding
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.PopupMenu
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var i=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(FirebaseAuth.getInstance().currentUser==null){
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        val navHostFragment=supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        val navController= navHostFragment!!.findNavController()

//        val preference= this.getSharedPreferences("info", AppCompatActivity.MODE_PRIVATE)
//        if(preference.getBoolean("isCart", false)){
//            navController.navigate((R.id.cartFragment))
//        }
        if(intent.hasExtra("cameFromPD")){
            navController.navigate((R.id.cartFragment))
        }
        if(intent.hasExtra("goToMore")){
            navController.navigate((R.id.moreFragment))
        }

        val popupMenu= PopupMenu(this,null)
        popupMenu.inflate(R.menu.bottom_nav)
        binding.bottomBar.setupWithNavController(popupMenu.menu,navController)
        binding.bottomBar.onItemSelected={
            when(it){
                0->{
                    i=0;
                    navController.navigate((R.id.homeFragment))
                }
                1-> i=1 ; //navController.navigate((R.id.cartFragment))
                2-> i=2 //navController.navigate((R.id.moreFragment))

            }
        }


//        navController.addOnDestinationChangedListener(object: NavController.OnDestinationChangedListener{
//            override fun onDestinationChanged(
//                controller: NavController,
//                destination: NavDestination,
//                arguments: Bundle?
//            ) {
//                title= when(destination.id){
//                    R.id.cartFragment->"My Cart"
//                    R.id.moreFragment->"My Dashboard"
//                    else-> "P-Kart"
//                }
//            }
//
//        })
    }
    override fun onBackPressed(){
        super.onBackPressed()
        if(i==0){
            finish()
        }
    }
}