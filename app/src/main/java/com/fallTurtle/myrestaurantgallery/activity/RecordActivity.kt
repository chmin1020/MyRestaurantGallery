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
import com.fallTurtle.myrestaurantgallery.model.room.Info
import com.fallTurtle.myrestaurantgallery.view_model.ItemViewModel

/**
 * 저장된 데이터를 확인할 때 사용하는 액티비티.
 * 인텐트를 통해서 시작이 되며, 여기서 extra 데이터로 받은 것들을 각 뷰에 적용해서 보여준다.
 * 메뉴를 통해 아이템을 삭제하거나 수정을 위한 AddActivity 제공 화면으로 이동할 수 있다.
 **/
class RecordActivity : AppCompatActivity() {
    //뷰 바인딩
    private val binding: ActivityRecordBinding by lazy { DataBindingUtil.setContentView(this, R.layout.activity_record)}

    //선택된 아이템 id
    private var itemId: String? = null

    //뷰모델
    private val viewModelFactory by lazy{ ViewModelProvider.AndroidViewModelFactory(this.application) }
    private val itemViewModel by lazy { ViewModelProvider(this, viewModelFactory)[ItemViewModel::class.java] }

    //옵저버들
    private val progressObserver = Observer<Boolean> { decideShowLoading(it)}
    private val finishObserver = Observer<Boolean> { if(it) finish() }
    private val itemObserver = Observer<Info> { binding.info = it }

    //로딩 다이얼로그
    private val progressDialog by lazy { ProgressDialog(this) }


    //--------------------------------------------
    // 액티비티 생명주기 및 오버라이딩 영역
    //

    /* onCreate()에서는 툴바를 설정하고 뷰에 내용을 세팅한다. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //툴바 설정
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        //인텐트로 선택된 데이터 db 아이디 가져와서 뷰모델에 적용 (실패 시 화면 종료)
        itemId = intent.getStringExtra("item_id")
        itemId?.let { itemViewModel.setProperItem(it) }
            ?: run { Toast.makeText(this, "오류 발생", Toast.LENGTH_SHORT).show(); finish() }

        //옵저버 설정
        itemViewModel.progressing.observe(this, progressObserver)
        itemViewModel.workFinishFlag.observe(this, finishObserver)
        itemViewModel.selectedItem.observe(this, itemObserver)
    }

    /* onOptionsItemSelected()에서는 툴바의 각 아이템 선택 시 수행할 행동을 정의한다. */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> finish() //뒤로 가기
            R.id.delete_item -> makeDeleteDialog() //삭제
            R.id.edit_item -> moveToEditActivity() //수정
        }
        return super.onOptionsItemSelected(item)
    }

    /* onCreateOptionsMenu()에서는 툴바의 추가 메뉴를 세팅한다.(수정, 삭제) */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.record_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    //--------------------------------------------
    // 내부 함수 영역
    //

    /* 이전 액티비티에서 받은 정보를 가지고 와서 뷰에 적용하는 함수 */
    private fun makeDeleteDialog(){
        AlertDialog.Builder(this)
            .setMessage(R.string.delete_message)
            .setPositiveButton(R.string.yes) {_,_ ->
                //삭제를 원하면 reference 내에서 해당 이미지 삭제
                itemId?.let{ itemViewModel.deleteItem(it) }
                Toast.makeText(this, R.string.delete_complete, Toast.LENGTH_SHORT).show()
                finish()
            }
            .setNegativeButton(R.string.no) {_,_ -> }
            .show()
    }

    /* 현재 가진 데이터를 모두 담아서 수정을 위해 AddActivity 화면으로 이동하는 함수  */
    private fun moveToEditActivity(){
        //intent 만들고 데이터 모두 extra 내에 담기
        val edit = Intent(this, AddActivity::class.java)
        edit.putExtra("isEdit", true)

        //intent 통해서 AddActivity 요청
        startActivity(edit)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out) //페이드 효과와 함께 화면 전환

        //현재 액티비티는 종료
        finish()
    }

    /* 유저와 아이템 작업 진행 여부에 따라 로딩 다이얼로그를 띄우는 함수 */
    private fun decideShowLoading(yes: Boolean){
        if(yes) progressDialog.show()
        else progressDialog.close()
    }
}