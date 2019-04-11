package br.com.bruno.join.viewModel

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.bruno.join.entity.Categoria
import br.com.bruno.join.entity.Transacao
import br.com.bruno.join.enums.TipoTransacao
import com.vicpin.krealmextensions.delete
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
                .subscribe { it ->
                    totalReceita.set(it.filter { it.tipo!! == TipoTransacao.RECEITA.name }.sumByDouble { it.valor })
                    totalDespesa.set(it.filter { it.tipo!! == TipoTransacao.DESPESA.name }.sumByDouble { it.valor })
                    saldo.set(totalReceita.get()!! - totalDespesa.get()!!)
                    listaTransacoes.onNext(it.sortedByDescending { it.data })
                }
        )
    }

    fun removeItem(idTransacao: Long) {
        Transacao().delete { equalTo("id", idTransacao)}
    }

    class Factory(
            val context: Context
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = MainViewModel(context) as T
    }
}