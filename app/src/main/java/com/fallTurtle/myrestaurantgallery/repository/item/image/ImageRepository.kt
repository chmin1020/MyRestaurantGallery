package com.fallTurtle.myrestaurantgallery.repository.item.image

import android.net.Uri
import java.io.InputStream

interface ImageRepository {
    suspend fun clearImages()
    suspend fun insertImage(imageName: String, uri: Uri): String?
    suspend fun deleteImage(imageName: String)

    /*suspend fun restoreLocalImages(){
        val loadResult = storageRepository.getAllImagesInStorage()
        loadResult?.let { localImageRepository.restoreImages(it) }
    }*/

}