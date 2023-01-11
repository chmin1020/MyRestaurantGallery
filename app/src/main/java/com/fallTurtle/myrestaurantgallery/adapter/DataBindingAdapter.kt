package com.fallTurtle.myrestaurantgallery.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.api.load
import com.fallTurtle.myrestaurantgallery.R
import java.io.File

object DataBindingAdapter {
    @BindingAdapter(value=["imagePath", "categoryNum"])
    @JvmStatic
    fun loadImage(imageView: ImageView, path: String?, categoryNum: Int){
        //이미지 적용
        path?.let {
            imageView.load(File("${imageView.context.filesDir}/$it")){
                crossfade(true)
                placeholder(R.drawable.loading_food)
            }
        } ?:
        run{
            when (categoryNum) {
                0 -> imageView.setImageResource(R.drawable.korean_food)
                1 -> imageView.setImageResource(R.drawable.chinese_food)
                2 -> imageView.setImageResource(R.drawable.japanese_food)
                3 -> imageView.setImageResource(R.drawable.western_food)
                4 -> imageView.setImageResource(R.drawable.coffee_and_drink)
                5 -> imageView.setImageResource(R.drawable.drink)
                6 -> imageView.setImageResource(R.drawable.etc)
            }
        }
    }
}