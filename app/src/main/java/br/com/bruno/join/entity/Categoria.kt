package br.com.bruno.join.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Bruno Costa on 09/08/2018.
 */
@Entity
class Categoria (

        @PrimaryKey(autoGenerate = true)
        var idCategoria: Long = 0,

        @ColumnInfo(name = "descricao_categoria")
        var descricao: String)