package com.fallTurtle.myrestaurantgallery.repository.item.image

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream

class LocalImageRepository(private val resolver: ContentResolver) {
    fun insertImage(imageName: String, uri: Uri){
        saveImage(imageName, uri)
    }

    ///////

    private fun saveImage(imagePath: String, uri: Uri){
        saveBitmapAsImage(makeBitMap(uri), imagePath)
    }

    private fun makeBitMap(uri: Uri): Bitmap{
        val inStream = resolver.openInputStream(uri)
        return BitmapFactory.decodeStream(inStream).also { inStream?.close() }
    }

    private fun saveBitmapAsImage(bitmap: Bitmap, imagePath: String){
        val newFile = File(imagePath)
        val outStream = FileOutputStream(newFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
        outStream.close()
    }

    //////

    fun deleteImage(imagePath: String){
        val deleteFile = File(imagePath)
        val result = deleteFile.delete().toString()
        Log.d("imageDeleteTry", result)
    }
}