package com.fallTurtle.myrestaurantgallery.repository.item.image

import android.net.Uri

class ImageRepository {
    private val storageRepository = StorageRepository()
    private val localImageRepository = LocalImageRepository()

    fun insertImage(uri: Uri){

    }

    fun deleteImage(imageName: String){
        storageRepository.deleteImage(imageName)
    }
}