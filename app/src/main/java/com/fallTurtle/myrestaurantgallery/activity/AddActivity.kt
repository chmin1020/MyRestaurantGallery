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
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.content.CursorLoader
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.databinding.ActivityAddBinding
import com.fallTurtle.myrestaurantgallery.etc.GlideApp
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


class AddActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAddBinding
    private val piece = Piece() //for edit

    //fireStore
    private val db = Firebase.firestore
    private val docRef = db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.email.toString())
    private val str = Firebase.storage
    private val strRef = str.reference.child(FirebaseAuth.getInstance().currentUser!!.email.toString())

    //이미지를 갤러리에서 받아오기 위한 요소들
    private var imgUri: Uri? = null
    private var imgUsed = false
    private val getImg = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result: ActivityResult ->
        imgUri = result.data?.data
        if(imgUri != null) {
            binding.ivImage.setImageURI(imgUri)
            imgUsed = true
        }
    }

    //수정 취소 시 실행될 함수
    private fun backToRecord(isEdit: Boolean){
        if(isEdit) {
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
        }
        finish()
    }

    //spinner 이미지 고르기
    private fun selectImg(position : Int){
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
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursorLoader = CursorLoader(this, uri!!, proj, null, null, null)
        val cursor: Cursor? = cursorLoader.loadInBackground()
        val index: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(index)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isEdit = intent.getBooleanExtra("isEdit", false)

        //toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.save_item -> {
                    //이름과 장소는 필수!
                    if (binding.etName.text.isEmpty() || binding.etLocation.text.isEmpty())
                        Toast.makeText(this, R.string.satisfy_warning, Toast.LENGTH_SHORT).show()
                    //저장 과정
                    else {
                        val id:String = if(isEdit) piece.getDBID().toString()
                        else SimpleDateFormat("yyyy-MM-dd-hh-mm-ss").format(Date(System.currentTimeMillis())).toString()
                        var image: String? = null

                        //이미지 설정
                        if(imgUsed) {
                            image = if(isEdit){
                                if(imgUri != null && !piece.getImage().equals(imgUri!!.lastPathSegment.toString())) {
                                    strRef.child(piece.getImage().toString()).delete()
                                    imgUri!!.lastPathSegment.toString()
                                } else
                                    piece.getImage().toString()
                            } else {
                                imgUri!!.lastPathSegment.toString()
                            }

                            if(!isEdit || (imgUri != null && !piece.getImage().equals(imgUri!!.lastPathSegment.toString()))) {
                                val stream = FileInputStream(File(getPath(imgUri)))
                                strRef.child(imgUri!!.lastPathSegment.toString()).putStream(stream)
                            }
                        }
                        else{
                            if(isEdit && piece.getImgUsed())
                                strRef.child(piece.getImage().toString()).delete()
                        }

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
                            "dbID" to id
                        )
                        docRef.collection("restaurants").document(id).set(newRes)

                        //로딩 화면 실행
                        val progress = Intent(this, ProgressActivity::class.java)
                        if(isEdit) progress.putExtra("endCode", 0)
                        else progress.putExtra("endCode", 1)

                        startActivity(progress)
                        finish()
                    }
                    true
                }
                else -> false
            }
        }

        //datePicker
        binding.ivDate.setOnClickListener {
            var dText = ""
            val cal = Calendar.getInstance()
            val dp = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                dText = "${year}년 ${month + 1}월 ${dayOfMonth}일"
                binding.tvDate.text = dText
            }
            val dpDialog = DatePickerDialog(this, dp, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
            dpDialog.datePicker
        }

        //spinner
        binding.spGenre.adapter = ArrayAdapter.createFromResource(this, R.array.genre_spinner, android.R.layout.simple_spinner_dropdown_item)
        binding.spGenre.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(!imgUsed) selectImg(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        if(isEdit){
            //adapter 데이터 받기
            piece.setDBID(intent.getStringExtra("dbID").toString())
            piece.setName(intent.getStringExtra("name"))
            piece.setGenreNum(intent.getIntExtra("genreNum", 0))
            piece.setGenre(intent.getStringExtra("genre"))
            piece.setImage(intent.getStringExtra("image"))
            piece.setRate(intent.getIntExtra("rate",0))
            piece.setImgUsed(intent.getBooleanExtra("imgUsed", false))
            imgUsed = piece.getImgUsed()
            piece.setLocation(intent.getStringExtra("location"))
            piece.setMemo(intent.getStringExtra("memo"))
            piece.setDate(intent.getStringExtra("date"))

            binding.etName.setText(piece.getName())
            binding.spGenre.setSelection(piece.getGenreNum()!!)
            binding.etLocation.setText(piece.getLocation())
            binding.etMemo.setText(piece.getMemo())
            binding.rbRatingBar.rating = piece.getRate()!!.toFloat()
            binding.tvDate.setText(piece.getDate())

            if(piece.getImgUsed()){
                val realRef = strRef.child(piece.getImage().toString())
                GlideApp.with(this)
                    .load(realRef).into(binding.ivImage)
            }
            else selectImg(piece.getGenreNum()!!)

        }
        else{
            //date picker default
            val sdf = SimpleDateFormat ( "yyyy년 M월 d일")
            val today = sdf.format(Date(Calendar.getInstance().timeInMillis))
            binding.tvDate.text = today
        }

        binding.ivImage.setOnClickListener{
            val imgDlg = ImgDialog(this)
            imgDlg.setOnGalleryClickListener {
                val gallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                getImg.launch(gallery)
                imgDlg.closeDialog()
            }

            imgDlg.setOnDefaultClickListener {
                imgUsed = false
                selectImg(binding.spGenre.selectedItemPosition)
                imgDlg.closeDialog()
            }
            imgDlg.create()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> {
                backToRecord(intent.getBooleanExtra("isEdit", false))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //val menuInflater = menuInflater
        menuInflater.inflate(R.menu.add_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        backToRecord(intent.getBooleanExtra("isEdit", false))
    }
}