package com.fallTurtle.myrestaurantgallery.repository

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fallTurtle.myrestaurantgallery.model.firebase.FirebaseUtils
import com.fallTurtle.myrestaurantgallery.model.room.Info
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Firebase 유저 관련 기능을 수행하는 리포지토리.
 * 현재 유저(FirebaseUser)에 대한 정보에 대해 로그인(update), 로그아웃, 탈퇴 기능을 수행한다.
 **/
class FirebaseUserRepository {
    private val isUserExist: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    /* 유저 로그인 상태 확인 함수 */
    fun getUserState(): LiveData<Boolean> = isUserExist

    /* 로그인을 위해 서버에 보낼 옵션 값을 반환하는 함수 */
    fun getOptionForLogin(request: String): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(request).requestEmail().build()
    }

    /* 응답 결과로부터 토큰을 추출 시도하는 함수 */
    fun getTokenForLogin(result: Intent?): String?{
        return try {
            //받은 결과의 아이디 토큰을 통해 파이어베이스 인증 시도
            val task = GoogleSignIn.getSignedInAccountFromIntent(result)
            val account = task.result ?: throw NullPointerException()
            account.idToken ?: throw NullPointerException()
        }
        catch (e: Exception) { null }
    }

    /* 토큰으로 인증을 통한 최종 로그인을 시도하는 함수 */
    fun finalLoginWithCredential(idToken: String){
        //Token 보내서 credential 받고 인증 시도 (성공 시 job 실행)
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseUtils.getAuth().signInWithCredential(credential).addOnCompleteListener { updateUser() }
    }


    /* 현재 유저 정보를 새롭게 갱신하는 함수 */
    fun updateUser(){
        FirebaseUtils.updateUser()
            .also { isUserExist.postValue(FirebaseUtils.getUser() != null) }
    }

    /* 앱에서 로그아웃하는 함수 */
    suspend fun logoutUser() {
        withContext(Dispatchers.IO){ FirebaseUtils.getAuth().signOut() }
    }

    /* 앱에서 사용자 탈퇴하는 함수 */
    suspend fun withDrawUser(deletingItems: List<Info>?){
        withContext(Dispatchers.IO) {
            //현재 유저의 저장 데이터 각각 제거(Firestore 특성 상)
            deletingItems?.forEach {
                FirebaseUtils.getStoreRef().collection("restaurants").document(it.dbID).delete()
                it.image?.let { path -> FirebaseUtils.getStorageRef().child(path).delete() }
            }

            //현재 유저의 저장 데이터를 담은 레퍼런스들을 제거
            FirebaseUtils.getStoreRef().delete()
            FirebaseUtils.getStorageRef().delete()

            //유저를 파이어베이스 시스템 내부에서 삭제
            FirebaseUtils.getUser()?.delete()?.addOnCompleteListener { task ->
                if (task.isSuccessful) FirebaseUtils.getAuth().signOut()
            }
        }
    }
}