package br.com.bruno.join.repository.contract

import br.com.bruno.join.model.Transaction

interface ITransactionRepository{
    fun saveTransaction(transaction: Transaction)
}