package com.fallTurtle.myrestaurantgallery.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.adapter.ListAdapter
import com.fallTurtle.myrestaurantgallery.databinding.ActivityMainBinding
import com.fallTurtle.myrestaurantgallery.item.Piece
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
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
    private val mAuth = FirebaseAuth.getInstance()
    private val curID = mAuth.currentUser!!.email.toString()
    private var docRef: DocumentReference? = null
    private val db = Firebase.firestore
    private val str = Firebase.storage
    private val strRef = str.reference.child(curID)

    //view binding
    private val binding:ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    //related to recyclerView
    private val listAdapter = ListAdapter()
    private var list  = ArrayList<Piece>()


    //--------------------------------------------
    // 변수 영역
    //

    //back press time
    private var backPressTime:Long = 0


    //--------------------------------------------
    // 액티비티 생명주기 및 오버라이딩 영역
    //

    /* onCreate()에서는 뷰와 퍼미션 체크, 리사이클러뷰, 툴바, 이벤트 등의 기본적인 것들을 세팅한다. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //permission asking setting
        showPermissionDialog()

        //recyclerView setting
        binding.recyclerView.layoutManager = GridLayoutManager(this,2)
        binding.recyclerView.adapter = listAdapter

        //toolbar its menus setting
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        //listeners setting
        initListeners()
    }

    /* onResume()에서는 검색창을 초기화하고 화면에 보여줄 내용을 데이터베이스에서 가져온다. */
    override fun onResume(){
        super.onResume()
        binding.etSearch.setText("")
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
                FirebaseAuth.getInstance().signOut()
                val progress = Intent(this, ProgressActivity::class.java)
                progress.putExtra("endCode",2)
                startActivity(progress)
                finish()
            }

            //탈퇴 선택 시
            R.id.menu_withdrawal -> {
                AlertDialog.Builder(this)
                    .setMessage(R.string.withdrawal_ask)
                    .setPositiveButton(R.string.yes) {dialog, which ->
                        withdrawCurrentUser()
                    }
                    .setNegativeButton(R.string.no){dialog, which ->}
                    .show()
            }

            //아이템 추가 선택 시
            R.id.add_item ->{
                val addIntent = Intent(this@MainActivity, AddActivity::class.java)
                addIntent.putExtra("isEdit", false)
                startActivity(addIntent)
                overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /* onBackPressed()에서는 앱에서 나가기 위해서는 두 번 연속으로 누르게 하기 위한 조치를 취한다. */
    override fun onBackPressed() {
        //현재 시간을 구해서 timeGap 값을 계산한다.
        val curTime = System.currentTimeMillis()
        val timeGap = curTime - backPressTime

        if(timeGap in 0..2000) //2초 이내로 한 번 더 누르면 2번 연속 누른 것으로 판단 -> 앱 종료
            finish()
        else //2초 이내가 아니라면 두 번 연속으로 누르라는 메시지를 출력
            Toast.makeText(this, "'뒤로' 버튼을 한 번 더 누르시면 종료됩니다.",Toast.LENGTH_SHORT).show()

        //뒤로 가기 버튼을 누른 가장 최근 시간을 현재 시간으로 갱신한다.
        backPressTime = curTime
    }


    //--------------------------------------------
    // 내부 함수 영역
    //

    /* 화면 내 사용자 입력 관련 뷰들의 이벤트 리스너를 등록하는 함수 */
    private fun initListeners(){
        //검색 기능 textWatcher를 통해 구현
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                listAdapter.filter.filter(binding.etSearch.text)
            }
        })

        //검색 설정 초기화 버튼
        binding.ivReset.setOnClickListener {
            binding.etSearch.text.clear()
        }
    }

    /* 앱 실행 전 권한을 받기 위한 다이얼로그 (TedPermission 사용) */
    private fun showPermissionDialog() {
        //퍼미션 질문에 대한 응답 리스너
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() { }
            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                val deniedToast = Toast.makeText(this@MainActivity, "권한이 없으면 레시피 저장 기능 사용이 불가능합니다.", Toast.LENGTH_SHORT)
                deniedToast.show()
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
        for(item in list){
            //저장 정보에 이미지도 있다면 storage 내부 해당 이미지 제거
            if(item.getImgUsed()){
                strRef.child(item.getImage()!!).delete()
            }
            //각 item id를 통해 데이터베이스 내부 저장 정보 하나씩 제거
            db.collection("users")
                .document(curID)
                .collection("restaurants")
                .document(item.getDBID()!!).delete()
        }

        //현재 유저의 저장 데이터를 담기 위한 document(이메일로 구분) 자체를 제거
        db.collection("users")
            .document(curID).delete()

        //유저 자체를 파이어베이스 시스템 내부에서 삭제 (실패 시 error 토스트 메시지 출력)
        mAuth.currentUser!!.delete().addOnCompleteListener{ task->
            if(task.isSuccessful)
                FirebaseAuth.getInstance().signOut()
            else
                Toast.makeText(this,"오류가 발생했습니다.",Toast.LENGTH_SHORT).show()
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
        //현재 유저를 위한 저장 document 찾아서 레퍼런스 저장
        if (mAuth.currentUser != null){
            docRef = db.collection("users").document(curID)

            // document 레퍼런스에서 저장 내용들 가져와서 리스트에 업데이트
            docRef!!.collection("restaurants").addSnapshotListener { value, e ->
                //리스트를 초기화하고 새로 등록
                list.clear()
                value?.forEach{ list.add(it.toObject(Piece::class.java)) }

                //갱신한 리스트대로 어댑터 갱신
                listAdapter.update(list)
            }
        }
    }
}