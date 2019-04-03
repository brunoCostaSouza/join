package br.com.bruno.join.Util

import android.os.Bundle
import android.os.Handler
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
import br.com.bruno.join.databinding.FullDialogBinding
import br.com.bruno.join.enums.TipoTransacao
import br.com.bruno.join.extensions.observe
import br.com.bruno.join.repository.ITransacaoRepository
import br.com.bruno.join.repository.TransacaoRepository
import br.com.bruno.join.viewModel.TransacaoViewModel
import com.transitionseverywhere.ArcMotion
import com.transitionseverywhere.ChangeBounds
import com.transitionseverywhere.Recolor
import com.transitionseverywhere.TransitionManager
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.full_dialog.*

class FullScreenDialog: DialogFragment() {

    companion object {
        var TAG = "FullScreenDialog"
    }

    val compDisposable = CompositeDisposable()
    lateinit var  viewModel: TransacaoViewModel
    lateinit var transacaoRepository: ITransacaoRepository
    lateinit var categoriaAdapter: CategoriaAdapter
    lateinit var app: JApplication
    var isReceita = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        transacaoRepository = TransacaoRepository()

        viewModel = ViewModelProviders
                .of(this, TransacaoViewModel.Factory(activity!!.applicationContext, null, transacaoRepository))
                .get(TransacaoViewModel::class.java)

        val binding: FullDialogBinding = DataBindingUtil.inflate(inflater, R.layout.full_dialog, container, true)
        binding.viewModel = viewModel

        app = activity!!.application as JApplication

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupViewModel()
        viewModel.getCategorias()
    }

    override fun onStart() {
        super.onStart()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setWindowAnimations(R.style.FullScreenDialogStyle)
    }

    private fun setupView() {
        categoriaAdapter = CategoriaAdapter(activity!!.applicationContext, mutableListOf())
        spnCategory.adapter = categoriaAdapter

        toolbar.apply {
            setNavigationIcon(R.drawable.ic_close_white)
            isSaveEnabled = true
            setNavigationOnClickListener { dialog.dismiss() }
            title = "Transação"
        }

    }

    private fun setupViewModel() {
        compDisposable.add(viewModel.categorias.observe {
            if(dialog.isShowing) dialog.dismiss()
            categoriaAdapter.categorias = it!!
            categoriaAdapter.notifyDataSetChanged()
        })

        compDisposable.add(viewModel.tipoTransacao.observe {
            doAnimate(it!! == TipoTransacao.RECEITA)
        })

        compDisposable.add(viewModel.showALert.subscribe {
            app.showAlert(it!!, JApplication.ALERT_TYPE_WARNING, Toast.LENGTH_SHORT)
        })

        compDisposable.add(viewModel.showSuccess.subscribe {
            dialog.dismiss()
            Handler().postDelayed({app.showAlert(it!!, JApplication.ALERT_TYPE_SUCCESS, Toast.LENGTH_SHORT)}, 300)
        })

    }

    fun doAnimate(isReceita: Boolean) {
        TransitionManager.beginDelayedTransition(llTTransacao, ChangeBounds().setPathMotion(ArcMotion()).setDuration(300))
        llToogleParent.gravity = if(isReceita) Gravity.START else Gravity.END

        TransitionManager.beginDelayedTransition(llTTransacao, Recolor())
        Handler().postDelayed({
            textReceita.setTextColor(ContextCompat.getColor(activity!!.applicationContext, if (isReceita) R.color.white else R.color.gray_dark))
            textDespesa.setTextColor(ContextCompat.getColor(activity!!.applicationContext, if (isReceita) R.color.gray_dark else R.color.white))
        }, 300)

        llToogle.setBackgroundResource(if (isReceita) R.drawable.background_primary else R.drawable.background_accent)

        //dialog.editValue.setTextColor(ContextCompat.getColor(context, if (isReceita) R.color.despesa else R.color.receita))

        btnAddReceita.visibility = if(isReceita) View.VISIBLE else View.GONE
        btnAddDespesa.visibility = if(isReceita) View.GONE else View.VISIBLE
    }

}