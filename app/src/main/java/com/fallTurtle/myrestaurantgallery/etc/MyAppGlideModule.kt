package com.fallTurtle.myrestaurantgallery.etc

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.fallTurtle.myrestaurantgallery.R
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.storage.StorageReference
import java.io.InputStream

/**
 * 네트워크를 통해 Firebase 내 저장된 이미지를 가져오기 때문에 imageView 만으로는 어렵다.
 * 따라서 Glide 라이브러리를 통해 이미지 로딩을 구현한다.
 * Glide 커스텀 사용을 위해서는 이 앱에서 사용할 전용 모듈을 구현해야 한다.
 */
@GlideModule
class MyAppGlideModule : AppGlideModule() {

    /* glide 모듈을 커스텀으로 만들어서 등록하는 함수 */
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        //순서대로 model class, data class, factory
        registry.append(
            StorageReference::class.java, InputStream::class.java,
            FirebaseImageLoader.Factory()
        )
    }

    /* GlideApp 기본 설정을 지정하는 함수 */
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)

        //이미지를 불러오고 있는 동안은 loading_food 이미지를 출력
        builder.setDefaultRequestOptions(
            RequestOptions().placeholder(R.drawable.loading_food)
        )
    }

    /* 매니페스트 파싱 여부를 묻는 함수, AppGlideModule 구현을 했으므로 비활성화 */
    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}