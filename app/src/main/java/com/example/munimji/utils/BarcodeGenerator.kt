package com.example.munimji.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException

object BarcodeGenerator {
    /**
     * Generate Code128 Barcode as Bitmap
     */
    fun generateBarcode(text: String, width: Int = 400, height: Int = 200): Bitmap? {
        return try {
            val writer = MultiFormatWriter()
            val bitMatrix = writer.encode(text, BarcodeFormat.CODE_128, width, height)
            bitMatrixToBitmap(bitMatrix)
        } catch (e: WriterException) {
            null
        }
    }

    /**
     * Generate QR Code as Bitmap
     */
    fun generateQRCode(content: String, width: Int = 512, height: Int = 512): Bitmap? {
        return try {
            val hints = mapOf(EncodeHintType.MARGIN to 1)
            val bitMatrix = MultiFormatWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                width,
                height,
                hints
            )
            bitMatrixToBitmap(bitMatrix)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Generate Data Matrix barcode
     */
    fun generateDataMatrix(content: String, width: Int = 300, height: Int = 300): Bitmap? {
        return try {
            val bitMatrix = MultiFormatWriter().encode(
                content,
                BarcodeFormat.DATA_MATRIX,
                width,
                height
            )
            bitMatrixToBitmap(bitMatrix)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Convert BitMatrix to Android Bitmap
     */
    private fun bitMatrixToBitmap(bitMatrix: com.google.zxing.common.BitMatrix): Bitmap {
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        
        for (y in 0 until height) {
            for (x in 0 until width) {
                bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bmp
    }

    /**
     * Create bill reference string for QR code
     * Format: Bill#|Amount|Date|CustomerName
     */
    fun createBillReference(billNumber: String, amount: Double, date: String, customerName: String): String {
        return "BILL#$billNumber|₹$amount|$date|$customerName"
    }

    /**
     * Create cheque reference string for QR code
     * Format: CHQ#|Amount|Date|BankName
     */
    fun createChequeReference(chequeNumber: String, amount: Double, date: String, bankName: String): String {
        return "CHQ#$chequeNumber|₹$amount|$date|$bankName"
    }
}
