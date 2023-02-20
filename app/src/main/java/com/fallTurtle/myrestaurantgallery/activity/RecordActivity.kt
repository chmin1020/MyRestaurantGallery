package com.fallTurtle.myrestaurantgallery.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.databinding.ActivityRecordBinding
import com.fallTurtle.myrestaurantgallery.dialog.ProgressDialog
import com.fallTurtle.myrestaurantgallery.etc.*
import com.fallTurtle.myrestaurantgallery.model.room.RestaurantInfo
import com.fallTurtle.myrestaurantgallery.view_model.ItemViewModel

/**
 * 저장된 데이터를 확인할 때 사용하는 액티비티.
 * 인텐트를 통해서 시작이 되며, 여기서 extra 데이터로 받은 것들을 각 뷰에 적용해서 보여준다.
 * 메뉴를 통해 아이템을 삭제하거나 수정을 위한 AddActivity 제공 화면으로 이동할 수 있다.
 **/
class RecordActivity : AppCompatActivity() {
    //데이터 바인딩
    private val binding: ActivityRecordBinding by lazy { DataBindingUtil.setContentView(this, R.layout.activity_record)}

    //선택된 아이템 id
    private var itemId: String? = null

    //뷰모델
    private val viewModelFactory by lazy{ ViewModelProvider.AndroidViewModelFactory(this.application) }
    private val itemViewModel by lazy { ViewModelProvider(this, viewModelFactory)[ItemViewModel::class.java] }

    //옵저버들
    private val progressObserver = Observer<Boolean> { decideShowLoading(it)}
    private val finishObserver = Observer<Boolean> { if(it) deleteWorkComplete() }
    private val itemObserver = Observer<RestaurantInfo> { binding.info = it }

    //로딩 다이얼로그
    private val progressDialog by lazy { ProgressDialog(this) }


    //--------------------------------------------
    // 액티비티 생명주기 영역

    /* onCreate()에서는 툴바를 설정하고 뷰에 내용을 세팅한다. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //툴바 설정
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        //인텐트로 선택된 데이터 db 아이디 가져와서 뷰모델에 적용 (실패 시 화면 종료)
        itemId = intent.getStringExtra(ITEM_ID)

        initListener() //리스너 지정
        setObservers() //옵저버 설정
    }

    /* onStart()마다 뷰모델에게 적절한 아이템 정보 갱신 요구 */
    override fun onStart() {
        super.onStart()
        itemId?.let { itemViewModel.setProperItem(it) }
            ?: run { Toast.makeText(this, "오류 발생", Toast.LENGTH_SHORT).show(); finish() }
    }


    //--------------------------------------------
    // 오버라이딩 영역

    /* onOptionsItemSelected()에서는 툴바의 각 아이템 선택 시 수행할 행동을 정의한다. */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> supportFinishAfterTransition() //뒤로 가기
            R.id.delete_item -> makeDeleteDialog() //삭제
            R.id.edit_item -> moveToEditActivity() //수정
        }
        return super.onOptionsItemSelected(item)
    }

    /* onCreateOptionsMenu()에서는 툴바의 추가 메뉴를 세팅한다.(수정, 삭제) */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.record_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //--------------------------------------------
    // 내부 함수 영역 (초기화)

    /* 뷰 클릭 리스너를 지정하는 함수 */
    private fun initListener(){
        binding.ivMap.setOnClickListener{ moveToMapDisplay() }
    }

    /* 데이터 변화 관찰을 위한 각 뷰모델과 옵저버 연결 함수 */
    private fun setObservers(){
        itemViewModel.progressing.observe(this, progressObserver)
        itemViewModel.workFinishFlag.observe(this, finishObserver)
        itemViewModel.selectedItem.observe(this, itemObserver)
    }


    //--------------------------------------------
    // 내부 함수 영역 (툴바 리스너 반응)

    /* 삭제 버튼 클릭시 재확인 다이얼로그를 만드는 함수 */
    private fun makeDeleteDialog(){
        AlertDialog.Builder(this)
            .setMessage(R.string.delete_message)
            .setPositiveButton(R.string.yes) { _,_ -> deleteCurrentItem() }
            .setNegativeButton(R.string.no) { _,_ -> }
            .show()
    }

    /* 삭제를 원하는 것이 확실할 시 해당 이미지 삭제 작업을 진행하는 함수 */
    private fun deleteCurrentItem(){
        itemId?.let{ itemViewModel.deleteItem(it) }
    }

    /* 위치 설정이 된 경우 지도로 이를 확인시켜주는 함수 */
    private fun moveToMapDisplay(){
        if(binding.info?.latitude == DEFAULT_LOCATION || binding.info?.longitude == DEFAULT_LOCATION )
            Toast.makeText(this, "지도 위치 설정이 되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
        else{
            Intent(this, MapActivity::class.java).let{
                it.putExtra(FOR_CHECK, true)
                it.putExtra(RESTAURANT_NAME, binding.info?.name)
                it.putExtra(LATITUDE, binding.info?.latitude)
                it.putExtra(LONGITUDE, binding.info?.longitude)
                startActivity(it)
            }
        }
    }

    /* 수정 버튼 클릭시 id 정보를 가지고 AddActivity 화면으로 이동하는 함수  */
    private fun moveToEditActivity(){
        Intent(this, AddActivity::class.java).also{
            it.putExtra(ITEM_ID, itemId)
            startActivity(it)
            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out)
        }
    }


    //--------------------------------------------
    // 내부 함수 영역 (옵저버 후속 작업)

    /* 유저와 아이템 작업 진행 여부에 따라 로딩 다이얼로그를 띄우는 함수 */
    private fun decideShowLoading(yes: Boolean){
        if(yes) progressDialog.create()
        else progressDialog.destroy()
    }

    /* 삭제 작업이 끝난 것을 확인 후 현재 화면을 종료하는 함수 */
    private fun deleteWorkComplete(){
        Toast.makeText(this, R.string.delete_complete, Toast.LENGTH_SHORT).show()
        finish()
    }
}