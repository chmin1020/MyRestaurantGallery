package com.fallTurtle.myrestaurantgallery.repository.item.image

import android.content.ContentResolver
import android.net.Uri

class ImageRepository(resolver: ContentResolver) {
    private val storageRepository = StorageRepository()
    private val localImageRepository = LocalImageRepository(resolver)

    fun restoreLocalImages(){
     /*   fireStoreRepository.getProperCollection().get().addOnSuccessListener {
            //io 스레드 내에서 roomDB 데이터 채우기
            CoroutineScope(Dispatchers.IO).launch{
                it.toObjects(Info::class.java).forEach{ item -> roomRepository.insertData(item) }
            }
        }*/
    }

    fun insertImage(imagePath: String, uri: Uri){
        storageRepository.insertImage(imagePath, uri)
        localImageRepository.insertImage(imagePath, uri)
    }

    fun deleteImage(imagePath: String){
        storageRepository.deleteImage(imagePath)
        localImageRepository.deleteImage(imagePath)
    }
}