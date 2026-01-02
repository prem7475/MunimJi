package com.example.munimji.workers

import android.content.Context
import androidx.work.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*
import java.util.concurrent.TimeUnit

class ChequeReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        return try {
            checkUpcomingCheques()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun checkUpcomingCheques() {
        val db = FirebaseFirestore.getInstance()
        val userId = inputData.getString("userId") ?: return

        // Get current date and tomorrow's date
        val calendar = Calendar.getInstance()
        val today = calendar.time

        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val tomorrow = calendar.time

        // Query for cheques due today or tomorrow
        val chequesRef = db.collection("users").document(userId).collection("cheques")

        // Check for tomorrow's cheques
        val tomorrowQuery = chequesRef
            .whereEqualTo("status", "Pending")
            .whereGreaterThanOrEqualTo("date", getStartOfDay(tomorrow))
            .whereLessThan("date", getEndOfDay(tomorrow))

        tomorrowQuery.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val chequeData = document.data
                val payeeName = chequeData["partyName"] as? String ?: "Unknown"
                val amount = chequeData["amount"] as? Double ?: 0.0
                val date = chequeData["date"] as? com.google.firebase.Timestamp

                // Send FCM notification for tomorrow's cheque
                sendChequeReminderNotification(
                    userId,
                    "Cheque Due Tomorrow",
                    "₹${amount} cheque to $payeeName is due tomorrow",
                    "cheque_reminder"
                )
            }
        }

        // Check for today's cheques
        val todayQuery = chequesRef
            .whereEqualTo("status", "Pending")
            .whereGreaterThanOrEqualTo("date", getStartOfDay(today))
            .whereLessThan("date", getEndOfDay(today))

        todayQuery.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val chequeData = document.data
                val payeeName = chequeData["partyName"] as? String ?: "Unknown"
                val amount = chequeData["amount"] as? Double ?: 0.0

                // Send FCM notification for today's cheque
                sendChequeReminderNotification(
                    userId,
                    "Cheque Due Today",
                    "₹${amount} cheque to $payeeName is due TODAY!",
                    "cheque_reminder"
                )
            }
        }
    }

    private fun getStartOfDay(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    private fun getEndOfDay(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.time
    }

    private fun sendChequeReminderNotification(
        userId: String,
        title: String,
        body: String,
        type: String
    ) {
        val db = FirebaseFirestore.getInstance()

        // Get user's FCM token
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val token = document.getString("fcmToken")
                if (token != null) {
                    // Send notification via FCM (this would typically be done via Cloud Functions)
                    // For now, we'll store the notification in Firestore to be picked up by Cloud Functions
                    val notificationData = hashMapOf(
                        "userId" to userId,
                        "token" to token,
                        "title" to title,
                        "body" to body,
                        "type" to type,
                        "timestamp" to com.google.firebase.Timestamp.now()
                    )

                    db.collection("notifications").add(notificationData)
                }
            }
    }

    companion object {
        fun scheduleDailyChequeCheck(context: Context, userId: String) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val inputData = Data.Builder()
                .putString("userId", userId)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<ChequeReminderWorker>(
                24, TimeUnit.HOURS // Run daily
            )
                .setConstraints(constraints)
                .setInputData(inputData)
                .setInitialDelay(8, TimeUnit.HOURS) // Start at 8 AM
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "cheque_reminder_work",
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
        }

        fun cancelChequeReminders(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork("cheque_reminder_work")
        }
    }
}
