package com.hmk1022.mementomori

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.WindowManager

import android.webkit.WebView
import android.widget.Toast
import java.lang.Exception
import androidx.annotation.RequiresApi
import com.hmk1022.mementomori.service.LockScreenService
import com.kakao.util.helper.Utility
import java.net.URISyntaxException


class MainActivity : AppCompatActivity() {

//    private lateinit var onBtn: Button
//    private lateinit var offBtn: Button

    private lateinit var webView: WebView
//  private lateinit var mProgressBar: ProgressBar
    private val REQ_CODE: Int = 111

    inner class WebViewClientClass : WebViewClient() {
        //페이지 이동

//        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
//            return checkUrl(url)
//        }
//
//        private fun checkUrl(url: String): Boolean {
//            //웹뷰 환경에서 '카카오로그인'버튼을 눌러서 MY_KAKAO_LOGIN_URL 로 이동하려고 한다.
//            if (url.contains("http://memento.webview.s3-website.ap-northeast-2.amazonaws.com/oauth")) {
//                //실제 카카오톡 로그인 기능을 실행할 LoginActivity 를 실행시킨다.
//                val intent = Intent(applicationContext, LoginActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                startActivityForResult(intent, REQ_CODE)
//                return true //리턴 true 하면, 웹뷰에서 실제로 위 URL 로 이동하지는 않는다.
//            }
//            return false
//        }


        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            // 추후 추가 파라미터 url: String
            if (request.url.scheme == "intent") {
                try {
                    // Intent 생성
                    val intent = Intent.parseUri(request.url.toString(), Intent.URI_INTENT_SCHEME)

                    // 실행 가능한 앱이 있으면 앱 실행
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                        Log.d(TAG, "ACTIVITY: ${intent.`package`}")
                        return true
                    }

                    // Fallback URL이 있으면 현재 웹뷰에 로딩
                    val fallbackUrl = intent.getStringExtra("browser_fallback_url")
                    if (fallbackUrl != null) {
                        view.loadUrl(fallbackUrl)
                        Log.d(TAG, "FALLBACK: $fallbackUrl")
                        return true
                    }

                    Log.e(TAG, "Could not parse anythings")

                } catch (e: URISyntaxException) {
                    Log.e(TAG, "Invalid intent request", e)
                }
            }

            // 나머지 서비스 로직 구현

            return false
//            if (url.startsWith("intent:")) {
//                try {
//                    val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
//                    val existPackage =
//                        packageManager.getLaunchIntentForPackage(intent.getPackage()!!)
//                    if (existPackage != null) {
//                        startActivity(intent)
//                    } else {
//                        val marketIntent = Intent(Intent.ACTION_VIEW)
//                        marketIntent.data = Uri.parse("market://details?id=" + intent.getPackage())
//                    }
//                    return true
//                } catch (e: Exception) {
//                    try {
//                        val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
//                        if (intent.action!!.contains("kakao")) {
//                            view.loadUrl(intent.getStringExtra("browser_fallback_url")!!)
//                        } else {
//                            val marketIntent = Intent(Intent.ACTION_VIEW)
//                            marketIntent.data
//                            Uri.parse("market://details?id=" + intent.getPackage())
//                        }
//                    } catch (ex: Exception) {
//                        ex.printStackTrace()
//                    }
//                }
//            } else {
//                view.loadUrl(url)
//            }
//            return true
        }



        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
//            mProgressBar.visibility = ProgressBar.VISIBLE
            webView.visibility = View.INVISIBLE
        }

        override fun onPageCommitVisible(view: WebView, url: String) {
            super.onPageCommitVisible(view, url)
//            mProgressBar.visibility = ProgressBar.GONE
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


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission()
        val keyHash = Utility.getKeyHash(this /* context */)

        System.out.println("키 해쉬 값 구하기" + keyHash)

        // 잠금화면 추가

//        var onBtn : Button = findViewById(R.id.onBtn)
//        var offBtn : Button = findViewById(R.id.off)
//
//        onBtn.setOnClickListener(View.OnClickListener {
//            val intent = Intent(applicationContext, ScreenService::class.java)
//            startService(intent)
//        })
//        offBtn.setOnClickListener(View.OnClickListener {
//            val intent = Intent(applicationContext, ScreenService::class.java)
//            stopService(intent)
//        })

        getWindow().addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        ////


        // 잠금화면 점유
        val wakeLock: PowerManager.WakeLock =
            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag").apply {
                    acquire()
                }
            }
//        var sCpuWakeLock: WakeLock?
//
//
////        if (sCpuWakeLock != null) {
////            return
////        }
//        sCpuWakeLock = getSystemService(POWER_SERVICE).newWakeLock(
//            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or
//                    PowerManager.ACQUIRE_CAUSES_WAKEUP or
//                    PowerManager.ON_AFTER_RELEASE, "hi"
//        )
//
//        sCpuWakeLock.acquire()
//
//
//        if (sCpuWakeLock != null) {
//            sCpuWakeLock.release()
//            sCpuWakeLock = null
//        }



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
            settings.setDisplayZoomControls(false); // 화면 확대 축소 버튼 숨김
            // Enable and setup web view cache
            settings.cacheMode =
                WebSettings.LOAD_NO_CACHE //브라우저 캐시 허용여부  // WebSettings.LOAD_DEFAULT

            settings.domStorageEnabled = true //로컬저장소 허용여부
            settings.displayZoomControls = true

            // 카카오 로그인 시 필요
            settings.setAllowContentAccess(true);
            settings.setAllowFileAccess(true);
            val cm = CookieManager.getInstance()
            cm.setAcceptCookie(true)
            cm.setAcceptThirdPartyCookies(webView, true);
            webView.setWebViewClient(WebViewClientClass())

            val agentNew = settings.userAgentString.toString() + " MY_HYBRID_APP"
            settings.setUserAgentString(agentNew)


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



        fun onActivityResult(requestCode :Int, resultCode: Int, data : Intent?) {
            super.onActivityResult(requestCode, resultCode, data);

            if(requestCode == REQ_CODE && resultCode == Activity.RESULT_OK) {
                var name = data?.getStringExtra("name");
                var email = data?.getStringExtra("email");
                var photoUrl = data?.getStringExtra("photoUrl");
                var kkId = data?.getStringExtra("kkId");
                var kkAccessToken = data?.getStringExtra("kkAccessToken");

                var url = "https://MY_DOMAIN.COM/HANDLE_LOGIN_URL";
                url += "?kkAccessToken="+kkAccessToken;
                if(email != null){
                    url += "&email="+email;
                }
                if(photoUrl != null){
                    url += "&photoUrl="+photoUrl;
                }
                if(kkId != null){
                    url += "&kkId="+kkId;
                }
                if(name != null){
                    url += "&name="+name;
                }
                this.webView.loadUrl(url);
            }
        }

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




    @RequiresApi(Build.VERSION_CODES.O)
    fun checkPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!Settings.canDrawOverlays(this)) {
                val uri = Uri.fromParts("package", packageName, null)
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri)
                startActivityForResult(intent, 0)
            } else {
                val intent = Intent(applicationContext, LockScreenService::class.java)
                startForegroundService(intent)
            }
        }
    }
//
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0) {
            if(!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "hello", Toast.LENGTH_LONG).show()
            } else {
                val intent = Intent(applicationContext, LockScreenService::class.java)
                //startForegroundService(intent)
            }
        }
    }


}