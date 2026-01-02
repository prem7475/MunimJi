package com.example.munimji.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AppRepository(private val dao: AppDao) {

    // Wallet
    val wallet: Flow<Wallet?> = dao.getWallet()

    suspend fun updateWallet(wallet: Wallet) {
        withContext(Dispatchers.IO) {
            dao.updateWallet(wallet)
        }
    }

    // Cheques
    val allCheques: Flow<List<Cheque>> = dao.getAllCheques()

    suspend fun insertCheque(cheque: Cheque) {
        withContext(Dispatchers.IO) {
            dao.insertCheque(cheque)
        }
    }

    suspend fun updateCheque(cheque: Cheque) {
        withContext(Dispatchers.IO) {
            dao.updateCheque(cheque)
        }
    }

    suspend fun deleteCheque(cheque: Cheque) {
        withContext(Dispatchers.IO) {
            dao.deleteCheque(cheque)
        }
    }

    // Inventory
    val allInventory: Flow<List<InventoryItem>> = dao.getAllInventory()

    suspend fun insertInventoryItem(item: InventoryItem) {
        withContext(Dispatchers.IO) {
            dao.insertInventoryItem(item)
        }
    }

    suspend fun updateInventoryItem(item: InventoryItem) {
        withContext(Dispatchers.IO) {
            dao.updateInventoryItem(item)
        }
    }

    suspend fun deleteInventoryItem(item: InventoryItem) {
        withContext(Dispatchers.IO) {
            dao.deleteInventoryItem(item)
        }
    }

    // Transactions
    val allTransactions: Flow<List<Transaction>> = dao.getAllTransactions()

    suspend fun insertTransaction(transaction: Transaction) {
        withContext(Dispatchers.IO) {
            dao.insertTransaction(transaction)
        }
    }

    // Customers
    val allCustomers: Flow<List<Customer>> = dao.getAllCustomers()

    suspend fun insertCustomer(customer: Customer) {
        withContext(Dispatchers.IO) {
            dao.insertCustomer(customer)
        }
    }

    suspend fun updateCustomer(customer: Customer) {
        withContext(Dispatchers.IO) {
            dao.updateCustomer(customer)
        }
    }

    suspend fun getCustomerByName(name: String): Customer? {
        return withContext(Dispatchers.IO) {
            dao.getCustomerByName(name)
        }
    }

    // Bank Accounts
    val allBankAccounts: Flow<List<BankAccount>> = dao.getAllBankAccounts()

    suspend fun insertBankAccount(account: BankAccount) {
        withContext(Dispatchers.IO) {
            dao.insertBankAccount(account)
        }
    }

    suspend fun updateBankAccount(account: BankAccount) {
        withContext(Dispatchers.IO) {
            dao.updateBankAccount(account)
        }
    }

    suspend fun deleteBankAccount(account: BankAccount) {
        withContext(Dispatchers.IO) {
            dao.deleteBankAccount(account)
        }
    }

    // Bills
    val allBills: Flow<List<Bill>> = dao.getAllBills()

    fun getBillsByType(type: String): Flow<List<Bill>> = dao.getBillsByType(type)

    suspend fun insertBill(bill: Bill): Long {
        return withContext(Dispatchers.IO) {
            dao.insertBill(bill)
        }
    }

    suspend fun updateBill(bill: Bill) {
        withContext(Dispatchers.IO) {
            dao.updateBill(bill)
        }
    }

    suspend fun deleteBill(bill: Bill) {
        withContext(Dispatchers.IO) {
            dao.deleteBill(bill)
        }
    }

    fun getBillItems(billId: Long): Flow<List<BillItem>> = dao.getBillItems(billId)

    suspend fun insertBillItem(item: BillItem) {
        withContext(Dispatchers.IO) {
            dao.insertBillItem(item)
        }
    }

    suspend fun insertBillItems(items: List<BillItem>) {
        withContext(Dispatchers.IO) {
            dao.insertBillItems(items)
        }
    }

    suspend fun updateBillItem(item: BillItem) {
        withContext(Dispatchers.IO) {
            dao.updateBillItem(item)
        }
    }

    suspend fun deleteBillItem(item: BillItem) {
        withContext(Dispatchers.IO) {
            dao.deleteBillItem(item)
        }
    }
}
