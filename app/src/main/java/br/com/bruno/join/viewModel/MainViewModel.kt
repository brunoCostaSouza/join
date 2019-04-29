package br.com.bruno.join.viewModel

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.bruno.join.entity.Transacao
import br.com.bruno.join.enums.TipoTransacao
import br.com.bruno.join.extensions.primeiroDiaMes
import br.com.bruno.join.extensions.ultimaDiaMes
import br.com.bruno.join.extensions.ultimaHora
import br.com.bruno.join.extensions.zeraHora
import com.vicpin.krealmextensions.query
import com.vicpin.krealmextensions.queryAllAsFlowable
import com.vicpin.krealmextensions.queryAsFlowable
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

    companion object {
        val FILTER_NO       = 0
        val FILTER_DESPESAS = 1
        val FILTER_RECEITAS = 2
        val FILTER_HOJE = 3
        val FILTER_SEMANA = 4
        val FILTER_15DIAS = 5
        val FILTER_MES = 6

    }

    var totalReceita = ObservableField<Double>(0.0)
    var totalDespesa = ObservableField<Double>(0.0)
    var saldo = ObservableField<Double>(0.0)
    var saldoPrevisto = ObservableField<Double>(0.0)
    var listaTransacoes = PublishSubject.create<List<Transacao>>()
    var valueProgress = PublishSubject.create<Int>()
    var composite = CompositeDisposable()

    var listaTransacoesAux: List<Transacao> = mutableListOf()
    var dateFilterList: Date = Calendar.getInstance().time
    var stateFilter = FILTER_NO

    init {
        initFlowables()
    }

    private fun initFlowables() {
        val query = Transacao().queryAsFlowable {
            beginGroup()
                    .between("data", dateFilterList.primeiroDiaMes(), dateFilterList.ultimaDiaMes())
            endGroup()
        }
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
        when(stateFilter) {
            FILTER_NO -> {
                val totalDespesas = it.filter { it.tipo!! == TipoTransacao.DESPESA.name }.sumByDouble { it.valor }
                val totalReceitas = it.filter { it.tipo!! == TipoTransacao.RECEITA.name }.sumByDouble { it.valor }
                saldoPrevisto.set(totalReceitas - totalDespesas)
            }
            FILTER_DESPESAS -> {
                val totalDespesas = it.filter { it.tipo!! == TipoTransacao.DESPESA.name }.sumByDouble { it.valor }
                saldoPrevisto.set(totalDespesas)
            }
            FILTER_RECEITAS -> {
                val totalReceitas = it.filter { it.tipo!! == TipoTransacao.RECEITA.name }.sumByDouble { it.valor }
                saldoPrevisto.set(totalReceitas)
            }
        }
    }

    private fun calcularSaldoConsolidado(it: List<Transacao>) {
        when (stateFilter) {
            FILTER_NO -> {
                totalReceita.set(it.filter { it.tipo!! == TipoTransacao.RECEITA.name && it.consolidado!! }.sumByDouble { it.valor })
                totalDespesa.set(it.filter { it.tipo!! == TipoTransacao.DESPESA.name && it.consolidado!! }.sumByDouble { it.valor })
                saldo.set(totalReceita.get()!! - totalDespesa.get()!!)
            }
            FILTER_DESPESAS -> {
                totalDespesa.set(it.filter { it.tipo!! == TipoTransacao.DESPESA.name && it.consolidado!! }.sumByDouble { it.valor })
                saldo.set(totalDespesa.get()!!)
            }
            FILTER_RECEITAS -> {
                totalReceita.set(it.filter { it.tipo!! == TipoTransacao.RECEITA.name && it.consolidado!! }.sumByDouble { it.valor })
                saldo.set(totalReceita.get()!!)
            }
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
            setList(listaTransacoesAux)
            calculateProgress(listaTransacoesAux)
            return
        }

        when (typeFilter) {
            FILTER_RECEITAS -> { listFiltered = listaTransacoesAux.filter { it.tipo!! == TipoTransacao.RECEITA.name} }
            FILTER_DESPESAS -> { listFiltered = listaTransacoesAux.filter { it.tipo!! == TipoTransacao.DESPESA.name} }
        }

        stateFilter = typeFilter
        setList(listFiltered)

    }

    fun filterByMenu(typeFilter: Int){
        when(typeFilter){
            FILTER_HOJE -> {
                val list = Transacao().query {
                    between("data", Calendar.getInstance().time.zeraHora(), Calendar.getInstance().time.ultimaHora())
                }
                setList(list)
            }
            FILTER_SEMANA -> {
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                val di = calendar.time

                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
                val df = calendar.time
                setList(Transacao().query { between("data", di.zeraHora(), df.ultimaHora()) })
            }
            FILTER_15DIAS -> {
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.DAY_OF_MONTH, 15)
                val df = calendar.time
                setList(Transacao().query { between("data", df.zeraHora(), Calendar.getInstance().time.ultimaDiaMes()) })
            }
            FILTER_MES -> {
                setList(Transacao().query { between("data", Calendar.getInstance().time.primeiroDiaMes(), Calendar.getInstance().time.ultimaDiaMes()) })
            }
        }
    }

    private fun setList(list: List<Transacao>){
        calcularSaldoPrevisto(list)
        calcularSaldoConsolidado(list)
        listaTransacoes.onNext(list.sortedWith(compareByDescending<Transacao>{ it.data }.thenByDescending { it.id }))
    }

    fun calculateProgress(list: List<Transacao>) {
        val c100 = list.asSequence().filter { it.consolidado!! }.sumByDouble { if (it.valor < 0){ (-1.0) * it.valor } else { it.valor } }
        val receita = list.asSequence().filter { it.tipo!! == TipoTransacao.RECEITA.name && it.consolidado!!}.sumByDouble { it.valor }
        val porcentagem = receita * 100 / c100
        valueProgress.onNext(if (porcentagem > 0) { porcentagem.toInt() } else { 0 })
    }

    class Factory(
            val context: Context
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = MainViewModel(context) as T
    }
}