package com.fallTurtle.myrestaurantgallery.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fallTurtle.myrestaurantgallery.databinding.ActivityProgressBinding
import com.fallTurtle.myrestaurantgallery.item.ProgressDialog
import com.squareup.okhttp.Dispatcher
import kotlinx.coroutines.*

class ProgressActivity: AppCompatActivity() {
    var mBinding : ActivityProgressBinding? = null
    val binding get() = mBinding!!

    private fun toastMake(message: String){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pd = ProgressDialog(this)
        pd.create()

        CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            toastMake("저장되었습니다.")
            finish()
        }
    }
}