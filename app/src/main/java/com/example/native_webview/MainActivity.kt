package com.example.native_webview

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.net.Uri
import android.provider.Settings

import android.webkit.WebView
import android.widget.Button
import android.widget.Toast
import java.lang.Exception


class MainActivity : AppCompatActivity() {

//    private lateinit var onBtn: Button
//    private lateinit var offBtn: Button

    private lateinit var webView: WebView
    private lateinit var mProgressBar: ProgressBar

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 잠금화면 추가

        var onBtn : Button = findViewById(R.id.onBtn)
        var offBtn : Button = findViewById(R.id.off)
//        onBtn = findViewById<View>(R.id.button) as Button
//        offBtn = findViewById<View>(R.id.button2) as Button
        onBtn.setOnClickListener(View.OnClickListener {
            val intent = Intent(applicationContext, ScreenService::class.java)
            startService(intent)
        })
        offBtn.setOnClickListener(View.OnClickListener {
            val intent = Intent(applicationContext, ScreenService::class.java)
            stopService(intent)
        })

        ////

        webView = findViewById(R.id.webView1)
//        mProgressBar = findViewById(R.id.progress1)

        webView.apply {
            webViewClient = WebViewClientClass() // new WebViewClient()); //클릭시 새창 안뜨게

            //팝업이나 파일 업로드 등 설정해주기 위해 webView.webChromeClient를 설정
            //웹뷰에서 크롬이 실행가능&& 새창띄우기는 안됨
            //webChromeClient = WebChromeClient()

            //웹뷰에서 팝업창 호출하기 위해
            webChromeClient = object : WebChromeClient() {
                override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
                    val newWebView = WebView(this@MainActivity).apply {
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                    }

                    val dialog = Dialog(this@MainActivity).apply {
                        setContentView(newWebView)
                        window!!.attributes.width = ViewGroup.LayoutParams.MATCH_PARENT
                        window!!.attributes.height = ViewGroup.LayoutParams.MATCH_PARENT
                        show()
                    }

                    newWebView.webChromeClient = object : WebChromeClient() {
                        override fun onCloseWindow(window: WebView?) {
                            dialog.dismiss()
                        }
                    }

                    (resultMsg?.obj as WebView.WebViewTransport).webView = newWebView
                    resultMsg.sendToTarget()
                    return true
                }
            }


            settings.javaScriptEnabled = true
            settings.setSupportMultipleWindows(true) // 새창띄우기 허용여부
            settings.javaScriptCanOpenWindowsAutomatically = true // 자바스크립트 새창뛰우기 (멀티뷰) 허용여부
            settings.loadWithOverviewMode = true //메타태크 허용여부
            settings.useWideViewPort = true //화면 사이즈 맞추기 허용여부
            settings.setSupportZoom(true) //화면 줌 허용여부
            settings.builtInZoomControls = true //화면 확대 축소 허용여부
            settings.setSupportMultipleWindows(true);

            // Enable and setup web view cache
            settings.cacheMode =
                WebSettings.LOAD_NO_CACHE //브라우저 캐시 허용여부  // WebSettings.LOAD_DEFAULT
            settings.domStorageEnabled = true //로컬저장소 허용여부
            settings.displayZoomControls = true

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                settings.safeBrowsingEnabled = true  // api 26
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                settings.mediaPlaybackRequiresUserGesture = false
            }

            settings.allowContentAccess = true
            settings.setGeolocationEnabled(true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                settings.allowUniversalAccessFromFileURLs = true
            }

            settings.allowFileAccess = true
            //settings.loadsImagesAutomatically = true

            fitsSystemWindows = true
        }

        val url = "http://memento.webview.s3-website.ap-northeast-2.amazonaws.com"
        webView.loadUrl(url)

        // 앞 뒤로가기 버튼추가
