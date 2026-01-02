package com.example.munimji.utils

import android.util.Log
import androidx.compose.material3.SnackbarDuration

/**
 * Global error handling utility for the MunimJi app
 * Provides consistent error messages and logging across the application
 */
object ErrorHandler {
    private const val TAG = "MunimJiError"

    sealed class ErrorResult {
        data class Success(val message: String? = null) : ErrorResult()
        data class Error(val message: String, val exception: Exception? = null) : ErrorResult()
        data class ValidationError(val field: String, val message: String) : ErrorResult()
    }

    /**
     * Log error and return user-friendly message
     */
    fun handleException(exception: Exception, context: String = ""): ErrorResult.Error {
        val message = when (exception) {
            is IllegalArgumentException -> "Invalid input: ${exception.message}"
            is NullPointerException -> "Data not found. Please try again."
            is NumberFormatException -> "Please enter valid numbers only"
            is IllegalStateException -> "Operation not allowed at this time"
            else -> exception.message ?: "An unexpected error occurred"
        }

        Log.e(TAG, "Error in $context: $message", exception)
        return ErrorResult.Error(message, exception)
    }

    /**
     * Validate amount input
     */
    fun validateAmount(amount: String, fieldName: String = "Amount"): ErrorResult {
        return when {
            amount.isBlank() -> ErrorResult.ValidationError(fieldName, "$fieldName is required")
            amount.toDoubleOrNull() == null -> ErrorResult.ValidationError(fieldName, "Please enter a valid number")
            amount.toDouble() <= 0 -> ErrorResult.ValidationError(fieldName, "$fieldName must be greater than 0")
            else -> ErrorResult.Success()
        }
    }

    /**
     * Validate text input
     */
    fun validateText(text: String, fieldName: String, minLength: Int = 1): ErrorResult {
        return when {
            text.isBlank() -> ErrorResult.ValidationError(fieldName, "$fieldName is required")
            text.length < minLength -> ErrorResult.ValidationError(fieldName, "$fieldName must be at least $minLength characters")
            else -> ErrorResult.Success()
        }
    }

    /**
     * Validate date input
     */
    fun validateDate(dateString: String): ErrorResult {
        return when {
            dateString.isBlank() -> ErrorResult.ValidationError("Date", "Date is required")
            !isValidDateFormat(dateString) -> ErrorResult.ValidationError("Date", "Please enter date in valid format")
            else -> ErrorResult.Success()
        }
    }

    private fun isValidDateFormat(dateString: String): Boolean {
        return try {
            val formats = listOf("dd/MM/yyyy", "dd-MM-yyyy", "yyyy-MM-dd")
            formats.any { format ->
                try {
                    java.text.SimpleDateFormat(format, java.util.Locale.getDefault()).apply {
                        isLenient = false
                    }.parse(dateString) != null
                } catch (e: Exception) {
                    false
                }
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get user-friendly error message for common database operations
     */
    fun getDatabaseErrorMessage(operation: String, exception: Exception): String {
        return when {
            exception.message?.contains("UNIQUE constraint failed") == true -> {
                "This record already exists. Please use a different value."
            }
            exception.message?.contains("NOT NULL constraint failed") == true -> {
                "Some required fields are missing."
            }
            else -> "Database error during $operation. Please try again."
        }
    }

    /**
     * Format error for display
     */
    fun formatErrorMessage(error: ErrorResult): String = when (error) {
        is ErrorResult.Error -> error.message
        is ErrorResult.ValidationError -> "${error.field}: ${error.message}"
        is ErrorResult.Success -> error.message ?: "Success"
    }
}
