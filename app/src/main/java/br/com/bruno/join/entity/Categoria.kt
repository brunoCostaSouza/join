package br.com.bruno.join.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by Bruno Costa on 09/08/2018.
 */
@Entity
class Categoria (

        @PrimaryKey(autoGenerate = true)
        var id: Long = 0,

        @ColumnInfo(name = "descricao")
        var descricao: String)