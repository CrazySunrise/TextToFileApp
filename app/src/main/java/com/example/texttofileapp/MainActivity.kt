package com.example.texttofileapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            handleIncomingText(intent)
        } else {
            Toast.makeText(
                this,
                "Выдели текст в любом приложении → Поделиться → это приложение",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }

    private fun handleIncomingText(intent: Intent) {
        val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (sharedText.isNullOrBlank()) {
            Toast.makeText(this, "Текст не получен", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        val textFile = saveTextToFile(sharedText)
        if (textFile == null) {
            Toast.makeText(this, "Ошибка создания файла", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        shareTextFile(textFile)
        finish()
    }

    private fun saveTextToFile(text: String): File? {
        return try {
            val file = File(cacheDir, "shared_text_${System.currentTimeMillis()}.txt")
            file.writeText(text)
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun shareTextFile(file: File) {
        val fileUri: Uri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            file
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM, fileUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, "Поделиться текстовым файлом"))
    }
}
