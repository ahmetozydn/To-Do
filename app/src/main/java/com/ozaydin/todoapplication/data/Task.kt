package com.ozaydin.todoapplication.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.*

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
    @ColumnInfo(name = "is_done")
    val isDone: Boolean? = null,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
)
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