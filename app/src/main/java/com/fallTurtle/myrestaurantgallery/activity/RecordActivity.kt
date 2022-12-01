package com.fallTurtle.myrestaurantgallery.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.fallTurtle.myrestaurantgallery.etc.GlideApp
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.databinding.ActivityRecordBinding
import com.fallTurtle.myrestaurantgallery.model.firebase.Info
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

/**
 * 저장된 데이터를 확인할 때 사용하는 액티비티.
 * 인텐트를 통해서 시작이 되며, 여기서 extra 데이터로 받은 것들을 각 뷰에 적용해서 보여준다.
 * 메뉴를 통해 아이템을 삭제하거나 수정을 위한 AddActivity 제공 화면으로 이동할 수 있다.
 **/
class RecordActivity : AppCompatActivity() {
    //--------------------------------------------
    // 인스턴스 영역
    //

    //view binding
    private val binding : ActivityRecordBinding by lazy { ActivityRecordBinding.inflate(layoutInflater) }

    //for saving edit information
    private lateinit var info:Info

    //firebase
    private val db = Firebase.firestore
    private val docRef = db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.email.toString())
    private val str = Firebase.storage
    private val strRef = str.reference.child(FirebaseAuth.getInstance().currentUser!!.email.toString())


    //--------------------------------------------
    // 액티비티 생명주기 및 오버라이딩 영역
    //

    /* onCreate()에서는 툴바를 설정하고 뷰에 내용을 세팅한다. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //툴바 설정
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        //받아온 데이터 각 뷰에 적용시켜서 화면 완성
        getSavedInfo()
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
    private fun getSavedInfo(){
        //adapter 데이터 받기
        info = intent.getSerializableExtra("info") as Info

        //받은 데이터를 적절한 뷰에 적용
        binding.tvName.text = info.name
        binding.tvGenre.text = info.genre
        binding.tvLocation.text = info.location
        binding.tvMemo.text = info.memo
        binding.rbRatingBar.rating = info.rate.toFloat()
        binding.tvDate.text = info.date

        //이미지 적용
        if(info.imgUsed) { //이미지 사용 시 Glide 기능으로 해당 이미지 로딩
            GlideApp.with(this)
                .load(strRef.child(info.image.toString())).into(binding.ivImage)
        }
        else { //이미지 미사용 시 기본 그림 이미지를 정보에 맞게 적용
            when (info.genreNum) {
                0 -> binding.ivImage.setImageResource(R.drawable.korean_food)
                1 -> binding.ivImage.setImageResource(R.drawable.chinese_food)
                2 -> binding.ivImage.setImageResource(R.drawable.japanese_food)
                3 -> binding.ivImage.setImageResource(R.drawable.western_food)
                4 -> binding.ivImage.setImageResource(R.drawable.coffee_and_drink)
                5 -> binding.ivImage.setImageResource(R.drawable.drink)
                6 -> binding.ivImage.setImageResource(R.drawable.etc)
            }
        }
    }

    /* 이전 액티비티에서 받은 정보를 가지고 와서 뷰에 적용하는 함수 */
    private fun makeDeleteDialog(){
        AlertDialog.Builder(this)
            .setMessage(R.string.delete_message)
            .setPositiveButton(R.string.yes) {dialog, which ->
                //삭제를 원하면 reference 내에서 해당 이미지 삭제
                if(info.imgUsed){
                    strRef.child(info.image.toString()).delete()
                }
                docRef.collection("restaurants").document(info.dbID.toString()).delete()
                Toast.makeText(this, R.string.delete_complete, Toast.LENGTH_SHORT).show()
                finish()
            }
            .setNegativeButton(R.string.no) {dialog, which -> }
            .show()
    }

    /* 현재 가진 데이터를 모두 담아서 수정을 위해 AddActivity 화면으로 이동하는 함수  */
    private fun moveToEditActivity(){
        //intent 만들고 데이터 모두 extra 내에 담기
        val edit = Intent(this, AddActivity::class.java)
        edit.putExtra("info", info)
        edit.putExtra("isEdit", true)

        //intent 통해서 AddActivity 요청
        startActivity(edit)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out) //페이드 효과와 함께 화면 전환

        //현재 액티비티는 종료
        finish()
    }
}