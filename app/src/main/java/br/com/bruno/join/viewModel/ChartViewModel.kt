package br.com.bruno.join.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.bruno.join.model.Categoria
import br.com.bruno.join.model.Transaction
import br.com.bruno.join.enums.TypeTransaction
import br.com.bruno.join.extensions.primeiroDiaMes
import br.com.bruno.join.extensions.ultimaDiaMes
import com.vicpin.krealmextensions.query
import io.reactivex.subjects.PublishSubject
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by Bruno Costa on 09/08/2018.
 */
class ChartViewModel(
    val context: Context
) : ViewModel() {

    var totalReceitaDespesa = PublishSubject.create<Map<String, Double>>()
    var totalPorCategoria = PublishSubject.create<Map<Categoria, Double>>()

    fun starCharts() {
        val data = Calendar.getInstance().time
        Transaction().query {
            between("data", data.primeiroDiaMes(), data.ultimaDiaMes())
        }.let {
            configValuesOfBarChart(it)
            configValuesOfPieChart(it)
        }
    }

    private fun configValuesOfBarChart(it: List<Transaction>) {
        val totalDespesa = it.filter { t -> t.tipo == TypeTransaction.DESPESA.name }
            .sumByDouble { t2 -> t2.valor }
        val totalReceita = it.filter { t -> t.tipo == TypeTransaction.RECEITA.name }
            .sumByDouble { t2 -> t2.valor }

        val map = HashMap<String, Double>()
        map[TypeTransaction.DESPESA.name] = totalDespesa
        map[TypeTransaction.RECEITA.name] = totalReceita
        totalReceitaDespesa.onNext(map)
    }

    private fun configValuesOfPieChart(it: List<Transaction>) {
        val map = HashMap<Categoria, Double>()
        val listPorCategoria = it.groupBy { it.categoria }
        listPorCategoria.forEach {
            map[it.key!!] = it.value.sumByDouble { it.valor }
        }

        totalPorCategoria.onNext(map)
    }

    class Factory (
        val context: Context
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = ChartViewModel(context) as T
    }
}