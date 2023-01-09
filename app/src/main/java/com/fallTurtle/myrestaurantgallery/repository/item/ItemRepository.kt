package com.fallTurtle.myrestaurantgallery.repository.item

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import com.fallTurtle.myrestaurantgallery.model.room.Info
import com.fallTurtle.myrestaurantgallery.repository.item.data.DataRepository
import com.fallTurtle.myrestaurantgallery.repository.item.image.ImageRepository

class ItemRepository(application: Application) {
    //firebase data 리포지토리
    private val dataRepository = DataRepository(application)
    private val imageRepository = ImageRepository(application.contentResolver)

    /* Firestore 데이터를 불러와서 room 데이터베이스로 전체 삽입하는 함수 */
    fun restoreFirestoreDataToRoom(){
        dataRepository.restoreLocalData()
    }

    /* roomDB 내의 모든 데이터를 삭제하는 함수 (로그아웃, 탈퇴 시 실행) */
    fun itemClear(){
        dataRepository.clearLocalData()
    }

    /* roomDB 내에서 리스트를 가져오는 작업을 정의한 함수 */
    fun getItems(): LiveData<List<Info>> {
        return dataRepository.getSavedData()
    }

    /* 아이템 삽입 이벤트를 정의한 함수 */
    fun itemInsert(item: Info, uri: Uri?, preImagePath: String?) {
        dataRepository.insertData(item)

        //이미지가 바뀌었다면 기존 이미지 제거, 현재 이미지 추가
        preImagePath?.let { if(it != item.image) imageRepository.deleteImage(it)}
        item.image?.let { name -> uri?.let{ imageRepository.insertImage(name, it) } }
    }

    /* 아이템 삭제 이벤트를 정의한 함수 */
    fun itemDelete(item: Info) {
        dataRepository.deleteData(item)
        item.image?.let { imageRepository.deleteImage(it) }
    }

}