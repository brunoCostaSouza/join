package br.com.bruno.join.entity

import androidx.room.*
import br.com.bruno.join.converter.DateConverter

/**
 * Created by Bruno Costa on 09/08/2018.
 */
@Entity
class Transacao (

        @PrimaryKey(autoGenerate = true)
        var _id: Long = 0,

        @ColumnInfo(name = "valor")
        var valor: Double = 0.0,

        @ColumnInfo(name = "descricao_transacao")
        var descricao: String,

        @Embedded
        var categoria: Categoria,

        @ColumnInfo(name = "tipo")
        var tipo: String,

        @ColumnInfo(name = "conta_id")
        var contaId: Long,

        @ColumnInfo(name = "data")
        @TypeConverters(DateConverter::class)
        var dataTransacao: Long)