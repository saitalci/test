package com.rimsa.terminal

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    private lateinit var web: WebView

    // Sayfadaki kamera düğmesi window.Android.scan() çağırır
    inner class Bridge {
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
