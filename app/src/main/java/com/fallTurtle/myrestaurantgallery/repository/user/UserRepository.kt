package com.fallTurtle.myrestaurantgallery.repository.user

import kotlinx.coroutines.flow.StateFlow

/**
 * 유저 리포지토리의 역할을 정의한 인터페이스.
 **/
interface UserRepository {
    /* 로그인이 된 상태인지 확인하는 함수 */
    fun getLoginCompleteAnswer(): StateFlow<Boolean>

    /* 로그인 함수 */
    suspend fun loginUser(idToken: String)

    /* 로그아웃 함수 */
    suspend fun logoutUser()

    /* 회원 탈퇴 함수 */
    suspend fun withDrawUser()
}