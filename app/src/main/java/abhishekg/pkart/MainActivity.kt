package abhishekg.pkart

import abhishekg.pkart.activity.LoginActivity
import abhishekg.pkart.backgroundService.NotificationService
import abhishekg.pkart.databinding.ActivityMainBinding
import abhishekg.pkart.fragment.HomeFragment
import abhishekg.pkart.fragment.etext
import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.PopupMenu
import android.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var i=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)






        FirebaseMessaging.getInstance().subscribeToTopic("notification")
        if(!(isServiceRunning(NotificationService::class.java))){
            val intent = Intent(this, NotificationService::class.java)
            ContextCompat.startForegroundService(this, intent);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            startForegroundService(intent)

        }else{
            startService(intent)
        }

        if(FirebaseAuth.getInstance().currentUser==null){
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        val navHostFragment=supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        val navController= navHostFragment!!.findNavController()

        var c=0
        if(intent.hasExtra("cameFromPD") && c==0){
            c=1
            navController.navigate((R.id.cartFragment))
        }
        if(intent.hasExtra("goToMore") && c==0){
            c=1
            navController.navigate((R.id.moreFragment))
        }



        ///////smoooth ttom//////////
        val popupMenu= PopupMenu(this,null)
        popupMenu.inflate(R.menu.bottom_nav)
        binding.bottomBar.setupWithNavController(popupMenu.menu,navController)
        binding.bottomBar.onItemSelected={
            when(it){
                0->{

                    navController.navigate((R.id.homeFragment))
                }
                1->  navController.navigate((R.id.homeFragment))
                2-> navController.navigate((R.id.homeFragment))

            }
        }
        /////////////////////////////////


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

    fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningServices = activityManager.getRunningServices(Integer.MAX_VALUE)
        for (service in runningServices) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
    override fun onBackPressed(){
        super.onBackPressed()


        if(etext!=""){
            etext=""

        }
        if(i==0){
            finish()
        }
    }
}