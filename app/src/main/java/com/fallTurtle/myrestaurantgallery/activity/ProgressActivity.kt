package com.fallTurtle.myrestaurantgallery.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fallTurtle.myrestaurantgallery.databinding.ActivityProgressBinding


class ProgressActivity : AppCompatActivity() {
    private var mBinding: ActivityProgressBinding? = null
    private val binding get()= mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}