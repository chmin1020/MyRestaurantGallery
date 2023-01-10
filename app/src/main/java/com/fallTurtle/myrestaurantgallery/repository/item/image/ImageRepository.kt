package com.fallTurtle.myrestaurantgallery.repository.item.image

import android.content.ContentResolver
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class ImageRepository(localPath: String, resolver: ContentResolver) {
    private val storageRepository = StorageRepository()
    private val localImageRepository = LocalImageRepository(localPath, resolver)

    fun clearLocalImages(){
        localImageRepository.clearSavedImages()
    }

    fun restoreLocalImages(){
        localImageRepository.restoreImages(storageRepository.getProperReference())
    }

    fun insertImage(imageName: String, uri: Uri){
        storageRepository.insertImage(imageName, uri)
        localImageRepository.insertImage(imageName, uri)
    }

    fun deleteImage(imageName: String){
        storageRepository.deleteImage(imageName)
        localImageRepository.deleteImage(imageName)
    }
}