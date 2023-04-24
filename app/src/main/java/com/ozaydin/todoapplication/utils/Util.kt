package com.ozaydin.todoapplication.utils

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.ozaydin.todoapplication.R
import com.ozaydin.todoapplication.data.Task
import com.ozaydin.todoapplication.notification.AlarmReceiver
import java.util.*

class Util {
    companion object {
        var NOTIFICATION = "notification"
        var NOTIFICATION_LIST = "NOTIFICATION_LIST"
        val CHANNEL_NAME = "Test Channel"
        val CHANNEL_ID = "0"
    }
}

@SuppressLint("UnspecifiedImmutableFlag")
fun createChannel(channelId: String, context: Context, task: Task) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // API 26 TODO create a function that lower APIs
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val (hours, min) = task.time!!.split(":").map { it.toInt() }
        val (year, month, day) = task.date!!.split("-")
            .map { it.toInt() } // if the date is saved as null, app always crushes
        val calendar = Calendar.getInstance()
        //calendar.set(Calendar.AM_PM, Calendar.AM);

        /*calendar.timeInMillis = System.currentTimeMillis()
        calendar.clear()*/
        calendar.set(year, month - 1, day, hours, min)
        if (calendar.timeInMillis >= System.currentTimeMillis()) {
            // Create the notification
            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.vc_done)
                .setContentTitle(task.title)
                .setContentText(task.description)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                //.setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC).build()
            println("the value of calendar is :  ${calendar.time}")
            // Create an intent to launch the notification
            val intent = Intent(context, AlarmReceiver::class.java)
            intent.putExtra(Util.NOTIFICATION, notification)

            val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // API 31
                PendingIntent.getBroadcast(
                    context,
                    (0..100000).random(),
                    intent,
                    PendingIntent.FLAG_MUTABLE
                )
            } else {
                PendingIntent.getBroadcast(
                    context, (0..100000).random(), intent, PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
            alarmManager?.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }
}

fun createNotification(task: Task, context: Context): Notification {
    val notificationBuilder = NotificationCompat.Builder(context, Util.CHANNEL_ID)
        .setSmallIcon(R.drawable.vc_done)
        .setContentTitle(task.title)
        .setContentText(task.description)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
    return notificationBuilder.build()
}

fun cancelAlarm(context: Context, id: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    alarmManager.cancel(pendingIntent)
}