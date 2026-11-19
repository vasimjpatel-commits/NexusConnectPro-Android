package com.nexusconnect.pro

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.ViewGroup
import android.webkit.GeolocationPermissions
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var webView: WebView
    private var pendingWebPermission: PermissionRequest? = null
    private var pendingGeoOrigin: String? = null
    private var pendingGeoCallback: GeolocationPermissions.Callback? = null

    companion object {
        private const val CAMERA_MIC_REQUEST = 410
        private const val LOCATION_REQUEST = 411
        private const val SPLASH_DURATION_MS = 900L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showBrandingScreen()
        Handler(Looper.getMainLooper()).postDelayed(
            { openExistingApp() },
            SPLASH_DURATION_MS
        )
    }

    private fun showBrandingScreen() {
        val screen = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.WHITE)
            setPadding(24, 24, 24, 24)
        }

        val name = TextView(this).apply {
            text = "Vasim"
            textSize = 28f
            setTextColor(Color.rgb(32, 32, 32))
            gravity = Gravity.CENTER
        }
        val appName = TextView(this).apply {
            text = "NexusConnect Pro"
            textSize = 21f
            setTextColor(Color.rgb(32, 32, 32))
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 0)
        }
        val credit = TextView(this).apply {
            text = "Designed by Vasim J Patel"
            textSize = 13f
            setTextColor(Color.rgb(110, 110, 110))
            gravity = Gravity.CENTER
            setPadding(0, 18, 0, 0)
        }

        screen.addView(name, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))
        screen.addView(appName, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))
        screen.addView(credit, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))
        setContentView(screen)
    }

    private fun openExistingApp() {
        if (isFinishing || isDestroyed) return

        webView = WebView(this)
        webView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        setContentView(webView)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.settings.setSupportMultipleWindows(true)
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true
        webView.settings.setGeolocationEnabled(true)

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                val url = request.url
                val scheme = url.scheme ?: ""
                if (scheme == "http" || scheme == "https" || scheme == "tel" || scheme == "mailto") {
                    if (url.host == "wa.me" || scheme == "tel" || scheme == "mailto") {
                        openExternal(url)
                        return true
                    }
                    if (scheme == "http") {
                        openExternal(url)
                        return true
                    }
                }
                return false
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                val resources = request.resources
                val needsCameraOrMic = resources.any {
                    it == PermissionRequest.RESOURCE_VIDEO_CAPTURE ||
                    it == PermissionRequest.RESOURCE_AUDIO_CAPTURE
                }
                if (needsCameraOrMic) {
                    pendingWebPermission = request
                    val perms = mutableListOf<String>()
                    if (resources.contains(PermissionRequest.RESOURCE_VIDEO_CAPTURE)) {
                        perms.add(Manifest.permission.CAMERA)
                    }
                    if (resources.contains(PermissionRequest.RESOURCE_AUDIO_CAPTURE)) {
                        perms.add(Manifest.permission.RECORD_AUDIO)
                    }
                    requestPermissions(perms.toTypedArray(), CAMERA_MIC_REQUEST)
                } else {
                    request.grant(resources)
                }
            }

            override fun onGeolocationPermissionsShowPrompt(
                origin: String,
                callback: GeolocationPermissions.Callback
            ) {
                pendingGeoOrigin = origin
                pendingGeoCallback = callback
                val fine = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                val coarse = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                if (fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED) {
                    callback.invoke(origin, true, false)
                } else {
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ),
                        LOCATION_REQUEST
                    )
                }
            }

            override fun onCreateWindow(
                view: WebView,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: android.os.Message
            ): Boolean {
                val popup = WebView(this@MainActivity)
                popup.settings.javaScriptEnabled = true
                popup.webViewClient = WebViewClient()
                val transport = resultMsg.obj as WebView.WebViewTransport
                transport.webView = popup
                resultMsg.sendToTarget()
                return true
            }
        }

        webView.loadUrl("file:///android_asset/index.html")
    }

    private fun openExternal(uri: Uri) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (_: Exception) {
            // No compatible app installed; keep the app open.
        }
    }

    @Deprecated("Deprecated in Android, retained for runtime permission callback compatibility")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_MIC_REQUEST -> {
                val allGranted = grantResults.isNotEmpty() &&
                    grantResults.all { it == PackageManager.PERMISSION_GRANTED }
                val request = pendingWebPermission
                pendingWebPermission = null
                if (allGranted && request != null) {
                    request.grant(request.resources.filter {
                        it == PermissionRequest.RESOURCE_VIDEO_CAPTURE ||
                        it == PermissionRequest.RESOURCE_AUDIO_CAPTURE
                    }.toTypedArray())
                } else {
                    request?.deny()
                }
            }
            LOCATION_REQUEST -> {
                val granted =
                    checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED
                pendingGeoCallback?.invoke(pendingGeoOrigin ?: "", granted, false)
                pendingGeoCallback = null
                pendingGeoOrigin = null
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        if (::webView.isInitialized) {
            webView.stopLoading()
            webView.destroy()
        }
        super.onDestroy()
    }
}
