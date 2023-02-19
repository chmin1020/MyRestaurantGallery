package com.fallTurtle.myrestaurantgallery.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.adapter.ItemAdapter
import com.fallTurtle.myrestaurantgallery.databinding.ActivityMainBinding
import com.fallTurtle.myrestaurantgallery.dialog.ProgressDialog
import com.fallTurtle.myrestaurantgallery.etc.IS_LOGIN
import com.fallTurtle.myrestaurantgallery.etc.LOGIN_CHECK_PREFERENCE
import com.fallTurtle.myrestaurantgallery.model.room.RestaurantInfo
import com.fallTurtle.myrestaurantgallery.view_model.UserViewModel
import com.fallTurtle.myrestaurantgallery.view_model.ItemViewModel
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission

/**
 * 앱을 처음 실행했을 때 나타나는 메인 화면을 담당하는 액티비티.
 * 이 액티비티에서는 맛집 리스트의 현황을 볼 수 있고, 리스트 검색을 할 수 있다.
 * 그 밖에 추가 버튼을 눌러 맛집 추가 페이지로 이동하거나, 메뉴를 통해 회원 로그아웃 및 탈퇴를 할 수 있다.
 * 정리하자면 리스트를 보여줌과 동시에, 앱에서 가진 모든 다른 화면으로 이동할 수 있는 창구의 역할을 한다.
 **/
class MainActivity : AppCompatActivity() {
    //뷰 바인딩
    private val binding:ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    //리사이클러뷰 어댑터
    private val itemsAdapter by lazy { ItemAdapter(resources.displayMetrics.widthPixels )}

    //뷰모델
    private val viewModelFactory by lazy{ ViewModelProvider.AndroidViewModelFactory(this.application) }
    private val itemViewModel by lazy{ ViewModelProvider(this, viewModelFactory)[ItemViewModel::class.java] }
    private val userViewModel by lazy { ViewModelProvider(this, viewModelFactory)[UserViewModel::class.java] }

    //옵저버들
    private val itemsObserver = Observer<List<RestaurantInfo>> { itemsAdapter.update(it) }
    private val itemProgressObserver = Observer<Boolean> { itemProgress = it; decideShowLoading() }
    private val itemFinishObserver = Observer<Boolean> { itemFinish = it; tryActivityFinish() }
    private val userProgressObserver = Observer<Boolean> { userProgress = it; decideShowLoading()}
    private val userFinishObserver = Observer<Boolean> { userFinish = it; tryActivityFinish() }

    //로딩 다이얼로그
    private val progressDialog by lazy { ProgressDialog(this) }

    //공유 설정 (로그인 유지 여부)
    private val sharedPreferences by lazy{ getSharedPreferences(LOGIN_CHECK_PREFERENCE, MODE_PRIVATE) }

    // 유저와 아이템 부분의 비즈니스 작업의 상태 등을 판별할 프로퍼티
    private var userProgress = false
    private var itemProgress = false
    private var userFinish = false
    private var itemFinish = false

    // 로그아웃 또는 탈퇴 시 적절한 메시지
    private var endToastMessage = R.string.logout_success


    //--------------------------------------------
    // 액티비티 생명주기 영역

    /* onCreate()에서는 뷰와 퍼미션 체크, 리사이클러뷰, 툴바, 이벤트 등의 기본적인 것들을 세팅한다. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //로그인 여부 확인 (로그인 상태 아니면 로그인 액티비티 실행)
        if(!sharedPreferences.getBoolean(IS_LOGIN, false)){
            //유저 연결해야함 -> 로그인 액티비티로 이동
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        else{
            //권한 허락을 위한 다이얼로그
            showPermissionDialog()

            //viewModel 관찰하는 옵저버들 설정
            setObservers()
        }

        //리사이클러뷰 세팅 (GridLayout)
        binding.recyclerView.layoutManager = GridLayoutManager(this,2)
        binding.recyclerView.adapter = itemsAdapter

        //툴바와 메뉴 세팅
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    /* onStart()가 실행될 때마다 아이템 리스트 갱신을 새롭게 해준다. */
    override fun onStart() {
        super.onStart()
        itemViewModel.getAllItems()
    }


    //--------------------------------------------
    // 오버라이딩 영역

    /* onCreateOptionsMenu()에서는 툴바에서 나타날 메뉴를 만든다. */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.account_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /* onOptionsItemSelected()에서는 툴바에서 선택한 옵션에 따라 나타날 이벤트를 정의한다. */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            //로그아웃 선택 시
            R.id.menu_logout -> {
                endToastMessage = R.string.logout_success
                sharedPreferences.edit().putBoolean(IS_LOGIN, false).apply()
                userViewModel.logoutUser()
                itemViewModel.clearAllLocalItems()
            }

            //탈퇴 선택 시
            R.id.menu_withdrawal -> {
                AlertDialog.Builder(this)
                    .setMessage(R.string.withdrawal_ask)
                    .setPositiveButton(R.string.yes) {_,_ -> withdrawCurrentUser() }
                    .setNegativeButton(R.string.no){_,_ -> }
                    .show()
            }

            //아이템 추가 선택 시
            R.id.add_item ->{
                val addIntent = Intent(this@MainActivity, AddActivity::class.java)
                startActivity(addIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    //--------------------------------------------
    // 내부 함수 영역 (초기화)

    /* 데이터 변화 관찰을 위한 각 뷰모델과 옵저버 연결 함수 */
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

    /* 앱 실행 전 권한을 받기 위한 다이얼로그 (TedPermission 사용) */
    private fun showPermissionDialog() {
        //권한 허가 질문에 대한 응답 리스너 생성
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() { }
            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(this@MainActivity, R.string.permission_ask, Toast.LENGTH_SHORT).show()
                finishAffinity()
            }
        }

        //TedPermission 객체에 체크할 퍼미션들과 퍼미션 리스너 등록
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

    /* 유저와 아이템 작업 진행 여부에 따라 로딩 다이얼로그를 띄우는 함수 */
    private fun decideShowLoading(){
        if(userProgress && itemProgress) progressDialog.create()
        else progressDialog.destroy()
    }

    /* 유저와 아이템 작업 종료 시 액티비티를 종료하는 함수 */
    private fun tryActivityFinish(){
        if(userFinish && itemFinish) {
            Toast.makeText(this, endToastMessage, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    /* 사용자의 탈퇴 처리를 위한 함수 */
    private fun withdrawCurrentUser(){
        endToastMessage = R.string.withdrawal_success
        sharedPreferences.edit().putBoolean(IS_LOGIN, false).apply()
        itemViewModel.clearAllRemoteItems()
        itemViewModel.clearAllLocalItems()
        userViewModel.withdrawUser()
    }
}