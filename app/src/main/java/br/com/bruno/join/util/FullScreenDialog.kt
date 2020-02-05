package br.com.bruno.join.util

import android.animation.Animator
import android.app.DatePickerDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import br.com.bruno.join.application.JApplication
import br.com.bruno.join.R
import br.com.bruno.join.view.Actions
import br.com.bruno.join.adapter.CategoriaAdapter
import br.com.bruno.join.databinding.FullDialogBinding
import br.com.bruno.join.enums.TypeTransaction
import br.com.bruno.join.extensions.observe
import br.com.bruno.join.extensions.replaceAllCharacters
import br.com.bruno.join.repository.contract.ITransactionRepository
import br.com.bruno.join.repository.implementation.TransactionRepository
import br.com.bruno.join.viewModel.TransactionViewModel
import com.felixsoares.sweetdialog.SweetDialog
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.full_dialog.animationDialog
import kotlinx.android.synthetic.main.full_dialog.animationDone
import kotlinx.android.synthetic.main.full_dialog.editValue
import kotlinx.android.synthetic.main.full_dialog.layoutDescription
import kotlinx.android.synthetic.main.full_dialog.layoutTextData
import kotlinx.android.synthetic.main.full_dialog.llForm
import kotlinx.android.synthetic.main.full_dialog.llformAnimation
import kotlinx.android.synthetic.main.full_dialog.rootDialog
import kotlinx.android.synthetic.main.full_dialog.spnCategory
import kotlinx.android.synthetic.main.full_dialog.switchConsolidadoDespesa
import kotlinx.android.synthetic.main.full_dialog.switchConsolidadoReceita
import kotlinx.android.synthetic.main.full_dialog.textData
import kotlinx.android.synthetic.main.full_dialog.textDescription
import kotlinx.android.synthetic.main.full_dialog.toolbarDialog
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FullScreenDialog : DialogFragment() {

    val ID_TRANSACAO = "idTransacao"
    var current = ""

    companion object {
        var TAG = "FullScreenDialog"
    }

    val compDisposable = CompositeDisposable()
    lateinit var app: JApplication
    lateinit var tipoTransacao: TypeTransaction
    lateinit var viewModel: TransactionViewModel
    lateinit var categoriaAdapter: CategoriaAdapter
    lateinit var transacaoRepository: ITransactionRepository

    var actions: Actions? = null
    var datePickerDialog: DatePickerDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        transacaoRepository = TransactionRepository()

        val idTransacao = arguments?.getLong(ID_TRANSACAO)

        viewModel = ViewModelProviders
            .of(
                this,
                TransactionViewModel.Factory(
                    activity!!.applicationContext,
                    idTransacao,
                    transacaoRepository
                )
            )
            .get(TransactionViewModel::class.java)

        val binding: FullDialogBinding =
            DataBindingUtil.inflate(inflater, R.layout.full_dialog, container, true)
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
        viewModel.setupViews()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog?.window?.setWindowAnimations(R.style.FullScreenDialogStyle)

        editValue.requestFocus()
        Util.showKeyBoard(activity!!, editValue)
    }

    override fun onDestroy() {
        super.onDestroy()
        actions?.closeWindowBefore()
    }

    private fun setupView() {
        categoriaAdapter = CategoriaAdapter(activity!!.applicationContext, mutableListOf())
        spnCategory.adapter = categoriaAdapter

        toolbarDialog.apply {
            setNavigationIcon(R.drawable.ic_close_white)
            setNavigationOnClickListener { dialog?.dismiss() }
            elevation = 0f
        }

        when (tipoTransacao) {
            TypeTransaction.RECEITA -> {
                toolbarDialog.title = "Receita"
                switchConsolidadoReceita.visibility = View.VISIBLE
                switchConsolidadoDespesa.visibility = View.GONE
            }
            else -> {
                toolbarDialog.title = "Despesa"
                switchConsolidadoReceita.visibility = View.GONE
                switchConsolidadoDespesa.visibility = View.VISIBLE
            }
        }

        spnCategory.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                Util.hideKeyBoard(context!!, editValue)
            }
        }

        textData.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) showCalendar() }
        textData.setOnClickListener { showCalendar() }

        editValue.run {

            addTextChangedListener(object : TextWatcher {
                var value = 0.0
                var aux = ""
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(sequence: CharSequence?, start: Int, before: Int, count: Int) {
                    val actual = editValue.text.toString()
                    if (current != actual && actual.length > current.length) {

                        aux += sequence.toString()[sequence!!.length - 1]
                        value = aux.toDouble() / 100

                        editValue.removeTextChangedListener(this)

                        val formatted =
                            DecimalFormat.getCurrencyInstance(Locale("pt", "BR")).format(value)
                        current = formatted
                        editValue.setText(formatted)
                        editValue.setSelection(formatted.length)

                        editValue.addTextChangedListener(this)
                    } else if (editValue.text.toString().length < current.length) {
                        aux = aux.substring(0, aux.length - 1)
                        if (aux.isEmpty()) {
                            aux = "0"
                        }
                        value = aux.toDouble() / 100

                        editValue.removeTextChangedListener(this)

                        val formatted =
                            DecimalFormat.getCurrencyInstance(Locale("pt", "BR")).format(value)
                        current = formatted
                        editValue.setText(formatted)
                        editValue.setSelection(formatted.length)
                        editValue.addTextChangedListener(this)
                    }
                }
            })
        }

        setColorsDialog()
        animationDone.playAnimation()
    }

    private fun showCalendar() {
        if (datePickerDialog == null || !datePickerDialog!!.isShowing) {
            Util.hideKeyBoard(context!!, textData)

            val cal = Calendar.getInstance()
            viewModel.transaction.data?.run { cal.time = this }

            datePickerDialog = DatePickerDialog(
                context!!,
                getStyleCalendar(),
                dpdListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog!!.show()
        }
    }

    private val dpdListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        viewModel.dataTransacao.set(format.format(calendar.time))
        datePickerDialog!!.dismiss()
    }

    private fun setupViewModel() {
        compDisposable.add(viewModel.categorias.observe {
            if (dialog?.isShowing!!) dialog?.dismiss()
            categoriaAdapter.categorias = it!!
            categoriaAdapter.notifyDataSetChanged()
        })

        compDisposable.add(viewModel.categoria.observe {
            if (it != null && it.id != -1L)
                spnCategory.setSelection(viewModel.categorias.get()!!.indexOfFirst { self -> self.id == it.id })
        })

        compDisposable.add(viewModel.showALert.subscribe {
            app.showAlert(activity!!, it!!, SweetDialog.Type.DANGER, 2700)
        })

        compDisposable.add(viewModel.showSuccess.subscribe {
            actions?.closeWindowBefore()

            llForm.visibility = View.GONE
            editValue.visibility = View.INVISIBLE
            animationDone.visibility = View.GONE
            llformAnimation.visibility = View.VISIBLE

            animationDialog.playAnimation()
            animationDialog.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {}

                override fun onAnimationEnd(animation: Animator?) {
                    Handler().postDelayed({ if (dialog?.isShowing!!) dialog?.dismiss() }, 500)
                }

                override fun onAnimationCancel(animation: Animator?) {}

                override fun onAnimationStart(animation: Animator?) {}
            })
        })
    }

    fun setColorsDialog() {
        viewModel.tipoTransacao.set(tipoTransacao)
        when (tipoTransacao) {
            TypeTransaction.RECEITA -> {
                val drawableGreen =
                    ColorDrawable(ContextCompat.getColor(context!!, R.color.colorPrimary))
                val colorGreen = context!!.getColor(R.color.colorPrimary)

                rootDialog.background = drawableGreen
                textDescription.apply {
                    background = drawableGreen
                    setTextColor(colorGreen)
                }
                textDescription.setTextColor(colorGreen)
                textData.apply {
                    background = drawableGreen
                    setTextColor(colorGreen)
                }
                animationDone.setAnimation("done_primary.json")
                animationDialog.setAnimation("sucess_primary.json")
            }

            TypeTransaction.DESPESA -> {
                val drawableGreen =
                    ColorDrawable(ContextCompat.getColor(context!!, R.color.secondPrimary))
                val color = context!!.getColor(R.color.secondPrimary)

                toolbarDialog.setBackgroundColor(color)
                rootDialog.setBackgroundColor(color)
                layoutDescription.boxStrokeColor = color
                layoutTextData.boxStrokeColor = color
                textDescription.apply {
                    background = drawableGreen
                    setTextColor(color)
                }
                textDescription.setTextColor(color)
                textData.apply {
                    background = drawableGreen
                    setTextColor(color)
                }

                animationDone.setAnimation("done_accent.json")
                animationDialog.setAnimation("sucess_accent.json")
            }
            else -> {
            }
        }
    }

    private fun getStyleCalendar(): Int {
        return when (tipoTransacao) {
            TypeTransaction.RECEITA -> R.style.DatePickerThemeReceita
            else -> R.style.DatePickerThemeDespesa
        }
    }

    /*
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
    }*/
}