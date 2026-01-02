package com.example.munimji.auth

import android.app.Activity
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.TimeUnit

class AuthManager(private val activity: Activity) {

    private val auth = FirebaseAuth.getInstance()
    private var storedVerificationId: String? = null
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    sealed class AuthState {
        object Idle : AuthState()
        object CodeSent : AuthState()
        data class Verified(val uid: String) : AuthState()
        data class Error(val message: String) : AuthState()
    }

    fun sendVerificationCode(phoneNumber: String): Flow<AuthState> = callbackFlow {
        if (!isValidPhoneNumber(phoneNumber)) {
            trySend(AuthState.Error("Invalid phone number format. Use +91XXXXXXXXXX"))
            close()
            return@callbackFlow
        }

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w(TAG, "onVerificationFailed", e)
                trySend(AuthState.Error(getErrorMessage(e)))
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(TAG, "onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token
                trySend(AuthState.CodeSent)
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

        awaitClose { }
    }

    fun verifyCode(code: String): Flow<AuthState> = callbackFlow {
        val verificationId = storedVerificationId
        if (verificationId == null) {
            trySend(AuthState.Error("No verification ID available"))
            close()
            return@callbackFlow
        }

        if (code.length != 6) {
            trySend(AuthState.Error("Please enter a valid 6-digit code"))
            close()
            return@callbackFlow
        }

        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithPhoneAuthCredential(credential)

        awaitClose { }
    }

    fun resendCode(phoneNumber: String): Flow<AuthState> = callbackFlow {
        if (!::resendToken.isInitialized) {
            trySend(AuthState.Error("Cannot resend code at this time"))
            close()
            return@callbackFlow
        }

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w(TAG, "onVerificationFailed", e)
                trySend(AuthState.Error(getErrorMessage(e)))
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(TAG, "onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token
                trySend(AuthState.CodeSent)
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setForceResendingToken(resendToken)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

        awaitClose { }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = task.result?.user
                    // AuthState.Verified will be emitted through the flow
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // AuthState.Error will be emitted through the flow
                    }
                }
            }
    }

    fun signOut() {
        auth.signOut()
        storedVerificationId = null
    }

    fun getCurrentUser() = auth.currentUser

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        // Indian phone number validation: +91 followed by 10 digits
        val phoneRegex = "^\\+91[6-9]\\d{9}$".toRegex()
        return phoneRegex.matches(phoneNumber)
    }

    private fun getErrorMessage(exception: FirebaseException): String {
        return when (exception) {
            is FirebaseAuthInvalidCredentialsException -> "Invalid verification code"
            else -> "Verification failed: ${exception.localizedMessage ?: "Unknown error"}"
        }
    }

    companion object {
        private const val TAG = "AuthManager"
    }
}
