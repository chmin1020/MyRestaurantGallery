package com.fallTurtle.myrestaurantgallery.adapter

import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import coil.load
import com.fallTurtle.myrestaurantgallery.R


/**
 * 데이터 바인딩 작업 시 추가적으로 필요한 바인딩 어댑터 함수들을 모아 놓은 object.
 **/
object DataBindingAdapter {
    //-------------------------------------------------------
    //이미지뷰에 적절한 이미지를 적용하기 위한 어댑터 함수

    @BindingAdapter(value=["imagePath", "categoryNum"])
    @JvmStatic
    fun loadImage(imageView: ImageView, url: String?, categoryNum: Int){
        //이미지 적용
        url?.let {
            imageView.load(it){
                crossfade(true)
                error(R.drawable.error_food)
                placeholder(R.drawable.loading_food)
            }
        } ?: setDefaultImage(imageView, categoryNum)
    }

    /*기본 이미지 로드를 위한 내부 함수*/
    private fun setDefaultImage(imageView: ImageView, categoryNum: Int){
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


    //-------------------------------------------------------
    //텍스트뷰에 적절한 정수를 문자열 형태로 적용하기 위한 어댑터 함수

    @BindingAdapter(value = ["intNumberForText"])
    @JvmStatic
    fun setTextWithInt(textView: TextView, number: Int){
        textView.text = number.toString()
    }


    //-------------------------------------------------------
    //스피너에 적절한 배열을 적용한 어댑터를 연결하기 위한 어댑터 함수

    @BindingAdapter("entries")
    @JvmStatic
    fun Spinner.setEntries(entries: Array<String>?) {
            entries?.let{
                val arrayAdapter = ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, it)
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                adapter = arrayAdapter
            }
    }

    //-------------------------------------------------------
    //editText 역방향 데이터 바인딩 어댑터

    // BindingAdapter (Setter 역할)
    @JvmStatic
    @BindingAdapter("android:text")
    fun getTextForView(view: EditText, content: String?) {
        if (view.text.toString() != content)
            view.setText(content)
    }

    // InverseBindingAdapter (Getter 역할)
    @JvmStatic
    @InverseBindingAdapter(attribute = "android:text", event = "textAttrChanged")
    fun getTextForData(view: EditText): String {
        return view.text.toString()
    }

    // InverseBindingListener (InverseBindingAdpater 실행 역할)
    @JvmStatic
    @BindingAdapter("textAttrChanged")
    fun setTextWatcher(view: EditText, textAttrChanged: InverseBindingListener?){
        view.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) { }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                textAttrChanged?.onChange()
            }
        })
    }
}