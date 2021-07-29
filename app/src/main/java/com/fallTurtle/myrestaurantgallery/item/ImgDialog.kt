package com.fallTurtle.myrestaurantgallery.item

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.WindowManager
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.databinding.ImgDialogBinding

class ImgDialog(context: Context) {
    //binding
    private var mBinding: ImgDialogBinding? = null
    private val binding get()= mBinding!!

    private val dialog = Dialog(context)
    private lateinit var onGalleryClickListener: View.OnClickListener
    private lateinit var onDefaultClickListener: View.OnClickListener

    fun create(){
        mBinding = ImgDialogBinding.inflate(dialog.layoutInflater)
        dialog.setContentView(binding.root)

        //다이얼로그 크기 및 취소 설정
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)

        //각 버튼 리스너 설정
        binding.tvGallery.setOnClickListener(onGalleryClickListener)
        binding.tvDefault.setOnClickListener(onDefaultClickListener)

        //다이얼로그 화면 표시
        dialog.show()
    }

    fun setOnGalleryClickListener(listener: View.OnClickListener){
        onGalleryClickListener = listener
    }
    fun setOnDefaultClickListener(listener: View.OnClickListener){
        onDefaultClickListener = listener
    }
}