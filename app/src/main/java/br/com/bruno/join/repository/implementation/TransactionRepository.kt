package br.com.bruno.join.repository.implementation

import br.com.bruno.join.model.Transaction
import br.com.bruno.join.repository.contract.ITransactionRepository
import com.vicpin.krealmextensions.queryLast
import com.vicpin.krealmextensions.save

class TransactionRepository : ITransactionRepository {

    override fun saveTransaction(transaction: Transaction) {
        if (transaction.id == 0L) {
            val lastTransaction = Transaction().queryLast()
            if (lastTransaction != null) {
                transaction.id = lastTransaction.id + 1
            } else {
                transaction.id = 1
            }
        }

        transaction.save()
    }
}