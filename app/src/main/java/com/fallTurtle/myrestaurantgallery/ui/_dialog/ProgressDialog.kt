package com.fallTurtle.myrestaurantgallery.ui._dialog

import android.app.Dialog
import android.content.Context
import com.fallTurtle.myrestaurantgallery.databinding.DialogProgressBinding

/**
 * 무언가 진행 중 임을 나타내는 다이얼로그 클래스.
 **/
class ProgressDialog(context: Context) {
    //뷰 바인딩
    private val binding by lazy { DialogProgressBinding.inflate(dialog.layoutInflater) }

    //dialog 객체
    private val dialog = Dialog(context)


    //-----------------------------------------
    // 함수 영역 (대화 상자)

    fun create(){
        //설정
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)

        dialog.show()
    }

    fun destroy(){
        dialog.dismiss()
    }
}