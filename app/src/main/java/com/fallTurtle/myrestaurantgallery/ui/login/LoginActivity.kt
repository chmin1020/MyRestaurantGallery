package com.fallTurtle.myrestaurantgallery.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.databinding.ActivityLoginBinding
import com.fallTurtle.myrestaurantgallery.ui.dialog.ProgressDialog
import com.fallTurtle.myrestaurantgallery.data.etc.IS_LOGIN
import com.fallTurtle.myrestaurantgallery.data.etc.LOGIN_CHECK_PREFERENCE
import com.fallTurtle.myrestaurantgallery.ui.main.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.AndroidEntryPoint

/**
 * 로그인 화면 담당 activity.
 **/
@AndroidEntryPoint
class LoginActivity: AppCompatActivity() {
    //뷰 바인딩
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    //뷰모델
    private val viewModel: LoginViewModel by viewModels()

    //로딩 dialog
    private val progressDialog by lazy { ProgressDialog(this) }

    //observers(유저 존재 유무, 관련 진행 과정 유무)
    private val userExistObserver = Observer<Boolean> { userExist = it }
    private val progressObserver = Observer<Boolean> { decideShowLoading(it) }

    //유저 존재 여부 (메인 화면 이동을 위해 필요)
    private var userExist = false

    //공유 설정 (로그인 유지 여부)
    private val sharedPreferences by lazy{ getSharedPreferences(LOGIN_CHECK_PREFERENCE, MODE_PRIVATE) }


    //--------------------------------------------
    // activity 결과 런처

    //로그인 요청에 대한 결과 처리 수행 런처
    private val getSignLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        //토큰 여부에 따라 후속 작업
        getTokenForLogin(it.data)
            ?.let { token-> viewModel.loginUser(token) } //성공 시 로그인
            ?: run{ Toast.makeText(this, R.string.google_auth_failure, Toast.LENGTH_SHORT).show()} //실패 시 메시지 출력
    }


    //--------------------------------------------
    // 생명 주기 영역

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initListeners()
        setObservers()
    }


    //--------------------------------------------
    // 내부 함수 영역 (초기화)

    /* 화면 내 사용자 입력 관련 뷰의 이벤트 리스너 등록 */
    private fun initListeners(){
        //로그인 버튼 누를 시
        binding.signInButton.setOnClickListener{ trySignIn() }
    }

    /* 유저 뷰모델 observers 설정 */
    private fun setObservers(){
        viewModel.userExist.observe(this, userExistObserver)
        viewModel.progressing.observe(this, progressObserver)
    }

    //--------------------------------------------
    // 내부 함수 영역 (구글 로그인)
    // <<뷰 영역에 맞지 않으나 인텐트 작업의 존재로 분리가 어려움>>

    /* 로그인 런처 intent 실행 함수 */
    private fun trySignIn(){
        val loginOption = getOptionForLogin(getString(R.string.firebase_client_id))

        //로그인 인텐트 생성 및 실행
        val signInIntent = GoogleSignIn.getClient(this, loginOption).signInIntent
        getSignLauncher.launch(signInIntent)
    }

    /* 로그인 옵션을 반환 */
    private fun getOptionForLogin(request: String): GoogleSignInOptions{
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(request).requestEmail().build()
    }

    /* 로그인 토큰을 반환 */
    private fun getTokenForLogin(result: Intent?): String?{
        return try {
            //받은 결과의 아이디 토큰을 통해 firebase 인증 시도
            val task = GoogleSignIn.getSignedInAccountFromIntent(result)
            val account = task.result ?: throw NullPointerException()
            account.idToken ?: throw NullPointerException()
        }
        catch (e: Exception) { null }
    }


    //--------------------------------------------
    // 내부 함수 영역 (옵저버 후속 작업)

    /* 유저의 존재(login 성공)를 확인한 뒤 후속 작업을 진행할 때 쓰이는 함수 */
    private fun loginCompletePostWork() {
        sharedPreferences.edit().putBoolean(IS_LOGIN, true).apply()
        showMain()
    }

    /* 로그인 완료(user 존재) 확인 후 메인 화면을 실행 */
    private fun showMain(){
        if(!userExist) return

        //새 로그인 안내 토스트 메시지
        Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show()

        //메인 화면 실행 및 현재 화면 종료
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    /* 진행 여부에 따른 progress dialog 생성 또는 제거 */
    private fun decideShowLoading(loginProgress: Boolean){
        if(loginProgress) progressDialog.create()
        else {
            progressDialog.destroy()
            loginCompletePostWork()
        }
    }
}