package br.com.bruno.join.persistence.dao

import androidx.room.*
import br.com.bruno.join.entity.Transacao

/**
 * Created by Bruno Costa on 10/08/2018.
 */
@Dao
interface TransacaoDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(transacao: Transacao)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg transacao: Transacao)

    @Update
    fun update(transacao: Transacao)

    @Delete
    fun remove(transacao: Transacao)

    @Query("SELECT * FROM Transacao WHERE _id = :tId ")
    fun findById(tId: Long): Transacao
}