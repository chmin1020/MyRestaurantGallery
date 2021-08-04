package com.fallTurtle.myrestaurantgallery.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.fallTurtle.myrestaurantgallery.databinding.ActivityRecordBinding

class RecordActivity : AppCompatActivity() {
    private var mBinding : ActivityRecordBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}