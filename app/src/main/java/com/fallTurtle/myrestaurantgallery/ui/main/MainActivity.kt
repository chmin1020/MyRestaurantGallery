package com.fallTurtle.myrestaurantgallery.ui.main

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.databinding.ActivityMainBinding
import com.fallTurtle.myrestaurantgallery.ui.dialog.ProgressDialog
import com.fallTurtle.myrestaurantgallery.data.etc.IS_LOGIN
import com.fallTurtle.myrestaurantgallery.data.etc.LOGIN_CHECK_PREFERENCE
import com.fallTurtle.myrestaurantgallery.data.room.RestaurantInfo
import com.fallTurtle.myrestaurantgallery.ui.add.AddActivity
import com.fallTurtle.myrestaurantgallery.ui.login.LoginActivity
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import dagger.hilt.android.AndroidEntryPoint

/**
 * 앱 최초 실행 시 나오는 메인 화면을 담당 activity.
 * 저장된 모든 맛집 데이터 현황을 볼 수 있다.
 * 그 밖에 추가 버튼을 눌러 맛집 추가 페이지 이동, 메뉴를 통해 회원 logout 및 탈퇴를 할 수 있다.
 **/
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    //뷰 바인딩
    private val binding:ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    //뷰모델 (식당 아이템, 유저)
    private val viewModel:MainViewModel by viewModels()

    //recyclerView 어댑터
    private val itemsAdapter by lazy { RestaurantAdapter(resources.displayMetrics.widthPixels) }

    //유저 아이템 observers (유저 작업 진행 여부, 작업 종료 여부)
    private val itemsObserver = Observer<List<RestaurantInfo>> { itemsAdapter.update(it) }
    private val progressObserver = Observer<Boolean> { decideShowLoading(it) }
    private val userExistObserver = Observer<Boolean> {
        sharedPreferences.edit().putBoolean(IS_LOGIN, it).apply()
        tryToShowLoginWindow()
    }

    //로딩 dialog
    private val progressDialog by lazy { ProgressDialog(this) }

    //공유 설정 (로그인 유지 여부)
    private val sharedPreferences by lazy{ getSharedPreferences(LOGIN_CHECK_PREFERENCE, MODE_PRIVATE) }

    // logout 또는 탈퇴 시 출력할 적절한 메시지
    private var endToastMessage = R.string.logout_success


    //--------------------------------------------
    // 생명 주기 영역

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //로그인 여부 확인 (로그인 상태 아니면 로그인 activity 이동)
        if(sharedPreferences.getBoolean(IS_LOGIN, false)){
            showPermissionDialog()
            initListeners()
            setObservers()
        }
        else goToLoginWindow()

        //recyclerView 세팅 (GridLayout)
        binding.recyclerView.layoutManager = GridLayoutManager(this,2)
        binding.recyclerView.adapter = itemsAdapter

        //툴바와 메뉴 세팅
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onStart() {
        super.onStart()
        viewModel.getAllItems() //식당 아이템 목록 변경 사항 갱신
    }


    //--------------------------------------------
    // overriding 영역

    /* 툴바 메뉴 생성 콜백 */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.account_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /* 튤바 메뉴 옵션에 따른 행동 지정 콜백 */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            //logout 선택 시
            R.id.menu_logout -> logoutCurrentUser()

            //탈퇴 선택 시 (잘못 누르는 경우 치명적 -> dialog 재질문
            R.id.menu_withdrawal -> {
                AlertDialog.Builder(this)
                    .setMessage(R.string.withdrawal_ask)
                    .setPositiveButton(R.string.yes) {_,_ -> withdrawCurrentUser() }
                    .setNegativeButton(R.string.no){_,_ -> }
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    //--------------------------------------------
    // 내부 함수 영역 (초기화)

    /* 뷰 클릭 listeners 지정 함수 */
    private fun initListeners(){
        //add button 클릭 시
        binding.floatingButtonAdd.setOnClickListener{
            val addIntent = Intent(this@MainActivity, AddActivity::class.java)
            startActivity(addIntent)
            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out)
        }
    }

    /* 데이터 변화 관찰을 위한 각 viewModel, observer 연결 */
    private fun setObservers(){
        //각 뷰모델 속 작업 진행 여부 변화 관찰
        viewModel.progressing.observe(this, progressObserver)
        viewModel.userExist.observe(this, userExistObserver)
        viewModel.dataItems.observe(this, itemsObserver)
    }

    /* 앱 실행 전 권한을 받기 위한 dialog 생성 (TedPermission 사용) */
    private fun showPermissionDialog() {
        //권한 허가 질문에 대한 응답 리스너 생성
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() { }
            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(this@MainActivity, R.string.permission_ask, Toast.LENGTH_SHORT).show()
                finishAffinity()
            }
        }

        //TedPermission 객체에 체크할 퍼미션, 리스너 등록
        TedPermission.create()
            .setPermissionListener(permissionListener)
            .setDeniedMessage(R.string.permission_ask)
            .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            .check()
    }


    //--------------------------------------------
    // 내부 함수 영역

    /* 유저와 아이템 작업 진행 여부에 따라 로딩 dialog 띄우는 함수 */
    private fun decideShowLoading(progress: Boolean){
        if(progress) progressDialog.create()
        else progressDialog.destroy()
    }

    /* 유저 존재 여부 확인에 따른 로그인 창 이동 결정 */
    private fun tryToShowLoginWindow(){
        //유저 없는 상태일 때만 로그인 창으로 변경
        if(sharedPreferences.getBoolean(IS_LOGIN, false)) return

        Toast.makeText(this, endToastMessage, Toast.LENGTH_SHORT).show()
        goToLoginWindow()
    }

    /* 실제 로그인 창 이동 */
    private fun goToLoginWindow(){
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    /* 사용자 logout */
    private fun logoutCurrentUser(){
        endToastMessage = R.string.logout_success
        viewModel.logoutUser()
    }

    /* 사용자 탈퇴 처리 */
    private fun withdrawCurrentUser(){
        endToastMessage = R.string.withdrawal_success
        viewModel.withdrawUser()
    }
}