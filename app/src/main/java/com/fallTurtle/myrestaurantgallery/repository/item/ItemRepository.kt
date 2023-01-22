package com.fallTurtle.myrestaurantgallery.repository.item

import android.app.Application
import android.net.Uri
import com.fallTurtle.myrestaurantgallery.model.room.Info
import com.fallTurtle.myrestaurantgallery.repository.item.data.DataRepository
import com.fallTurtle.myrestaurantgallery.repository.item.image.ImageRepository
import com.fallTurtle.myrestaurantgallery.repository.item.image.StorageRepository

class ItemRepository(application: Application) {
    //firebase data 리포지토리
    private val localDataRepository = DataRepository(application)

    private val remoteImageRepository:ImageRepository = StorageRepository()

    /* roomDB 내에서 리스트를 가져오는 작업을 정의한 함수 */
    suspend fun getAllItems() = localDataRepository.getAllItems()

    suspend fun getProperItem(id: String) = localDataRepository.getProperData(id)

    /* Firestore 데이터를 불러와서 로컬 저장 공간에 전체 삽입하는 함수 */
    suspend fun restorePreviousItem(){
        localDataRepository.restoreLocalData()
    }

    /* roomDB 내의 모든 데이터를 삭제하는 함수 (로그아웃, 탈퇴 시 실행) */
    suspend fun localItemClear(){
        localDataRepository.clearLocalData()
    }

    suspend fun remoteItemClear(){
        val deletingItems = localDataRepository.getAllItems()
        localDataRepository.clearRemoteData(deletingItems.map { it.dbID })
        remoteImageRepository.clearImages()
    }

    /* 아이템 삽입 이벤트를 정의한 함수 */
    suspend fun itemInsert(item: Info, uri: Uri?, preImageName: String?) {
        //이미지가 바뀌었다면 기존 이미지 제거, 현재 이미지 추가
        preImageName?.let { if(it != item.imageName) { remoteImageRepository.deleteImage(it) } }

        item.imageName?.let { name -> uri?.let{ item.imagePath = remoteImageRepository.insertImage(name, it) } }

        localDataRepository.insertData(item)
    }

    /* 아이템 삭제 이벤트를 정의한 함수 */
    suspend fun itemDelete(itemId: String) {
        val targetItem = localDataRepository.getProperData(itemId)
        localDataRepository.deleteData(targetItem)

        targetItem.imageName?.let { remoteImageRepository.deleteImage(it) }
    }

}