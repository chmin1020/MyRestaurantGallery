package com.fallTurtle.myrestaurantgallery.activity

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.content.CursorLoader
import com.fallTurtle.myrestaurantgallery.etc.GlideApp
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.databinding.ActivityAddBinding
import com.fallTurtle.myrestaurantgallery.item.ImgDialog
import com.fallTurtle.myrestaurantgallery.item.Piece
import com.fallTurtle.myrestaurantgallery.item.ProgressDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import java.io.File
import java.io.FileInputStream
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
            piece.setImage(intent.getStringExtra("image"))
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

            if(piece.getImgUsed()){
                val realRef = strRef.child(piece.getImage().toString())
                GlideApp.with(this)
                    .load(realRef).into(binding.ivImage)
            }
            else selectImg(piece.getGenreNum()!!)

        }

        binding.ivClear.setOnClickListener{
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
                startActivity(back)
            }

            finish() }
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
        binding.tvSave.setOnClickListener {
            if (binding.etName.text.isEmpty() || binding.etLocation.text.isEmpty()) {
                Toast.makeText(this, R.string.satisfy_warning, Toast.LENGTH_SHORT).show()
            } else {
                val id:String = if(isEdit) piece.getDBID().toString()
                    else SimpleDateFormat("yyyy-MM-dd-hh-mm-ss").format(Date(System.currentTimeMillis())).toString()
                var image: String? = null

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
                    "name" to binding.etName.text.toString(),
                    "genreNum" to binding.spGenre.selectedItemPosition,
                    "genre" to binding.spGenre.selectedItem.toString(),
                    "location" to binding.etLocation.text.toString(),
                    "imgUsed" to imgUsed,
                    "memo" to binding.etMemo.text.toString(),
                    "rate" to binding.rbRatingBar.rating, "dbID" to id
                )
                docRef.collection("restaurants").document(id).set(newRes)
                //Toast.makeText(this, "저장되었습니다", Toast.LENGTH_SHORT).show()
                val progress = Intent(this, ProgressActivity::class.java)
                startActivity(progress)

            }
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
}