package br.com.bruno.join.activity

import android.animation.ValueAnimator
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.bruno.join.R
import br.com.bruno.join.Util.FullScreenDialog
import br.com.bruno.join.Util.TransacaoPopup
import br.com.bruno.join.adapter.ItemTransacaoAdapter
import br.com.bruno.join.databinding.HomeBinding
import br.com.bruno.join.extensions.observe
import br.com.bruno.join.viewModel.MainViewModel
import formatMoney
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.home.*

class MainActivity : AppCompatActivity() {

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

    fun setupViewModel(){
        compositeDisposable.add(viewModel.listaTransacoes.subscribe {
            this@MainActivity!!.runOnUiThread {
                transacaoAdapter.updateList(it)
                listItens.adapter = transacaoAdapter
                animateValue()
            }
        })

        compositeDisposable.add(viewModel.saldo.observe {
            if(it!! < 0) {
                layoutTop.background = ContextCompat.getDrawable(this, R.drawable.shape_negative)
                window.statusBarColor = ContextCompat.getColor(this, R.color.secondPrimary)
                //supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.secondPrimary)))
            } else {
                layoutTop.background = ContextCompat.getDrawable(this, R.drawable.shape_positive)
                window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
                //supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimary)))
            }
        })

    }

    private fun setupView(){

        transacaoAdapter = ItemTransacaoAdapter(applicationContext)
        listItens.apply {
            layoutManager = LinearLayoutManager(context)
            layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.animation_item)
            adapter = transacaoAdapter
        }

        btnAdd.setOnClickListener {
            /*val dialog = TransacaoPopup()
            dialog.show(this.supportFragmentManager,"")*/
            FullScreenDialog().show(supportFragmentManager.beginTransaction(), FullScreenDialog.TAG)
        }
    }

    private fun animateValue() {
        val endValue = viewModel.saldo.get()!!.toFloat()
        val animator = ValueAnimator.ofFloat(0f, endValue)
        animator.duration = 800
        animator.addUpdateListener { animation ->
            textSaldoAcumulado.text = (animation.animatedValue as Float).toDouble().formatMoney()
        }
        animator.start()
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
