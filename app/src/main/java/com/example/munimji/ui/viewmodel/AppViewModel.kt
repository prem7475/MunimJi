package com.example.munimji.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.munimji.data.AppRepository
import com.example.munimji.data.Bill
import com.example.munimji.data.BillItem
import com.example.munimji.data.Cheque
import com.example.munimji.data.Customer
import com.example.munimji.data.InventoryItem
import com.example.munimji.data.Transaction
import com.example.munimji.data.Wallet
import com.example.munimji.data.BankAccount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.File

class AppViewModel(val repository: AppRepository) : ViewModel() {

    // Wallet
    val wallet: Flow<Wallet?> = repository.wallet

    fun updateWallet(wallet: Wallet) {
        viewModelScope.launch {
            repository.updateWallet(wallet)
        }
    }

    // Cheques
    val allCheques: Flow<List<Cheque>> = repository.allCheques

    fun insertCheque(cheque: Cheque) {
        viewModelScope.launch {
            repository.insertCheque(cheque)
        }
    }

    fun updateCheque(cheque: Cheque) {
        viewModelScope.launch {
            repository.updateCheque(cheque)
        }
    }

    fun deleteCheque(cheque: Cheque) {
        viewModelScope.launch {
            repository.deleteCheque(cheque)
        }
    }

    // Inventory
    val allInventory: Flow<List<InventoryItem>> = repository.allInventory
    val inventory: Flow<List<InventoryItem>> = repository.allInventory

    fun insertInventoryItem(item: InventoryItem) {
        viewModelScope.launch {
            repository.insertInventoryItem(item)
        }
    }

    fun updateInventoryItem(item: InventoryItem) {
        viewModelScope.launch {
            repository.updateInventoryItem(item)
        }
    }

    fun deleteInventoryItem(item: InventoryItem) {
        viewModelScope.launch {
            repository.deleteInventoryItem(item)
        }
    }

    // Transactions
    val allTransactions: Flow<List<Transaction>> = repository.allTransactions
    val transactions: Flow<List<Transaction>> = repository.allTransactions

    fun insertTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.insertTransaction(transaction)
        }
    }

    // Customers
    val allCustomers: Flow<List<Customer>> = repository.allCustomers
    val customers: Flow<List<Customer>> = repository.allCustomers

    fun insertCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.insertCustomer(customer)
        }
    }

    fun updateCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.updateCustomer(customer)
        }
    }

    suspend fun getCustomerByName(name: String): Customer? {
        return repository.getCustomerByName(name)
    }

    // Bank Accounts - stubs for now
    val bankAccounts: Flow<List<BankAccount>> = repository.allBankAccounts

    fun insertBankAccount(account: BankAccount) {
        viewModelScope.launch {
            repository.insertBankAccount(account)
        }
    }

    // Bills
    val allBills: Flow<List<Bill>> = repository.allBills

    fun getBillsByType(type: String): Flow<List<Bill>> = repository.getBillsByType(type)

    fun insertBill(bill: Bill) {
        viewModelScope.launch {
            repository.insertBill(bill)
        }
    }

    fun updateBill(bill: Bill) {
        viewModelScope.launch {
            repository.updateBill(bill)
        }
    }

    fun deleteBill(bill: Bill) {
        viewModelScope.launch {
            repository.deleteBill(bill)
        }
    }

    fun getBillItems(billId: Long): Flow<List<BillItem>> = repository.getBillItems(billId)

    fun insertBillItem(item: BillItem) {
        viewModelScope.launch {
            repository.insertBillItem(item)
        }
    }

    fun insertBillItems(items: List<BillItem>) {
        viewModelScope.launch {
            repository.insertBillItems(items)
        }
    }
}

