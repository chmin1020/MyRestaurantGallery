package com.fallTurtle.myrestaurantgallery.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.databinding.ActivityAddBinding
import com.fallTurtle.myrestaurantgallery.item.ImgDialog
import com.fallTurtle.myrestaurantgallery.item.Piece
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.*


class AddActivity : AppCompatActivity() {
    private var mBinding: ActivityAddBinding? = null
    private val binding get()= mBinding!!
    private val piece = Piece() //for edit

    //fireStore
    private val db = Firebase.firestore
    private val docRef = db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.email.toString())
    private val str = Firebase.storage

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isEdit = intent.getBooleanExtra("isEdit", false)

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
            piece.setRate(intent.getIntExtra("rate",0))
            piece.setImgUsed(intent.getBooleanExtra("imgUsed", false))
            imgUsed = piece.getImgUsed()
            piece.setLocation(intent.getStringExtra("location"))
            piece.setMemo(intent.getStringExtra("memo"))

            binding.etName.setText(piece.getName())
            binding.spGenre.setSelection(piece.getGenreNum()!!)
            binding.etLocation.setText(piece.getLocation())
            binding.etMemo.setText(piece.getMemo())
            binding.rbRatingBar.rating = piece.getRate()!!.toFloat()

            if(!piece.getImgUsed()) selectImg(piece.getGenreNum()!!)
        }

        binding.ivClear.setOnClickListener{
            if(isEdit) {
                val back = Intent(this, RecordActivity::class.java)
                back.putExtra("dbID",piece.getDBID())
                back.putExtra("name",piece.getName())
                back.putExtra("genreNum",piece.getGenreNum())
                back.putExtra("genre",piece.getGenre())
                back.putExtra("location",piece.getLocation())
                back.putExtra("imgUsed",piece.getImgUsed())
                back.putExtra("memo",piece.getMemo())
                back.putExtra("rate",piece.getRate())
                startActivity(back)
            }

            finish() }
        binding.ivImage.setOnClickListener{
            val imgDlg = ImgDialog(this)
            imgDlg.setOnGalleryClickListener {
                val gallery =
                    Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
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
        binding.tvSave.setOnClickListener {
            if (binding.etName.text.isEmpty() || binding.etLocation.text.isEmpty()) {
                Toast.makeText(this, R.string.satisfy_warning, Toast.LENGTH_SHORT).show()
            } else {
                val id:String = if(isEdit) intent.getStringExtra("dbID").toString()
                    else SimpleDateFormat("yyyy-MM-dd-hh-mm-ss").format(Date(System.currentTimeMillis())).toString()


                if(imgUsed) {

                }
                else {
                    val newRes = mapOf(
                        "image" to null,
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
                    Toast.makeText(this, "저장되었습니다", Toast.LENGTH_SHORT).show()
                }
                finish()
            }
        }
    }
}