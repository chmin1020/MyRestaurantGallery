package com.fallTurtle.myrestaurantgallery.model.etc

import android.widget.ImageView
import coil.api.load
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.model.firebase.FirebaseHandler
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File

abstract class ImageHandler {
    companion object {
        /* 이미지를 캐시에 저장하는 함수 */
        fun saveOnCache(){

        }

        /* 이미지 관련 정보를 얻는 함수 */
        fun getImageInfo(path: File, fid: String): Pair<Boolean, File>{
            var isExist = false
            val localFile = File(path, fid)

            if(localFile.exists())
                isExist = true
            else
                FirebaseHandler.getStorageRef().child(fid).getFile(localFile).addOnSuccessListener { isExist = true }

            return Pair(isExist, localFile)
        }

        /* 가져온 파일 정보를 통해서 이미지뷰에 로드하는 함수 */
        fun loadImage(info: Pair<Boolean, File>, realRef: StorageReference, view:ImageView){
            if(info.first) {
                MainScope().launch {
                    view.load(info.second) {
                        crossfade(true)
                        placeholder(R.drawable.loading_food)
                    }
                }
            }
            else {
                realRef.downloadUrl.addOnCompleteListener { task ->
                    MainScope().launch {
                        view.load(task.result) {
                            crossfade(true)
                            placeholder(R.drawable.loading_food)
                        }
                    }
                }.addOnFailureListener {
                    MainScope().launch { view.load(R.drawable.loading_food) }
                }
            }
        }
    }
}