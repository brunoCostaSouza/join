package br.com.bruno.join.viewModel

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.bruno.join.entity.Categoria
import br.com.bruno.join.entity.Transacao
import br.com.bruno.join.enums.TipoTransacao
import com.vicpin.krealmextensions.queryAllAsFlowable
import com.vicpin.krealmextensions.save
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.*

/**
 * Created by Bruno Costa on 09/08/2018.
 */
class MainViewModel(
        val context: Context
): ViewModel() {

    var totalReceita = ObservableField<Double>(0.0)
    var totalDespesa = ObservableField<Double>(0.0)
    var saldo = ObservableField<Double>(0.0)
    var listaTransacoes = PublishSubject.create<List<Transacao>>()
    var composite = CompositeDisposable()

    init {
        initFlowables()
    }

    private fun initFlowables() {
        val query = Transacao().queryAllAsFlowable()
        composite.add(query
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    totalReceita.set(it.filter { it.tipo!! == TipoTransacao.RECEITA.name }.sumByDouble { it.valor })
                    totalDespesa.set(it.filter { it.tipo!! == TipoTransacao.DESPESA.name }.sumByDouble { it.valor })
                    saldo.set(totalReceita.get()!! - totalDespesa.get()!!)
                    listaTransacoes.onNext(it)
                }
        )
    }

    private fun getListaTransacoes(){
        val time = Calendar.getInstance().timeInMillis
        val list = mutableListOf<Transacao>()
        /*list.add(Transacao(descricao = "Salário do mês", categoria = Categoria(descricao = "Remuneração"), valor = 1500.0, dataTransacao = time, tipo = TipoTransacao.RECEITA.name, contaId = 0))
        list.add(Transacao(descricao = "Almoço", categoria = Categoria(descricao = "Alimentação"), valor = 12.5, dataTransacao = time, tipo = TipoTransacao.DESPESA.name, contaId = 0))
        list.add(Transacao(descricao = "Bom bom do félix", categoria = Categoria(descricao = "Sobremesa"), valor = 50.5, dataTransacao = time, tipo = TipoTransacao.TRANSFERENCIA.name, contaId = 0))
        list.add(Transacao(descricao = "Freelance web site", categoria = Categoria(descricao = "Remuneração"), valor = 1050.5, dataTransacao = time, tipo = TipoTransacao.DESPESA.name, contaId = 0))
        list.add(Transacao(descricao = "Manutenção notebook", categoria = Categoria(descricao = "Remuneração"), valor = 75.0, dataTransacao = time, tipo = TipoTransacao.RECEITA.name, contaId = 0))
        list.add(Transacao(descricao = "Cartão Nubank", categoria = Categoria(descricao = "Cartão de crédito"), valor = 930.5, dataTransacao = time, tipo = TipoTransacao.DESPESA.name, contaId = 0))
        list.add(Transacao(descricao = "Cartão crédito Itaú", categoria = Categoria(descricao = "Cartão de crédito"), valor = 900.5, dataTransacao = time, tipo = TipoTransacao.DESPESA.name, contaId = 0))
        list.add(Transacao(descricao = "Compras do mês", categoria = Categoria(descricao = "Casa"), valor = 810.5, dataTransacao = time, tipo = TipoTransacao.DESPESA.name, contaId = 0))
        list.add(Transacao(descricao = "Água", categoria = Categoria(descricao = "Casa"), valor = 50.5, dataTransacao = time, tipo = TipoTransacao.DESPESA.name, contaId = 0))
        list.add(Transacao(descricao = "Internet", categoria = Categoria(descricao = "Casa"), valor = 110.5, dataTransacao = time, tipo = TipoTransacao.DESPESA.name, contaId = 0))
        */
    }

    class Factory(
            val context: Context
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = MainViewModel(context) as T
    }
}