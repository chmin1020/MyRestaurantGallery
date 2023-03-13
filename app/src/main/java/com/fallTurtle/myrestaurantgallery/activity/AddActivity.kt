package com.fallTurtle.myrestaurantgallery.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.databinding.ActivityAddBinding
import com.fallTurtle.myrestaurantgallery.dialog.ImgDialog
import com.fallTurtle.myrestaurantgallery.dialog.ProgressDialog
import com.fallTurtle.myrestaurantgallery.etc.*
import com.fallTurtle.myrestaurantgallery.model.room.RestaurantInfo
import com.fallTurtle.myrestaurantgallery.view_model.ItemViewModel
import java.util.Calendar


/**
 * 항목 추가 화면 담당을 하는 activity.
 * 새로운 맛집 정보 저장을 하는 작업과 기존 내용을 수정을 하는 작업을 하게 해준다.
 * 더하여 여기서 지역 주소 입력을 위해 Map 화면으로도 이동할 수 있다.
 **/
class AddActivity : AppCompatActivity(){
    //데이터 바인딩
    private val binding:ActivityAddBinding by lazy { DataBindingUtil.setContentView(this, R.layout.activity_add) }

    //뷰모델
    private val viewModelFactory by lazy{ ViewModelProvider.AndroidViewModelFactory(this.application) }
    private val itemViewModel by lazy { ViewModelProvider(this, viewModelFactory)[ItemViewModel::class.java] }

    //옵저버(진행 과정 여부, 종료 여부, 선택된 아이템)
    private val progressObserver = Observer<Boolean> { if(it) progressDialog.create() else progressDialog.destroy() }
    private val finishObserver = Observer<Boolean> { if(it) workCompleteFinish() }
    private val selectedItemObserver = Observer<RestaurantInfo> { setContentsWithItem(it) }

    //로딩 dialog
    private val progressDialog by lazy { ProgressDialog(this) }

    //선택된 아이템 관련 변수들
    private var preImgName: String? = null

    //이미지 이름, 기기 내 이미지 실제 uri
    private var curImgPath: String? = null
    private var imgUri: Uri? = null


    //--------------------------------------------
    // 액티비티 결과 런처

