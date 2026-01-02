package com.example.munimji.utils

import android.content.Context
import com.example.munimji.data.AppDatabase
import com.example.munimji.data.Wallet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DatabaseInitializer {
    fun initializeDatabase(context: Context) {
        val database = AppDatabase.getDatabase(context)
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Check if wallet exists, if not create default
                val walletFlow = database.appDao().getWallet()
                walletFlow.collect { wallet ->
                    if (wallet == null) {
                        val defaultWallet = Wallet(amount = 0.0)
                        database.appDao().updateWallet(defaultWallet)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
