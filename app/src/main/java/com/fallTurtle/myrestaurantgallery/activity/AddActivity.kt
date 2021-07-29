package com.fallTurtle.myrestaurantgallery.activity

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.databinding.ActivityAddBinding

class AddActivity : AppCompatActivity() {
    private var mBinding: ActivityAddBinding? = null
    private val binding get()= mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivClear.setOnClickListener{ finish() }
        binding.tvSave.setOnClickListener {
            //저장~~
            Toast.makeText(this, "저장되었습니다", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.spGenre.adapter = ArrayAdapter.createFromResource(this, R.array.genre_spinner, android.R.layout.simple_spinner_dropdown_item)
        binding.spGenre.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position){
                    0 ->{
                        binding.ivImage.setImageResource(R.drawable.korean_food)
                    }
                    1 ->{
                        binding.ivImage.setImageResource(R.drawable.chinese_food)
                    }
                    2 ->{
                        binding.ivImage.setImageResource(R.drawable.japanese_food)
                    }
                    3 ->{
                        binding.ivImage.setImageResource(R.drawable.western_food)
                    }
                    4 ->{
                        binding.ivImage.setImageResource(R.drawable.coffee_and_drink)
                    }
                    5 ->{
                        binding.ivImage.setImageResource(R.drawable.drink)
                    }
                    6 ->{
                        binding.ivImage.setImageResource(R.drawable.etc)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}