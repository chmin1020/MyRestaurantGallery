package com.fallTurtle.myrestaurantgallery.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.databinding.ActivityLoginBinding
import com.fallTurtle.myrestaurantgallery.dialog.ProgressDialog
import com.fallTurtle.myrestaurantgallery.etc.IS_LOGIN
import com.fallTurtle.myrestaurantgallery.etc.LOGIN_CHECK_PREFERENCE
import com.fallTurtle.myrestaurantgallery.ui.main.MainActivity
import com.fallTurtle.myrestaurantgallery.ui.view_model.UserViewModel
import com.fallTurtle.myrestaurantgallery.ui.view_model.ItemViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

/**
 * 앱의 로그인 화면을 담당하는 액티비티.
 * 로그인 작업을 수행할 수 있으며, 그 기능을 위한 수행은 최대한 viewModel이 수행을 한다.
 **/
class LoginActivity: AppCompatActivity() {
    //뷰 바인딩
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    //뷰모델(식당 아이템, 유저)
    private val viewModelFactory by lazy{ ViewModelProvider.AndroidViewModelFactory(this.application) }
    private val userViewModel by lazy { ViewModelProvider(this, viewModelFactory)[UserViewModel::class.java] }
    private val itemViewModel by lazy { ViewModelProvider(this, viewModelFactory)[ItemViewModel::class.java]}

    //로딩 dialog
    private val progressDialog by lazy { ProgressDialog(this) }

    //observers(유저 존재 유무, 관련 진행 과정 유무)
    private val userExistObserver = Observer<Boolean> { if(it) doProperWorkAccordingToLoginState() }
    private val userProgressObserver = Observer<Boolean> { userProgress = it; decideShowLoading() }

    //observers(아이템 복원 진행 과정 유무, 해당 과정 종료 유무)
    private val itemRestoreFinishObserver = Observer<Boolean> { if(it) showMain() }
    private val itemRestoreProgressObserver = Observer<Boolean> { itemProgress = it; decideShowLoading() }

    // 유저와 아이템 부분의 business 작업의 상태 등 판별 property
    private var userProgress = false
    private var itemProgress = false

    //공유 설정 (로그인 유지 여부)
    private val sharedPreferences by lazy{ getSharedPreferences(LOGIN_CHECK_PREFERENCE, MODE_PRIVATE) }


    //--------------------------------------------
    // activity 결과 런처

    //로그인 요청에 대한 결과를 가져오는 런처
    private val getSignLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        //토큰을 받아온 후 추가 작업 수행
        getTokenForLogin(it.data)
            ?.let { token-> userViewModel.loginUser(token) } //성공 시 로그인
            ?: run{ Toast.makeText(this, R.string.google_auth_failure, Toast.LENGTH_SHORT).show()} //실패 시 메시지 출력
    }


    //--------------------------------------------
    // 액티비티 생명주기 영역

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initListeners() //리스너 설정
        setUserObserver() //유저 observer 설정 (유저가 특정이 되어야 아이템 복원 가능)
    }


    //--------------------------------------------
    // 내부 함수 영역 (초기화)

    /* 화면 내 사용자 입력 관련 뷰의 이벤트 리스너를 등록하는 함수 */
    private fun initListeners(){
        //로그인 버튼 누를 시
        binding.signInButton.setOnClickListener{ trySignIn() }
    }

    /* 유저 뷰모델 observers 설정 함수 */
    private fun setUserObserver(){
        userViewModel.loginCompleteAnswer.observe(this, userExistObserver)
        userViewModel.workFinishFlag.observe(this, userProgressObserver)
    }

    /* 아이템 뷰모델 observers 설정 함수 (유저가 결정이 되어야 외부 저장소 역시 결정) */
    private fun setItemRestoreObserver(){
        itemViewModel.workFinishFlag.observe(this,itemRestoreFinishObserver)
        itemViewModel.progressing.observe(this, itemRestoreProgressObserver)
    }


    //--------------------------------------------
    // 내부 함수 영역 (구글 로그인)
    // <<뷰 영역에 맞지 않으나 인텐트 작업의 존재로 분리가 어려움>>

    /* (구글)로그인을 위한 런처 intent 실행 함수 */
    private fun trySignIn(){
        //구글 로그인을 위한 인텐트 생성
        val signInIntent = GoogleSignIn
                            .getClient(this, getOptionForLogin(getString(R.string.firebase_client_id)))
                            .signInIntent

        //인텐트 결과에 따른 후속 동작을 하는 런처를 통해 실행
        getSignLauncher.launch(signInIntent)
    }

    /* (구글)로그인을 위해 요청에 대응되는 로그인 옵션을 반환하는 함수 */
    private fun getOptionForLogin(request: String): GoogleSignInOptions{
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(request).requestEmail().build()
    }

    /* (구글)로그인을 위해 요청에 대응되는 로그인 토큰을 반환하는 함수 */
    private fun getTokenForLogin(result: Intent?): String?{
        return try {
            //받은 결과의 아이디 토큰을 통해 파이어베이스 인증 시도
            val task = GoogleSignIn.getSignedInAccountFromIntent(result)
            val account = task.result ?: throw NullPointerException()
            account.idToken ?: throw NullPointerException()
        }
        catch (e: Exception) { null }
    }


    //--------------------------------------------
    // 내부 함수 영역 (옵저버 후속 작업)

    /* 유저의 존재(login 성공)를 확인한 뒤 후속 작업을 진행할 때 쓰이는 함수 */
    private fun doProperWorkAccordingToLoginState() {
        sharedPreferences.edit().putBoolean(IS_LOGIN, true).apply()

        //외부 저장소 데이터 복구
        setItemRestoreObserver().also { itemViewModel.restoreItemsFromAccount() }
    }

    /* 로그인이 완료된 것을 확인한 뒤 메인 화면을 실행하는 함수 */
    private fun showMain(){
        //새 로그인 안내 토스트 메시지
        Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show()

        //메인화면 실행 및 현재 화면 종료
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    /* 유저와 아이템 작업 진행 여부를 확인한 뒤 이에 따라 로딩 다이얼로그를 띄우는 함수 */
    private fun decideShowLoading(){
        if(userProgress || itemProgress) progressDialog.create() //둘 중 하나라도 진행 중
        else progressDialog.destroy()
    }
}