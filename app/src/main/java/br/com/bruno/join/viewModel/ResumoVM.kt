package br.com.bruno.join.viewModel

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import br.com.bruno.join.entity.Transacao

/**
 * Created by Bruno Costa on 09/08/2018.
 */
class ResumoVM: ViewModel() {
    var saldo = ObservableField<String>()

    init {
        saldo.set("3.700,00")
    }
}