package br.com.bruno.join.viewModel

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import br.com.bruno.join.entity.Resumo

/**
 * Created by Bruno Costa on 09/08/2018.
 */
class ResumoVM: ViewModel() {

    lateinit var resumo: Resumo;

    var saldo = ObservableField<String>()
    var receita = ObservableField<String>()
    var despesas = ObservableField<String>()

    init {
        saldo.set("3.700,00")
    }
}