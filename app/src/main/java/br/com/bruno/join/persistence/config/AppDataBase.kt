package br.com.bruno.join.persistence.config

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import br.com.bruno.join.entity.Categoria
import br.com.bruno.join.entity.Conta
import br.com.bruno.join.entity.Resumo
import br.com.bruno.join.entity.Transacao
import br.com.bruno.join.persistence.dao.TransacaoDao

/**
 * Created by Bruno Costa on 10/08/2018.
 */
@Database(entities = arrayOf(Transacao::class, Resumo::class, Categoria::class, Conta::class), version = 1, exportSchema = false)
abstract class AppDataBase : RoomDatabase(){
    abstract fun transacaoDao(): TransacaoDao
}