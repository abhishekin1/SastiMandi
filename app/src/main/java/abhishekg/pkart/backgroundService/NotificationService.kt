package abhishekg.pkart.backgroundService

import abhishekg.pkart.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


@Suppress("DEPRECATION")
class NotificationService : Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
    var i=0
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var preferences= this.getSharedPreferences("userr", MODE_PRIVATE)
        Toast.makeText(this,preferences.getString("numberr","lkl")!!,Toast.LENGTH_SHORT).show()

        startBackground()

        var db: FirebaseDatabase = FirebaseDatabase.getInstance()
        val datasetRef = db.getReference("notifications")
            .child(preferences.getString("numberr","lkl")!!)
        datasetRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
//
                val content= dataSnapshot.child("content").getValue()?.toString() ?: ""
                val userName= dataSnapshot.child("userName").getValue()?.toString() ?: ""
                Log.e("hiiii", "data change in notification")
                if(i!=0){
                    showNotification(userName,content)
                }
                i++;

            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors that occur during the data retrieval
                Log.e("hi", "Error retrieving data", databaseError.toException())
            }
        })


        return START_STICKY

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun startBackground() {
        val channelId = "run_app"
        val notificationManager = getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(channelId,"run app", NotificationManager.IMPORTANCE_NONE)
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, channelId)
//            .setContentText("$content")
            .setSmallIcon(R.drawable.empty_cart)
            .setAutoCancel(true)
            .build()
//        notificationManager.notify(2, notification)
        startForeground(1,notification)

    }

    var notificationId=2;
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification(userName: String?, content: String) {

        val channelId = "imp_noti"
        val notificationManager = getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(channelId,"Important Notifications", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Namaste $userName 🙏")
            .setContentText("$content")
            .setSmallIcon(R.drawable.empty_cart)
            .setAutoCancel(true)
            .build()
            if(notificationId>=20){
                notificationId=2
            }
            notificationManager.notify(++notificationId, notification)



//        stopForeground(false)







    }


}