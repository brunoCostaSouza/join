package br.com.bruno.join.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Bruno Costa on 09/08/2018.
 */
@Entity
class Conta (

        @PrimaryKey(autoGenerate = true)
        var id: Long = 0,

        @ColumnInfo(name = "nome")
        var nome: String,

        @ColumnInfo(name = "ativo")
        var ativo: Boolean = true)