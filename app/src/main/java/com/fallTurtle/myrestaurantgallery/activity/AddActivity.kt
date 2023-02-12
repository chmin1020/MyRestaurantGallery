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
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.databinding.ActivityAddBinding
import com.fallTurtle.myrestaurantgallery.dialog.ImgDialog
import com.fallTurtle.myrestaurantgallery.dialog.ProgressDialog
import com.fallTurtle.myrestaurantgallery.etc.NetworkWatcher
import com.fallTurtle.myrestaurantgallery.model.retrofit.value_object.LocationPair
import com.fallTurtle.myrestaurantgallery.model.room.RestaurantInfo
import com.fallTurtle.myrestaurantgallery.view_model.ItemViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Date


/**
 * 항목 추가 화면을 담당하는 액티비티.
 * 이 액티비티에서는 새로운 맛집 정보를 저장하거나 기존 내용을 수정하게 해준다.
 * 더하여 여기서 지역 주소 입력을 위해 Map 화면으로도 이동할 수 있다.
 **/
class  AddActivity : AppCompatActivity(){
    //바인딩
    private val binding:ActivityAddBinding by lazy { DataBindingUtil.setContentView(this, R.layout.activity_add) }

    //뷰모델
    private val viewModelFactory by lazy{ ViewModelProvider.AndroidViewModelFactory(this.application) }
    private val itemViewModel by lazy { ViewModelProvider(this, viewModelFactory)[ItemViewModel::class.java] }

    //옵저버
    private val progressObserver = Observer<Boolean> { if(it) progressDialog.create() else progressDialog.destroy() }
    private val finishObserver = Observer<Boolean> { if(it) finish() }
    private val selectedItemObserver = Observer<RestaurantInfo> { setContentsWithItem(it) }

    //로딩 다이얼로그
    private val progressDialog by lazy { ProgressDialog(this) }

    //선택된 아이템 관련 변수들
    private var itemId: String? = null
    private var itemLocation = LocationPair()
    private var preImgName: String? = null

    //이미지 이름, 기기 내 이미지 실제 uri
    private var curImgName: String? = null
    private var curImgPath: String? = null
    private var imgUri: Uri? = null


    //--------------------------------------------
    // 액티비티 결과 런처

