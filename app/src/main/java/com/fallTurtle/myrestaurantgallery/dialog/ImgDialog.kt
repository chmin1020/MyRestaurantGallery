package com.fallTurtle.myrestaurantgallery.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.WindowManager
import com.fallTurtle.myrestaurantgallery.databinding.ImgDialogBinding

/**
 * 이미지를 변경하려고 할 시 나타나는 커스텀 다이얼로그.
 * 본인 갤러리에서 가져오거나, 앱에서 주어지는 기본 이미지를 사용하거나를 선택할 수 있다.
 **/
class ImgDialog(context: Context) {
    //binding
    private lateinit var binding: ImgDialogBinding

    private val dialog = Dialog(context)
    private lateinit var onGalleryClickListener: View.OnClickListener
    private lateinit var onDefaultClickListener: View.OnClickListener

    fun create(){
        binding = ImgDialogBinding.inflate(dialog.layoutInflater)
        dialog.setContentView(binding.root)

        //다이얼로그 크기 및 취소 설정
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)

        //각 버튼 리스너 설정
        binding.tvGallery.setOnClickListener(onGalleryClickListener)
        binding.tvDefault.setOnClickListener(onDefaultClickListener)

        //다이얼로그 화면 표시
        dialog.show()
    }

    fun closeDialog(){
        dialog.dismiss()
    }

    fun setOnGalleryClickListener(listener: View.OnClickListener){
        onGalleryClickListener = listener
    }
    fun setOnDefaultClickListener(listener: View.OnClickListener){
        onDefaultClickListener = listener
    }
}