package com.fallTurtle.myrestaurantgallery.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.fallTurtle.myrestaurantgallery.databinding.ActivityRecordBinding
import com.fallTurtle.myrestaurantgallery.item.Piece

class RecordActivity : AppCompatActivity() {
    private var mBinding : ActivityRecordBinding? = null
    private val binding get() = mBinding!!

    var piece = Piece()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //adapter 데이터 받기
        val info = intent
        piece.setDBID(info.getStringExtra("dbID").toString())
        piece.setName(info.getStringExtra("name"))
        piece.setGenre(info.getStringExtra("genre"))
        piece.setRate(info.getIntExtra("rate",0))
        piece.setImgUsed(info.getBooleanExtra("imgUsed", false))
        piece.setDate(info.getStringExtra("date"))
        piece.setLocation(info.getStringExtra("location"))
        piece.setMemo(info.getStringExtra("memo"))

        binding.tvName.text = piece.getName()
        binding.tvGenre.text = piece.getGenre()
        binding.tvLocation.text = piece.getLocation()
        binding.tvMemo.text = piece.getMemo()
        binding.rbRatingBar.rating = piece.getRate()!!.toFloat()

    }
}