package com.example.messmanagement.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.example.messmanagement.models.Payment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PdfGenerator {
    companion object {
        fun generatePaymentReceipt(context: Context, payment: Payment): File? {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            try {
                drawPaymentReceipt(canvas, payment)
                pdfDocument.finishPage(page)

                // Create directory if it doesn't exist
                val pdfDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "PaymentReceipts")
                if (!pdfDir.exists()) {
                    pdfDir.mkdirs()
                }

                // Create file
                val fileName = "Receipt_${payment.id}_${System.currentTimeMillis()}.pdf"
                val file = File(pdfDir, fileName)

                // Write to file
                pdfDocument.writeTo(FileOutputStream(file))
                pdfDocument.close()

                return file
            } catch (e: IOException) {
                e.printStackTrace()
                pdfDocument.close()
                return null
            }
        }

        private fun drawPaymentReceipt(canvas: Canvas, payment: Payment) {
            val paint = Paint()

            // Draw header
            paint.color = Color.rgb(0, 102, 204)
            paint.textSize = 36f
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText("PAYMENT RECEIPT", 295f, 80f, paint)

            // Draw line
            paint.strokeWidth = 2f
            canvas.drawLine(50f, 100f, 545f, 100f, paint)

            // Reset paint for content
            paint.color = Color.BLACK
            paint.textSize = 14f
            paint.textAlign = Paint.Align.LEFT

            // Draw payment details
            var yPosition = 150f

            canvas.drawText("Receipt ID: ${payment.id}", 50f, yPosition, paint)
            yPosition += 30f

            canvas.drawText("Amount: ₹${payment.amount}", 50f, yPosition, paint)
            yPosition += 30f

            canvas.drawText("UTR Number: ${payment.utrNumber}", 50f, yPosition, paint)
            yPosition += 30f

            val status = payment.status ?: "Unknown"
            canvas.drawText("Status: $status", 50f, yPosition, paint)
            yPosition += 30f

            // Format payment date
            val paymentDate = payment.paymentDate?.let {
                "${it[2]}/${it[1]}/${it[0]}"
            } ?: "N/A"
            canvas.drawText("Payment Date: $paymentDate", 50f, yPosition, paint)
            yPosition += 30f

            if (payment.verificationDate != null) {
                val verificationDate = "${payment.verificationDate[2]}/${payment.verificationDate[1]}/${payment.verificationDate[0]}"
                canvas.drawText("Verification Date: $verificationDate", 50f, yPosition, paint)
                yPosition += 30f
            }

            if (!payment.remarks.isNullOrEmpty()) {
                canvas.drawText("Remarks: ${payment.remarks}", 50f, yPosition, paint)
                yPosition += 30f
            }

            // Draw footer
            paint.textAlign = Paint.Align.CENTER
            paint.textSize = 12f
            canvas.drawText("This is a computer generated receipt and does not require a signature.", 295f, 700f, paint)

            // Draw current date at the bottom
            val currentDate = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
            canvas.drawText("Generated on: $currentDate", 295f, 720f, paint)
        }
    }
}