package br.com.bruno.join.converter

import androidx.room.TypeConverter
import java.util.*

/**
 * Created by Bruno Costa on 10/08/2018.
 */
class DateConverter {

    @TypeConverter
    fun fromTimestemp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestemp(data: Date?): Long? {
        return data?.time
    }
}