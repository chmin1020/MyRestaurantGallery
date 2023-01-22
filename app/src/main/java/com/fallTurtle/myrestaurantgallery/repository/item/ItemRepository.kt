package com.fallTurtle.myrestaurantgallery.repository.item

import android.app.Application
import android.net.Uri
import com.fallTurtle.myrestaurantgallery.model.room.Info
import com.fallTurtle.myrestaurantgallery.repository.item.data.DataRepository
import com.fallTurtle.myrestaurantgallery.repository.item.data.FireStoreRepository
import com.fallTurtle.myrestaurantgallery.repository.item.data.RoomRepository
import com.fallTurtle.myrestaurantgallery.repository.item.image.ImageRepository
import com.fallTurtle.myrestaurantgallery.repository.item.image.StorageRepository


/**
 * 아이템 리포지토리의 역할을 정의한 클래스.
 * 데이터와 이미지 리포지토리의 역할을 종합적으로 수행하는 파사드의 역할을 한다.
 **/
class ItemRepository(application: Application) {
    //data 리포지토리
    private val remoteDataRepository:DataRepository = FireStoreRepository()
    private val localDataRepository:DataRepository = RoomRepository(application)

    //image 리포지토리 (용량과 저장 시간 등의 문제로 이미지는 외부 저장만)
    private val remoteImageRepository:ImageRepository = StorageRepository()


    //-----------------------------------------
    // 비즈니스 로직 함수 영역

    /* 아이템 삽입 이벤트를 정의한 함수 */
    suspend fun itemInsert(item: Info, uri: Uri?, preImageName: String?) {
        //이미지가 바뀌었다면 기존 이미지 제거, 현재 이미지 추가
        preImageName?.let { if(it != item.imageName) { remoteImageRepository.deleteImage(it) } }
        item.imageName?.let { name -> uri?.let{ item.imagePath = remoteImageRepository.insertImage(name, it) } }

        //내 외부에 데이터 저장
        remoteDataRepository.insertData(item)
        localDataRepository.insertData(item)
    }

    /* 아이템 삭제 이벤트를 정의한 함수 */
    suspend fun itemDelete(itemId: String) {
        val targetItem = localDataRepository.getProperData(itemId)

        //id를 가진 아이템 데이터 내 외부 모두 제거
        remoteDataRepository.deleteData(targetItem)
        localDataRepository.deleteData(targetItem)

        //이미지가 있었다면 remote 이미지도 제거
        targetItem.imageName?.let { remoteImageRepository.deleteImage(it) }
    }

    /* 현재 저장된 아이템들을 모두 가져오는 함수 */
    suspend fun getAllItems() = localDataRepository.getAllData()

    /* 특정 id를 가진 아이템 하나를 가져오는 함수 */
    suspend fun getProperItem(id: String) = localDataRepository.getProperData(id)

    /* 외부 데이터를 불러와서 로컬 저장 공간에 전체 삽입(복원)하는 함수 */
    suspend fun restorePreviousItem(){
        remoteDataRepository.getAllData().forEach {
            localDataRepository.insertData(it)
        }
    }

    /* 내부의 모든 데이터를 삭제하는 함수 (로그아웃, 탈퇴 시 실행) */
    suspend fun localItemClear(){
        localDataRepository.clearData()
    }

    /* 내외부 모든 데이터를 삭제하는 함수 (탈퇴 시 실행) */
    suspend fun remoteItemClear(){
        //remote, local 모두 데이터 제거
        remoteDataRepository.clearData()
        localDataRepository.clearData()

        //remote 내 이미지도 모두 제거
        remoteImageRepository.clearImages()
    }
}