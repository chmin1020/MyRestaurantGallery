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
import com.fallTurtle.myrestaurantgallery.adapter.ListAdapter
import com.fallTurtle.myrestaurantgallery.databinding.ActivityMainBinding
import com.fallTurtle.myrestaurantgallery.model.room.Info
import com.fallTurtle.myrestaurantgallery.view_model.FirebaseUserViewModel
import com.fallTurtle.myrestaurantgallery.view_model.DataViewModel
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission

/**
 * 앱을 처음 실행했을 때 나타나는 메인 화면을 담당하는 액티비티.
 * 이 액티비티에서는 맛집 리스트의 현황을 볼 수 있고, 리스트 검색을 할 수 있다.
 * 그 밖에 추가 버튼을 눌러 맛집 추가 페이지로 이동하거나, 메뉴를 통해 회원 로그아웃 및 탈퇴를 할 수 있다.
 * 정리하자면 리스트를 보여줌과 동시에, 앱에서 가진 모든 다른 화면으로 이동할 수 있는 창구의 역할을 한다.
 **/
class MainActivity : AppCompatActivity() {
    //--------------------------------------------
    // 인스턴스 영역
    //

    //view binding
    private val binding:ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    //related to recyclerView
    private val itemsAdapter by lazy { ListAdapter(this.cacheDir, this) }

    //뷰모델
    private val viewModelFactory by lazy{ ViewModelProvider.AndroidViewModelFactory(this.application) }
    private val dataViewModel by lazy{ ViewModelProvider(this, viewModelFactory)[DataViewModel::class.java] }
    private val userViewModel by lazy { ViewModelProvider(this, viewModelFactory)[FirebaseUserViewModel::class.java] }

    //공유 설정 (로그인 유지 여부)
    private val sharedPreferences by lazy{ getSharedPreferences("loginCheck", MODE_PRIVATE) }

    //--------------------------------------------
    // 액티비티 생명주기 및 오버라이딩 영역
    //

    /* onCreate()에서는 뷰와 퍼미션 체크, 리사이클러뷰, 툴바, 이벤트 등의 기본적인 것들을 세팅한다. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //권한 허락을 위한 다이얼로그
        showPermissionDialog()

        //새로 로그인 -> 데이터 복구 필요함
        if(intent.getBooleanExtra("newLogin", true))
            dataViewModel.restoreItemsFromAccount()

        //LiveData, observer 기능을 통해 실시간 검색 결과 변화 감지 및 출력
        val listObserver = Observer<List<Info>> { itemsAdapter.update(it) }
        dataViewModel.dataItems.observe(this, listObserver)

        //리사이클러뷰 세팅 (GridLayout)
        binding.recyclerView.layoutManager = GridLayoutManager(this,2)
        binding.recyclerView.adapter = itemsAdapter

        //툴바와 메뉴 세팅
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    /* onCreateOptionsMenu()에서는 툴바에서 나타날 메뉴를 만든다. */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.account_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /* onOptionsItemSelected()에서는 툴바에서 선택한 옵션에 따라 나타날 이벤트를 정의한다. */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            //로그아웃 선택 시
            R.id.menu_logout -> {
                dataViewModel.clearAllItems()
                userViewModel.logoutUser().also { sharedPreferences.edit().putBoolean("isLogin", false).apply() }

                val progress = Intent(this, ProgressActivity::class.java)
                progress.putExtra("endCode",2)
                startActivity(progress)
                finish()
            }

            //탈퇴 선택 시
            R.id.menu_withdrawal -> {
                AlertDialog.Builder(this)
                    .setMessage(R.string.withdrawal_ask)
                    .setPositiveButton(R.string.yes) {_,_ -> withdrawCurrentUser() }
                    .setNegativeButton(R.string.no){_,_ ->}
                    .show()
            }

            //아이템 추가 선택 시
            R.id.add_item ->{
                val addIntent = Intent(this@MainActivity, AddActivity::class.java)
                addIntent.putExtra("isEdit", false)
                startActivity(addIntent)

                overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out) //전환 효과 (슬라이딩)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    //--------------------------------------------
    // 내부 함수 영역
    //

    /* 앱 실행 전 권한을 받기 위한 다이얼로그 (TedPermission 사용) */
    private fun showPermissionDialog() {
        //권한 허가 질문에 대한 응답 리스너
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() { }
            //권한 획득 거부 시 --> 앱 사용 불가능
            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(this@MainActivity, "권한을 허용해주세요", Toast.LENGTH_SHORT).show()
                finishAffinity()
            }
        }

        //TedPermission 객체에 체크할 퍼미션들과 퍼미션 리스너 등록
        TedPermission.with(this)
            .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            .setPermissionListener(permissionListener)
            .check()
    }

    /* 사용자의 탈퇴 처리를 위한 함수 */
    private fun withdrawCurrentUser(){
        dataViewModel.clearAllItems()
        userViewModel.withdrawUser(dataViewModel.dataItems.value)
            .also { sharedPreferences.edit().putBoolean("isLogin", false).apply() }

        //탈퇴 처리 시간동안 사용자에게 대기 화면을 보여주기 위해 intent 로 progressActivity 실행 요청
        val progress = Intent(this, ProgressActivity::class.java)
        progress.putExtra("endCode",3)
        startActivity(progress)

        //현재 액티비티(메인 화면)은 종료
        finish()
    }
}
