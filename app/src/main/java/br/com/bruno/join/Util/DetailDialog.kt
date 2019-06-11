package br.com.bruno.join.Util

import android.animation.Animator
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import br.com.bruno.join.R
import br.com.bruno.join.activity.Actions
import br.com.bruno.join.entity.Transacao
import br.com.bruno.join.enums.TipoTransacao
import br.com.bruno.join.extensions.formataData
import com.vicpin.krealmextensions.delete
import formatMoney
import kotlinx.android.synthetic.main.detail_dialog.view.*

class DetailDialog: DialogFragment(), Actions {

    companion object {
        var TAG = "DETAIL_DIALOG"
    }

    val dialogForm = FullScreenDialog()
    lateinit var transacao: Transacao
    lateinit var actions: Actions

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
            lblConsolidado.visibility = if(transacao.consolidado!!){ View.GONE } else { View.VISIBLE }

            btnEdit.setOnClickListener(listenerEdit)
            btnRemove.setOnClickListener(listenerRemove)
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context!!, android.R.color.transparent)))
        setListenersButton(layout)
        return layout!!
    }

    private fun setListenersButton (layout: View) {
        layout.btnRemove.setOnClickListener {
            layout.clDetail.visibility = View.INVISIBLE
            layout.clQuestion.visibility = View.VISIBLE
            layout.animDetail.visibility = View.GONE
        }

        layout.btnQuestionNo.setOnClickListener {
            layout.clDetail.visibility = View.VISIBLE
            layout.clQuestion.visibility = View.GONE
            layout.animDetail.visibility = View.GONE
        }

        layout.btnQuestionYes.setOnClickListener {
            Transacao().delete { equalTo("id", transacao.id) }
            layout.clDetail.visibility = View.INVISIBLE
            layout.clQuestion.visibility = View.GONE
            layout.animDetail.visibility = View.VISIBLE

            if (transacao.tipo == TipoTransacao.RECEITA.name) layout.animDetail.setAnimation("removedReceita.json") else layout.animDetail.setAnimation("removedDespesa.json")
            layout.animDetail.playAnimation()
            layout.animDetail.speed = 1.2F
            layout.animDetail.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {}

                override fun onAnimationEnd(animation: Animator?) {
                    if (dialog.isShowing) dialog.dismiss()
                }

                override fun onAnimationCancel(animation: Animator?) {}

                override fun onAnimationStart(animation: Animator?) {}

            })
        }
    }

    private val listenerEdit = View.OnClickListener {

        if(!dialogForm.isVisible) {
            dialogForm.tipoTransacao = if (transacao.tipo == TipoTransacao.RECEITA.name) TipoTransacao.RECEITA else TipoTransacao.DESPESA
            val args = Bundle()
            args.putLong("idTransacao", transacao.id)
            dialogForm.arguments = args
            dialogForm.actions = this
            dialogForm.show(activity?.supportFragmentManager, FullScreenDialog.TAG)
        }
    }

    private val listenerRemove = View.OnClickListener {
        Toast.makeText(context, "remove", Toast.LENGTH_SHORT).show()
    }

    override fun closeWindowBefore() {
        dismiss()
    }

    override fun gotoAddReceita() {}

    override fun gotoAddDespesa() {}

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