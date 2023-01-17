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
    //뷰 바인딩
    private val binding by lazy {ImgDialogBinding.inflate(dialog.layoutInflater)}

    //다이얼로그 객체
    private val dialog = Dialog(context)

    //각각 긍정,부정 리스너
    private lateinit var onGalleryClickListener: View.OnClickListener
    private lateinit var onDefaultClickListener: View.OnClickListener


    //-----------------------------------------
    // 함수 영역 (대화 상자)

    /* 이미지 다이얼로그를 보여주는 함수 */
    fun create(){
        dialog.setContentView(binding.root)

        //각 버튼 리스너 설정
        binding.tvGallery.setOnClickListener(onGalleryClickListener)
        binding.tvDefault.setOnClickListener(onDefaultClickListener)

        //다이얼로그 크기 및 취소 설정
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)

        dialog.show()
    }

    /* 이미지 다이얼로그를 제거하는 함수 */
    fun destroy(){
        dialog.dismiss()
    }


    //-------------------------------
    // 함수 영역 (선택 리스너 설정)

    /* 다이얼로그의 첫번째 선택지 (갤러리에서 가져오기) 리스너를 설정하는 함수 */
    fun setOnGalleryClickListener(listener: View.OnClickListener){
        onGalleryClickListener = listener
    }

    /* 다이얼로그의 두번째 선택지 (기본 이미지 설정) 리스너를 설정하는 함수 */
    fun setOnDefaultClickListener(listener: View.OnClickListener){
        onDefaultClickListener = listener
    }
}