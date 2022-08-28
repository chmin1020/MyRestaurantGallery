package com.fallTurtle.myrestaurantgallery.item

import android.app.Dialog
import android.content.Context
import android.view.Window
import com.fallTurtle.myrestaurantgallery.databinding.ProgressDialogBinding

class ProgressDialog(context: Context) {
    //binding
    private val dialog = Dialog(context)
    private val binding by lazy { ProgressDialogBinding.inflate(dialog.layoutInflater) }

    fun create() {
        dialog.setCancelable(false)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.show()
    }
}