//        previos_btn.setOnClickListener {
//            val canGoBack: Boolean = webView.canGoBack()
//            if (canGoBack) {
//                webView.goBack()
//            }
//        }
//
//
//        next_btn.setOnClickListener {
//            val canGoForward: Boolean = webView.canGoForward()
//            if (canGoForward) {
//                webView.goForward()
//            }
//        }

//
//        //프로그레스 다이얼로그
//        btn5.setOnClickListener {
//
//            val progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleLarge)
//            var params = RelativeLayout.LayoutParams(100, 100)
//            params.addRule(RelativeLayout.CENTER_IN_PARENT)
//            main_layout.addView(progressBar, params)
//
//            // 핸들러를 통해서 종료 작업을 한다.
////            var handler = Handler()
////            var thread = Runnable { pro?.cancel() }
////            handler.postDelayed(thread, 5000) // 딜레이는 5초
//        }
    }

    //웹뷰에서 홈페이지를 띄웠을때 새창이 아닌 기존창에서 실행이 되도록 아래 코드를 넣어준다.
    inner class WebViewClientClass : WebViewClient() {
        //페이지 이동
//        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
//            view.loadUrl(url)
//            return true
//        }
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {

            if (url.startsWith("intent:")) {
                try {
                    val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                    val existPackage =
                        packageManager.getLaunchIntentForPackage(intent.getPackage()!!)
                    if (existPackage != null) {
                        startActivity(intent)
                    } else {
                        val marketIntent = Intent(Intent.ACTION_VIEW)
                        marketIntent.data = Uri.parse("market://details?id=" + intent.getPackage())
                    }
                    return true
                } catch (e: Exception) {
                    try {
                        val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                        if (intent.action!!.contains("kakao")) {
                            view.loadUrl(intent.getStringExtra("browser_fallback_url")!!)
                        } else {
                            val marketIntent = Intent(Intent.ACTION_VIEW)
                            marketIntent.data
                                Uri.parse("market://details?id=" + intent.getPackage())
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
            } else {
                view.loadUrl(url)
            }
            return true
        }


        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            mProgressBar.visibility = ProgressBar.VISIBLE
            webView.visibility = View.INVISIBLE
        }

        override fun onPageCommitVisible(view: WebView, url: String) {
            super.onPageCommitVisible(view, url)
            mProgressBar.visibility = ProgressBar.GONE
            webView.visibility = View.VISIBLE
        }


        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            var builder: android.app.AlertDialog.Builder =
                android.app.AlertDialog.Builder(this@MainActivity)
            var message = "SSL Certificate error."
            when (error.primaryError) {
                SslError.SSL_UNTRUSTED -> message = "The certificate authority is not trusted."
                SslError.SSL_EXPIRED -> message = "The certificate has expired."
                SslError.SSL_IDMISMATCH -> message = "The certificate Hostname mismatch."
                SslError.SSL_NOTYETVALID -> message = "The certificate is not yet valid."
            }
            message += " Do you want to continue anyway?"
            builder.setTitle("SSL Certificate Error")
            builder.setMessage(message)
            builder.setPositiveButton("continue",
                DialogInterface.OnClickListener { _, _ -> handler.proceed() })
            builder.setNegativeButton("cancel",
                DialogInterface.OnClickListener { dialog, which -> handler.cancel() })
            val dialog: android.app.AlertDialog? = builder.create()
            dialog?.show()
        }
    }

    // 잠금화면 점유
//    fun checkPermission() {
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if(!Settings.canDrawOverlays(this)) {
//                val uri = Uri.fromParts("package", packageName, null)
//                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri)
//                startActivityForResult(intent, 0)
//            } else {
//                val intent = Intent(applicationContext, LockScreenService::class.java)
//                startForegroundService(intent)
//            }
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if(requestCode == 0) {
//            if(!Settings.canDrawOverlays(this)) {
//                Toast.makeText(this, "해라", Toast.LENGTH_LONG).show()
//            } else {
//                val intent = Intent(applicationContext, LockScreenService::class.java)
//                startForegroundService(intent)
//            }
//        }
//    }


}