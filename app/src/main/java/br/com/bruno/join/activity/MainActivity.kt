package br.com.bruno.join.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import br.com.bruno.join.R
import br.com.bruno.join.adapter.ItemTransacaoAdapter
import br.com.bruno.join.databinding.ActivityMainBinding
import br.com.bruno.join.entity.Categoria
import br.com.bruno.join.entity.Transacao
import br.com.bruno.join.enums.TipoTransacao
import br.com.bruno.join.viewModel.ResumoVM
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ResumoVM
    private lateinit var transacaoAdapter: ItemTransacaoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(ResumoVM::class.java)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.resumeCard.resumoVM = viewModel

        setupView()
        setupViewModel()

        transacaoAdapter.updateList(getListaTransacoes())
    }

    fun setupViewModel(){
        viewModel.saldo.set("3.330,00")
    }

    fun setupView(){
        transacaoAdapter = ItemTransacaoAdapter(applicationContext)
        listItens.apply {
            adapter = transacaoAdapter
        }
    }

    fun getListaTransacoes(): List<Transacao>{
        val time = Calendar.getInstance().timeInMillis
        val list = mutableListOf<Transacao>()
        list.add(Transacao(descricao = "Description 1", categoria = Categoria(descricao = "Categoria1"), valor = 25.5, dataTransacao = time, tipo = TipoTransacao.RECEITA.name, contaId = 0))
        list.add(Transacao(descricao = "Description 2", categoria = Categoria(descricao = "Categoria1"), valor = 50.5, dataTransacao = time, tipo = TipoTransacao.DESPESA.name, contaId = 0))
        list.add(Transacao(descricao = "Description 3", categoria = Categoria(descricao = "Categoria1"), valor = 1200.5, dataTransacao = time, tipo = TipoTransacao.TRANSFERENCIA.name, contaId = 0))
        list.add(Transacao(descricao = "Description 4", categoria = Categoria(descricao = "Categoria1"), valor = 150.5, dataTransacao = time, tipo = TipoTransacao.DESPESA.name, contaId = 0))
        list.add(Transacao(descricao = "Description 5", categoria = Categoria(descricao = "Categoria1"), valor = 930.5, dataTransacao = time, tipo = TipoTransacao.RECEITA.name, contaId = 0))
        return list
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

}
