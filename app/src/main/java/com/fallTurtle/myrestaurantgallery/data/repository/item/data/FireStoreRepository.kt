package com.fallTurtle.myrestaurantgallery.data.repository.item.data

import com.fallTurtle.myrestaurantgallery.data.etc.*
import com.fallTurtle.myrestaurantgallery.data.firebase.FirebaseUtils
import com.fallTurtle.myrestaurantgallery.data.room.RestaurantInfo
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * FireStore API 기능을 통해 아이템 처리를 수행하는 리포지토리.
 **/
class FireStoreRepository : DataRepository {
    //파이어스토어에서 적절한 수행을 하기 위한 레퍼런스
    private fun collectionRef(): CollectionReference? =
        FirebaseUtils.getStoreRef()?.collection(REF_PATH_RESTAURANTS)


    //--------------------------------------------
    // 오버라이딩 영역

    /* 모든 데이터를 가져오는 함수 */
    override suspend fun getAllData(): List<RestaurantInfo> {
        val snapshot = collectionRef()?.get()?.await()
        return snapshot?.toObjects(RestaurantInfo::class.java) ?: listOf()
    }

    /* 특정 데이터를 가져오는 함수 */
    override suspend fun getProperData(id: String): RestaurantInfo? {
        val snapshot = collectionRef()?.document(id)?.get()?.await()
        return snapshot?.toObject(RestaurantInfo::class.java)
    }

    /* 데이터를 모두 지우는 함수 */
    override suspend fun clearData() {
        getAllData().forEach { deleteData(it) }
    }

    /* 특정 데이터를 추가하는 함수 */
    override suspend fun insertData(data: RestaurantInfo) {
        collectionRef()?.document(data.dbID)?.set(data)?.await()
    }

    /* 특정 데이터를 갱신하는 함수 */
    override suspend fun updateData(data: RestaurantInfo) {
        collectionRef()?.let {
            with(it.document(data.dbID)) {
                update(FIELD_CATEGORY, data.category).await()
                update(FIELD_CATEGORY_NUM, data.categoryNum).await()
                update(FIELD_DATE, data.date).await()
                update(FIELD_IMAGE_NAME, data.imageName).await()
                update(FIELD_IMAGE_PATH, data.imagePath).await()
                update(FIELD_LATITUDE, data.latitude).await()
                update(FIELD_LONGITUDE, data.longitude).await()
                update(FIELD_MEMO, data.memo).await()
                update(FIELD_NAME, data.name).await()
                update(FIELD_RATE, data.rate).await()
            }
        }
    }

    /* 특정 데이터를 제거하는 함수 */
    override suspend fun deleteData(data: RestaurantInfo) {
        collectionRef()?.document(data.dbID)?.delete()?.await()
    }
}