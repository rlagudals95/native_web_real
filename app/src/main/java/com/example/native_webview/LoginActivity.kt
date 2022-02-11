//
//import android.content.Intent
//
//import android.app.Activity
//
//import android.content.DialogInterface
//
//import android.os.Bundle
//
//import androidx.appcompat.app.AppCompatActivity
////import com.kakao.auth.Session
//
//class LoginActivity : AppCompatActivity() {
//
//    private var isNeedLogin = true
//    override fun onDestroy() {
//        super.onDestroy()
//        Session.getCurrentSession().removeCallback(mKakaoSessionCallback)
//    }
//
//    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        Session.getCurrentSession().close()
//        Session.getCurrentSession().addCallback(mKakaoSessionCallback)
//    }
//
//    override fun onResume() {
//        super.onResume()
//        if (isNeedLogin) {
//            isNeedLogin = false
//            Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, this)
//        }
//    }
//
//    /**
//     * 카카오 세션 콜백.
//     */
//    private val mKakaoSessionCallback: ISessionCallback = object : ISessionCallback() {
//        fun onSessionOpened() {
//            if (Session.getCurrentSession().isOpened()) {
//                Log.e("LoginActivity", "카카오 로그인 성공")
//            }
//            val keys: MutableList<String> = ArrayList()
//            keys.add("kakao_account.profile")
//            keys.add("kakao_account.email")
//            UserManagement.getInstance().me(keys, object : MeV2ResponseCallback() {
//                fun onSessionClosed(errorResult: ErrorResult) {
//                    finishWithError(errorResult.getErrorMessage())
//                }
//
//                fun onSuccess(result: MeV2Response) {
//                    Log.e("LoginActivity", "카카오톡 프로필 가져오기 성공했다.")
//                    val kakaoId: String = result.getId().toString() + ""
//                    val email: String = result.getKakaoAccount().getEmail()
//                    val koAccessToken: String =
//                        Session.getCurrentSession().getTokenInfo().getAccessToken()
//                    val photoUrl: String =
//                        result.getKakaoAccount().getProfile().getProfileImageUrl()
//                    val name: String = result.getKakaoAccount().getProfile().getNickname()
//                    finishWithSuccess(name, email, photoUrl, kakaoId, koAccessToken)
//                }
//
//                fun onFailure(errorResult: ErrorResult) {
//                    super.onFailure(errorResult)
//                    finishWithError(errorResult.getErrorMessage())
//                }
//            })
//        }
//
//        fun onSessionOpenFailed(ex: KakaoException) {
//            Log.e("LoginActivity", "온 쎄션 오픈 페일드.")
//            finishWithError(ex.getLocalizedMessage())
//        }
//    }
//
//    private fun finishWithError(err: String) {
//        val builder: AlertDialog.Builder = Builder(this)
//        builder.setTitle(" 로그인")
//        builder.setMessage(err)
//        builder.setCancelable(false)
//        builder.setPositiveButton("확인",
//            DialogInterface.OnClickListener { dialogInterface, i -> finish() }).show()
//    }
//
//    private fun finishWithSuccess(
//        name: String,
//        email: String,
//        photoUrl: String,
//        kkId: String,
//        kkAccessToken: String
//    ) {
//        val intent = Intent()
//        intent.putExtra("name", name)
//        intent.putExtra("email", email)
//        intent.putExtra("photoUrl", photoUrl)
//        intent.putExtra("kkId", kkId)
//        intent.putExtra("kkAccessToken", kkAccessToken)
//        setResult(RESULT_OK, intent)
//        finish()
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
//        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
//            return
//        }
//        super.onActivityResult(requestCode, resultCode, data)
//    }
//}