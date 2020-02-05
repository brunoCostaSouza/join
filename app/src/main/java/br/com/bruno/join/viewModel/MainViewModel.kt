package br.com.bruno.join.viewModel

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.bruno.join.view.Actions
import br.com.bruno.join.model.Transaction
import br.com.bruno.join.enums.TypeTransaction
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
) : ViewModel() {

    companion object {
        const val STATE_FILTER_NO = 0
        const val STATE_FILTER_DESPESAS = 1
        const val STATE_FILTER_RECEITAS = 2

        const val MENU_FILTER_HOJE = 3
        const val MENU_FILTER_SEMANA = 4
        const val MENU_FILTER_15DIAS = 5
        const val MENU_FILTER_MES = 6
    }

    private var composite = CompositeDisposable()
    private var dateFilterList: Date = Calendar.getInstance().time

    var totalIncome = ObservableField<Double>(0.0)
    var totalExpenses = ObservableField<Double>(0.0)
    var balance = ObservableField<Double>(0.0)
    var balanceExpected = ObservableField<Double>(0.0)
    var listTransactions = PublishSubject.create<List<Transaction>>()

    var valueProgress = PublishSubject.create<Int>()
    var listTransactionsAuxes: List<Transaction> = mutableListOf()
    var stateFilter = STATE_FILTER_NO

    init {
        initFlowables()
    }

    private fun initFlowables() {

        //TODO: Arrumar esse filtro, quando salva uma transaction e tem algum filtro selecionado a home lista apenas os ultimos do mes, mas precisar voltar a lista filtrada.

        val query = Transaction().queryAsFlowable {
            beginGroup()
                .between("data", dateFilterList.primeiroDiaMes(), dateFilterList.ultimaDiaMes())
            endGroup()
        }
        composite.add(query
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                listTransactionsAuxes = it
                val st = stateFilter
                stateFilter = STATE_FILTER_NO
                filter(st)
            }
        )
    }

    private fun calcularSaldoPrevisto(it: List<Transaction>) {
        when (stateFilter) {
            STATE_FILTER_NO -> {
                val totalDespesas =
                    it.filter { it.tipo!! == TypeTransaction.DESPESA.name }.sumByDouble { it.valor }
                val totalReceitas =
                    it.filter { it.tipo!! == TypeTransaction.RECEITA.name }.sumByDouble { it.valor }
                balanceExpected.set(totalReceitas - totalDespesas)
            }
            STATE_FILTER_DESPESAS -> {
                val totalDespesas =
                    it.filter { it.tipo!! == TypeTransaction.DESPESA.name }.sumByDouble { it.valor }
                balanceExpected.set(totalDespesas)
            }
            STATE_FILTER_RECEITAS -> {
                val totalReceitas =
                    it.filter { it.tipo!! == TypeTransaction.RECEITA.name }.sumByDouble { it.valor }
                balanceExpected.set(totalReceitas)
            }
        }
    }

    private fun calcularSaldoConsolidado(it: List<Transaction>) {

        totalIncome.set(it.filter { it.tipo!! == TypeTransaction.RECEITA.name && it.consolidado!! }.sumByDouble { it.valor })
        totalExpenses.set(it.filter { it.tipo!! == TypeTransaction.DESPESA.name && it.consolidado!! }.sumByDouble { it.valor })

        when (stateFilter) {
            STATE_FILTER_NO -> {
                balance.set(totalIncome.get()!! - totalExpenses.get()!!)
            }
            STATE_FILTER_DESPESAS -> {
                balance.set(totalExpenses.get()!!)
            }
            STATE_FILTER_RECEITAS -> {
                balance.set(totalIncome.get()!!)
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
        var listFiltered: List<Transaction> = mutableListOf()

        if (typeFilter == stateFilter) {
            stateFilter = STATE_FILTER_NO
            setList(listTransactionsAuxes)
            return
        }

        when (typeFilter) {
            STATE_FILTER_RECEITAS -> {
                listFiltered =
                    listTransactionsAuxes.filter { it.tipo!! == TypeTransaction.RECEITA.name }
            }
            STATE_FILTER_DESPESAS -> {
                listFiltered =
                    listTransactionsAuxes.filter { it.tipo!! == TypeTransaction.DESPESA.name }
            }
        }

        stateFilter = typeFilter
        setList(listFiltered)
    }

    fun filterByMenu(typeFilter: Int) {
        stateFilter = STATE_FILTER_NO
        when (typeFilter) {
            MENU_FILTER_HOJE -> {
                listTransactionsAuxes = Transaction().query {
                    between(
                        "data",
                        Calendar.getInstance().time.zeraHora(),
                        Calendar.getInstance().time.ultimaHora()
                    )
                }
                setList(listTransactionsAuxes)
            }
            MENU_FILTER_SEMANA -> {
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                val di = calendar.time

                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
                val df = calendar.time

                listTransactionsAuxes =
                    Transaction().query { between("data", di.zeraHora(), df.ultimaHora()) }
                setList(listTransactionsAuxes)
            }
            MENU_FILTER_15DIAS -> {
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_MONTH, -15)
                val df = calendar.time
                listTransactionsAuxes = Transaction().query {
                    between(
                        "data",
                        df.zeraHora(),
                        Calendar.getInstance().time.ultimaHora()
                    )
                }
                setList(listTransactionsAuxes)
            }
            MENU_FILTER_MES -> {
                listTransactionsAuxes = Transaction().query {
                    between(
                        "data",
                        Calendar.getInstance().time.primeiroDiaMes(),
                        Calendar.getInstance().time.ultimaDiaMes()
                    )
                }
                setList(listTransactionsAuxes)
            }
        }
    }

    fun filterByPeriod(inicio: Date, fim: Date) {
        listTransactionsAuxes = Transaction().query { between("data", inicio, fim) }
        setList(listTransactionsAuxes)
    }

    private fun setList(list: List<Transaction>) {
        calcularSaldoPrevisto(list)
        calcularSaldoConsolidado(list)
        calculateProgress(list)
        when (stateFilter) {
            STATE_FILTER_RECEITAS -> {
                listTransactions.onNext(
                    list.filter { it.tipo!! == TypeTransaction.RECEITA.name }.sortedWith(
                        compareByDescending<Transaction> { it.data }.thenByDescending { it.id })
                )
            }
            STATE_FILTER_DESPESAS -> {
                listTransactions.onNext(
                    list.filter { it.tipo!! == TypeTransaction.DESPESA.name }.sortedWith(
                        compareByDescending<Transaction> { it.data }.thenByDescending { it.id })
                )
            }
            STATE_FILTER_NO -> {
                listTransactions.onNext(list.sortedWith(compareByDescending<Transaction> { it.data }.thenByDescending { it.id }))
            }
        }
    }

    private fun calculateProgress(list: List<Transaction>) {
        val c100 = list.asSequence().filter { it.consolidado!! }.sumByDouble {
            if (it.valor < 0) {
                (-1.0) * it.valor
            } else {
                it.valor
            }
        }
        val receita =
            list.asSequence().filter { it.tipo!! == TypeTransaction.RECEITA.name && it.consolidado!! }
                .sumByDouble { it.valor }
        val porcentagem = receita * 100 / c100
        valueProgress.onNext(
            if (porcentagem > 0) {
                porcentagem.toInt()
            } else {
                0
            }
        )
    }

    fun gotoAddReceita() {
        action.gotoAddReceita()
    }

    fun gotoAddDespesa() {
        action.gotoAddDespesa()
    }

    fun gotoRelatorios() {
        action.gotoRelatorios()
    }

    class Factory(
        val context: Context,
        private val action: Actions
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            MainViewModel(context, action) as T
    }
}