package com.fallTurtle.myrestaurantgallery.activity

import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.fallTurtle.myrestaurantgallery.databinding.ActivityLocationListBinding


class LocationListActivity : AppCompatActivity() {
    private lateinit var etSearch: EditText
    private lateinit var imm: InputMethodManager

    private lateinit var binding:ActivityLocationListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        etSearch = binding.etSearch

        imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        //뒤로 가기
        binding.ivBack.setOnClickListener {
            imm.hideSoftInputFromWindow(etSearch.windowToken, 0)
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
        //키보드 바로 올리기(activity 화면 출력 후 설정해야 오류 없음)
        etSearch.requestFocus()
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }
}