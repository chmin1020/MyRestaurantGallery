package com.fallTurtle.myrestaurantgallery.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.adapter.ListAdapter
import com.fallTurtle.myrestaurantgallery.databinding.ActivityMainBinding
import com.fallTurtle.myrestaurantgallery.model.firebase.FirebaseHandler
import com.fallTurtle.myrestaurantgallery.model.firebase.Info
import com.google.firebase.auth.FirebaseAuth
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

    //Firebase
    private val docRef by lazy{ FirebaseHandler.getFirestoreRef() }
    private val strRef by lazy{ FirebaseHandler.getStorageRef() }
    private val fireUser by lazy{ FirebaseHandler.getUser() }

    //view binding
    private val binding:ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    //related to recyclerView
    private val itemsAdapter by lazy { ListAdapter(this.cacheDir) }
    private var items = ArrayList<Info>()


    //--------------------------------------------
    // 액티비티 생명주기 및 오버라이딩 영역
    //

    /* onCreate()에서는 뷰와 퍼미션 체크, 리사이클러뷰, 툴바, 이벤트 등의 기본적인 것들을 세팅한다. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //권한 허락을 위한 다이얼로그
        showPermissionDialog()

        //파이어베이스 레퍼런스 세팅
        FirebaseHandler.updateUserId()

        //리사이클러뷰 세팅 (GridLayout)
        binding.recyclerView.layoutManager = GridLayoutManager(this,2)
        binding.recyclerView.adapter = itemsAdapter

        //툴바와 메뉴 세팅
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    /* onResume()에서는 화면에 보여줄 (변경된) 내용을 데이터베이스에서 가져온다. */
    override fun onResume(){
        super.onResume()
        updateDB()
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
                FirebaseHandler.logout()
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
        //파이어스토어에 담겨 있는 유저 관련 DB 파일 전부 삭제
        for(item in items){
            item.image?.let{ strRef.child(it).delete() } //이미지
            docRef.collection("restaurants").document(item.dbID).delete() //아이템 데이터
        }

        //현재 유저의 저장 데이터를 담기 위한 document(이메일로 구분) 자체를 제거
        docRef.delete()
        strRef.delete()

        //유저 자체를 파이어베이스 시스템 내부에서 삭제 (실패 시 error 토스트 메시지 출력)
        fireUser?.delete()?.addOnCompleteListener{ task->
            if(task.isSuccessful) FirebaseAuth.getInstance().signOut()
            else Toast.makeText(this,"오류가 발생했습니다.",Toast.LENGTH_SHORT).show()
        }

        //탈퇴 처리 시간동안 사용자에게 대기 화면을 보여주기 위해 intent 로 progressActivity 실행 요청
        val progress = Intent(this, ProgressActivity::class.java)
        progress.putExtra("endCode",3)
        startActivity(progress)

        //현재 액티비티(메인 화면)은 종료
        finish()
    }

    /* 파이어베이스에서 현재 유저를 위한 DB 데이터를 가져와서 화면에 갱신하는 함수 */
    private fun updateDB() {
        // document 레퍼런스 내부를 리스트로 업데이트
        docRef.collection("restaurants").addSnapshotListener { value, _ ->
            //리스트를 초기화하고 새로 등록
            items.clear()
            value?.forEach{ items.add(it.toObject(Info::class.java)) }

            //갱신한 리스트대로 어댑터 갱신
            itemsAdapter.update(items)
        }
    }
}