    /* 갤러리에서 가지고 온 이미지 결과를 처리하는 런처 */
    private val getImg = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        imgUri = it.data?.data
        imgUri?.let{ uri->
            //시간을 통한 고유 이미지 이름 생성
            curImgName = uri.lastPathSegment.toString() + System.currentTimeMillis().toString()

            //사진 설정
            binding.ivImage.setImageURI(uri)
        }
    }

    /* 위치 검색에서 선택하여 가져온 위치 결과를 처리하는 런처 */
    private val getAddress = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.data?.getBooleanExtra("isChanged", false) == true) {
            val address = it.data?.getStringExtra("address")

            //받아온 내용 적용
            itemLocation.latitude = it.data?.getDoubleExtra("latitude", -1.0) ?: -1.0
            itemLocation.longitude = it.data?.getDoubleExtra("longitude", -1.0) ?: -1.0
            binding.etLocation.setText(address)
        }
    }


    //--------------------------------------------
    // 액티비티 생명주기 영역

    /* onCreate()에서는 뷰와 퍼미션 체크, 리사이클러뷰, 툴바, 이벤트 등의 기본적인 것들을 세팅한다. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //데이터 바인딩 (default)
        binding.info = RestaurantInfo()
        binding.spinnerEntries = resources.getStringArray(R.array.category_spinner)

        //인텐트로 선택된 데이터 db 아이디 가져와서 뷰모델에 적용 (실패 시 화면 종료)
        itemId = intent.getStringExtra("item_id")
        itemId?.let { itemViewModel.setProperItem(it) }

        //각 뷰의 리스너들 설정
        initListeners()

        //toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.save_item -> true.also { saveCurrentItemProcess() }
                else -> false
            }
        }

        setObservers() //옵저버와 뷰모델 연결
    }


    //--------------------------------------------
    // 오버라이딩 영역

    /* onCreateOptionsMenu()에서는 툴바에서 나타날 메뉴를 만든다. */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.add_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /* onOptionsItemSelected()에서는 툴바에서 선택한 옵션에 따라 나타날 이벤트를 정의한다. */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }


    //--------------------------------------------
    // 내부 함수 영역 (초기화)

    /* 화면 내 사용자 입력 관련 뷰들의 이벤트 리스너를 등록하는 함수 */
    private fun initListeners(){
        //스피너에 표시할 아이템 목록 설정
        binding.spCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                curImgName ?: selectFoodDefaultImage(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        //datePicker 다이얼로그 설정
        binding.llDate.setOnClickListener {
            val cal = Calendar.getInstance()
            val dp = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val dText = "${year}년 ${month + 1}월 ${dayOfMonth}일"
                binding.tvDate.text = dText
            }
            DatePickerDialog(this, dp, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        //map (주소 가져오기)
        binding.btnMap.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("latitude", itemLocation.latitude)
            intent.putExtra("longitude", itemLocation.longitude)
            getAddress.launch(intent)
        }

        //이미지뷰 클릭 시
        binding.ivImage.setOnClickListener{
            val imgDlg = ImgDialog(this)

            //갤러리에서 사진 가져오는 것을 선택했다면
            imgDlg.setOnGalleryClickListener {
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also{ getImg.launch(it) }
                imgDlg.destroy()
            }

            //기본 그림 이미지 사용을 선택했다면
            imgDlg.setOnDefaultClickListener {
                curImgName = null
                selectFoodDefaultImage(binding.spCategory.selectedItemPosition)
                imgDlg.destroy()
            }

            //설정한 다이얼로그 생성
            imgDlg.create()
        }
    }

    /* 각 옵저버를 적절한 뷰모델 내 데이터와 연결하는 함수 */
    private fun setObservers(){
        itemViewModel.progressing.observe(this, progressObserver)
        itemViewModel.workFinishFlag.observe(this, finishObserver)
        itemViewModel.selectedItem.observe(this, selectedItemObserver)
    }


    //--------------------------------------------
    // 내부 함수 영역 (옵저버 후속 작업)

    /* 들어온 아이템 정보에 따라 화면을 세팅하는 함수 */
    private fun setContentsWithItem(item: RestaurantInfo){
        binding.info = item
        binding.spCategory.setSelection(item.categoryNum)
        itemLocation = LocationPair(item.latitude, item.longitude)
        curImgName = item.imageName
        curImgPath = item.imagePath
        preImgName = item.imageName
    }


    //--------------------------------------------
    // 내부 함수 영역 (데이터 저장)

    /* 지금까지 작성한 정보를 아이템으로서 저장하는 과정을 담은 함수 */
    private fun saveCurrentItemProcess(){
        fun getNewID(): String
            = SimpleDateFormat("yyyy-MM-dd-hh-mm-ss", Locale.KOREA).format(Date(System.currentTimeMillis())).toString()

        //이미지가 지정되지 않은 상태면 경로도 null -> 이미지 로딩 에러 방지
        curImgName ?: run{ curImgPath = null }

        //네트워크 연결 상태라면 저장과정 실행
        if(NetworkWatcher.checkNetworkState(this)) {
            if (binding.etName.text.isNullOrEmpty() || binding.etLocation.text.isNullOrEmpty())
                Toast.makeText(this, R.string.satisfy_warning, Toast.LENGTH_SHORT).show()
            else {
                //기존 아이디 사용 혹은 현재 시간을 사용한 아이디 생성 (계정마다 따로 저장하므로 겹칠 일 x)
                val id =  itemId ?: getNewID()

                //위에서 설정한 값들, 뷰에서 가져온 값들을 하나의 맵에 모두 담아서 document 최종 저장
                val newItem = RestaurantInfo(imageName = curImgName, imagePath = curImgPath, date = binding.tvDate.text.toString(),
                                name = binding.etName.text.toString(), categoryNum = binding.spCategory.selectedItemPosition,
                                category = binding.spCategory.selectedItem.toString(), location = binding.etLocation.text.toString(),
                                memo = binding.etMemo.text.toString(), rate = binding.rbRatingBar.rating.toInt(),
                                latitude = itemLocation.latitude, longitude = itemLocation.longitude, dbID = id)

                itemViewModel.insertItem(newItem, imgUri, preImgName)
            }
        }
        else
            Toast.makeText(this, "네트워크에 연결되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
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