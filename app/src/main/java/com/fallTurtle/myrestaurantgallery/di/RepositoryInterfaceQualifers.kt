package com.fallTurtle.myrestaurantgallery.di

import javax.inject.Qualifier

/**
 * Created by 최제민 on 2023-03-23.
 */

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FireStoreRepositoryForData

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RoomRepositoryForData

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class StorageRepositoryForImage

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitRepositoryForLocation

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FirebaseRepositoryForUser




