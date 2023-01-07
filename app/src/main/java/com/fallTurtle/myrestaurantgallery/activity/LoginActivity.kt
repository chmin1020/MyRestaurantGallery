package com.fallTurtle.myrestaurantgallery.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.databinding.ActivityLoginBinding
import com.fallTurtle.myrestaurantgallery.view_model.FirebaseUserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

/**
 * 앱의 로그인 화면을 담당하는 액티비티.
 * 이 앱에서는 파이어베이스의 기능 중 하나인 구글 계정 연동을 사용한다.
 * 따라서 FirebaseAuth, 그 밖에 다른 연관된 작업을 통해 로그인 확인 및 통과 등을 수행한다.
 **/
class LoginActivity: AppCompatActivity() {
    //binding
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    //google Login
    private val mAuth by lazy { FirebaseAuth.getInstance() }
    private val mClient by lazy { GoogleSignIn.getClient(this, googleSignInOptions) }
    private lateinit var googleSignInOptions: GoogleSignInOptions

    //뷰모델
    private val viewModelFactory by lazy{ ViewModelProvider.AndroidViewModelFactory(this.application) }
    private val userViewModel by lazy { ViewModelProvider(this, viewModelFactory)[FirebaseUserViewModel::class.java] }


    //--------------------------------------------
    // 활동 결과를 받기 위한 launcher
    //

    //로그인 시도를 하고 그 결과를 가져오는 launcher
    private val getSignLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        //로그인 시도 활동의 결과로 받은 task
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)

        try {
            //받은 결과의 아이디 토큰을 통해 파이어베이스 인증 시도
            val account = task.getResult(ApiException::class.java) ?: throw NullPointerException()
            val idToken = account.idToken ?: throw NullPointerException()
            firebaseAuthWithGoogle(idToken)
        }
        catch (e: NullPointerException) {
            // null -> 정상적인 데이터를 받아오지 못함
            Toast.makeText(this, "구글 계정 인증 실패", Toast.LENGTH_SHORT).show()
        }
    }


    //--------------------------------------------
    // 액티비티 생명주기 영역
    //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //로그인 시도를 위한 구글 로그인 관련 옵션
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
           .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        //login 버튼을 누르면 구글 계정으로 로그인을 시도하는 이벤트 발생
        binding.signInButton.setOnClickListener{ signIn() }

        //if already login -> skip this activity
        mAuth.currentUser?.let { showMain(false) }
    }


    //--------------------------------------------
    // 내부 함수 영역
    //

    /* 정보를 받아와서 (구글)로그인을 시도하는 함수 */
    private fun signIn(){
        val signInIntent = mClient.signInIntent
        getSignLauncher.launch(signInIntent)
    }

    /* 받은 아이디 토큰을 통해서 파이어베이스 인증을 시도하는 함수 */
    private fun firebaseAuthWithGoogle(idToken: String) {
        //Token 보내서 credential 받아오기
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        //받은 credential 통해 인증 시도
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                //인증이 되었다면 유저 데이터 받을 수 있음 -> 유저 데이터를 가지고 메인 화면으로 이동
                if (task.isSuccessful)
                    mAuth.currentUser?.let { showMain(true) }
            }
    }

    /* 파이어베이스 인증에도 성공했다면, 메인 화면으로 이동할 수 있게 하는 함수 */
    private fun showMain(newLogin: Boolean) {
        userViewModel.updateUser()
        val intent = Intent(this, MainActivity::class.java).also { it.putExtra("newLogin", newLogin) }
        startActivity(intent)
        finish()
    }
}
