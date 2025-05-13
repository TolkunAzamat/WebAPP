package com.example.webapp

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import android.widget.TextView
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var errorLayout: View
    private lateinit var errorTextView: TextView
    private lateinit var errorImageView: ImageView
    private lateinit var reloadButton: ImageButton
    private lateinit var loadingIndicator: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        webView = findViewById(R.id.webview)
        errorLayout = findViewById(R.id.error_layout)
        errorTextView = findViewById(R.id.error_text)
        errorImageView = findViewById(R.id.error_image)
        reloadButton = findViewById(R.id.reload_button)
        loadingIndicator = findViewById(R.id.loading_indicator)

        val ipAddress = "http://192.168.4.1"
//        val ipAddress = "https://innovationcampus.ru/"
        reloadButton.setOnClickListener {
            // Скрываем изображение и текст ошибки, показываем индикатор загрузки
            errorImageView.visibility = View.GONE
            errorTextView.visibility = View.GONE
            loadingIndicator.visibility = View.VISIBLE
            reloadButton.visibility = View.GONE

            checkConnectionAsync(ipAddress) { isConnected ->
                // Скрываем индикатор загрузки
                loadingIndicator.visibility = View.GONE
                reloadButton.visibility = View.VISIBLE

                if (isConnected) {
                    webView.visibility = View.VISIBLE
                    errorLayout.visibility = View.GONE
                    webView.settings.javaScriptEnabled = true
                    webView.webViewClient = WebViewClient()
                    webView.loadUrl(ipAddress)
                } else {
                    // Если нет подключения, показываем текст и картинку ошибки
                    errorLayout.visibility = View.VISIBLE
                    errorImageView.visibility = View.VISIBLE
                    errorTextView.visibility = View.VISIBLE
                    errorTextView.text = "Алгач \"Eseptegich\" Wi-Fi тармагына кошулуңуз!"
                    errorImageView.setImageResource(R.drawable.wifi)
                }
            }
        }

        // Первоначальная проверка подключения
        checkConnectionAsync(ipAddress) { isConnected ->
            if (isConnected) {
                webView.visibility = View.VISIBLE
                errorLayout.visibility = View.GONE

                webView.settings.javaScriptEnabled = true
                webView.webViewClient = WebViewClient()
                webView.loadUrl(ipAddress)
            } else {
                webView.visibility = View.GONE
                errorLayout.visibility = View.VISIBLE
                errorTextView.text = "Алгач \"Eseptegич\" Wi-Fi тармагына кошулуңуз!"
                errorImageView.setImageResource(R.drawable.wifi)
            }
        }
    }

    private fun checkConnectionAsync(urlString: String, callback: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val isConnected = try {
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 500 // Уменьшение таймаута до 500 мс
                connection.connect()
                val responseCode = connection.responseCode
                connection.disconnect()
                responseCode == 200
            } catch (e: Exception) {
                false
            }
            withContext(Dispatchers.Main) {
                callback(isConnected)
            }
        }
    }
}
