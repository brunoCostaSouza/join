package br.com.bruno.join.persistence.config

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Created by Bruno Costa on 10/08/2018.
 */
class ConfigDB(private val context: Context) {
    fun config(): RoomDatabase.Builder<AppDataBase> {
        if (Room.inMemoryDatabaseBuilder(context, AppDataBase::class.java) != null) {
            return Room.inMemoryDatabaseBuilder(context, AppDataBase::class.java)
        }
        return Room.databaseBuilder(context, AppDataBase::class.java, "joinbrdb")
    }
}