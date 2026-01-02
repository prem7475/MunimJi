package com.example.munimji.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Wallet
    @Query("SELECT * FROM wallet LIMIT 1")
    fun getWallet(): Flow<Wallet?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateWallet(wallet: Wallet)

    // Cheques
    @Query("SELECT * FROM cheques ORDER BY id DESC")
    fun getAllCheques(): Flow<List<Cheque>>

    @Insert
    fun insertCheque(cheque: Cheque)

    @Update
    fun updateCheque(cheque: Cheque)

    @Delete
    fun deleteCheque(cheque: Cheque)

    // Inventory
    @Query("SELECT * FROM inventory ORDER BY id DESC")
    fun getAllInventory(): Flow<List<InventoryItem>>

    @Insert
    fun insertInventoryItem(item: InventoryItem)

    @Update
    fun updateInventoryItem(item: InventoryItem)

    @Delete
    fun deleteInventoryItem(item: InventoryItem)

    // Transactions
    @Query("SELECT * FROM transaction_table ORDER BY id DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Insert
    fun insertTransaction(transaction: Transaction)

    // Customers
    @Query("SELECT * FROM customers ORDER BY id DESC")
    fun getAllCustomers(): Flow<List<Customer>>

    @Insert
    fun insertCustomer(customer: Customer)

    @Update
    fun updateCustomer(customer: Customer)

    @Query("SELECT * FROM customers WHERE name = :name LIMIT 1")
    fun getCustomerByName(name: String): Customer?

    // Bank Accounts
    @Query("SELECT * FROM bank_accounts ORDER BY id DESC")
    fun getAllBankAccounts(): Flow<List<BankAccount>>

    @Insert
    fun insertBankAccount(account: BankAccount)

    @Update
    fun updateBankAccount(account: BankAccount)

    @Delete
    fun deleteBankAccount(account: BankAccount)

    // Bills
    @Query("SELECT * FROM bills ORDER BY date DESC")
    fun getAllBills(): Flow<List<Bill>>

    @Query("SELECT * FROM bills WHERE type = :type ORDER BY date DESC")
    fun getBillsByType(type: String): Flow<List<Bill>>

    @Insert
    fun insertBill(bill: Bill): Long

    @Update
    fun updateBill(bill: Bill)

    @Delete
    fun deleteBill(bill: Bill)

    // Bill Items
    @Query("SELECT * FROM bill_items WHERE billId = :billId")
    fun getBillItems(billId: Long): Flow<List<BillItem>>

    @Insert
    fun insertBillItem(item: BillItem)

    @Insert
    fun insertBillItems(items: List<BillItem>)

    @Update
    fun updateBillItem(item: BillItem)

    @Delete
    fun deleteBillItem(item: BillItem)
}

