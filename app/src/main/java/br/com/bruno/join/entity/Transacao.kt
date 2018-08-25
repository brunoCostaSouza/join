package br.com.bruno.join.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import br.com.bruno.join.converter.DateConverter
import br.com.bruno.join.enums.TipoTransacao
import java.math.BigDecimal
import java.util.*

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