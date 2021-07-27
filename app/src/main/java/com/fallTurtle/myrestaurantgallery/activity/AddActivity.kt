package com.fallTurtle.myrestaurantgallery.activity

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fallTurtle.myrestaurantgallery.databinding.ActivityAddBinding

class AddActivity : AppCompatActivity() {
    private var mBinding: ActivityAddBinding? = null
    private val binding get()= mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivClear.setOnClickListener{ finish() }
        binding.tvSave.setOnClickListener{
            //저장~~
            Toast.makeText(this,"저장되었습니다",Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}