package com.example.munimji.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MunimJiFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // Handle incoming messages here
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Handle new token here
    }
}
