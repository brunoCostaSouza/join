package br.com.bruno.join.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.bruno.join.R
import br.com.bruno.join.entity.Transacao
import br.com.bruno.join.enums.TipoTransacao
import br.com.bruno.join.extensions.formataData
import kotlinx.android.synthetic.main.item_transacao.view.*
import java.util.*

class ItemTransacaoAdapter(
        val context: Context
): RecyclerView.Adapter<TransacaoViewHolder>() {

    private var listaTransacoes = mutableListOf<Transacao>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransacaoViewHolder {
        return TransacaoViewHolder(LayoutInflater.from(context).inflate(R.layout.item_transacao, parent), context)
    }

    override fun getItemCount(): Int {
        return listaTransacoes.size
    }

    override fun onBindViewHolder(holder: TransacaoViewHolder, position: Int) {
        holder.bind(listaTransacoes[position])
    }

    fun updateList(transacoes: List<Transacao>){
        listaTransacoes.clear()
        listaTransacoes.addAll(transacoes)
        notifyDataSetChanged()
    }
}

class TransacaoViewHolder(
        itemView: View,
        val context: Context
): RecyclerView.ViewHolder(itemView){
    fun bind(transacao: Transacao){
        when(transacao.tipo){
            TipoTransacao.RECEITA.name -> {
                itemView.imgItem.setImageResource(R.drawable.ic_arrow_upward)
                itemView.textValorItem.setTextColor(context.getColor(R.color.colorPrimary))
            }

            TipoTransacao.DESPESA.name -> {
                itemView.imgItem.setImageResource(R.drawable.ic_arrow_down)
                itemView.textValorItem.setTextColor(context.getColor(R.color.colorAccent))
            }

            TipoTransacao.TRANSFERENCIA.name -> {
                itemView.imgItem.setImageResource(R.drawable.ic_transfer)
                itemView.textValorItem.setTextColor(context.getColor(R.color.transferencia))
            }
        }
        val cal = Calendar.getInstance()
        cal.timeInMillis = transacao.dataTransacao

        itemView.descricaoItem.text = transacao.descricao
        itemView.textCategoriaItem.text = transacao.categoria.descricao
        itemView.dataItem.text = cal.time.formataData()

    }
}