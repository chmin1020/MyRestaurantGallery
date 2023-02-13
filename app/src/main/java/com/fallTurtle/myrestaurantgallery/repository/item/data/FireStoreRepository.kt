package com.fallTurtle.myrestaurantgallery.repository.item.data

import com.fallTurtle.myrestaurantgallery.model.firebase.FirebaseUtils
import com.fallTurtle.myrestaurantgallery.model.room.RestaurantInfo
import com.google.firebase.firestore.CollectionReference
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * FireStore API 기능을 통해 아이템 처리를 수행하는 리포지토리.
 **/
class FireStoreRepository: DataRepository {
    //파이어스토어에서 적절한 수행을 하기 위한 레퍼런스
    private fun collectionRef(): CollectionReference = FirebaseUtils.getStoreRef().collection("restaurants")


    //--------------------------------------------
    // 오버라이딩 영역

    /* 모든 데이터를 가져오는 함수 */
    override suspend fun getAllData(): List<RestaurantInfo> {
        return suspendCoroutine { continuation ->
            collectionRef().get().addOnCompleteListener {
                if(it.isSuccessful) continuation.resume(it.result.toObjects(RestaurantInfo::class.java))
                else continuation.resume(listOf())
            }
        }
    }

    /* 특정 데이터를 가져오는 함수 */
    override suspend fun getProperData(id: String): RestaurantInfo? {
        return suspendCoroutine { continuation ->
            collectionRef().document(id).get().addOnCompleteListener {
                if(it.isSuccessful) continuation.resume(it.result.toObject(RestaurantInfo::class.java))
                else continuation.resume(null)
            }
        }
    }

    /* 데이터를 모두 지우는 함수 */
    override suspend fun clearData() {
        getAllData().forEach { deleteData(it) }
    }

    /* 특정 데이터를 추가하는 함수 */
    override suspend fun insertData(data: RestaurantInfo) {
        suspendCoroutine<Any?> { continuation ->
            collectionRef().document(data.dbID).set(data).addOnCompleteListener { continuation.resume(null) }
        }
    }

    /* 특정 데이터를 갱신하는 함수 */
    override suspend fun updateData(data: RestaurantInfo) {
        suspendCoroutine<Any?> { continuation ->
            with(collectionRef().document(data.dbID)){
                update("category", data.category)
                update("categoryNum", data.categoryNum)
                update("date", data.date)
                update("imageName", data.imageName)
                update("imagePath", data.imagePath)
                update("latitude", data.latitude)
                update("longitude", data.longitude)
                update("memo", data.memo)
                update("name", data.name)
                update("rate", data.rate)
            }.addOnCompleteListener {
                continuation.resume(null)
            }
        }
    }

    /* 특정 데이터를 제거하는 함수 */
    override suspend fun deleteData(data: RestaurantInfo) {
        suspendCoroutine<Any?> { continuation ->
            collectionRef().document(data.dbID).delete().addOnCompleteListener { continuation.resume(null) }
        }
    }
}