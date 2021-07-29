package com.fallTurtle.myrestaurantgallery.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.databinding.ActivityAddBinding
import com.fallTurtle.myrestaurantgallery.item.ImgDialog


class AddActivity : AppCompatActivity() {
    private var mBinding: ActivityAddBinding? = null
    private val binding get()= mBinding!!

    //이미지를 갤러리에서 받아오기 위한 요소들
    private var imgUri: Uri? = null
    private var imgUsed = false
    private val getImg = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result: ActivityResult ->
        imgUri = result.data?.data
        if(imgUri != null) {
            binding.ivImage.setImageURI(imgUri)
            imgUsed = true
        }
    }

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

        binding.ivImage.setOnClickListener{
            val imgDlg = ImgDialog(this)
            imgDlg.setOnGalleryClickListener {
                val gallery =
                    Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                getImg.launch(gallery)
                imgDlg.closeDialog()
            }

            imgDlg.setOnDefaultClickListener {
                imgUsed = false
                when(binding.spGenre.selectedItemPosition){
                    0 -> binding.ivImage.setImageResource(R.drawable.korean_food)
                    1 -> binding.ivImage.setImageResource(R.drawable.chinese_food)
                    2 -> binding.ivImage.setImageResource(R.drawable.japanese_food)
                    3 -> binding.ivImage.setImageResource(R.drawable.western_food)
                    4 -> binding.ivImage.setImageResource(R.drawable.coffee_and_drink)
                    5 -> binding.ivImage.setImageResource(R.drawable.drink)
                    6 -> binding.ivImage.setImageResource(R.drawable.etc)
                }
                imgDlg.closeDialog()
            }
            imgDlg.create()
        }

        binding.spGenre.adapter = ArrayAdapter.createFromResource(this, R.array.genre_spinner, android.R.layout.simple_spinner_dropdown_item)
        binding.spGenre.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(!imgUsed) {
                    when (position) {
                        0 -> binding.ivImage.setImageResource(R.drawable.korean_food)
                        1 -> binding.ivImage.setImageResource(R.drawable.chinese_food)
                        2 -> binding.ivImage.setImageResource(R.drawable.japanese_food)
                        3 -> binding.ivImage.setImageResource(R.drawable.western_food)
                        4 -> binding.ivImage.setImageResource(R.drawable.coffee_and_drink)
                        5 -> binding.ivImage.setImageResource(R.drawable.drink)
                        6 -> binding.ivImage.setImageResource(R.drawable.etc)
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}