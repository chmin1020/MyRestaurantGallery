package com.fallTurtle.myrestaurantgallery.activity

import android.content.Intent
import android.os.Bundle
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

    //observers
    private val progressObserver = Observer<Boolean> { decideShowLoading(it)}
    private val finishObserver = Observer<Boolean> { if(it) deleteWorkComplete() }
    private val itemObserver = Observer<RestaurantInfo> { binding.info = it }

    //로딩 dialog
    private val progressDialog by lazy { ProgressDialog(this) }


    //--------------------------------------------
    // 액티비티 생명주기 영역

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //툴바 설정
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        //선택된 데이터 db 아이디, intent 통해 가져 와서 viewModel 적용
        itemId = intent.getStringExtra(ITEM_ID)

        initListeners() //리스너 지정
        setObservers() //옵저버 설정
    }

    override fun onStart() {
        super.onStart()

        //아이디 없음 -> 화면 종료
        itemId?.let { itemViewModel.setProperItem(it) }
            ?: run { Toast.makeText(this, "오류 발생", Toast.LENGTH_SHORT).show(); finish() }
    }


    //--------------------------------------------
    // 오버라이딩 영역

    /* 툴바 메뉴 생성 콜백 */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> supportFinishAfterTransition() //뒤로 가기
            R.id.delete_item -> makeDeleteDialog() //삭제
            R.id.edit_item -> moveToEditActivity() //수정
        }
        return super.onOptionsItemSelected(item)
    }

    /* 튤바 메뉴 옵션에 따른 행동 지정 콜백 */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.record_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //--------------------------------------------
    // 내부 함수 영역 (초기화)

    /* 뷰 클릭 listener 지정 함수 */
    private fun initListeners(){
        //맵 버튼 클릭 시
        binding.ivMap.setOnClickListener{ moveToMapDisplay() }
    }

    /* 데이터 변화 관찰을 위한 각 뷰모델, 옵저버 연결 함수 */
    private fun setObservers(){
        itemViewModel.progressing.observe(this, progressObserver)
        itemViewModel.workFinishFlag.observe(this, finishObserver)
        itemViewModel.selectedItem.observe(this, itemObserver)
    }


    //--------------------------------------------
    // 내부 함수 영역 (툴바 리스너 반응)

    /* 삭제 버튼 클릭시 재확인 dialog 만드는 함수 */
    private fun makeDeleteDialog(){
        AlertDialog.Builder(this)
            .setMessage(R.string.delete_message)
            .setPositiveButton(R.string.yes) { _,_ -> deleteCurrentItem() }
            .setNegativeButton(R.string.no) { _,_ -> }
            .show()
    }

    /* 삭제를 원하는 것이 확실할 시 해당 이미지 삭제 작업 진행 함수 */
    private fun deleteCurrentItem(){
        itemId?.let{ itemViewModel.deleteItem(it) }
    }

    /* 위치 설정이 된 경우 지도 이동을 하는 함수 */
    private fun moveToMapDisplay(){
        //위치 좌표 값 설정이 되어 있지 않음
        if(binding.info?.latitude == DEFAULT_LOCATION || binding.info?.longitude == DEFAULT_LOCATION )
            Toast.makeText(this, R.string.no_location_selection, Toast.LENGTH_SHORT).show()
        else{
            //설정이 되어 있음 (설정 값 가지고 맵으로 이동)
            Intent(this, MapActivity::class.java).let{
                it.putExtra(FOR_CHECK, true)
                it.putExtra(RESTAURANT_NAME, binding.info?.name)
                it.putExtra(LATITUDE, binding.info?.latitude)
                it.putExtra(LONGITUDE, binding.info?.longitude)
                startActivity(it)
            }
        }
    }

    /* 수정 버튼 클릭시 id 정보를 가지고 AddActivity 이동을 하는 함수  */
    private fun moveToEditActivity(){
        Intent(this, AddActivity::class.java).also{
            it.putExtra(ITEM_ID, itemId)
            startActivity(it)
            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out)
        }
    }


    //--------------------------------------------
    // 내부 함수 영역 (옵저버 후속 작업)

    /* 유저와 아이템 작업 진행 여부에 따라 로딩 dialog 띄우는 함수 */
    private fun decideShowLoading(yes: Boolean){
        if(yes) progressDialog.create()
        else progressDialog.destroy()
    }

    /* 삭제 작업 이후 현재 화면 종료 함수 */
    private fun deleteWorkComplete(){
        Toast.makeText(this, R.string.delete_complete, Toast.LENGTH_SHORT).show()
        finish()
    }
}