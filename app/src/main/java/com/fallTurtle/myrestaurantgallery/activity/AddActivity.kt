package com.fallTurtle.myrestaurantgallery.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.databinding.ActivityAddBinding
import com.fallTurtle.myrestaurantgallery.etc.GlideApp
import com.fallTurtle.myrestaurantgallery.etc.NetworkManager
import com.fallTurtle.myrestaurantgallery.item.ImgDialog
import com.fallTurtle.myrestaurantgallery.item.Piece
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.FileInputStream
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
    // 인스턴스 영역
    //

    //for saving edit information
    private val piece = Piece()

    //view binding
    private val binding by lazy { ActivityAddBinding.inflate(layoutInflater) }

    //network connection check
    private val nm: NetworkManager by lazy { NetworkManager(this) }

    //fireStore
    private val db = Firebase.firestore
    private val docRef = db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.email.toString())
    private val str = Firebase.storage
    private val strRef = str.reference.child(FirebaseAuth.getInstance().currentUser!!.email.toString())

    //이미지를 갤러리에서 받아오기 위한 요소들
    private var imgUri: Uri? = null
    private var imgUsed = false
    private val getImg = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        imgUri = it.data?.data
        if(imgUri != null) {
            binding.ivImage.setImageURI(imgUri)
            imgUsed = true
        }
    }

    //맵에서 주소를 받아오기 위한 요소들
    private var address: String? = null
    private val getAddr = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.data?.getBooleanExtra("isChanged", false) == true) {
            address = it.data?.getStringExtra("address")
            piece.setLatitude(it.data?.getDoubleExtra("latitude", -1.0)!!)
            piece.setLongitude(it.data?.getDoubleExtra("longitude", -1.0)!!)
            binding.etLocation.setText(address)
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

        //edit 여부 체크
        val isEdit = intent.getBooleanExtra("isEdit", false)
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

        //spinner
        binding.spGenre.adapter =
            ArrayAdapter.createFromResource(this, R.array.genre_spinner, android.R.layout.simple_spinner_dropdown_item)
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

    /* onBackPressed()에서는 뒤로 가기 버튼을 눌렸을 때 수행할 일을 정의한다. */
    override fun onBackPressed() {
        super.onBackPressed()
        backToRecord(intent.getBooleanExtra("isEdit", false))
    }


    //--------------------------------------------
    // 내부 함수 영역
    //

    /* 화면 내 사용자 입력 관련 뷰들의 이벤트 리스너를 등록하는 함수 */
    private fun initListeners(){
        //스피너에 표시할 아이템 목록 설정
        binding.spGenre.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(!imgUsed) selectFoodDefaultImage(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        //datePicker 다이얼로그 설정
        binding.llDate.setOnClickListener {
            val cal = Calendar.getInstance()
            val dp = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
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
            intent.putExtra("latitude", piece.getLatitude())
            intent.putExtra("longitude", piece.getLongitude())
            getAddr.launch(intent)
        }

        //이미지 가져오기
        binding.ivImage.setOnClickListener{
            val imgDlg = ImgDialog(this)

            //갤러리에서 사진 가져오는 것을 선택했다면
            imgDlg.setOnGalleryClickListener {
                val gallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                getImg.launch(gallery)
                imgDlg.closeDialog()
            }

            //기본 그림 이미지 사용을 선택했다면
            imgDlg.setOnDefaultClickListener {
                imgUsed = false
                selectFoodDefaultImage(binding.spGenre.selectedItemPosition)
                imgDlg.closeDialog()
            }

            //설정한 다이얼로그 생성
            imgDlg.create()
        }
    }

    /* 수정 관련 정보들을 받아서 저장하고 또 적용하는 함수 */
    private fun getEditInfo(){
        //adapter 데이터 받기 (수정 취소를 대비하여 객체에 내용 백업)
        piece.setDBID(intent.getStringExtra("dbID").toString())
        piece.setName(intent.getStringExtra("name"))
        piece.setGenreNum(intent.getIntExtra("genreNum", 0))
        piece.setGenre(intent.getStringExtra("genre"))
        piece.setImage(intent.getStringExtra("image"))
        piece.setRate(intent.getIntExtra("rate",0))
        piece.setImgUsed(intent.getBooleanExtra("imgUsed", false))
        piece.setLocation(intent.getStringExtra("location"))
        piece.setMemo(intent.getStringExtra("memo"))
        piece.setDate(intent.getStringExtra("date"))
        piece.setLatitude(intent.getDoubleExtra("latitude", -1.0))
        piece.setLongitude(intent.getDoubleExtra("longitude", -1.0))

        //받은 데이터를 변수 혹은 뷰에 적용
        imgUsed = piece.getImgUsed()
        binding.etName.setText(piece.getName())
        binding.spGenre.setSelection(piece.getGenreNum()!!)
        binding.etLocation.setText(piece.getLocation())
        binding.etMemo.setText(piece.getMemo())
        binding.rbRatingBar.rating = piece.getRate()!!.toFloat()
        binding.tvDate.text = piece.getDate()

        //이미지를 사용하는 정보라면 storage 내에서 이미지도 가져온다. (아니면 default)
        if(piece.getImgUsed()){
            val realRef = strRef.child(piece.getImage().toString())
            GlideApp.with(this).load(realRef).into(binding.ivImage)
        }
        else
            selectFoodDefaultImage(piece.getGenreNum()!!)
    }

    /* 지금까지 작성한 정보를 아이템으로서 저장하는 과정을 담은 함수 */
    private fun saveCurrentItemProcess(isEdit:Boolean){
        if(nm.checkNetworkState()) {
            //저장 과정 (이름과 장소는 필수!)
            if (binding.etName.text.isEmpty() || binding.etLocation.text.isEmpty())
                Toast.makeText(this, R.string.satisfy_warning, Toast.LENGTH_SHORT)
                    .show()
            else {
                //기존 아이디 사용 혹은 현재 시간을 사용한 아이디 생성
                val id: String =
                    if (isEdit)
                        piece.getDBID().toString()
                    else
                        SimpleDateFormat("yyyy-MM-dd-hh-mm-ss", Locale.KOREA)
                            .format(Date(System.currentTimeMillis()))
                            .toString()


                //이미지 설정
                var image: String? = null
                if (imgUsed) {
                    image = if (isEdit) {
                        if (imgUri != null && !piece.getImage()
                                .equals(imgUri!!.lastPathSegment.toString())
                        ) {
                            strRef.child(piece.getImage().toString()).delete()
                            imgUri!!.lastPathSegment.toString()
                        } else
                            piece.getImage().toString()
                    } else {
                        imgUri!!.lastPathSegment.toString()
                    }

                    if (!isEdit || (imgUri != null && !piece.getImage()
                            .equals(imgUri!!.lastPathSegment.toString()))
                    ) {
                        val stream = FileInputStream(File(getPath(imgUri)))
                        strRef.child(imgUri!!.lastPathSegment.toString())
                            .putStream(stream)
                    }
                } else {
                    if (isEdit && piece.getImgUsed())
                        strRef.child(piece.getImage().toString()).delete()
                }

                //위에서 설정한 값들, 뷰에서 가져온 값들을 하나의 맵에 모두 담아서 document 최종 저장
                val newRes = mapOf(
                    "image" to image,
                    "date" to binding.tvDate.text.toString(),
                    "name" to binding.etName.text.toString(),
                    "genreNum" to binding.spGenre.selectedItemPosition,
                    "genre" to binding.spGenre.selectedItem.toString(),
                    "location" to binding.etLocation.text.toString(),
                    "imgUsed" to imgUsed,
                    "memo" to binding.etMemo.text.toString(),
                    "rate" to binding.rbRatingBar.rating,
                    "latitude" to piece.getLatitude(),
                    "longitude" to piece.getLongitude(),
                    "dbID" to id
                )
                docRef.collection("restaurants").document(id).set(newRes)

                //로딩 화면 실행 (저장 작업을 위한 extra time 마련)
                val progress = Intent(this, ProgressActivity::class.java)
                if (isEdit) progress.putExtra("endCode", 0)
                else progress.putExtra("endCode", 1)
                startActivity(progress)

                //이 화면은 종료
                finish()
            }
        }
        else
            Toast.makeText(this, "네트워크에 연결되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
    }


    //수정 취소 시 실행될 함수
    private fun backToRecord(isEdit: Boolean){
        if(isEdit) {
            //기존에 백업했던 기존 정보들을 다시 record로 보냄
            val back = Intent(this, RecordActivity::class.java)
            back.putExtra("dbID",piece.getDBID())
            back.putExtra("name",piece.getName())
            back.putExtra("genreNum",piece.getGenreNum())
            back.putExtra("genre",piece.getGenre())
            back.putExtra("location",piece.getLocation())
            back.putExtra("image",piece.getImage())
            back.putExtra("imgUsed",piece.getImgUsed())
            back.putExtra("memo",piece.getMemo())
            back.putExtra("rate",piece.getRate())
            back.putExtra("date",piece.getDate())
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

    //제대로 된 uri 가져오기
    private fun getPath(uri: Uri?): String {
        val cursor: Cursor? = contentResolver.query(uri!!, null, null, null, null)
        cursor!!.moveToNext()
        val path = cursor.getString(cursor.getColumnIndex("_data"))
        cursor.close()
        return path
    }
}