package com.fallTurtle.myrestaurantgallery.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.adapter.ListAdapter
import com.fallTurtle.myrestaurantgallery.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    //binding
    private var mBinding:ActivityMainBinding? = null
    private val binding get()= mBinding!!

    //recyclerview adapter
    private val listAdapter = ListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = GridLayoutManager(this,2)
        binding.recyclerView.adapter = listAdapter

        binding.ivAddPic.setOnClickListener{
            val addIntent = Intent(this, AddActivity::class.java)
            startActivity(addIntent)
        }
    }


}