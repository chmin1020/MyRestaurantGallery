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
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.databinding.ActivityAddBinding
import com.fallTurtle.myrestaurantgallery.dialog.ImgDialog
import com.fallTurtle.myrestaurantgallery.etc.NetworkManager
import com.fallTurtle.myrestaurantgallery.model.room.Info
import com.fallTurtle.myrestaurantgallery.view_model.DataViewModel
import java.text.SimpleDateFormat
import java.util.*


/**
 * 항목 추가 화면을 담당하는 액티비티.
 * 이 액티비티에서는 새로운 맛집 정보를 저장할 수 있는 기능을 제공한다.
 * 새 항목 추가와 수정 모두 이 액티비티를 사용하므로, isEdit 값을 인텐트로 받아서 이 여부를 체크한다.
 * 더하여 여기서 지역 주소 입력을 위해 Map 화면으로도 이동할 수 있다.
 **/
class AddActivity : AppCompatActivity(){
    //--------------------------------------------
    // 프로퍼티 영역
    //

    //수정 데이터 저장을 위한 객체
    private val isEdit by lazy { intent.getBooleanExtra("isEdit", false) }
    private var info = Info()

    //뷰 바인딩
    private val binding by lazy { ActivityAddBinding.inflate(layoutInflater) }

    //네트워크 연결 체크 매니저
    private val networkManager: NetworkManager by lazy { NetworkManager(this) }

    //뷰모델
    private val viewModelFactory by lazy{ ViewModelProvider.AndroidViewModelFactory(this.application) }
    private val dataViewModel by lazy { ViewModelProvider(this, viewModelFactory)[DataViewModel::class.java] }

    //이미지를 갤러리에서 받아오기 위한 요소들
    private var curImgName: String? = null

