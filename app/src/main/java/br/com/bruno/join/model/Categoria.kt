package br.com.bruno.join.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

/**
 * Created by Bruno Costa on 09/08/2018.
 */
@RealmClass
open class Categoria : RealmObject() {

    @PrimaryKey
    open var id: Long = 0
    open var resorce: Int? = null
    open var descricao: String? = null
    open var colorIcon: Int? = null
}