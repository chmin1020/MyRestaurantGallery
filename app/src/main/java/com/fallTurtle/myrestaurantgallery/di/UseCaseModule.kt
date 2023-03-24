package com.fallTurtle.myrestaurantgallery.di

import com.fallTurtle.myrestaurantgallery.data.repository.item.ItemRepository
import com.fallTurtle.myrestaurantgallery.data.repository.location.LocationRepository
import com.fallTurtle.myrestaurantgallery.data.repository.user.UserRepository
import com.fallTurtle.myrestaurantgallery.usecase.item.*
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
    fun provideUserCheckUseCase(@FirebaseRepositoryForUser repository: UserRepository): UserCheckUseCase {
        return UserCheckUseCase(repository)
    }

    @Provides
    fun provideLoginUseCase(@FirebaseRepositoryForUser repository: UserRepository): LoginUseCase {
        return LoginUseCase(repository)
    }

    @Provides
    fun provideLogoutUseCase(
        @FirebaseRepositoryForUser userRepository: UserRepository,
        itemRepository: ItemRepository
    ): LogoutUseCase {
        return LogoutUseCase(userRepository, itemRepository)
    }

    @Provides
    fun provideWithdrawUseCase(
        @FirebaseRepositoryForUser userRepository: UserRepository,
        itemRepository: ItemRepository
    ): WithdrawUseCase {
        return WithdrawUseCase(userRepository, itemRepository)
    }


    //---------------------------------
    // 아이템 useCase

    @Provides
    fun provideItemAllSelectUseCase(repository: ItemRepository): ItemAllSelectUseCase {
        return ItemAllSelectUseCase(repository)
    }

    @Provides
    fun provideItemEachSelectUseCase(repository: ItemRepository): ItemEachSelectUseCase {
        return ItemEachSelectUseCase(repository)
    }

    @Provides
    fun provideItemInsertUseCase(repository: ItemRepository): ItemInsertUseCase {
        return ItemInsertUseCase(repository)
    }

    @Provides
    fun provideItemUpdateUseCase(repository: ItemRepository): ItemUpdateUseCase {
        return ItemUpdateUseCase(repository)
    }

    @Provides
    fun provideItemDeleteUseCase(repository: ItemRepository): ItemDeleteUseCase {
        return ItemDeleteUseCase(repository)
    }

    @Provides
    fun provideItemRestoreUseCase(repository: ItemRepository): ItemRestoreUseCase {
        return ItemRestoreUseCase(repository)
    }


    //---------------------------------
    // 위치 useCase

    @Provides
    fun provideLocationSearchUseCase(@RetrofitRepositoryForLocation repository: LocationRepository): LocationSearchUseCase {
        return LocationSearchUseCase(repository)
    }
}