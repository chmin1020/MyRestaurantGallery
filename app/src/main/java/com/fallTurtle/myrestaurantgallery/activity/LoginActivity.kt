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
import com.fallTurtle.myrestaurantgallery.view_model.FirebaseUserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn

/**
 * 앱의 로그인 화면을 담당하는 액티비티.
 * 이 앱에서는 파이어베이스의 기능 중 하나인 구글 계정 연동을 사용한다.
 * 따라서 FirebaseAuth, 그 밖에 다른 연관된 작업을 통해 로그인 확인 및 통과 등을 수행한다.
 **/
class LoginActivity: AppCompatActivity() {
    //뷰 바인딩
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    //뷰모델
    private val viewModelFactory by lazy{ ViewModelProvider.AndroidViewModelFactory(this.application) }
    private val userViewModel by lazy { ViewModelProvider(this, viewModelFactory)[FirebaseUserViewModel::class.java] }

    //공유 설정 (로그인 유지 여부)
    private val sharedPreferences by lazy{ getSharedPreferences("loginCheck", MODE_PRIVATE) }


    //--------------------------------------------
    // 활동 결과를 받기 위한 launcher
    //

    //로그인 요청에 대한 결과를 가져오는 launcher
    private val getSignLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        //요청이 성공해서 토큰을 받았다면 계정 인증 시도, 실패 시 토스트 메시지 출력
        userViewModel.getTokenForLogin(it.data)
            ?.let { token-> userViewModel.loginUser(token) }
            ?: run{ Toast.makeText(this, "구글 계정 인증 실패", Toast.LENGTH_SHORT).show() }
    }


    //--------------------------------------------
    // 액티비티 생명주기 영역
    //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //login 버튼을 누르면 구글 계정으로 로그인을 시도하는 이벤트 발생
        binding.signInButton.setOnClickListener{ signIn() }

        //LiveData, observer 기능을 통해 실시간 검색 결과 변화 감지 및 출력
        val userObserver = Observer<Boolean> { if(it) tryToShowMain() }
        userViewModel.userState.observe(this, userObserver)
    }


    //--------------------------------------------
    // 내부 함수 영역
    //

    /* (구글)로그인을 시도하는 함수 */
    private fun signIn(){
        val signInIntent = GoogleSignIn
                            .getClient(this, userViewModel.getOptionForLogin(getString(R.string.firebase_client_id)))
                            .signInIntent
        getSignLauncher.launch(signInIntent)
    }

    /* 파이어베이스 인증에도 성공했다면, 메인 화면으로 이동할 수 있게 하는 함수 */
    private fun tryToShowMain() {
        val newLogin = !sharedPreferences.getBoolean("isLogin", false)
        sharedPreferences.edit().putBoolean("isLogin", true).apply()

        val intent = Intent(this, MainActivity::class.java).also { it.putExtra("newLogin", newLogin) }
        if(newLogin) Toast.makeText(this, "로그인 완료!", Toast.LENGTH_SHORT).show()

        startActivity(intent)
        finish()
    }
}
