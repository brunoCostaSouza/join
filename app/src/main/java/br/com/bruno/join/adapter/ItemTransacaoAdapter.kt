package br.com.bruno.join.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import br.com.bruno.join.R
import br.com.bruno.join.Util.DetailDialog
import br.com.bruno.join.entity.Transacao
import br.com.bruno.join.enums.TipoTransacao
import br.com.bruno.join.extensions.formataData
import formatMoney
import kotlinx.android.synthetic.main.item_transacao.view.*
import java.util.*

class ItemTransacaoAdapter(
        val context: Context,
        val supportFragmentManager: FragmentManager
): RecyclerView.Adapter<TransacaoViewHolder>() {

    private var listaTransacoes = mutableListOf<Transacao>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransacaoViewHolder {
        return TransacaoViewHolder(LayoutInflater.from(context).inflate(R.layout.item_transacao, parent, false), context, supportFragmentManager)
    }

    override fun getItemCount() = listaTransacoes.size

    override fun onBindViewHolder(holder: TransacaoViewHolder, position: Int) {
        holder.bind(listaTransacoes[position])
    }

    fun updateList(transacoes: List<Transacao>){
        listaTransacoes.clear()
        listaTransacoes.addAll(transacoes)
        this.notifyDataSetChanged()
    }
}

class TransacaoViewHolder(
        itemView: View,
        val context: Context,
        val supportFragmentManager: FragmentManager
): RecyclerView.ViewHolder(itemView){
    fun bind(transacao: Transacao){
        when(transacao.tipo){
            TipoTransacao.RECEITA.name -> {
                itemView.imgItem.setImageResource(R.drawable.ic_arrow_upward)
                itemView.imgItem.borderColor = context.getColor(R.color.receita)
                itemView.textValorItem.setTextColor(context.getColor(R.color.receita))
                itemView.textValorItem.text = " + "+transacao.valor.formatMoney()
            }

            TipoTransacao.DESPESA.name -> {
                itemView.imgItem.setImageResource(R.drawable.ic_arrow_down)
                itemView.textValorItem.setTextColor(context.getColor(R.color.despesa))
                itemView.imgItem.borderColor = context.getColor(R.color.despesa)
                itemView.textValorItem.text = " - "+transacao.valor.formatMoney()
            }

            TipoTransacao.TRANSFERENCIA.name -> {
                itemView.imgItem.setImageResource(R.drawable.ic_transfer_2)
                itemView.textValorItem.setTextColor(context.getColor(R.color.transferencia))
                itemView.imgItem.borderColor = context.getColor(R.color.transferencia)
                itemView.textValorItem.text = transacao.valor.formatMoney()
            }
        }
        val cal = Calendar.getInstance()
        cal.timeInMillis = transacao.data!!.time

        itemView.descricaoItem.text = transacao.descricao
        itemView.textCategoriaItem.text = transacao.categoria!!.descricao
        itemView.dataItem.text = cal.time.formataData()
        itemView.setOnClickListener {
            DetailDialog().apply {
                this.transacao = transacao
            }.show(supportFragmentManager, DetailDialog.TAG)
        }
    }
}