package br.com.bruno.join.viewModel

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.bruno.join.activity.Actions
import br.com.bruno.join.entity.Transacao
import br.com.bruno.join.enums.TipoTransacao
import br.com.bruno.join.extensions.primeiroDiaMes
import br.com.bruno.join.extensions.ultimaDiaMes
import br.com.bruno.join.extensions.ultimaHora
import br.com.bruno.join.extensions.zeraHora
import com.vicpin.krealmextensions.query
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
        val context: Context,
        val action: Actions
): ViewModel() {

    companion object {
        const val STATE_FILTER_NO       = 0
        const val STATE_FILTER_DESPESAS = 1
        const val STATE_FILTER_RECEITAS = 2

        const val MENU_FILTER_HOJE   = 3
        const val MENU_FILTER_SEMANA = 4
        const val MENU_FILTER_15DIAS = 5
        const val MENU_FILTER_MES    = 6

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
    var stateFilter = STATE_FILTER_NO

    init {
        initFlowables()
    }

    private fun initFlowables() {
        /**
         * Arrumar esse filtro, quando salva uma transacao e tem algum filtro selecionado
         * a home lista apenas os ultimos do mes, mas precisar voltar a lista filtrada.
         */
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
                    stateFilter = STATE_FILTER_NO
                    filter(st)
                }
        )
    }

    private fun calcularSaldoPrevisto(it: List<Transacao>) {
        when(stateFilter) {
            STATE_FILTER_NO -> {
                val totalDespesas = it.filter { it.tipo!! == TipoTransacao.DESPESA.name }.sumByDouble { it.valor }
                val totalReceitas = it.filter { it.tipo!! == TipoTransacao.RECEITA.name }.sumByDouble { it.valor }
                saldoPrevisto.set(totalReceitas - totalDespesas)
            }
            STATE_FILTER_DESPESAS -> {
                val totalDespesas = it.filter { it.tipo!! == TipoTransacao.DESPESA.name }.sumByDouble { it.valor }
                saldoPrevisto.set(totalDespesas)
            }
            STATE_FILTER_RECEITAS -> {
                val totalReceitas = it.filter { it.tipo!! == TipoTransacao.RECEITA.name }.sumByDouble { it.valor }
                saldoPrevisto.set(totalReceitas)
            }
        }
    }

    private fun calcularSaldoConsolidado(it: List<Transacao>) {

        totalReceita.set(it.filter { it.tipo!! == TipoTransacao.RECEITA.name && it.consolidado!! }.sumByDouble { it.valor })
        totalDespesa.set(it.filter { it.tipo!! == TipoTransacao.DESPESA.name && it.consolidado!! }.sumByDouble { it.valor })

        when (stateFilter) {
            STATE_FILTER_NO -> {
                saldo.set(totalReceita.get()!! - totalDespesa.get()!!)
            }
            STATE_FILTER_DESPESAS -> {
                saldo.set(totalDespesa.get()!!)
            }
            STATE_FILTER_RECEITAS -> {
                saldo.set(totalReceita.get()!!)
            }
        }

    }

    fun filterDespesas() {
        filter(STATE_FILTER_DESPESAS)
    }

    fun filterReceitas() {
        filter(STATE_FILTER_RECEITAS)
    }

    private fun filter(typeFilter: Int) {
        var listFiltered: List<Transacao> = mutableListOf()

        if (typeFilter == stateFilter) {
            stateFilter = STATE_FILTER_NO
            setList(listaTransacoesAux)
            return
        }

        when (typeFilter) {
            STATE_FILTER_RECEITAS -> { listFiltered = listaTransacoesAux.filter { it.tipo!! == TipoTransacao.RECEITA.name} }
            STATE_FILTER_DESPESAS -> { listFiltered = listaTransacoesAux.filter { it.tipo!! == TipoTransacao.DESPESA.name} }
        }

        stateFilter = typeFilter
        setList(listFiltered)

    }

    fun filterByMenu(typeFilter: Int){
        stateFilter = STATE_FILTER_NO
        when (typeFilter) {
            MENU_FILTER_HOJE -> {
                listaTransacoesAux = Transacao().query {
                    between("data", Calendar.getInstance().time.zeraHora(), Calendar.getInstance().time.ultimaHora())
                }
                setList(listaTransacoesAux)
            }
            MENU_FILTER_SEMANA -> {
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                val di = calendar.time

                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
                val df = calendar.time

                listaTransacoesAux = Transacao().query { between("data", di.zeraHora(), df.ultimaHora()) }
                setList(listaTransacoesAux)
            }
            MENU_FILTER_15DIAS -> {
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_MONTH, -15)
                val df = calendar.time
                listaTransacoesAux = Transacao().query { between("data", df.zeraHora(), Calendar.getInstance().time.ultimaHora()) }
                setList(listaTransacoesAux)
            }
            MENU_FILTER_MES -> {
                listaTransacoesAux = Transacao().query { between("data", Calendar.getInstance().time.primeiroDiaMes(), Calendar.getInstance().time.ultimaDiaMes()) }
                setList(listaTransacoesAux)
            }
        }
    }

    fun filterByPeriod(inicio: Date, fim: Date) {
        listaTransacoesAux = Transacao().query { between("data", inicio, fim) }
        setList(listaTransacoesAux)
    }

    private fun setList(list: List<Transacao>){
        calcularSaldoPrevisto(list)
        calcularSaldoConsolidado(list)
        calculateProgress(list)
        when (stateFilter) {
            STATE_FILTER_RECEITAS -> { listaTransacoes.onNext(list.filter { it.tipo!! == TipoTransacao.RECEITA.name}.sortedWith(compareByDescending<Transacao>{ it.data }.thenByDescending { it.id })) }
            STATE_FILTER_DESPESAS -> { listaTransacoes.onNext(list.filter { it.tipo!! == TipoTransacao.DESPESA.name}.sortedWith(compareByDescending<Transacao>{ it.data }.thenByDescending { it.id })) }
            STATE_FILTER_NO -> {listaTransacoes.onNext(list.sortedWith(compareByDescending<Transacao>{ it.data }.thenByDescending { it.id })) }
        }

    }

    fun calculateProgress(list: List<Transacao>) {
        val c100 = list.asSequence().filter { it.consolidado!! }.sumByDouble { if (it.valor < 0){ (-1.0) * it.valor } else { it.valor } }
        val receita = list.asSequence().filter { it.tipo!! == TipoTransacao.RECEITA.name && it.consolidado!!}.sumByDouble { it.valor }
        val porcentagem = receita * 100 / c100
        valueProgress.onNext(if (porcentagem > 0) { porcentagem.toInt() } else { 0 })
    }

    fun gotoAddReceita() {
        action.gotoAddReceita()
    }

    fun gotoAddDespesa() {
        action.gotoAddDespesa()
    }

    class Factory(
            val context: Context,
            val action: Actions
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = MainViewModel(context, action) as T
    }
}