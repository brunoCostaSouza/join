package br.com.bruno.join.viewModel

import android.content.Context
import android.view.View
import android.view.ViewParent
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.bruno.join.entity.Categoria
import br.com.bruno.join.entity.Transacao
import br.com.bruno.join.enums.TipoTransacao
import br.com.bruno.join.extensions.unFormatMoney
import com.vicpin.krealmextensions.queryAll
import com.vicpin.krealmextensions.queryFirst
import io.reactivex.subjects.PublishSubject
import java.util.*

class TransacaoViewModel(
        val context: Context,
        val idTransacao: Long?
): ViewModel() {

    var transacao: Transacao
    var valor = ObservableField<String>()
    var descricao = ObservableField<String>("")
    var categoria = ObservableField<Categoria>()
    var tipoTransacao = ObservableField<TipoTransacao>(TipoTransacao.DESPESA)
    var categorias = ObservableField<List<Categoria>>()
    var showALert = ObservableField<String>()

    init {
        transacao = if(idTransacao == null) {
            Transacao()
        } else {
            Transacao().queryFirst {equalTo("id", idTransacao)}!!
        }

        /*Categoria().apply {
            id = 1L
            descricao = "Salário"
            resorce = R.drawable.ic_money
            save()
        }

        Categoria().apply {
            id = 2L
            descricao = "Viagem"
            resorce = R.drawable.ic_travel
            save()
        }

        Categoria().apply {
            id = 3L
            descricao = "Alimentação"
            resorce = R.drawable.ic_food
            save()
        }*/

    }

    fun addReceita() {
        transacao.categoria = categoria.get()
        transacao.tipo = tipoTransacao.get()!!.name
        transacao.descricao = descricao.get()!!
        transacao.data = Date()
        transacao.valor = valor.get()?.unFormatMoney()?:0.0
    }

    fun addDespesa() {
        showALert.set("..")
    }

    fun getCategorias() {
        val list = mutableListOf<Categoria>()
        list.add(getCategoriaVazia())
        list.addAll(Categoria().queryAll())
        categorias.set(list)
    }

    fun clickTipoTransacao() {
        tipoTransacao.set(if (tipoTransacao.get()!! == TipoTransacao.RECEITA) TipoTransacao.DESPESA else TipoTransacao.RECEITA)
    }

    fun itemSelecionado(parent: ViewParent, view: View, position: Int, id: Long) {
        categoria.set(categorias.get()!![position])
    }

    private fun getCategoriaVazia(): Categoria {
        val cat = Categoria()
        cat.id = -1
        return cat
    }

    class Factory(
            val context: Context,
            val idTransacao: Long?
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = TransacaoViewModel(context, idTransacao) as T
    }
}