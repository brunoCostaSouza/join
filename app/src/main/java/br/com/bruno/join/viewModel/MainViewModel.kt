package br.com.bruno.join.viewModel

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.bruno.join.entity.Transacao
import br.com.bruno.join.enums.TipoTransacao
import com.vicpin.krealmextensions.queryAllAsFlowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

/**
 * Created by Bruno Costa on 09/08/2018.
 */
class MainViewModel(
        val context: Context
): ViewModel() {

    companion object {
        val FILTER_NO       = 0
        val FILTER_DESPESAS = 1
        val FILTER_RECEITAS = 2
    }

    var totalReceita = ObservableField<Double>(0.0)
    var totalDespesa = ObservableField<Double>(0.0)
    var saldo = ObservableField<Double>(0.0)
    var saldoPrevisto = ObservableField<Double>(0.0)
    var listaTransacoes = PublishSubject.create<List<Transacao>>()
    var valueProgress = PublishSubject.create<Int>()
    var composite = CompositeDisposable()

    var listaTransacoesAux: List<Transacao> = mutableListOf()
    var stateFilter = FILTER_NO

    init {
        initFlowables()
    }

    private fun initFlowables() {
        val query = Transacao().queryAllAsFlowable()
        composite.add(query
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { it ->
                    listaTransacoesAux = it
                    val st = stateFilter
                    stateFilter = FILTER_NO
                    filter(st)
                }
        )
    }

    private fun calcularSaldoPrevisto(it: List<Transacao>) {
        val totalDespesas = it.filter { it.tipo!! == TipoTransacao.DESPESA.name }.sumByDouble { it.valor }
        val totalReceitas = it.filter { it.tipo!! == TipoTransacao.RECEITA.name }.sumByDouble { it.valor }
        saldoPrevisto.set(totalReceitas - totalDespesas)
    }

    private fun calcularSaldoConsolidado(it: List<Transacao>) {
        when(stateFilter){
            FILTER_NO -> {
                totalReceita.set(it.filter { it.tipo!! == TipoTransacao.RECEITA.name && it.consolidado!! }.sumByDouble { it.valor })
                totalDespesa.set(it.filter { it.tipo!! == TipoTransacao.DESPESA.name && it.consolidado!! }.sumByDouble { it.valor })
                saldo.set(totalReceita.get()!! - totalDespesa.get()!!)
            }
            FILTER_RECEITAS -> { saldo.set(it.filter { it.tipo!! == TipoTransacao.RECEITA.name && it.consolidado!! }.sumByDouble { it.valor }) }
            FILTER_DESPESAS -> { saldo.set(it.filter { it.tipo!! == TipoTransacao.DESPESA.name && it.consolidado!! }.sumByDouble { it.valor }) }
        }
    }

    fun filterDespesas() {
        filter(FILTER_DESPESAS)
    }

    fun filterReceitas() {
        filter(FILTER_RECEITAS)
    }

    fun filter(typeFilter: Int) {
        var listFiltered: List<Transacao> = mutableListOf()

        if (typeFilter == stateFilter) {
            stateFilter = FILTER_NO
            calcularSaldoPrevisto(listaTransacoesAux)
            calcularSaldoConsolidado(listaTransacoesAux)
            listaTransacoes.onNext(listaTransacoesAux.sortedByDescending{ it.data })
            calculateProgress(listaTransacoesAux)
            return
        }

        when (typeFilter) {
            FILTER_RECEITAS -> { listFiltered = listaTransacoesAux.filter { it.tipo!! == TipoTransacao.RECEITA.name} }
            FILTER_DESPESAS -> { listFiltered = listaTransacoesAux.filter { it.tipo!! == TipoTransacao.DESPESA.name} }
        }

        stateFilter = typeFilter
        calcularSaldoPrevisto(listFiltered)
        calcularSaldoConsolidado(listFiltered)
        listaTransacoes.onNext(listFiltered.sortedByDescending{ it.data })

    }

    fun calculateProgress(list: List<Transacao>) {
        val c100 = list.filter { it.consolidado!! }.sumByDouble { if (it.valor < 0){ (-1.0) * it.valor } else { it.valor } }
        val receita = list.filter { it.tipo!! == TipoTransacao.RECEITA.name && it.consolidado!!}.sumByDouble { it.valor }
        val porcentagem = receita * 100 / c100
        valueProgress.onNext(if (porcentagem > 0) { porcentagem.toInt() } else { 0 })
    }

    class Factory(
            val context: Context
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = MainViewModel(context) as T
    }
}