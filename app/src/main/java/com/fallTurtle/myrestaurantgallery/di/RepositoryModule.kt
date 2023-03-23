package com.fallTurtle.myrestaurantgallery.di

import com.fallTurtle.myrestaurantgallery.model.room.InfoRoomDao
import com.fallTurtle.myrestaurantgallery.repository.item.ItemRepository
import com.fallTurtle.myrestaurantgallery.repository.item.data.DataRepository
import com.fallTurtle.myrestaurantgallery.repository.item.data.FireStoreRepository
import com.fallTurtle.myrestaurantgallery.repository.item.data.RoomRepository
import com.fallTurtle.myrestaurantgallery.repository.item.image.ImageRepository
import com.fallTurtle.myrestaurantgallery.repository.item.image.StorageRepository
import com.fallTurtle.myrestaurantgallery.repository.location.LocationRepository
import com.fallTurtle.myrestaurantgallery.repository.location.RetrofitLocationRepository
import com.fallTurtle.myrestaurantgallery.repository.user.FirebaseUserRepository
import com.fallTurtle.myrestaurantgallery.repository.user.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    //----------------------------------
    // 유저 계열

    @Provides
    @FirebaseRepositoryForUser
    fun provideFirebaseUserRepository(): UserRepository {
        return FirebaseUserRepository()
    }


    //----------------------------------
    // 아이템 계열 (data, image)

    @Provides
    @RoomRepositoryForData
    fun provideRoomDataRepository(roomDao: InfoRoomDao) : DataRepository {
        return RoomRepository(roomDao)
    }

    @Provides
    @FireStoreRepositoryForData
    fun provideFireStoreRepository(): DataRepository{
        return FireStoreRepository()
    }

    @Provides
    @StorageRepositoryForImage
    fun provideStorageRepository(): ImageRepository{
        return StorageRepository()
    }

    @Provides
    fun provideItemRepository(
        @RoomRepositoryForData localDataRepository: DataRepository,
        @FireStoreRepositoryForData remoteDataRepository: DataRepository,
        @StorageRepositoryForImage remoteImageRepository: ImageRepository
    ): ItemRepository{
        return ItemRepository(localDataRepository, remoteDataRepository, remoteImageRepository)
    }


    //----------------------------------
    // 위치 정보 계열

    @Provides
    @RetrofitRepositoryForLocation
    fun provideRetrofitLocationRepository(): LocationRepository {
        return RetrofitLocationRepository()
    }
}