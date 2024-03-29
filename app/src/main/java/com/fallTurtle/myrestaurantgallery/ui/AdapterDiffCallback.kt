package com.fallTurtle.myrestaurantgallery.ui

import androidx.recyclerview.widget.DiffUtil


/**
 * recyclerView Adapter 공용 DiffUtil.Callback 추상 클래스.
 * 사용 이유는 자연스러운 리스트 실시간 갱신을 위함.
 * 단, 두 개의 아이템이 같은 것인지 확인하는 방법은 각자 다르므로 이를 미완성으로 둔다.
 */
abstract class AdapterDiffCallback<T>
    (private val oldItems: List<T>, private val newItems: List<T>): DiffUtil.Callback() {
    //기존과 새 아이템 리스트 크기 구하는 함수들
    override fun getOldListSize() = oldItems.size
    override fun getNewListSize() = newItems.size

    //areItemsTheSame -> true 나올 시 내부 내용도 같은지 확인
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean
            = oldItems[oldItemPosition] == newItems[newItemPosition]
}