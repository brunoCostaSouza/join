package br.com.bruno.join.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.util.*

/**
 * Created by Bruno Costa on 09/08/2018.
*/
@RealmClass
open class Transaction: RealmObject(){
    @PrimaryKey
    open var id: Long = 0
    open var valor: Double = 0.0
    open var descricao: String = ""
    open var categoria: Categoria? = null
    open var tipo: String? = null
    open var data: Date? = null
    open var consolidado: Boolean? = null
}