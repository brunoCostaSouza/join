package br.com.bruno.join.activity

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.bruno.join.R
import br.com.bruno.join.adapter.ItemTransacaoAdapter
import br.com.bruno.join.databinding.ActivityMainBinding
import br.com.bruno.join.viewModel.ResumoVM
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.transitionseverywhere.ArcMotion
import com.transitionseverywhere.ChangeBounds
import com.transitionseverywhere.Recolor
import com.transitionseverywhere.TransitionManager
import formatMoney
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.sheet_transacao.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ResumoVM
    private lateinit var transacaoAdapter: ItemTransacaoAdapter
    private lateinit var bottomBehavior: BottomSheetBehavior<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(ResumoVM::class.java)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.viewModel = viewModel

        setupView()
        setupViewModel()
        viewModel.addItem()
    }

    fun setupViewModel(){
        viewModel.saldo.set(1000.0)
        viewModel.totalDespesa.set(2000.0)
        viewModel.totalReceita.set(3000.0)
        viewModel.listaTransacoes.subscribe {
            transacaoAdapter.updateList(it)
            listItens.adapter = transacaoAdapter
            animateValue()
        }
    }

    private fun setupView(){
        transacaoAdapter = ItemTransacaoAdapter(applicationContext)
        listItens.apply {
            layoutManager = LinearLayoutManager(context)
            layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.animation_item)
            adapter = transacaoAdapter
        }

        btnAdd.setOnClickListener {
            bottomBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        bottomBehavior = BottomSheetBehavior.from(sheetTransacao)
        bottomBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        var isReceita = true
        llTipoTransacao.setOnClickListener {

            TransitionManager.beginDelayedTransition(llTipoTransacao, ChangeBounds().setPathMotion(ArcMotion()).setDuration(400))
            llToogleParent.gravity = if(isReceita) Gravity.END else Gravity.START

            TransitionManager.beginDelayedTransition(llTipoTransacao, Recolor())
            textReceita.setTextColor(ContextCompat.getColor(this, if (isReceita) R.color.gray else R.color.whiteDark))
            textDespesa.setTextColor(ContextCompat.getColor(this, if (isReceita) R.color.whiteDark else R.color.gray))
            llToogle.setBackgroundResource(if (isReceita) R.drawable.background_accent else R.drawable.background_primary)

            editValue.setTextColor(ContextCompat.getColor(this, if (isReceita) R.color.despesa else R.color.receita))
            isReceita = !isReceita
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
