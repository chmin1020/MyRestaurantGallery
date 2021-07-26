package com.fallTurtle.myrestaurantgallery.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var mBinding:ActivityMainBinding? = null
    private val binding get()= mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivAddPic.setOnClickListener{
            val addIntent = Intent(this, AddActivity::class.java)
            startActivity(addIntent)
        }
    }


}