package br.com.bruno.join.activity

import android.animation.ValueAnimator
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.bruno.join.R
import br.com.bruno.join.Util.FullScreenDialog
import br.com.bruno.join.Util.PeriodDialog
import br.com.bruno.join.adapter.ItemTransacaoAdapter
import br.com.bruno.join.databinding.HomeBinding
import br.com.bruno.join.enums.TipoTransacao
import br.com.bruno.join.extensions.formataData
import br.com.bruno.join.extensions.observe
import br.com.bruno.join.viewModel.MainViewModel
import com.transitionseverywhere.Recolor
import com.transitionseverywhere.TransitionManager
import formatMoney
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.home.*
import java.util.*


class MainActivity : AppCompatActivity(), Actions {

    private lateinit var viewModel: MainViewModel
    private lateinit var transacaoAdapter: ItemTransacaoAdapter
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, MainViewModel.Factory(this)).get(MainViewModel::class.java)
        val binding: HomeBinding = DataBindingUtil.setContentView(this, R.layout.home)
        binding.viewModel = viewModel

        setSupportActionBar(toolbarhome)
        supportActionBar?.elevation = 0F

        setupView()
        setupViewModel()
    }

    private fun setupViewModel() {
        compositeDisposable.add(viewModel.listaTransacoes.subscribe {
            this@MainActivity!!.runOnUiThread {
                transacaoAdapter.updateList(it)
                listItens.adapter = transacaoAdapter
                animateValue()

                if (it.isEmpty() && viewModel.totalDespesa.get() == 0.0 && viewModel.totalReceita.get() == 0.0) {
                    textEmpty.visibility = View.VISIBLE
                    listItens.visibility = View.GONE

                } else {
                    textEmpty.visibility = View.GONE
                    listItens.visibility = View.VISIBLE
                }
            }
        })

        compositeDisposable.add(viewModel.saldo.observe {
            TransitionManager.beginDelayedTransition(layoutTop, Recolor().setDuration(1000))
            Handler().postDelayed({
                if (it!! < 0) {
                    layoutTop.background = ContextCompat.getDrawable(this, R.drawable.shape_negative)
                    window.statusBarColor = ContextCompat.getColor(this, R.color.secondPrimary)
                } else {
                    layoutTop.background = ContextCompat.getDrawable(this, R.drawable.shape_positive)
                    window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
                }
            }, 100)

            when (viewModel.stateFilter) {
                MainViewModel.STATE_FILTER_NO -> {
                    filterDespesaSelected.visibility = View.GONE
                    filterReceitaSelected.visibility = View.GONE
                    progress.visibility = View.VISIBLE
                }
                MainViewModel.STATE_FILTER_RECEITAS -> {
                    filterDespesaSelected.visibility = View.GONE
                    filterReceitaSelected.visibility = View.VISIBLE
                    progress.visibility = View.GONE
                }
                MainViewModel.STATE_FILTER_DESPESAS -> {
                    filterDespesaSelected.visibility = View.VISIBLE
                    filterReceitaSelected.visibility = View.GONE
                    progress.visibility = View.GONE
                }
            }

            if (it!! == 0.0 && viewModel.totalDespesa.get() == 0.0 && viewModel.totalReceita.get() == 0.0) progress.visibility = View.GONE
        })

        compositeDisposable.add(viewModel.valueProgress.subscribe {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                progress.setProgress(it, true)
            } else {
                progress.progress = it
            }
        })

    }

    private fun setupView() {

        transacaoAdapter = ItemTransacaoAdapter(applicationContext, supportFragmentManager)
        listItens.apply {
            layoutManager = LinearLayoutManager(context)
            layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.animation_item)
            adapter = transacaoAdapter
        }

        listItens.addOnScrollListener(scrollListener)

        btnAddReceita.setOnClickListener {
            val dialog = FullScreenDialog()
            dialog.actions = this
            dialog.tipoTransacao = TipoTransacao.RECEITA
            dialog.show(supportFragmentManager.beginTransaction(), FullScreenDialog.TAG)
        }

        btnAddDespesa.setOnClickListener {
            val dialog = FullScreenDialog()
            dialog.actions = this
            dialog.tipoTransacao = TipoTransacao.DESPESA
            dialog.show(supportFragmentManager.beginTransaction(), FullScreenDialog.TAG)
        }

        //rootFab.setIconAnimationInterpolator(AnimationUtils.loadInterpolator(this, R.anim.anim_rotate))

    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, lastPosition: Int, newPosition: Int) {
            super.onScrolled(recyclerView, lastPosition, newPosition)
            when (lastPosition < newPosition) {
                true -> rootFab.hideMenu(true)
                else -> rootFab.showMenu(true)
            }
        }
    }

    override fun closeWindowBefore() {
        rootFab.close(true)
    }

    private fun animateValue() {
        val endValue = viewModel.saldo.get()!!.toFloat()
        val animator = ValueAnimator.ofFloat(0f, endValue)
        animator.duration = 500
        animator.addUpdateListener { animation ->
            textSaldoAcumulado.text = (animation.animatedValue as Float).toDouble().formatMoney()
        }
        animator.start()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_filter, menu)
        return true
    }

    override fun onOptionsMenuClosed(menu: Menu?) {
        super.onOptionsMenuClosed(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.filterUltimasHoje -> {
                textDescricaoFiltro.text = "Últimas de hoje"
                viewModel.filterByMenu(MainViewModel.FILTER_HOJE)
            }
            R.id.filterUltimaSemana -> {
                textDescricaoFiltro.text = "Últimas da semana"
                viewModel.filterByMenu(MainViewModel.FILTER_SEMANA)
            }
            R.id.filterUltimas15Dias -> {
                textDescricaoFiltro.text = "Últimos 15 dias"
                viewModel.filterByMenu(MainViewModel.FILTER_15DIAS)
            }
            R.id.filterUltimasMes -> {
                textDescricaoFiltro.text = "Últimas deste mês."
                viewModel.filterByMenu(MainViewModel.FILTER_MES)
            }
            R.id.filterPeriodo -> {
                val dialog = PeriodDialog()
                dialog.show(supportFragmentManager, "DIALOGPERIOD")
                dialog.listenerOnClickFilter { inicio, fim ->
                    textDescricaoFiltro.text = "${inicio.formataData()}  á  ${fim.formataData()}"
                    dialog.dismiss()
                    viewModel.filterByPeriod(inicio, fim)
                }

            }

        }
        return true
    }
}

interface Actions {
    fun closeWindowBefore()
}
