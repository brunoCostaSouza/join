package br.com.bruno.join.Util

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import br.com.bruno.join.Application.JApplication
import br.com.bruno.join.R
import br.com.bruno.join.entity.Transacao
import br.com.bruno.join.enums.TipoTransacao
import br.com.bruno.join.extensions.formataData
import formatMoney
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.detail_dialog.view.*

class DetailDialog: DialogFragment() {

    companion object {
        var TAG = "DETAIL_DIALOG"
    }

    val compDisposable = CompositeDisposable()
    lateinit var transacao: Transacao

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = layoutInflater.inflate(R.layout.detail_dialog, container, false)
        layout.apply {
            lblDescricao.text = transacao.descricao
            lblValor.text = transacao.valor.formatMoney()
            if(transacao.tipo!! == TipoTransacao.RECEITA.name) {
                lblValor.setTextColor(ContextCompat.getColor(context, R.color.receita))
            } else {
                lblValor.setTextColor(ContextCompat.getColor(context, R.color.despesa))
            }

            lblData.text = "Data da transação: ${transacao.data!!.formataData()}"
            lblCategoria.text = transacao.categoria!!.descricao

            btnEdit.setOnClickListener(listenerEdit)
            btnRemove.setOnClickListener(listenerRemove)
        }
        dialog.window.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context!!, android.R.color.transparent)))
        return layout!!
    }

    private val listenerEdit = View.OnClickListener {
        val dialog = FullScreenDialog()
        dialog.tipoTransacao = if(transacao.tipo == TipoTransacao.RECEITA.name) TipoTransacao.RECEITA else TipoTransacao.DESPESA
        val args = Bundle()
        args.putLong("idTransacao", transacao.id)
        dialog.arguments = args
        dialog.show(activity?.supportFragmentManager, FullScreenDialog.TAG)
    }

    private val listenerRemove = View.OnClickListener {
        Toast.makeText(context, "remove", Toast.LENGTH_SHORT).show()
    }

    /*
    val compDisposable = CompositeDisposable()
    lateinit var  viewModel: TransacaoViewModel
    lateinit var transacaoRepository: ITransacaoRepository
    lateinit var categoriaAdapter: CategoriaAdapter
    lateinit var app: JApplication
    var isReceita = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        transacaoRepository = TransacaoRepository()

        viewModel = ViewModelProviders
                .of(this, TransacaoViewModel.Factory(activity!!.applicationContext, null, transacaoRepository))
                .get(TransacaoViewModel::class.java)

        val binding: TransacaoDialogBinding = DataBindingUtil.inflate(inflater, R.layout.transacao_dialog, container, true)
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val metrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(metrics)
        dialog.window!!.setGravity(Gravity.CENTER)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun setupView() {
        categoriaAdapter = CategoriaAdapter(activity!!.applicationContext, mutableListOf())
        spnCategory.adapter = categoriaAdapter
        imgClose.setOnClickListener { dialog.dismiss() }

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
    */
}