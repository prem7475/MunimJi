package com.example.munimji.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.example.munimji.data.Bill
import com.example.munimji.data.BillItem
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class PdfBillGenerator(private val context: Context) {
    
    fun generateBillPdf(
        bill: Bill,
        items: List<BillItem>,
        shopName: String = "Business Mate",
        shopPhone: String = ""
    ): File? {
        return try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas
            val paint = Paint().apply { textSize = 12f }
            
            var yPosition = 40f
            
            // Header
            paint.apply { textSize = 18f }
            canvas.drawText(shopName, 50f, yPosition, paint)
            yPosition += 25f
            
            paint.apply { textSize = 10f }
            canvas.drawText("Phone: $shopPhone", 50f, yPosition, paint)
            yPosition += 20f
            canvas.drawText("Bill No: ${bill.billNumber}", 50f, yPosition, paint)
            yPosition += 20f
            
            val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
            canvas.drawText("Date: ${dateFormat.format(Date(bill.date))}", 50f, yPosition, paint)
            yPosition += 20f
            canvas.drawText("Type: ${bill.type}", 50f, yPosition, paint)
            yPosition += 30f
            
            // Items Header
            paint.apply { textSize = 10f }
            canvas.drawText("Item", 50f, yPosition, paint)
            canvas.drawText("Qty", 250f, yPosition, paint)
            canvas.drawText("Price", 320f, yPosition, paint)
            canvas.drawText("Total", 420f, yPosition, paint)
            yPosition += 15f
            
            // Items
            items.forEach { item ->
                canvas.drawText(item.itemName.take(20), 50f, yPosition, paint)
                canvas.drawText(item.quantity.toString(), 250f, yPosition, paint)
                canvas.drawText("₹${String.format("%.2f", item.unitPrice)}", 320f, yPosition, paint)
                canvas.drawText("₹${String.format("%.2f", item.itemTotal)}", 420f, yPosition, paint)
                yPosition += 15f
            }
            
            yPosition += 10f
            // Total
            paint.apply { textSize = 12f }
            canvas.drawText("Tax (${bill.taxPercentage}%): ₹${String.format("%.2f", bill.taxAmount)}", 320f, yPosition, paint)
            yPosition += 20f
            canvas.drawText("Total: ₹${String.format("%.2f", bill.totalAmount)}", 320f, yPosition, paint)
            
            pdfDocument.finishPage(page)
            
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "bill_${bill.billNumber}.pdf")
            file.parentFile?.mkdirs()
            pdfDocument.writeTo(file.outputStream())
            pdfDocument.close()
            
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
