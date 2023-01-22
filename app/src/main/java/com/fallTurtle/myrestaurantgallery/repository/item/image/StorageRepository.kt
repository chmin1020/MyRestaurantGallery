package com.fallTurtle.myrestaurantgallery.repository.item.image

import android.net.Uri
import com.fallTurtle.myrestaurantgallery.model.firebase.FirebaseUtils
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Firebase Storage API 기능을 통해 이미지 처리를 수행하는 리포지토리.
 **/
class StorageRepository: ImageRepository{
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
    override suspend fun insertImage(imageName: String, uri: Uri):String?{
        suspendCoroutine<Any?> { continuation ->
            storageRef.child(imageName).putFile(uri).addOnCompleteListener{ continuation.resume(null)}
        }

        return suspendCoroutine { continuation ->
            storageRef.child(imageName).downloadUrl.addOnCompleteListener {
                if(it.isSuccessful)
                    continuation.resume(it.result.toString())
            }
        }
    }

    /* 특정 이미지를 제거하는 함수 */
    override suspend fun deleteImage(imageName: String){
        suspendCoroutine<Any?> { continuation ->
            storageRef.child(imageName).delete().addOnCompleteListener { continuation.resume(null) }
        }
    }


    //--------------------------------------------
    // 내부 함수 영역

    /* 저장된 모든 이미지의 이름을 가져오는 함수 */
    private suspend fun getAllImages(): List<String> {
        return suspendCoroutine { continuation ->
            val images = mutableListOf<String>()
            storageRef.listAll().addOnCompleteListener {
                if(it.isSuccessful){
                    it.result.items.forEach{ ref -> images.add(ref.name) }
                }
                continuation.resume(images)
            }
        }
    }
}