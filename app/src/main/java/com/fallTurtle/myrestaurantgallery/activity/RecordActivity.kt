package com.fallTurtle.myrestaurantgallery.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.databinding.ActivityRecordBinding
import com.fallTurtle.myrestaurantgallery.item.Piece
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RecordActivity : AppCompatActivity() {
    private var mBinding : ActivityRecordBinding? = null
    private val binding get() = mBinding!!

    private var piece = Piece()

    private val db = Firebase.firestore
    private val docRef = db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.email.toString())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //adapter 데이터 받기
        piece.setDBID(intent.getStringExtra("dbID").toString())
        piece.setName(intent.getStringExtra("name"))
        piece.setGenreNum(intent.getIntExtra("genreNum", 0))
        piece.setGenre(intent.getStringExtra("genre"))
        piece.setRate(intent.getIntExtra("rate",0))
        piece.setImgUsed(intent.getBooleanExtra("imgUsed", false))
        piece.setDate(intent.getStringExtra("date"))
        piece.setLocation(intent.getStringExtra("location"))
        piece.setMemo(intent.getStringExtra("memo"))

        binding.tvName.text = piece.getName()
        binding.tvGenre.text = piece.getGenre()
        binding.tvLocation.text = piece.getLocation()
        binding.tvMemo.text = piece.getMemo()
        binding.rbRatingBar.rating = piece.getRate()!!.toFloat()

        if(!piece.getImgUsed()){
            when(piece.getGenreNum()!!){
                0 -> binding.ivImage.setImageResource(R.drawable.korean_food)
                1 -> binding.ivImage.setImageResource(R.drawable.chinese_food)
                2 -> binding.ivImage.setImageResource(R.drawable.japanese_food)
                3 -> binding.ivImage.setImageResource(R.drawable.western_food)
                4 -> binding.ivImage.setImageResource(R.drawable.coffee_and_drink)
                5 -> binding.ivImage.setImageResource(R.drawable.drink)
                6 -> binding.ivImage.setImageResource(R.drawable.etc)
            }
        }

        //edit 데이터 보내기
        binding.tvEdit.setOnClickListener{
            val edit = Intent(this, AddActivity::class.java)
            edit.putExtra("isEdit", true)
            edit.putExtra("dbID",piece.getDBID())
            edit.putExtra("name",piece.getName())
            edit.putExtra("genreNum",piece.getGenreNum())
            edit.putExtra("genre",piece.getGenre())
            edit.putExtra("location",piece.getLocation())
            edit.putExtra("imgUsed",piece.getImgUsed())
            edit.putExtra("memo",piece.getMemo())
            edit.putExtra("date",piece.getDate())
            edit.putExtra("rate",piece.getRate())
            startActivity(edit)
        }

        //아이템 삭제하기
        binding.tvDelete.setOnClickListener{
            AlertDialog.Builder(this)
                .setMessage(R.string.delete_message)
                .setPositiveButton(R.string.yes) {dialog, which ->
                    docRef.collection("restaurants").document(piece.getDBID().toString()).delete()
                    Toast.makeText(this, R.string.delete_complete, Toast.LENGTH_SHORT).show()
                    finish()
                }
                .setNegativeButton(R.string.no) {dialog, which -> }
                .show()
        }

        //액티비티 탈출
        binding.ivClear.setOnClickListener{ finish() }
    }
}