package com.fallTurtle.myrestaurantgallery.data.repository.item

import android.media.Image
import android.net.Uri
import com.fallTurtle.myrestaurantgallery.data.room.RestaurantInfo
import com.fallTurtle.myrestaurantgallery.data.repository.item.data.DataRepository
import com.fallTurtle.myrestaurantgallery.data.repository.item.image.ImageRepository
import javax.inject.Inject

/**
 * 아이템 리포지토리의 역할을 정의한 클래스.
 * 데이터와 이미지 리포지토리의 역할을 종합적으로 수행하는 파사드의 역할을 한다.
 **/
class ItemRepository @Inject constructor(
    private val localDataRepository: DataRepository,
    private val remoteDataRepository: DataRepository,
    private val remoteImageRepository: ImageRepository
) {
    /* 아이템 삽입 이벤트를 정의한 함수 */
    suspend fun itemInsert(item: RestaurantInfo, uri: Uri?) {
        item.imageName?.let { name -> uri?.let{ item.imagePath = remoteImageRepository.insertImage(name, it) } }

        remoteDataRepository.insertData(item)
        localDataRepository.insertData(item)
    }

    /* 아이템 갱신 이벤트를 정의한 함수 */
    suspend fun itemUpdate(item: RestaurantInfo, uri: Uri?, preImageName: String?){
        preImageName?.let { if(it != item.imageName) { remoteImageRepository.deleteImage(it) } }
        item.imageName?.let { name -> uri?.let{ item.imagePath = remoteImageRepository.insertImage(name, it) } }

        remoteDataRepository.updateData(item)
        localDataRepository.updateData(item)
    }

    /* 아이템 삭제 이벤트를 정의한 함수 */
    suspend fun itemDelete(itemId: String) {
        val targetItem = localDataRepository.getProperData(itemId)

        //id를 가진 아이템 데이터 내 외부 모두 제거
        targetItem?.let {
            remoteDataRepository.deleteData(it)
            localDataRepository.deleteData(it)
        }

        //이미지가 있었다면 remote 이미지도 제거
        targetItem?.imageName?.let { remoteImageRepository.deleteImage(it) }
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