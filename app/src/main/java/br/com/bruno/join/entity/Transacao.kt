package br.com.bruno.join.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import br.com.bruno.join.converter.DateConverter

/**
 * Created by Bruno Costa on 09/08/2018.
 */
@Entity
class Transacao (

        @PrimaryKey(autoGenerate = true)
        var id: Long = 0,

        @ColumnInfo(name = "valor")
        var valor: Double,

        @ColumnInfo(name = "descricao")
        var descricao: String,

        @ColumnInfo(name = "categoria_id")
        var categoriaId: Long,

        @ColumnInfo(name = "tipo")
        var tipo: String,

        @ColumnInfo(name = "conta_id")
        var contaId: Long,

        @ColumnInfo(name = "data")
        @TypeConverters(DateConverter::class)
        var dataTransacao: Long)