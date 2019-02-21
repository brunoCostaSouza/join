package br.com.bruno.join.entity

/**
 * Created by Bruno Costa on 09/08/2018.
 */
//@Entity
class Resumo(

        //@PrimaryKey(autoGenerate = true)
        var id: Long = 0,

        //@ColumnInfo(name = "despesas")
        var despesas: Double? = 0.0,

        //@ColumnInfo(name = "receitas")
        var receitas: Double? = 0.0,

        //@Ignore
        var saldo: Double? = 0.0)