package com.ozaydin.todoapplication.utils

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.ozaydin.todoapplication.R
import com.ozaydin.todoapplication.data.Task
import com.ozaydin.todoapplication.notification.AlarmReceiver
import com.ozaydin.todoapplication.presentation.TaskList
import java.util.*

class Util {
    companion object {
        var NOTIFICATION = "notification"
        var NOTIFICATION_LIST = "NOTIFICATION_LIST"
        val CHANNEL_NAME = "Reminder"
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
        val notificationIntent = Intent(context, TaskList::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pIntent= if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // API 31
            PendingIntent.getBroadcast( // every pending intent must be unique to show different notifications.
                context,
                0,
                notificationIntent,
                PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        val iconDrawable: Drawable = ContextCompat.getDrawable(context, R.drawable.vc_done)!!
        val iconBitmap: Bitmap = iconDrawable.toBitmap()
      /*  val contentIntent = PendingIntent.getActivity(
            context,
            0, notificationIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )*/

        val contentIntent= if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // start activity from notification
            PendingIntent.getActivity(
                context,
                0,
                notificationIntent,
                PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getActivity(
                context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        val nm: NotificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        calendar.set(year, month - 1, day, hours, min,0)
        if (calendar.timeInMillis > System.currentTimeMillis()) { // set alarm if the time is in future


            val sb: Spannable = SpannableString(task.title)
            sb.setSpan(StyleSpan(Typeface.BOLD), 0, sb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            val notificationBuilder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.vc_done)
                .setChannelId(channelId)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setWhen(calendar.timeInMillis)
                .setContentIntent(contentIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setStyle(NotificationCompat
                    .InboxStyle()
                    .addLine(task.description)
                    //.addLine("another line")
                    .setBigContentTitle(sb)
                    .setSummaryText("Reminder"))
                .build()
            val intent = Intent(context, AlarmReceiver::class.java) // Create an intent to launch the notification
            intent.putExtra(Util.NOTIFICATION, notificationBuilder)

            val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // API 31
                PendingIntent.getBroadcast( // every pending intent must be unique to show different notifications.
                    context,
                    task.alarmId.toInt(),
                    intent,
                    PendingIntent.FLAG_MUTABLE
                )
            } else {
                PendingIntent.getBroadcast(
                    context, task.alarmId.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT
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
    val notificationBuilder = NotificationCompat.Builder(context, getUniqueId().toString())
        .setSmallIcon(R.drawable.vc_done)
        .setContentTitle(task.title)
        .setContentText(task.description)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)



    return notificationBuilder.build()
}
fun cancelAlarm(context: Context, id: Long) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java)
    val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // API 31
        PendingIntent.getBroadcast(
            context,
            id.toInt(),
            intent,
            PendingIntent.FLAG_MUTABLE
        )
    } else {
        PendingIntent.getBroadcast(
            context, id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
    alarmManager.cancel(pendingIntent)
}

fun String.capitalized(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase())
            it.titlecase(Locale.getDefault())
        else it.toString()
    }
}

fun getUniqueId() = ((System.currentTimeMillis() % 1000000).toLong())
