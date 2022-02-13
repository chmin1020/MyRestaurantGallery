package com.fallTurtle.myrestaurantgallery.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fallTurtle.myrestaurantgallery.databinding.ActivityLocationListBinding

class LocationListActivity : AppCompatActivity() {
    private lateinit var binding:ActivityLocationListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationListBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}