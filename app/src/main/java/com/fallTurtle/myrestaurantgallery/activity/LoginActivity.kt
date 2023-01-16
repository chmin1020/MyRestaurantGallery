package com.fallTurtle.myrestaurantgallery.activity

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
import com.fallTurtle.myrestaurantgallery.view_model.FirebaseUserViewModel
import com.fallTurtle.myrestaurantgallery.view_model.ItemViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn

/**
 * 앱의 로그인 화면을 담당하는 액티비티.
 * 이 앱에서는 파이어베이스의 기능 중 하나인 구글 계정 연동을 사용한다.
 * 따라서 FirebaseAuth, 그 밖에 다른 연관된 작업을 통해 로그인 확인 및 통과 등을 수행한다.
 **/
class LoginActivity: AppCompatActivity() {
    //뷰 바인딩
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    //로딩 다이얼로그
    private val progressDialog by lazy { ProgressDialog(this) }

    //뷰모델
    private val viewModelFactory by lazy{ ViewModelProvider.AndroidViewModelFactory(this.application) }
    private val userViewModel by lazy { ViewModelProvider(this, viewModelFactory)[FirebaseUserViewModel::class.java] }
    private val itemViewModel by lazy { ViewModelProvider(this, viewModelFactory)[ItemViewModel::class.java]}

    //옵저버들
    private val userExistObserver = Observer<Boolean> { if(it) doProperWorkAccordingToLoginState() }
    private val userProgressObserver = Observer<Boolean> { userProgress = it; decideShowLoading() }
    private val itemRestoreFinishObserver = Observer<Boolean> { if(it) showMain(true) }
    private val itemRestoreProgressObserver = Observer<Boolean> { itemProgress = it; decideShowLoading() }

    // 유저와 아이템 부분의 비즈니스 작업의 상태 등을 판별할 프로퍼티
    private var userProgress = false
    private var itemProgress = false

    //공유 설정 (로그인 유지 여부)
    private val sharedPreferences by lazy{ getSharedPreferences("loginCheck", MODE_PRIVATE) }


    //--------------------------------------------
    // 액티비티 결과 런처

    //로그인 요청에 대한 결과를 가져오는 런처
    private val getSignLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        userViewModel.getTokenForLogin(it.data)
            ?.let { token-> userViewModel.loginUser(token) } //성공 시 로그인
            ?: run{ Toast.makeText(this, "구글 계정 인증 실패", Toast.LENGTH_SHORT).show() } //실패 시 메시지 출력
    }


    //--------------------------------------------
    // 액티비티 생명주기 영역

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initListeners() //리스너 설정
        setUserObserver() //유저 옵저버부터 설정
    }


    //--------------------------------------------
    // 내부 함수 영역 (초기화)

    /* 화면 내 사용자 입력 관련 뷰의 이벤트 리스너를 등록하는 함수 */
    private fun initListeners(){
        //로그인 버튼 누를 시
        binding.signInButton.setOnClickListener{ trySignIn() }
    }

    /* 유저 뷰모델 옵저버를 설정하는 함수 */
    private fun setUserObserver(){
        userViewModel.userState.observe(this, userExistObserver)
        userViewModel.workFinishFlag.observe(this, userProgressObserver)
    }

    /* 아이템 뷰모델 옵저버를 설정하는 함수 (유저가 정해진 후 뷰모델이 생성되어야 하므로 따로 분리) */
    private fun setItemRestoreObserver(){
        itemViewModel.workFinishFlag.observe(this,itemRestoreFinishObserver)
        itemViewModel.progressing.observe(this, itemRestoreProgressObserver)
    }


    //--------------------------------------------
    // 내부 함수 영역 (로그인)

    /* (구글)로그인을 위해 런처로 인텐트를 실행하는 함수 */
    private fun trySignIn(){
        val signInIntent = GoogleSignIn
                            .getClient(this, userViewModel.getOptionForLogin(getString(R.string.firebase_client_id)))
                            .signInIntent
        getSignLauncher.launch(signInIntent)
    }


    //--------------------------------------------
    // 내부 함수 영역 (옵저버 후속 작업)

    /* 유저가 존재한다는 걸 확인한 뒤 후속 작업을 진행하는 함수 */
    private fun doProperWorkAccordingToLoginState() {
        if(sharedPreferences.getBoolean("isLogin", false)) //이전에 로그인 했으므로 그냥 메인 실행
            showMain(false)
        else { //새 로그인이므로 상태 저장 및 데이터 복원 실시
            sharedPreferences.edit().putBoolean("isLogin", true).apply()
            setItemRestoreObserver().also { itemViewModel.restoreItemsFromAccount() }
        }
    }

    /* 로그인이 완료된 것을 확인한 뒤 메인 화면을 실행하는 함수 */
    private fun showMain(newLogin: Boolean){
        //새 로그인
        if(newLogin) Toast.makeText(this, "로그인 완료!", Toast.LENGTH_SHORT).show()

        //메인화면 실행 및 현재 화면 종료
        Intent(this, MainActivity::class.java).also{ startActivity(it); finish() }
    }

    /* 유저와 아이템 작업 진행 여부를 확인한 뒤 이에 따라 로딩 다이얼로그를 띄우는 함수 */
    private fun decideShowLoading(){
        if(userProgress || itemProgress) progressDialog.show() //둘 중 하나라도 진행 중
        else progressDialog.close()
    }
}