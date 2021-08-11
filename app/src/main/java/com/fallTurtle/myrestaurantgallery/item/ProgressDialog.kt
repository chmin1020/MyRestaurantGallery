package com.fallTurtle.myrestaurantgallery.item

import android.app.Dialog
import android.content.Context
import android.view.Window
import com.fallTurtle.myrestaurantgallery.databinding.ProgressDialogBinding

class ProgressDialog(context: Context) {
    //binding
    private var mBinding: ProgressDialogBinding? = null
    private val binding get()= mBinding!!

    private val dialog = Dialog(context)

    fun create() {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mBinding = ProgressDialogBinding.inflate(dialog.layoutInflater)
        dialog.setContentView(binding.root)
        dialog.show()
    }
}