    /* 갤러리에서 가지고 온 이미지 결과를 처리하는 런처 */
    private val getImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        imgUri = it.data?.data?.also{ uri->
            //현재 시간을 통한 고유 이미지 이름 생성(중복 방지)
            binding.info?.imageName = uri.lastPathSegment.toString() + System.currentTimeMillis().toString()

            //사진 설정
            binding.ivImage.setImageURI(uri)
        }
    }

    /* 위치 검색에서 선택하여 가져온 위치 결과를 처리하는 런처 */
    private val getLocationLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.data?.getBooleanExtra(IS_CHANGED, false) == true) {
            //위도, 경도 적용
            binding.info?.latitude = it.data?.getDoubleExtra(LATITUDE, DEFAULT_LOCATION) ?: DEFAULT_LOCATION
            binding.info?.longitude = it.data?.getDoubleExtra(LONGITUDE, DEFAULT_LOCATION) ?: DEFAULT_LOCATION

            //식당 이름 적용
            it.data?.getStringExtra(RESTAURANT_NAME)?.let {name->
                binding.info?.name = name
                binding.etName.setText(name)
            }
        }
    }


    //--------------------------------------------
    // 액티비티 생명주기 영역

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //데이터 디폴트 설정
        binding.info = RestaurantInfo()
        binding.spinnerEntries = resources.getStringArray(R.array.category_spinner)

        //인텐트로 선택된 데이터 db 아이디 뷰모델에 적용
        intent.getStringExtra(ITEM_ID)?.let { itemViewModel.setProperItem(it) }

        //toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        //초기 설정
        initListeners()
        setObservers()
    }


    //--------------------------------------------
    // 오버라이딩 영역

    /* 툴바 메뉴 생성 콜백 */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.add_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /* 튤바 메뉴 옵션에 따른 행동 지정 콜백 */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.save_item -> saveCurrentItemProcess() //저장
            android.R.id.home -> finish() //취소, 종료
        }
        return super.onOptionsItemSelected(item)
    }

    /* 화면 종료 정의(애니메이션 효과) */
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out)
    }


    //--------------------------------------------
    // 내부 함수 영역 (초기화)

    /* 화면 내 사용자 입력 관련 뷰들의 이벤트 listener 등록 함수 */
    private fun initListeners(){
        //spinner 아이템 목록 설정
        binding.spCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                binding.info?.run {
                    //바인딩 데이터 갱신
                    category = binding.spCategory.selectedItem.toString()
                    categoryNum = position

                    //선택 이미지 없으면 기본 그림 이미지 설정
                    imageName ?: selectFoodDefaultImage(position)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        //날짜 항목 클릭 시
        binding.llDate.setOnClickListener {
            //달력 요소, 달력 선택 리스너
            val cal = Calendar.getInstance()
            val dateSelectListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val dText = "${year}년 ${month + 1}월 ${dayOfMonth}일"
                binding.info?.date = dText
                binding.tvDate.text = dText
            }

            //데이트 피커 dialog 생성
            DatePickerDialog(this, dateSelectListener,
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                .show()
        }

        //map 버튼 클릭 시
        binding.btnMap.setOnClickListener {
            //장소 정보 얻기 위한 intent 실행
            Intent(this, MapActivity::class.java).let {
                //기존 정보가 있을 시 이를 intent 통해서 전송
                it.putExtra(LATITUDE, binding.info?.latitude)
                it.putExtra(LONGITUDE, binding.info?.longitude)
                it.putExtra(RESTAURANT_NAME, binding.etName.text.toString())

                //결과 받기 위해 런처로 실행
                getLocationLauncher.launch(it)
            }
        }

        //imageView 클릭 시 (dialog 생성)
        binding.ivImage.setOnClickListener{ _ ->
            ImgDialog(this).also { dialog ->
                //갤러리 사진 사용 선택
                dialog.setOnGalleryClickListener{
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also{ getImageLauncher.launch(it) }
                    dialog.destroy()
                }

                //기본 그림 이미지 사용 선택
                dialog.setOnDefaultClickListener{
                    binding.info?.imageName = null
                    selectFoodDefaultImage(binding.spCategory.selectedItemPosition)
                    dialog.destroy()
                }
            }.create()
        }

        //식당 이름 텍스트 변경 시 (에러 체크)
        binding.etName.addTextChangedListener {
            it?.let { text ->
                binding.textLayoutName.error = when(text.length){
                    0 -> PLEASE_INSERT_NAME
                    else -> null
                }
            }
        }
    }

    /* 각 observer, viewModel 연결을 하는 함수 */
    private fun setObservers(){
        itemViewModel.progressing.observe(this, progressObserver)
        itemViewModel.workFinishFlag.observe(this, finishObserver)
        itemViewModel.selectedItem.observe(this, selectedItemObserver)
    }


    //--------------------------------------------
    // 내부 함수 영역 (옵저버 후속 작업)

    /* 들어온 아이템 정보에 따라 화면을 세팅하는 함수 */
    private fun setContentsWithItem(item: RestaurantInfo){
        with(binding){
            info = item.copy()
            spCategory.setSelection(item.categoryNum)
        }

        //이미지 관련 값 세팅(현재 이미지 이름과 경로, 이전 이미지 경로<이미지 변경 시>)
        curImgPath = item.imagePath
        preImgName = item.imageName
    }

    /* 완료한 작업에 따라 적절한 메시지와 함께 화면을 종료하는 함수 */
    private fun workCompleteFinish(){
        val completeText = if(binding.info == null) R.string.save_complete else R.string.edit_complete
        Toast.makeText(this, completeText, Toast.LENGTH_SHORT).show()
        finish()
    }


    //--------------------------------------------
    // 내부 함수 영역 (데이터 저장)

    /* 작성한 정보의 저장 과정을 담은 함수 */
    private fun saveCurrentItemProcess(){
        //이미지 지정 x라면 경로도 null -> 이미지 로딩 에러 방지
        binding.info?.imageName ?: run{
            curImgPath = null
            binding.info?.imagePath = null
        }

        //network 연결 원활함 -> 저장 과정 실행
        if(NetworkWatcher.checkNetworkState(this)) {
            if (binding.etName.text.isNullOrEmpty())
                Toast.makeText(this, R.string.satisfy_warning, Toast.LENGTH_SHORT).show()
            else //갱신 혹은 삽입
                intent.getStringExtra(ITEM_ID)?.let { updateItem() } ?: run{ insertItem() }
        }
        else
            Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT).show()
    }

    /* 기존 아이템 갱신 함수 */
    private fun updateItem(){
        binding.info?.let { itemViewModel.updateItem(it, imgUri, preImgName) }
    }

    /* 새 아이템 추가 함수 */
    private fun insertItem(){
        binding.info?.let{ itemViewModel.insertItem(it, imgUri) }
    }


    //--------------------------------------------
    // 내부 함수 영역 (이미지 설정)

    //spinner 기본 이미지 고르기
    private fun selectFoodDefaultImage(position : Int){
        when(position){
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