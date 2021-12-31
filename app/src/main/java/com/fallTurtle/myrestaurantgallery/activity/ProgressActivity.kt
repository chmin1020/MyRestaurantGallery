package com.fallTurtle.myrestaurantgallery.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.databinding.ActivityProgressBinding
import com.fallTurtle.myrestaurantgallery.item.ProgressDialog
import com.squareup.okhttp.Dispatcher
import kotlinx.coroutines.*

class ProgressActivity: AppCompatActivity() {
    private lateinit var binding : ActivityProgressBinding

    private fun toastMake(message: Int){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pd = ProgressDialog(this)
        pd.create()

        CoroutineScope(Dispatchers.Main).launch {
            delay(2500)
            when(intent.getIntExtra("endCode", -1)) {
                -1-> toastMake(R.string.error_happened)
                0-> toastMake(R.string.edit_complete)
                1-> toastMake(R.string.save_complete)
                2-> toastMake(R.string.logout_success)
                3-> toastMake(R.string.withdrawal_success)
            }
            finish()
        }
    }
}