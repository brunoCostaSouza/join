package br.com.bruno.join.viewModel

import android.content.Context
import android.view.View
import android.view.ViewParent
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.bruno.join.model.Categoria
import br.com.bruno.join.model.Transaction
import br.com.bruno.join.enums.TypeTransaction
import br.com.bruno.join.extensions.formataData
import br.com.bruno.join.extensions.unFormatData
import br.com.bruno.join.extensions.unFormatMoney
import br.com.bruno.join.repository.contract.ITransactionRepository
import com.vicpin.krealmextensions.queryAll
import com.vicpin.krealmextensions.queryFirst
import formatMoney
import io.reactivex.subjects.PublishSubject

class TransactionViewModel(
    val context: Context,
    private val idTransacao: Long?,
    private val repository: ITransactionRepository
) : ViewModel() {

    var transaction: Transaction = if (idTransacao == null) {
        Transaction()
    } else {
        Transaction().queryFirst { equalTo("id", idTransacao) }!!
    }

    var valor = ObservableField<String>()
    var descricao = ObservableField<String>("")
    var dataTransacao = ObservableField<String>()
    var categoria = ObservableField<Categoria>()
    var consolidado = ObservableField<Boolean>(true)
    var tipoTransacao = ObservableField<TypeTransaction>()
    var categorias = ObservableField<List<Categoria>>()
    var showALert = PublishSubject.create<String>()
    var showSuccess = PublishSubject.create<String>()

    init {
        tipoTransacao.set(TypeTransaction.RECEITA)
    }

    fun setupViews() {
        if (idTransacao != null) {
            valor.set(transaction.valor.formatMoney())
            descricao.set(transaction.descricao)
            dataTransacao.set(transaction.data?.formataData())
            categoria.set(transaction.categoria!!)
            consolidado.set(transaction.consolidado)
        }
    }

    fun salvarTransacao() {
        if (validarCampos()) {
            transaction.let {
                it.categoria = categoria.get()
                it.tipo = tipoTransacao.get()!!.name
                it.descricao = descricao.get()!!
                it.valor = valor.get()!!.unFormatMoney()
                it.data = dataTransacao.get()!!.unFormatData()
                it.consolidado = consolidado.get()
            }
            repository.saveTransaction(transaction)
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

    fun checkConsolidado() {
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
        val repository: ITransactionRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            TransactionViewModel(context, idTransacao, repository) as T
    }
}