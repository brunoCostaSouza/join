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
import br.com.bruno.join.extensions.unFormatMoney
import br.com.bruno.join.repository.ITransacaoRepository
import com.vicpin.krealmextensions.deleteAll
import com.vicpin.krealmextensions.queryAll
import com.vicpin.krealmextensions.queryFirst
import com.vicpin.krealmextensions.save
import io.reactivex.subjects.PublishSubject
import java.util.*

class TransacaoViewModel(
        val context: Context,
        val idTransacao: Long?,
        val repository: ITransacaoRepository
): ViewModel() {

    var transacao: Transacao
    var valor = ObservableField<String>("")
    var descricao = ObservableField<String>("")
    var categoria = ObservableField<Categoria>()
    var tipoTransacao = ObservableField<TipoTransacao>()
    var categorias = ObservableField<List<Categoria>>()
    var showALert = PublishSubject.create<String>()
    var showSuccess = PublishSubject.create<String>()

    init {
        transacao = if(idTransacao == null) {
            Transacao()
        } else {
            Transacao().queryFirst {equalTo("id", idTransacao)}!!
        }
        tipoTransacao.set(TipoTransacao.RECEITA)

        if (Categoria().queryAll().isEmpty()) {
            Categoria().apply {
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
            }

            Categoria().apply {
                id = 4L
                descricao = "Manutenção"
                resorce = R.drawable.ic_car
                save()
            }

            Categoria().apply {
                id = 5L
                descricao = "Supermercado"
                resorce = R.drawable.ic_supermarket
                save()
            }

            Categoria().apply {
                id = 6L
                descricao = "Cartão de crédito"
                resorce = R.drawable.ic_payment
                save()
            }

            Categoria().apply {
                id = 7L
                descricao = "Combustível"
                resorce = R.drawable.ic_gas
                save()
            }

            Categoria().apply {
                id = 8L
                descricao = "Lazer"
                resorce = R.drawable.ic_fan
                save()
            }
        }
    }

    fun salvarTransacao() {
        if (validarCampos()) {
            transacao.let {
                it.categoria = categoria.get()
                it.tipo = tipoTransacao.get()!!.name
                it.descricao = descricao.get()!!
                it.data = Date()
                it.valor = valor.get()!!.unFormatMoney()?:0.0
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
            val idTransacao: Long?,
            val repository: ITransacaoRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = TransacaoViewModel(context, idTransacao, repository) as T
    }
}