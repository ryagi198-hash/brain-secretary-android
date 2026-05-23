package com.coop.brainsecretary

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    companion object {
        const val URL =
            "https://script.google.com/a/macros/coop-s.co.jp/s/" +
                "AKfycbxJeAF7J-4pbRJ--oZivwoWdo0L82oIQwzT-y9ESRno9v9cXL3nRpD7Gei0HWDd1qumHg/exec"
        const val VERSION_CHECK_URL = "$URL?check=version"
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webview)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.databaseEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.useWideViewPort = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.setSupportZoom(true)
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = false
        webView.settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webView.settings.cacheMode = android.webkit.WebSettings.LOAD_NO_CACHE
        webView.clearCache(true)
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()
        webView.loadUrl(URL)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) webView.goBack() else finish()
            }
        })

        // 起動時に最新version確認 → 新版あればAlertDialog
        checkForUpdate()
    }

    private fun checkForUpdate() {
        val current = packageManager.getPackageInfo(packageName, 0).versionName ?: "0"
        thread {
            try {
                val conn = URL(VERSION_CHECK_URL).openConnection() as HttpURLConnection
                conn.connectTimeout = 5000
                conn.readTimeout = 5000
                conn.instanceFollowRedirects = true
                val text = conn.inputStream.bufferedReader().readText()
                val json = JSONObject(text)
                val latest = json.optString("version", "0")
                val apkUrl = json.optString("apk_url", "")
                val notes = json.optString("release_notes", "")
                if (latest != current && apkUrl.isNotEmpty() && compareVersion(latest, current) > 0) {
                    runOnUiThread {
                        AlertDialog.Builder(this)
                            .setTitle("📱 新バージョン あり ($latest)")
                            .setMessage("現在: $current → 最新: $latest\n\n$notes")
                            .setPositiveButton("更新する") { _, _ ->
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(apkUrl)))
                            }
                            .setNegativeButton("後で", null)
                            .show()
                    }
                }
            } catch (_: Exception) {
                // version check失敗は無視 (ネットワーク無等)
            }
        }
    }

    private fun compareVersion(a: String, b: String): Int {
        val ap = a.split(".").mapNotNull { it.toIntOrNull() }
        val bp = b.split(".").mapNotNull { it.toIntOrNull() }
        for (i in 0 until maxOf(ap.size, bp.size)) {
            val av = ap.getOrNull(i) ?: 0
            val bv = bp.getOrNull(i) ?: 0
            if (av != bv) return av - bv
        }
        return 0
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
