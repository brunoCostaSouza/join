package br.com.bruno.join.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.bruno.join.application.JoinAppCompatActivity
import br.com.bruno.join.R
import br.com.bruno.join.util.FullScreenDialog
import br.com.bruno.join.util.PeriodDialog
import br.com.bruno.join.adapter.ItemTransactionAdapter
import br.com.bruno.join.databinding.HomeBinding
import br.com.bruno.join.enums.TypeTransaction
import br.com.bruno.join.extensions.formataData
import br.com.bruno.join.extensions.observe
import br.com.bruno.join.viewModel.MainViewModel
import com.transitionseverywhere.Recolor
import com.transitionseverywhere.TransitionManager
import formatMoney
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.home.*
import java.util.*


class MainActivity : JoinAppCompatActivity(), Actions {

    private lateinit var viewModel: MainViewModel
    private lateinit var transactionAdapter: ItemTransactionAdapter
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        setupView()
        setupViewModel()
    }

    override fun initViewModel() {
        viewModel = ViewModelProviders.of(this, MainViewModel.Factory(this, this)).get(MainViewModel::class.java)
        val binding: HomeBinding = DataBindingUtil.setContentView(this, R.layout.home)
        binding.viewModel = viewModel
    }

    override fun setupViewModel() {
        compositeDisposable.add(viewModel.listTransactions.subscribe {
            this@MainActivity.runOnUiThread {
                transactionAdapter.updateList(it)
                listItens.adapter = transactionAdapter
                animateValue()

                if (it.isEmpty() && viewModel.totalExpenses.get() == 0.0 && viewModel.totalIncome.get() == 0.0) {
                    textEmpty.visibility = View.VISIBLE
                    listItens.visibility = View.GONE

                } else {
                    textEmpty.visibility = View.GONE
                    listItens.visibility = View.VISIBLE
                }
            }
        })

        compositeDisposable.add(viewModel.balance.observe {
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

            if (it!! == 0.0 && viewModel.totalExpenses.get() == 0.0 && viewModel.totalIncome.get() == 0.0) progress.visibility = View.GONE
        })

        compositeDisposable.add(viewModel.valueProgress.subscribe {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                progress.setProgress(it, true)
            } else {
                progress.progress = it
            }
        })

    }

    override fun setupView() {
        setSupportActionBar(toolbarhome)
        supportActionBar?.elevation = 0F

        transactionAdapter = ItemTransactionAdapter(applicationContext, supportFragmentManager)
        listItens.apply {
            layoutManager = LinearLayoutManager(context)
            layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.animation_item)
            adapter = transactionAdapter
        }

        listItens.addOnScrollListener(scrollListener)

        //rootFab.setIconAnimationInterpolator(AnimationUtils.loadInterpolator(this, R.anim.anim_rotate))

    }

    override fun gotoAddDespesa() {
        val dialog = FullScreenDialog()
        dialog.actions = this
        dialog.tipoTransacao = TypeTransaction.DESPESA
        dialog.show(supportFragmentManager.beginTransaction(), FullScreenDialog.TAG)
    }

    override fun gotoAddReceita() {
        val dialog = FullScreenDialog()
        dialog.actions = this
        dialog.tipoTransacao = TypeTransaction.RECEITA
        dialog.show(supportFragmentManager.beginTransaction(), FullScreenDialog.TAG)
    }

    override fun gotoRelatorios() {
        startActivity(Intent(this, ChartActivity::class.java))
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
        val endValue = viewModel.balance.get()!!.toFloat()
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

    @SuppressLint("SetTextI18n")
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.filterUltimasHoje -> {
                textDescricaoFiltro.text = "Últimas de hoje"
                viewModel.filterByMenu(MainViewModel.MENU_FILTER_HOJE)
            }
            R.id.filterUltimaSemana -> {
                textDescricaoFiltro.text = "Últimas da semana"
                viewModel.filterByMenu(MainViewModel.MENU_FILTER_SEMANA)
            }
            R.id.filterUltimas15Dias -> {
                val di = Calendar.getInstance().apply {add(Calendar.DAY_OF_MONTH, -15)}
                textDescricaoFiltro.text = "Últimos 15 dias (${di.time.formataData()} até hoje)"
                viewModel.filterByMenu(MainViewModel.MENU_FILTER_15DIAS)
            }
            R.id.filterUltimasMes -> {
                textDescricaoFiltro.text = "Últimas deste mês."
                viewModel.filterByMenu(MainViewModel.MENU_FILTER_MES)
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
    fun gotoAddReceita()
    fun gotoAddDespesa()
    fun gotoRelatorios()
}
