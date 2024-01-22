package com.yousefelsayed.gamescheap.view.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Process
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.yousefelsayed.gamescheap.R
import com.yousefelsayed.gamescheap.databinding.ActivityWebBinding
import com.yousefelsayed.gamescheap.util.ApiUtilities
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WebActivity: AppCompatActivity() {

    private lateinit var v: ActivityWebBinding
    private lateinit var dealId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        v = DataBindingUtil.setContentView(this, R.layout.activity_web)
        init()
        setupListeners()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun init(){
        //finish()
        window.setBackgroundDrawable(null)
        dealId = intent.getStringExtra("dealId").toString()
        //WebView
        v.webActivityWebView.settings.javaScriptEnabled = true
        v.webActivityWebView.settings.javaScriptCanOpenWindowsAutomatically = false
        v.webActivityWebView.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return false
            }
            override fun onPageFinished(view1: WebView?, url: String?) {
                super.onPageFinished(view1, url)
                if (!url!!.contains("cheapshark.com")) {
                    hideLoadingScreen()
                }
            }
            override fun onPageStarted(view1: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view1, url, favicon)
                if (url!!.contains("cheapshark.com")) {
                    showLoadingScreen()
                }
            }
        }
        v.webActivityWebView.loadUrl(ApiUtilities.CHEAP_SHARK_REDIRECT_LINK+dealId)

    }
    private fun setupListeners(){
        v.activityWebExitImage.setOnClickListener {
            finish()
        }
    }
    private fun showLoadingScreen(){
        v.webActivityLoadingView.visibility = View.VISIBLE
    }
    private fun hideLoadingScreen(){
        v.webActivityLoadingView.visibility = View.GONE
    }

    private fun destroyWebView() {
        v.webActivityMainLayout.removeAllViews()
        v.webActivityWebView.clearHistory()
        v.webActivityWebView.clearCache(true)
        v.webActivityWebView.loadUrl("about:blank")
        v.webActivityWebView.pauseTimers()
    }
    override fun onDestroy() {
        destroyWebView()
        super.onDestroy()
    }
}