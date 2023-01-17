package com.fallTurtle.myrestaurantgallery.repository.item.image

import android.content.ContentResolver
import android.net.Uri

class ImageRepository(localPath: String, resolver: ContentResolver) {
    private val storageRepository = StorageRepository()
    private val localImageRepository = LocalImageRepository(localPath, resolver)

    suspend fun clearLocalImages(){
        localImageRepository.clearSavedImages()
    }

    suspend fun clearRemoteImages(deletingImages: List<String?>){
        storageRepository.clearAllImagesInStorage(deletingImages)
    }

    suspend fun restoreLocalImages(){
        val loadResult = storageRepository.getAllImagesInStorage()
        loadResult?.let { localImageRepository.restoreImages(it) }
    }

    suspend fun insertImage(imageName: String, uri: Uri){
        storageRepository.insertImage(imageName, uri)
        localImageRepository.insertImage(imageName, uri)
    }

    suspend fun deleteImage(imageName: String){
        storageRepository.deleteImage(imageName)
        localImageRepository.deleteImage(imageName)
    }
}