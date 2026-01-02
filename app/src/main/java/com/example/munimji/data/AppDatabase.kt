package com.example.munimji.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        Wallet::class,
        Cheque::class,
        InventoryItem::class,
        Transaction::class,
        Customer::class,
        BankAccount::class,
        GeneralLedger::class,
        Bill::class,
        BillItem::class
    ],
    // Database schema version bumped to 2 to match current entity definitions.
    // When the schema changes, increment this version and provide proper
    // migrations. For now we allow destructive migration to recover cleanly.
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "munimji_database"
                )
                // Register migrations to preserve data where possible.
                .addMigrations(*AppMigrations.ALL_MIGRATIONS)
                // As a safety net in development only, keep destructive fallback commented.
                // .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
