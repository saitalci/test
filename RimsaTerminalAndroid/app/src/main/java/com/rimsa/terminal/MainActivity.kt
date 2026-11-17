package com.rimsa.terminal

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {
    private lateinit var web: WebView

    inner class Bridge {
        // Kamera ile barkod okuma
        @JavascriptInterface
        fun scan() {
            runOnUiThread {
                GmsBarcodeScanning.getClient(this@MainActivity).startScan()
                    .addOnSuccessListener { b ->
                        val v = JSONObject.quote(b.rawValue ?: "")
                        web.evaluateJavascript("window.onNativeScan($v)", null)
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@MainActivity, "Barkod okunamadı", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        // Sunucu (API) istekleri: sayfa window.Android.http(...) çağırır, sonuç window.onNativeHttp ile döner
        @JavascriptInterface
        fun http(id: String, method: String, url: String, body: String, token: String) {
            Thread {
                var status = 0
                var text = ""
                try {
                    val c = URL(url).openConnection() as HttpURLConnection
                    c.requestMethod = method
                    c.connectTimeout = 8000
                    c.readTimeout = 15000
                    if (token.isNotEmpty()) c.setRequestProperty("Authorization", "Bearer $token")
                    if (body.isNotEmpty()) {
                        c.doOutput = true
                        c.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                        c.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
                    }
                    status = c.responseCode
                    val s = if (status in 200..299) c.inputStream else c.errorStream
                    text = s?.bufferedReader(Charsets.UTF_8)?.readText() ?: ""
                } catch (e: Exception) {
                    status = 0
                    text = e.message ?: ""
                }
                val js = "window.onNativeHttp(${JSONObject.quote(id)},$status,${JSONObject.quote(text)})"
                runOnUiThread { web.evaluateJavascript(js, null) }
            }.start()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        web = WebView(this)
        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.addJavascriptInterface(Bridge(), "Android")
        web.loadUrl("file:///android_asset/index.html")
        setContentView(web)
    }
}
