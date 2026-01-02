package com.example.munimji.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object AppMigrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            fun hasTable(table: String): Boolean {
                val cursor = db.query("SELECT name FROM sqlite_master WHERE type='table' AND name=?", arrayOf(table))
                cursor.use {
                    return it.count > 0
                }
            }

            fun hasColumn(table: String, column: String): Boolean {
                if (!hasTable(table)) return false
                val cursor = db.query("PRAGMA table_info('$table')")
                cursor.use {
                    while (it.moveToNext()) {
                        try {
                            val nameIndex = it.getColumnIndex("name")
                            if (nameIndex >= 0) {
                                val name = it.getString(nameIndex)
                                if (name == column) return true
                            }
                        } catch (_: Exception) {}
                    }
                }
                return false
            }

            fun addColumnIfMissing(table: String, columnName: String, columnDef: String) {
                if (!hasTable(table)) return
                if (!hasColumn(table, columnName)) {
                    db.execSQL("ALTER TABLE $table ADD COLUMN $columnDef")
                }
            }

            // Wallet
            addColumnIfMissing("wallet", "amount", "amount REAL NOT NULL DEFAULT 0.0")

            // Cheques
            addColumnIfMissing("cheques", "partyName", "partyName TEXT NOT NULL DEFAULT ''")
            addColumnIfMissing("cheques", "number", "number TEXT NOT NULL DEFAULT ''")
            addColumnIfMissing("cheques", "date", "date TEXT NOT NULL DEFAULT ''")
            addColumnIfMissing("cheques", "amount", "amount REAL NOT NULL DEFAULT 0.0")
            addColumnIfMissing("cheques", "status", "status TEXT NOT NULL DEFAULT 'Pending'")

            // Inventory
            addColumnIfMissing("inventory", "name", "name TEXT NOT NULL DEFAULT ''")
            addColumnIfMissing("inventory", "quantity", "quantity INTEGER NOT NULL DEFAULT 0")
            addColumnIfMissing("inventory", "buyPrice", "buyPrice REAL NOT NULL DEFAULT 0.0")
            addColumnIfMissing("inventory", "sellPrice", "sellPrice REAL NOT NULL DEFAULT 0.0")
            addColumnIfMissing("inventory", "barcode", "barcode TEXT NOT NULL DEFAULT ''")

            // Transaction table
            addColumnIfMissing("transaction_table", "type", "type TEXT NOT NULL DEFAULT ''")
            addColumnIfMissing("transaction_table", "partyName", "partyName TEXT NOT NULL DEFAULT ''")
            addColumnIfMissing("transaction_table", "billNo", "billNo TEXT NOT NULL DEFAULT ''")
            addColumnIfMissing("transaction_table", "amount", "amount REAL NOT NULL DEFAULT 0.0")
            addColumnIfMissing("transaction_table", "isCredit", "isCredit INTEGER NOT NULL DEFAULT 0")
            addColumnIfMissing("transaction_table", "date", "date INTEGER NOT NULL DEFAULT 0")
            addColumnIfMissing("transaction_table", "itemName", "itemName TEXT NOT NULL DEFAULT ''")
            addColumnIfMissing("transaction_table", "gstRate", "gstRate REAL NOT NULL DEFAULT 18.0")
            addColumnIfMissing("transaction_table", "gstAmount", "gstAmount REAL NOT NULL DEFAULT 0.0")
            addColumnIfMissing("transaction_table", "cgst", "cgst REAL NOT NULL DEFAULT 0.0")
            addColumnIfMissing("transaction_table", "sgst", "sgst REAL NOT NULL DEFAULT 0.0")
            addColumnIfMissing("transaction_table", "igst", "igst REAL NOT NULL DEFAULT 0.0")
            addColumnIfMissing("transaction_table", "totalWithTax", "totalWithTax REAL NOT NULL DEFAULT 0.0")
            addColumnIfMissing("transaction_table", "discount", "discount REAL NOT NULL DEFAULT 0.0")
            addColumnIfMissing("transaction_table", "paymentMethod", "paymentMethod TEXT NOT NULL DEFAULT 'Cash'")
            addColumnIfMissing("transaction_table", "bankAccountId", "bankAccountId INTEGER")
            addColumnIfMissing("transaction_table", "invoiceNumber", "invoiceNumber TEXT")
            addColumnIfMissing("transaction_table", "notes", "notes TEXT NOT NULL DEFAULT ''")

            // Customers
            addColumnIfMissing("customers", "name", "name TEXT NOT NULL DEFAULT ''")
            addColumnIfMissing("customers", "totalDue", "totalDue REAL NOT NULL DEFAULT 0.0")

            // Bank accounts
            addColumnIfMissing("bank_accounts", "name", "name TEXT NOT NULL DEFAULT ''")
            addColumnIfMissing("bank_accounts", "accountNumber", "accountNumber TEXT NOT NULL DEFAULT ''")
            addColumnIfMissing("bank_accounts", "bankName", "bankName TEXT NOT NULL DEFAULT ''")
            addColumnIfMissing("bank_accounts", "balance", "balance REAL NOT NULL DEFAULT 0.0")
            addColumnIfMissing("bank_accounts", "ifscCode", "ifscCode TEXT NOT NULL DEFAULT ''")

            // General ledger
            addColumnIfMissing("general_ledger", "date", "date INTEGER NOT NULL DEFAULT 0")
            addColumnIfMissing("general_ledger", "description", "description TEXT NOT NULL DEFAULT ''")
            addColumnIfMissing("general_ledger", "accountType", "accountType TEXT NOT NULL DEFAULT ''")
            addColumnIfMissing("general_ledger", "accountName", "accountName TEXT NOT NULL DEFAULT ''")
            addColumnIfMissing("general_ledger", "debit", "debit REAL NOT NULL DEFAULT 0.0")
            addColumnIfMissing("general_ledger", "credit", "credit REAL NOT NULL DEFAULT 0.0")
            addColumnIfMissing("general_ledger", "balance", "balance REAL NOT NULL DEFAULT 0.0")
            addColumnIfMissing("general_ledger", "transactionId", "transactionId TEXT")

            // Bills & Bill Items
            db.execSQL("CREATE TABLE IF NOT EXISTS `bills` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `billNumber` TEXT NOT NULL, `type` TEXT NOT NULL, `customerId` INTEGER NOT NULL, `vendorName` TEXT NOT NULL, `date` INTEGER NOT NULL, `totalAmount` REAL NOT NULL, `taxPercentage` REAL NOT NULL, `taxAmount` REAL NOT NULL, `paymentMode` TEXT NOT NULL, `notes` TEXT NOT NULL, `billImageUrl` TEXT NOT NULL, `status` TEXT NOT NULL)")
            db.execSQL("CREATE TABLE IF NOT EXISTS `bill_items` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `billId` INTEGER NOT NULL, `itemId` INTEGER NOT NULL, `itemName` TEXT NOT NULL, `barcode` TEXT NOT NULL, `quantity` REAL NOT NULL, `unitPrice` REAL NOT NULL, `itemTotal` REAL NOT NULL)")
        }
    }

    val ALL_MIGRATIONS = arrayOf(MIGRATION_1_2)
}
