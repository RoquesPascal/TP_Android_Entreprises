package com.example.a00_tp_android

import androidx.room.TypeConverter
import java.util.*

class DateConverter
{
    @TypeConverter
    fun toDate(value: Long?): Date?
    {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun toLong(value: Date?): Long?
    {
        return value?.time
    }
}
