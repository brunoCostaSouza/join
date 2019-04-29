package br.com.bruno.join.viewModel

import android.content.Context
import android.view.View
import android.view.ViewParent
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.bruno.join.R
import br.com.bruno.join.entity.Categoria
import br.com.bruno.join.entity.Transacao
import br.com.bruno.join.enums.TipoTransacao
import br.com.bruno.join.extensions.formataData
import br.com.bruno.join.extensions.unFormatData
import br.com.bruno.join.extensions.unFormatMoney
import br.com.bruno.join.repository.ITransacaoRepository
import co.metalab.asyncawait.async
import com.vicpin.krealmextensions.deleteAll
import com.vicpin.krealmextensions.queryAll
import com.vicpin.krealmextensions.queryFirst
import com.vicpin.krealmextensions.save
import formatMoney
import io.reactivex.subjects.PublishSubject
import java.util.*

class TransacaoViewModel(
        val context: Context,
        private val idTransacao: Long?,
        private val repository: ITransacaoRepository
): ViewModel() {

    var transacao: Transacao = if(idTransacao == null) {
        Transacao()
    } else {
        Transacao().queryFirst {equalTo("id", idTransacao)}!!
    }

    var valor = ObservableField<String>()
    var descricao = ObservableField<String>("")
    var dataTransacao = ObservableField<String>()
    var categoria = ObservableField<Categoria>()
    var consolidado = ObservableField<Boolean>(true)
    var tipoTransacao = ObservableField<TipoTransacao>()
    var categorias = ObservableField<List<Categoria>>()
    var showALert = PublishSubject.create<String>()
    var showSuccess = PublishSubject.create<String>()

    init {
        tipoTransacao.set(TipoTransacao.RECEITA)
    }

    fun setupViews() {
        if (idTransacao != null) {
            valor.set(transacao.valor.formatMoney())
            descricao.set(transacao.descricao)
            dataTransacao.set(transacao.data?.formataData())
            categoria.set(transacao.categoria!!)
            consolidado.set(transacao.consolidado)
        }
    }

    fun salvarTransacao() {
        if (validarCampos()) {
            transacao.let {
                it.categoria = categoria.get()
                it.tipo = tipoTransacao.get()!!.name
                it.descricao = descricao.get()!!
                it.valor = valor.get()!!.unFormatMoney()?:0.0
                it.data = dataTransacao.get()!!.unFormatData()
                it.consolidado = consolidado.get()
            }
            repository.salvarTransacao(transacao)
            showSuccess.onNext("Transação salva com sucesso.")
        } else {
            showALert.onNext("Informe todos os campos.")
        }
    }

    private fun validarCampos(): Boolean {
        if (categoria.get() == null) return false
        if (descricao.get() == null || descricao.get()!!.trim().isEmpty()) return false
        if (valor.get() == null) return false
        if (categoria.get()!!.id == -1L) return false
        return true
    }

    fun getCategorias() {

            val list = mutableListOf<Categoria>()
            list.add(getCategoriaVazia())
            list.addAll(Categoria().queryAll())
            categorias.set(list)

    }




    fun itemSelecionado(parent: ViewParent, view: View, position: Int, id: Long) {
        categoria.set(categorias.get()!![position])
    }

    fun checkConsolidado(){
        consolidado.set(!consolidado.get()!!)
    }

    private fun getCategoriaVazia(): Categoria {
        val cat = Categoria()
        cat.id = -1
        return cat
    }

    class Factory(
            val context: Context,
            val idTransacao: Long?,
            val repository: ITransacaoRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = TransacaoViewModel(context, idTransacao, repository) as T
    }
}