package com.ozaydin.todoapplication.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.ozaydin.todoapplication.data.Task
import com.ozaydin.todoapplication.repository.TaskRepository
import com.ozaydin.todoapplication.utils.Util
import com.ozaydin.todoapplication.utils.createChannel
import com.ozaydin.todoapplication.utils.getUniqueId
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityScoped
class BootReceiver @Inject constructor(private val taskRepository: TaskRepository) :
    BroadcastReceiver() {
    /*
    * restart reminders alarms when user's device reboots
    * */
    lateinit var alarmList: ArrayList<Task>

    @SuppressLint("ServiceCast")
    override fun onReceive(context: Context, intent: Intent) {
        println("inside boot Receiver")

        // Create and display the notification
        /*val channelId = "my_channel_id"
        val title = "My Notification"
        val message = "This is my notification message"
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.vc_done)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager.notify(0, notificationBuilder.build())*/

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.IO).launch {
                alarmList = (taskRepository.getAllTasks() as ArrayList<Task>?)!!
            }
            // Recreate your alarms here
            alarmList.forEach {
                //val notification =  createNotification(it,context)
                if (it.date != null && it.time != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {  // API 26
                    val channelId = Util.CHANNEL_ID
                    val channelName = Util.CHANNEL_NAME
                    val importance = NotificationManager.IMPORTANCE_DEFAULT
                    val channel =
                        NotificationChannel(channelId.toString(), channelName, importance)
                    val notificationManager =
                        context.getSystemService(NotificationManager::class.java)
                    notificationManager.createNotificationChannel(channel)
                    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // min API level is 31
                                       }*/
                    createChannel(channelId.toString(), context, it)
                }
            }
            /* val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
             val intent = Intent(context, AlarmReceiver::class.java)
             intent.putParcelableArrayListExtra(Util.NOTIFICATION_LIST,alarmList)*/


            /*val pendingIntent =
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + INTERVAL,
                pendingIntent
            )*/
        }
    }

}

/*
Also, keep in mind that the BOOT_COMPLETED broadcast is not guaranteed to be delivered to your app, and some devices may restrict
the use of the setExactAndAllowWhileIdle method. Therefore, it is important to test your app on a variety of devices and configurations to
ensure that the alarms are recreated correctly.
*/
