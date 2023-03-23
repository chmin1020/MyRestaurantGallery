package com.fallTurtle.myrestaurantgallery.di

import com.fallTurtle.myrestaurantgallery.repository.item.ItemRepository
import com.fallTurtle.myrestaurantgallery.repository.location.LocationRepository
import com.fallTurtle.myrestaurantgallery.repository.user.UserRepository
import com.fallTurtle.myrestaurantgallery.usecase.item.ItemRestoreUseCase
import com.fallTurtle.myrestaurantgallery.usecase.location_search.LocationSearchUseCase
import com.fallTurtle.myrestaurantgallery.usecase.user.LoginUseCase
import com.fallTurtle.myrestaurantgallery.usecase.user.LogoutUseCase
import com.fallTurtle.myrestaurantgallery.usecase.user.UserCheckUseCase
import com.fallTurtle.myrestaurantgallery.usecase.user.WithdrawUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Created by 최제민 on 2023-03-23.
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    //---------------------------------
    // 유저 useCase

    @Provides
    fun provideUserCheckUseCase(@FirebaseRepositoryForUser repository: UserRepository): UserCheckUseCase{
        return UserCheckUseCase(repository)
    }

    @Provides
    fun provideLoginUseCase(@FirebaseRepositoryForUser repository: UserRepository):LoginUseCase{
        return LoginUseCase(repository)
    }

    @Provides
    fun provideLogoutUseCase(repository: UserRepository): LogoutUseCase {
        return LogoutUseCase()
    }

    @Provides
    fun provideWithdrawUseCase(repository: UserRepository):WithdrawUseCase{
        return WithdrawUseCase()
    }


    //---------------------------------
    // 아이템 useCase

    @Provides
    fun provideItemRestoreUseCase(repository: ItemRepository): ItemRestoreUseCase{
        return ItemRestoreUseCase(repository)
    }

    //---------------------------------
    // 위치 useCase

    @Provides
    fun provideLocationSearchUseCase
        (@RetrofitRepositoryForLocation repository: LocationRepository): LocationSearchUseCase{
        return LocationSearchUseCase(repository)
    }
}