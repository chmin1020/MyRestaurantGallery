package com.fallTurtle.myrestaurantgallery.repository.item

import android.app.Application
import android.net.Uri
import com.fallTurtle.myrestaurantgallery.model.room.Info
import com.fallTurtle.myrestaurantgallery.repository.item.data.DataRepository
import com.fallTurtle.myrestaurantgallery.repository.item.data.FireStoreRepository
import com.fallTurtle.myrestaurantgallery.repository.item.data.RoomRepository
import com.fallTurtle.myrestaurantgallery.repository.item.image.ImageRepository
import com.fallTurtle.myrestaurantgallery.repository.item.image.StorageRepository

class ItemRepository(application: Application) {
    //data 리포지토리
    private val remoteDataRepository:DataRepository = FireStoreRepository()
    private val localDataRepository:DataRepository = RoomRepository(application)

    //image 리포지토리 (용량과 저장 시간 등의 문제로 이미지는 외부 저장만)
    private val remoteImageRepository:ImageRepository = StorageRepository()

    /* roomDB 내에서 리스트를 가져오는 작업을 정의한 함수 */
    suspend fun getAllItems() = localDataRepository.getAllData()

    suspend fun getProperItem(id: String) = localDataRepository.getProperData(id)

    /* Firestore 데이터를 불러와서 로컬 저장 공간에 전체 삽입하는 함수 */
    suspend fun restorePreviousItem(){
        remoteDataRepository.getAllData().forEach {
            localDataRepository.insertData(it)
        }
    }

    /* roomDB 내의 모든 데이터를 삭제하는 함수 (로그아웃, 탈퇴 시 실행) */
    suspend fun localItemClear(){
        localDataRepository.clearData()
    }

    suspend fun remoteItemClear(){
        remoteDataRepository.clearData()
        localDataRepository.clearData()

        remoteImageRepository.clearImages()
    }

    /* 아이템 삽입 이벤트를 정의한 함수 */
    suspend fun itemInsert(item: Info, uri: Uri?, preImageName: String?) {
        //이미지가 바뀌었다면 기존 이미지 제거, 현재 이미지 추가
        preImageName?.let { if(it != item.imageName) { remoteImageRepository.deleteImage(it) } }

        item.imageName?.let { name -> uri?.let{ item.imagePath = remoteImageRepository.insertImage(name, it) } }

        remoteDataRepository.insertData(item)
        localDataRepository.insertData(item)
    }

    /* 아이템 삭제 이벤트를 정의한 함수 */
    suspend fun itemDelete(itemId: String) {
        val targetItem = localDataRepository.getProperData(itemId)

        remoteDataRepository.deleteData(targetItem)
        localDataRepository.deleteData(targetItem)

        targetItem.imageName?.let { remoteImageRepository.deleteImage(it) }
    }

}