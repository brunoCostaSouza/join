package br.com.bruno.join.persistence.config

import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

/**
 * Created by Bruno Costa on 10/08/2018.
 */
class ConfigDB(private val context: Context) {
    fun config(): RoomDatabase.Builder<AppDataBase> {
        return Room.databaseBuilder(context, AppDataBase::class.java, "joinbrdb")
    }
}