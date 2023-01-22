package com.fallTurtle.myrestaurantgallery.repository.item.image

import android.net.Uri

/**
 * 이미지 리포지토리의 역할을 정의한 인터페이스.
 **/
interface ImageRepository {
    suspend fun clearImages()
    suspend fun insertImage(imageName: String, uri: Uri): String?
    suspend fun deleteImage(imageName: String)
}