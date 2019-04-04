package br.com.bruno.join.Util

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import br.com.bruno.join.Application.JApplication
import br.com.bruno.join.R
import br.com.bruno.join.activity.Actions
import br.com.bruno.join.adapter.CategoriaAdapter
import br.com.bruno.join.databinding.FullDialogBinding
import br.com.bruno.join.enums.TipoTransacao
import br.com.bruno.join.extensions.observe
import br.com.bruno.join.repository.ITransacaoRepository
import br.com.bruno.join.repository.TransacaoRepository
import br.com.bruno.join.viewModel.TransacaoViewModel
import com.airbnb.lottie.LottieDrawable
import com.felixsoares.sweetdialog.SweetDialog
import com.transitionseverywhere.*
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
    lateinit var actions: Actions
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

    override fun onDestroy() {
        super.onDestroy()
        actions.closeButton()
    }
    private fun setupView() {
        categoriaAdapter = CategoriaAdapter(activity!!.applicationContext, mutableListOf())
        spnCategory.adapter = categoriaAdapter

        toolbarDialog.apply {
            setNavigationIcon(R.drawable.ic_close_white)
            isSaveEnabled = true
            setNavigationOnClickListener { dialog.dismiss() }
            title = "Transação"
            //background = ColorDrawable(ContextCompat.getColor(context!!, R.color.secondPrimary))
            elevation = 0f
        }

        animationDone.setAnimation("done_primary.json")
        animationDone.playAnimation()
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
            app.showAlert(activity!!, it!!, SweetDialog.Type.DANGER, 2700)
        })

        compDisposable.add(viewModel.showSuccess.subscribe {
            actions.closeButton()
            layoutForm.visibility = View.GONE
            formAnimation.visibility = View.VISIBLE
            animationDialog.setAnimation("sucess.json")
            animationDialog.playAnimation()
            animationDialog.addAnimatorListener(object : Animator.AnimatorListener{
                override fun onAnimationRepeat(animation: Animator?) {}

                override fun onAnimationEnd(animation: Animator?) {
                    Handler().postDelayed({if(dialog != null && dialog.isShowing) dialog.dismiss()}, 500)
                }

                override fun onAnimationCancel(animation: Animator?) {}

                override fun onAnimationStart(animation: Animator?) {}

            })
        })

    }
    fun doAnimate(isReceita: Boolean) {
        TransitionManager.beginDelayedTransition(llTTransacao, ChangeBounds().setPathMotion(ArcMotion()).setDuration(300))
        llToogleParent.gravity = if(isReceita) Gravity.START else Gravity.END

        Handler().postDelayed({
            textReceita.setTextColor(ContextCompat.getColor(activity!!.applicationContext, if (isReceita) R.color.white else R.color.gray_dark))
            textDespesa.setTextColor(ContextCompat.getColor(activity!!.applicationContext, if (isReceita) R.color.gray_dark else R.color.white))
        }, 300)

        TransitionManager.beginDelayedTransition(root, Recolor())
        llToogle.setBackgroundResource(if (isReceita) R.drawable.background_primary else R.drawable.background_accent)

        dialog.editValue.setTextColor(ContextCompat.getColor(context!!, if (isReceita) R.color.receita else R.color.despesa))
        dialog.textDescription.setTextColor(ContextCompat.getColor(context!!, if (isReceita) R.color.receita else R.color.despesa))
        dialog.toolbarDialog.background = if (isReceita) ColorDrawable(ContextCompat.getColor(context!!, R.color.colorPrimary)) else ColorDrawable(ContextCompat.getColor(context!!, R.color.secondPrimary))

        btnAddReceita.visibility = if(isReceita) View.VISIBLE else View.GONE
        btnAddDespesa.visibility = if(isReceita) View.GONE else View.VISIBLE
    }

}