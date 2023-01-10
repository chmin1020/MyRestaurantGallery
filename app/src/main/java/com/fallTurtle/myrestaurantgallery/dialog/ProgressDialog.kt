package com.fallTurtle.myrestaurantgallery.dialog

import android.app.Dialog
import android.content.Context
import android.view.Window
import com.fallTurtle.myrestaurantgallery.databinding.ProgressDialogBinding

/**
 * 무언가 진행 중 임을 나타내는 다이얼로그 클래스.
 * 프로그레스 원 표시만 하면 크게 더 할 것은 없다.
 **/
class ProgressDialog(context: Context) {

    //binding
    private val dialog = Dialog(context)
    private val binding by lazy { ProgressDialogBinding.inflate(dialog.layoutInflater) }

    init {
        dialog.setCancelable(false) //사용자가 임의로 취소 x
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
    }

    fun show(){
        dialog.show()
    }

    fun close(){
        dialog.cancel()
    }
}