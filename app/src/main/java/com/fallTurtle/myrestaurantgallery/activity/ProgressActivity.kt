package com.fallTurtle.myrestaurantgallery.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.databinding.ActivityProgressBinding
import com.fallTurtle.myrestaurantgallery.dialog.ProgressDialog
import kotlinx.coroutines.*

/**
 * 저장 작업을 하거나 로그아웃을 하는 등 대기 화면을 보여주고 싶을 때 사용하는 액티비티.
 * progressDialog, 토스트 메시지를 띄우는 간단한 역할만을 수행한다.
 **/
class ProgressActivity: AppCompatActivity() {
    //뷰 바인딩
    private val binding by lazy { ActivityProgressBinding.inflate(layoutInflater)}

    /* onCreate()에서 대기 화면과 상황에 따른 적절한 메시지 결정 */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        /* 대기 화면을 담당할 다이얼로그를 만들어서 띄움 */
        val pd = ProgressDialog(this)
        pd.create()

        /* intent 통해서 endCode 를 받고 이에 맞는 메시지 출력 */
        MainScope().launch {
            delay(2500)
            when(intent.getIntExtra("endCode", -1)) {
                -1-> makeToastMsg(R.string.error_happened)
                0-> makeToastMsg(R.string.edit_complete)
                1-> makeToastMsg(R.string.save_complete)
                2-> makeToastMsg(R.string.logout_success)
                3-> makeToastMsg(R.string.withdrawal_success)
            }
            finish()
        }
    }

    /* 메시지 내용을 받아 토스트 메시지를 받는 함수 */
    private fun makeToastMsg(message: Int){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

}