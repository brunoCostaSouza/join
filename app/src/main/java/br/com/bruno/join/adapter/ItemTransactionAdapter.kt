package br.com.bruno.join.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import br.com.bruno.join.R
import br.com.bruno.join.util.DetailDialog
import br.com.bruno.join.model.Transaction
import br.com.bruno.join.enums.TypeTransaction
import br.com.bruno.join.extensions.formataData
import formatMoney
import kotlinx.android.synthetic.main.item_transacao.view.*
import java.util.*

class ItemTransactionAdapter(
        val context: Context,
        val supportFragmentManager: FragmentManager
): RecyclerView.Adapter<TransacaoViewHolder>() {

    private var listaTransacoes = mutableListOf<Transaction>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransacaoViewHolder {
        return TransacaoViewHolder(LayoutInflater.from(context).inflate(R.layout.item_transacao, parent, false), context, supportFragmentManager)
    }

    override fun getItemCount() = listaTransacoes.size

    override fun onBindViewHolder(holder: TransacaoViewHolder, position: Int) {
        holder.bind(listaTransacoes[position])
    }

    fun updateList(transacoes: List<Transaction>){
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
    fun bind(transaction: Transaction){
        when(transaction.tipo){
            TypeTransaction.RECEITA.name -> {

                if (transaction.consolidado!!) {
                    itemView.imgItem.borderColor = context.getColor(R.color.receita)
                    itemView.textValorItem.setTextColor(context.getColor(R.color.receita))
                    itemView.imgItem.setImageResource(R.drawable.ic_arrow_upward)
                } else {
                    itemView.imgItem.borderColor = context.getColor(R.color.gray_dark)
                    itemView.textValorItem.setTextColor(context.getColor(R.color.gray_dark))
                    itemView.imgItem.setImageResource(R.drawable.ic_arrow_upward_gray)
                }

                itemView.textValorItem.text = " + "+transaction.valor.formatMoney()
            }

            TypeTransaction.DESPESA.name -> {

                if(transaction.consolidado!!) {
                    itemView.imgItem.borderColor = context.getColor(R.color.despesa)
                    itemView.imgItem.setImageResource(R.drawable.ic_arrow_down)
                    itemView.textValorItem.setTextColor(context.getColor(R.color.despesa))
                } else {
                    itemView.imgItem.borderColor = context.getColor(R.color.gray_dark)
                    itemView.imgItem.setImageResource(R.drawable.ic_arrow_down_gray)
                    itemView.textValorItem.setTextColor(context.getColor(R.color.gray_dark))
                }

                itemView.textValorItem.text = " - "+transaction.valor.formatMoney()
            }

            TypeTransaction.TRANSFERENCIA.name -> {
                itemView.imgItem.setImageResource(R.drawable.ic_transfer_2)
                itemView.textValorItem.setTextColor(context.getColor(R.color.transferencia))
                itemView.imgItem.borderColor = context.getColor(R.color.transferencia)
                itemView.textValorItem.text = transaction.valor.formatMoney()
            }
        }
        val cal = Calendar.getInstance()
        cal.timeInMillis = transaction.data!!.time

        itemView.descricaoItem.text = transaction.descricao
        itemView.textCategoriaItem.text = transaction.categoria!!.descricao
        itemView.dataItem.text = cal.time.formataData()
        itemView.clRoot.setOnClickListener {
            DetailDialog().apply {
                this.transaction = transaction
            }.show(supportFragmentManager, DetailDialog.TAG)
        }
    }
}