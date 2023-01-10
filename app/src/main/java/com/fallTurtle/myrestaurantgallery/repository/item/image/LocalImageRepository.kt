package com.fallTurtle.myrestaurantgallery.repository.item.image

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream

class LocalImageRepository(private val localPath: String, private val resolver: ContentResolver) {
    suspend fun restoreImages(backupReference: StorageReference){
        withContext(Dispatchers.IO) {
            backupReference.listAll().addOnSuccessListener {
                it.items.forEach { eachRef -> eachRef.getFile(File("$localPath/${eachRef.name}")) }
            }
        }
    }

    suspend fun clearSavedImages(){
        withContext(Dispatchers.Default){File(localPath).listFiles()?.forEach { it.delete() } }
    }

    suspend fun insertImage(imageName: String, uri: Uri){
        if(!File(imageName).exists())
            withContext(Dispatchers.Default){saveImage(imageName, uri)}
    }

    ///////

    private fun saveImage(imageName: String, uri: Uri){
        saveBitmapAsImage(makeBitMap(uri), imageName)
    }

    private fun makeBitMap(uri: Uri): Bitmap{
        val inStream = resolver.openInputStream(uri)
        return BitmapFactory.decodeStream(inStream).also { inStream?.close() }
    }

    private fun saveBitmapAsImage(bitmap: Bitmap, imageName: String){
        val newFile = File("$localPath/$imageName")
        val outStream = FileOutputStream(newFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
        outStream.close()
    }

    //////

    suspend fun deleteImage(imageName: String){
        withContext(Dispatchers.Default){ File(imageName).delete().toString() }
    }
}