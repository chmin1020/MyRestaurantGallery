package com.fallTurtle.myrestaurantgallery.repository.item.image

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LocalImageRepository(private val localPath: String, private val resolver: ContentResolver) {
    suspend fun restoreImages(backupImages: ListResult) {
        backupImages.items.forEach { downloadFile(it) }
    }

    suspend fun clearSavedImages(){
        withContext(Dispatchers.Default){File(localPath).listFiles()?.forEach { it.delete() } }
    }

    suspend fun insertImage(imageName: String, uri: Uri){
        if(!File(imageName).exists())
            saveImage(imageName, uri)
    }

    suspend fun deleteImage(imageName: String){
        withContext(Dispatchers.Default){ File("$localPath/$imageName").delete() }
    }

    private suspend fun downloadFile(downloadRef: StorageReference){
        suspendCoroutine<Any?> { continuation ->
            downloadRef.getFile(File("$localPath/${downloadRef.name}")).addOnCompleteListener{
                continuation.resume(null)
            }
        }
    }

    private suspend fun saveImage(imageName: String, uri: Uri){
        withContext(Dispatchers.Default) {saveBitmapAsImage(makeBitMap(uri), imageName) }
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
}