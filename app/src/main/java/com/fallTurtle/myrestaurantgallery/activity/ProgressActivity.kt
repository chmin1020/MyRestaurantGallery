package com.fallTurtle.myrestaurantgallery.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fallTurtle.myrestaurantgallery.databinding.ActivityProgressBinding
import com.fallTurtle.myrestaurantgallery.item.ProgressDialog

class ProgressActivity: AppCompatActivity() {
    var mBinding : ActivityProgressBinding? = null
    val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pd = ProgressDialog(this)
        pd.create()
    }
}