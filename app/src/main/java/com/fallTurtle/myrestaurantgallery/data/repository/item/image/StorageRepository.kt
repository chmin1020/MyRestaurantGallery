package com.fallTurtle.myrestaurantgallery.data.repository.item.image

import android.net.Uri
import com.fallTurtle.myrestaurantgallery.data.firebase.FirebaseUtils
import kotlinx.coroutines.tasks.await

/**
 * Firebase Storage API 기능을 통해 이미지 처리를 수행하는 리포지토리.
 **/
class StorageRepository: ImageRepository {
    //파이어베이스 스토리지에서 적절한 수행을 하기 위한 레퍼런스
    private val storageRef = FirebaseUtils.getStorageRef()


    //--------------------------------------------
    // 오버라이딩 영역

    /* 모든 이미지를 제거하는 함수 */
    override suspend fun clearImages() {
        val images = getAllImages()
        images.forEach { deleteImage(it) }
    }

    /* 특정 이미지를 추가하는 함수 */
    override suspend fun insertImage(imageName: String, uri: Uri):String{
        storageRef?.child(imageName)?.putFile(uri)?.await()
        return storageRef?.child(imageName)?.downloadUrl?.await().toString()
    }

    /* 특정 이미지를 제거하는 함수 */
    override suspend fun deleteImage(imageName: String){
        storageRef?.child(imageName)?.delete()?.await()
    }


    //--------------------------------------------
    // 내부 함수 영역

    /* 저장된 모든 이미지의 이름을 가져오는 함수 */
    private suspend fun getAllImages(): List<String> {
        val images = mutableListOf<String>()
        val imageRefs = storageRef?.listAll()?.await()

        imageRefs?.items?.forEach { images.add(it.name) }
        return images
    }
}