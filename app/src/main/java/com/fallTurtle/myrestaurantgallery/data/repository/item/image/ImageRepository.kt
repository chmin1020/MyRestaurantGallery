package com.fallTurtle.myrestaurantgallery.data.repository.item.image

import android.net.Uri

/**
 * 이미지 리포지토리의 역할을 정의한 인터페이스.
 **/
interface ImageRepository {
    /* 모든 이미지를 제거하는 함수 */
    suspend fun clearImages()

    /* 특정 이미지를 추가하는 함수 */
    suspend fun insertImage(imageName: String, uri: Uri): String

    /* 특정 이미지를 제거하는 함수 */
    suspend fun deleteImage(imageName: String)
}