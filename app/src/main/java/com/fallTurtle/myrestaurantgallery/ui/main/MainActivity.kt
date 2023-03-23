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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.ui.adapter.RestaurantAdapter
import com.fallTurtle.myrestaurantgallery.databinding.ActivityMainBinding
import com.fallTurtle.myrestaurantgallery.ui.dialog.ProgressDialog
import com.fallTurtle.myrestaurantgallery.data.etc.IS_LOGIN
import com.fallTurtle.myrestaurantgallery.data.etc.LOGIN_CHECK_PREFERENCE
import com.fallTurtle.myrestaurantgallery.data.room.RestaurantInfo
import com.fallTurtle.myrestaurantgallery.ui.add.AddActivity
import com.fallTurtle.myrestaurantgallery.ui.login.LoginActivity
import com.fallTurtle.myrestaurantgallery.ui.view_model.UserViewModel
import com.fallTurtle.myrestaurantgallery.ui.view_model.ItemViewModel
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import dagger.hilt.android.AndroidEntryPoint

/**
 * 앱을 처음 실행했을 때 나타나는 메인 화면을 담당하는 액티비티.
 * 이 액티비티에서는 맛집 리스트의 현황을 볼 수 있고, 리스트 검색을 할 수 있다.
 * 그 밖에 추가 버튼을 눌러 맛집 추가 페이지로 이동하거나, 메뉴를 통해 회원 로그아웃 및 탈퇴를 할 수 있다.
 * 정리하자면 리스트를 보여줌과 동시에, 앱에서 가진 모든 다른 화면으로 이동할 수 있는 창구의 역할을 한다.
 **/
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    //뷰 바인딩
    private val binding:ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    //recyclerView 어댑터
    private val itemsAdapter by lazy { RestaurantAdapter(resources.displayMetrics.widthPixels) }

    //뷰모델 (식당 아이템, 유저)
    private val viewModelFactory by lazy{ ViewModelProvider.AndroidViewModelFactory(this.application) }
    private val itemViewModel:ItemViewModel by viewModels()
    //private val itemViewModel by lazy{ ViewModelProvider(this, viewModelFactory)[ItemViewModel::class.java] }
    private val userViewModel by lazy { ViewModelProvider(this, viewModelFactory)[UserViewModel::class.java] }

    //식당 아이템 observers (아이템 목록, 관련 작업 진행 여부, 관련 작업 종료 여부)
    private val itemsObserver = Observer<List<RestaurantInfo>> { itemsAdapter.update(it) }
    private val itemProgressObserver = Observer<Boolean> { itemProgress = it; decideShowLoading() }
    private val itemFinishObserver = Observer<Boolean> { itemFinish = it; tryActivityFinish() }

    //유저 아이템 observers (유저 작업 진행 여부, 작업 종료 여부)
    private val userProgressObserver = Observer<Boolean> { userProgress = it; decideShowLoading()}
    private val userFinishObserver = Observer<Boolean> { userFinish = it; tryActivityFinish() }

    //로딩 dialog
    private val progressDialog by lazy { ProgressDialog(this) }

    //공유 설정 (로그인 유지 여부)
    private val sharedPreferences by lazy{ getSharedPreferences(LOGIN_CHECK_PREFERENCE, MODE_PRIVATE) }

    // 유저와 아이템 부분의 business 작업의 상태 등을 판별할 property
    private var itemProgress = false
    private var itemFinish = false
    private var userProgress = false
    private var userFinish = false

    // 로그아웃 또는 탈퇴 시 출력할 적절한 메시지
    private var endToastMessage = R.string.logout_success


    //--------------------------------------------
    // 액티비티 생명주기 영역

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //로그인 여부 확인 (로그인 상태 아니면 로그인 activity 이동)
        if(sharedPreferences.getBoolean(IS_LOGIN, false)){
            //권한 허락을 위한 dialog
            showPermissionDialog()

            //클릭 리스너 지정
            initListeners()

            //viewModel 관찰하는 옵저버들 설정
            setObservers()
        }
        else{
            //앱과 유저 연결 필요 -> 로그인 activity 이동
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        //recyclerView 세팅 (GridLayout)
        binding.recyclerView.layoutManager = GridLayoutManager(this,2)
        binding.recyclerView.adapter = itemsAdapter

        //툴바와 메뉴 세팅
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onStart() {
        super.onStart()
        itemViewModel.getAllItems() //식당 아이템 목록 변경 사항 갱신
    }


    //--------------------------------------------
    // 오버라이딩 영역

    /* 툴바 메뉴 생성 콜백 */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.account_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /* 튤바 메뉴 옵션에 따른 행동 지정 콜백 */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            //logout 선택 시
            R.id.menu_logout -> {
                //logout 상태로 변경
                endToastMessage = R.string.logout_success
                sharedPreferences.edit().putBoolean(IS_LOGIN, false).apply()

                //logout, 로컬 데이터 초기화
                userViewModel.logoutUser()
                itemViewModel.clearAllLocalItems()
            }

            //탈퇴 선택 시
            R.id.menu_withdrawal -> {
                //탈퇴는 잘못 누르는 경우 치명적 -> 따라서 dialog 통해 재질문
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

    /* 데이터 변화 관찰을 위한 각 viewModel, observer 연결 함수 */
    private fun setObservers(){
        //각 뷰모델 속 작업 진행 여부 변화 관찰
        userViewModel.progressing.observe(this, userProgressObserver)
        itemViewModel.progressing.observe(this, itemProgressObserver)

        //각 뷰모델 속 작업 종료 여부 변화 관찰
        userViewModel.workFinishFlag.observe(this, userFinishObserver)
        itemViewModel.workFinishFlag.observe(this, itemFinishObserver)

        //아이템 뷰모델 속 데이터 리스트 변화 관찰
        itemViewModel.dataItems.observe(this, itemsObserver)
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
    // 내부 함수 영역 (옵저버 후속 작업)

    /* 유저와 아이템 작업 진행 여부에 따라 로딩 dialog 띄우는 함수 */
    private fun decideShowLoading(){
        if(userProgress && itemProgress) progressDialog.create()
        else progressDialog.destroy()
    }

    /* 유저와 아이템 작업 종료 시 화면을 끄는 함수 */
    private fun tryActivityFinish(){
        if(userFinish && itemFinish) {
            Toast.makeText(this, endToastMessage, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    /* 사용자 탈퇴 처리를 위한 함수 */
    private fun withdrawCurrentUser(){
        //탈퇴 관련 설정
        endToastMessage = R.string.withdrawal_success
        sharedPreferences.edit().putBoolean(IS_LOGIN, false).apply()

        //회원 탈퇴 및 로컬과 외부 데이터 모두 초기화
        itemViewModel.clearAllRemoteItems()
        itemViewModel.clearAllLocalItems()
        userViewModel.withdrawUser()
    }
}