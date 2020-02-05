package br.com.bruno.join.util

import android.app.DatePickerDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import br.com.bruno.join.R
import br.com.bruno.join.extensions.ultimaHora
import br.com.bruno.join.extensions.zeraHora
import kotlinx.android.synthetic.main.periodo_dialog.*
import kotlinx.android.synthetic.main.periodo_dialog.view.*
import java.text.SimpleDateFormat
import java.util.*

class PeriodDialog : DialogFragment() {

    private var datePicker: DatePickerDialog? = null
    private var editSelected = 0

    private var dataInicial: Date? = null
    private var dataFinal: Date? = null

    companion object {
        const val INIT = 1
        const val END  = 2
    }

    lateinit var func: (Date, Date) -> Unit
    fun listenerOnClickFilter(callback: (Date, Date) -> Unit){
        func = callback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = layoutInflater.inflate(R.layout.periodo_dialog, container, false)
        setListeners(layout)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context!!, android.R.color.transparent)))
        return layout
    }

    private fun setListeners(layout: View) {
        layout.editDataInicio.setOnClickListener {
            editSelected = INIT
            openCalendar()
        }
        layout.editDataFim.setOnClickListener {
            editSelected = END
            openCalendar()
        }

        layout.btnPesquisa.setOnClickListener {
            if (dataInicial == null || dataFinal == null) {
                textMsg.text = "Informe o período completo."
                textMsg.visibility = View.VISIBLE
            } else {
                textMsg.visibility = View.GONE
                func(dataInicial!!, dataFinal!!)
            }
        }
    }

    private fun openCalendar() {
        val cal = Calendar.getInstance()
        datePicker = DatePickerDialog(context!!, R.style.DatePickerThemeAccent, dpdListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
        datePicker!!.show()
    }

    private val dpdListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.US)

        when (editSelected) {
            INIT -> {
                editDataInicio.text.clear()
                dataInicial = calendar.time.zeraHora()
                editDataInicio.text.append(format.format(calendar.time))
            }
            END -> {
                editDataFim.text.clear()
                dataFinal = calendar.time.ultimaHora()
                editDataFim.text.append(format.format(calendar.time))
            }
        }
        datePicker!!.dismiss()
    }
}