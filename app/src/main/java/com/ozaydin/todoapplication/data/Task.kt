package com.ozaydin.todoapplication.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import kotlinx.parcelize.Parcelize
import java.util.*
@Parcelize
@Entity(tableName = "task") // var changed to val!
data class Task(
    @ColumnInfo(name = "title")
    val title:String? = null,
    @ColumnInfo(name = "description")
    val description:String? = null,
    @ColumnInfo(name = "date")
    val date: String? = null,
    @ColumnInfo(name = "time")
    val time: String? = null,     // LocalDateTime or LocalDate or Date which one should be used?
    @ColumnInfo(name = "is_done") // request_code for alarm must unique
    val isDone: Boolean? = null,
    @ColumnInfo(name = "priority")
    val priority : String? = "",
    @ColumnInfo(name = "alarm_id")
    val alarmId : Long = 0,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
) :Parcelable
/*
    category, color sections can be added to the database.
 */



object DateConverter {  // use a date convertor to store date type
    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return if (timestamp == null) null else Date(timestamp)
    }

    @TypeConverter
    fun toTimestamp(date: Date?): Long? {
        return date?.time
    }
}