    private var imgUri: Uri? = null
    private val getImg = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        imgUri = it.data?.data
        imgUri?.let{ uri->
            curImgName = uri.lastPathSegment.toString()
            binding.ivImage.setImageURI(uri)
        }
    }

    //맵에서 주소를 받아오기 위한 요소들
    private var address: String? = null
    private val getAddress = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.data?.getBooleanExtra("isChanged", false) == true) {
            address = it.data?.getStringExtra("address")
            info.latitude = it.data?.getDoubleExtra("latitude", -1.0) ?: -1.0
            info.longitude = it.data?.getDoubleExtra("longitude", -1.0) ?: -1.0
            binding.etLocation.setText(address)
        }
    }

    //뒤로 가기 버튼 누를 시 작동할 트리거
    private val backPressCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            backToRecord(intent.getBooleanExtra("isEdit", false))
        }
    }


    //--------------------------------------------
    // 액티비티 생명주기 및 오버라이딩 영역
    //

    /* onCreate()에서는 뷰와 퍼미션 체크, 리사이클러뷰, 툴바, 이벤트 등의 기본적인 것들을 세팅한다. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //각 뷰의 리스너들 설정
        initListeners()

        //spinner
        binding.spCategory.adapter =
            ArrayAdapter.createFromResource(this, R.array.category_spinner, android.R.layout.simple_spinner_dropdown_item)

        //edit 여부 체크
        if(isEdit)
            getEditInfo()
        else{
            //date picker default(수정 작업이 아니므로 날짜만 기본 세팅)
            val sdf = SimpleDateFormat ( "yyyy년 M월 d일", Locale.KOREA)
            val today = sdf.format(Date(Calendar.getInstance().timeInMillis))
            binding.tvDate.text = today
        }

        //toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.save_item -> {
                    saveCurrentItemProcess(isEdit)
                    true
                }
                else -> false
            }
        }

        //뒤로 가기 액션 추가
        this.onBackPressedDispatcher.addCallback(this, backPressCallback)
    }

    /* onCreateOptionsMenu()에서는 툴바에서 나타날 메뉴를 만든다. */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /* onOptionsItemSelected()에서는 툴바에서 선택한 옵션에 따라 나타날 이벤트를 정의한다. */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> backToRecord(intent.getBooleanExtra("isEdit", false))
        }
        return super.onOptionsItemSelected(item)
    }


    //--------------------------------------------
    // 내부 함수 영역
    //

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
            val dpDialog = DatePickerDialog(
                this,
                dp,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            dpDialog.show()
        }

        //map (주소 가져오기)
        binding.btnMap.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)

            var latitude:Double? = null
            var longitude:Double? = null

            if(isEdit) {
                latitude = info.latitude
                longitude = info.longitude
            }

            intent.putExtra("latitude", latitude)
            intent.putExtra("longitude", longitude)
            getAddress.launch(intent)
        }

        //이미지 가져오기
        binding.ivImage.setOnClickListener{
            val imgDlg = ImgDialog(this)

            //갤러리에서 사진 가져오는 것을 선택했다면
            imgDlg.setOnGalleryClickListener {
                val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                getImg.launch(gallery)
                imgDlg.closeDialog()
            }

            //기본 그림 이미지 사용을 선택했다면
            imgDlg.setOnDefaultClickListener {
                curImgName = null
                selectFoodDefaultImage(binding.spCategory.selectedItemPosition)
                imgDlg.closeDialog()
            }

            //설정한 다이얼로그 생성
            imgDlg.create()
        }
    }

    /* 수정 관련 정보들을 받아서 저장하고 또 적용하는 함수 */
    private fun getEditInfo(){
        //adapter 데이터 받기 (수정 취소를 대비하여 객체에 내용 백업)
        info = intent.getSerializableExtra("info") as Info

        //받은 데이터를 변수 혹은 뷰에 적용
        binding.etName.setText(info.name)
        binding.spCategory.setSelection(info.categoryNum)
        binding.etLocation.setText(info.location)
        binding.etMemo.setText(info.memo)
        binding.rbRatingBar.rating = info.rate.toFloat()
        binding.tvDate.text = info.date

        //이미지를 사용하는 정보라면 storage 내에서 이미지도 가져온다. (아니면 default)
        curImgName = info.image
        //info.image?.let { GlideApp.with(this).load(firebaseViewModel.getImageRef(it)).into(binding.ivImage) }
    }

    /* 지금까지 작성한 정보를 아이템으로서 저장하는 과정을 담은 함수 */
    private fun saveCurrentItemProcess(isEdit:Boolean){
        fun getNewID(): String
            = SimpleDateFormat("yyyy-MM-dd-hh-mm-ss", Locale.KOREA).format(Date(System.currentTimeMillis())).toString()

        //네트워크 연결 상태라면
        if(networkManager.checkNetworkState()) {
            //저장 과정 (이름과 장소는 필수!)
            if (binding.etName.text.isEmpty() || binding.etLocation.text.isEmpty())
                Toast.makeText(this, R.string.satisfy_warning, Toast.LENGTH_SHORT).show()
            else {
                //기존 아이디 사용 혹은 현재 시간을 사용한 아이디 생성 (계정마다 따로 저장하므로 겹칠 일 x)
                val id: String = if (isEdit) info.dbID else getNewID()

                //위에서 설정한 값들, 뷰에서 가져온 값들을 하나의 맵에 모두 담아서 document 최종 저장
                val newItem = Info(image = curImgName, date = binding.tvDate.text.toString(),
                                name = binding.etName.text.toString(), categoryNum = binding.spCategory.selectedItemPosition,
                                category = binding.spCategory.selectedItem.toString(), location = binding.etLocation.text.toString(),
                                memo = binding.etMemo.text.toString(), rate = binding.rbRatingBar.rating.toInt(),
                                latitude = info.latitude, longitude = info.longitude, dbID = id)
                dataViewModel.insertNewItem(newItem)

                //로딩 화면 실행 (저장 작업을 위한 extra time 마련)
                val progress = Intent(this, ProgressActivity::class.java)
                if (isEdit) progress.putExtra("endCode", 0)
                else progress.putExtra("endCode", 1)
                startActivity(progress)
                finish()
            }
        }
        else
            Toast.makeText(this, "네트워크에 연결되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
    }

    /* 수정 취소 시 실행될 함수 */
    private fun backToRecord(isEdit: Boolean){
        if(isEdit) {
            //기존에 백업했던 기존 정보들을 다시 record로 보냄
            val back = Intent(this, RecordActivity::class.java)
            back.putExtra("info", info)
            startActivity(back)
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
        else {
            finish()
            overridePendingTransition(R.anim.slide_down_in, R.anim.slide_down_out)
        }
    }

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