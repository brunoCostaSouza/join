package br.com.bruno.join.repository

import br.com.bruno.join.entity.Transacao
import com.vicpin.krealmextensions.queryLast
import com.vicpin.krealmextensions.save

interface ITransacaoRepository{
    fun salvarTransacao(transacao: Transacao)
}

class TransacaoRepository(): ITransacaoRepository{

    override fun salvarTransacao(transacao: Transacao) {
        if (transacao.id == 0L) {
            val ultimaTransacao = Transacao().queryLast()
            if (ultimaTransacao != null) {
                transacao.id = ultimaTransacao.id + 1
            } else {
                transacao.id = 1
            }
        }

        transacao.save()
    }
}