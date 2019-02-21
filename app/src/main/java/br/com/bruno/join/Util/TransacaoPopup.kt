package br.com.bruno.join.Util

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import br.com.bruno.join.Application.JApplication
import br.com.bruno.join.R
import br.com.bruno.join.adapter.CategoriaAdapter
import br.com.bruno.join.databinding.TransacaoDialogBinding
import br.com.bruno.join.enums.TipoTransacao
import br.com.bruno.join.extensions.observe
import br.com.bruno.join.viewModel.TransacaoViewModel
import com.transitionseverywhere.ArcMotion
import com.transitionseverywhere.ChangeBounds
import com.transitionseverywhere.Recolor
import com.transitionseverywhere.TransitionManager
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.transacao_dialog.*

class TransacaoPopup: DialogFragment() {

    val compDisposable = CompositeDisposable()
    lateinit var  viewModel: TransacaoViewModel
    lateinit var categoriaAdapter: CategoriaAdapter
    lateinit var app: JApplication

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProviders
                .of(this, TransacaoViewModel.Factory(activity!!.applicationContext, null))
                .get(TransacaoViewModel::class.java)

        val binding: TransacaoDialogBinding = DataBindingUtil.inflate(inflater, R.layout.transacao_dialog, container, true)
        binding.viewModel = viewModel

        app = activity!!.application as JApplication

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        super.onViewCreated(view, savedInstanceState)
        setupView(view)
        setupViewModel()
        viewModel.getCategorias()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val metrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(metrics)
        dialog.window!!.setGravity(Gravity.CENTER)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //dialog.setCancelable(true)
    }

    private fun setupView(view: View) {
        categoriaAdapter = CategoriaAdapter(activity!!.applicationContext, mutableListOf())
        spnCategory.adapter = categoriaAdapter


        imgClose.setOnClickListener {
            dialog.dismiss()
        }

    }

    private fun setupViewModel() {
        compDisposable.add(viewModel.categorias.observe {
            categoriaAdapter.categorias = it!!
            categoriaAdapter.notifyDataSetChanged()
        })

        compDisposable.add(viewModel.tipoTransacao.observe {
            doAnimate(it!! == TipoTransacao.RECEITA)
        })

        compDisposable.add(viewModel.showALert.observe {
            app.showAlert("Adicionado com sucesso", JApplication.ALERT_TYPE_SUCCESS, Toast.LENGTH_SHORT)
        })
    }

    fun doAnimate(isReceita: Boolean) {
        TransitionManager.beginDelayedTransition(llTTransacao, ChangeBounds().setPathMotion(ArcMotion()).setDuration(300))
        llToogleParent.gravity = if(isReceita) Gravity.END else Gravity.START

        TransitionManager.beginDelayedTransition(llTTransacao, Recolor())
        Handler().postDelayed({
            textReceita.setTextColor(ContextCompat.getColor(activity!!.applicationContext, if (isReceita) R.color.gray_dark else R.color.white))
            textDespesa.setTextColor(ContextCompat.getColor(activity!!.applicationContext, if (isReceita) R.color.white else R.color.gray_dark))
        }, 300)

        llToogle.setBackgroundResource(if (isReceita) R.drawable.background_accent else R.drawable.background_primary)

        //dialog.editValue.setTextColor(ContextCompat.getColor(context, if (isReceita) R.color.despesa else R.color.receita))

        btnAddReceita.visibility = if(!isReceita) View.VISIBLE else View.GONE
        btnAddDespesa.visibility = if(!isReceita) View.GONE else View.VISIBLE
    }

}