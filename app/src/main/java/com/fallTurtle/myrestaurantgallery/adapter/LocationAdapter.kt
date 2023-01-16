package com.fallTurtle.myrestaurantgallery.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fallTurtle.myrestaurantgallery.activity.MapActivity
import com.fallTurtle.myrestaurantgallery.databinding.MapListBinding
import com.fallTurtle.myrestaurantgallery.model.etc.LocationResult

/**
 * 위치 정보 검색 결과에 대한 리스트를 위한 recyclerView 전용 어댑터
 **/
class LocationAdapter: RecyclerView.Adapter<LocationAdapter.CustomViewHolder>(){
    private val itemList: MutableList<LocationResult> = mutableListOf()

    var currentPage = 1
        private set
    var isEnd = false
        private set
    var currentKeyword = ""
        private set

    //--------------------------------------------
    // 리사이클러뷰 필수 오버라이딩 함수 영역

    /* 새로운 뷰홀더 만들어질 때 실행되는 callback 함수 */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding = MapListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }

    /* 뷰홀더와 새 항목(position 확인)을 연결할 때 실행되는 callback 함수 */
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    /* 리스트 생성을 위해 아이템 개수를 파악할 때 실행되는 callback 함수 */
    override fun getItemCount() = itemList.size


    //--------------------------------------------
    // 내부 함수 영역

    fun searchSettingReset(keyword: String){
        currentPage = 1 //페이지 초기화
        currentKeyword = keyword //현재 검색 키워드 백업
        isEnd = false //리스트 검색 활동 초기화
    }

    fun readyToNextPage(){
        currentPage++
    }

    fun update(items: List<LocationResult>?) {
        if(items?.size == itemCount) isEnd = true

        items?.let{
            //기존 리스트와 새 리스트 차이점을 파악하기 위한 diff 연산
            val diffResult = DiffUtil.calculateDiff(DiffUtilCallback(this.itemList, it))

            //결과에 따라 아이템 리스트 새롭게 갱신
            this.itemList.run {
                diffResult.dispatchUpdatesTo(this@LocationAdapter)
                clear()
                addAll(it)
            }
        }
    }


    //----------------------------
    //관련 클래스


    /* 해당 리사이클러뷰에서 사용하는 뷰홀더 클래스 */
    class CustomViewHolder(private val binding: MapListBinding) : RecyclerView.ViewHolder(binding.root){
        //초기화 블록 (리스너 설정)
        init {
            itemView.setOnClickListener {
                //뷰홀더가 가진 데이터를 인텐트로 보내며 해당 액티비티 종료
                (itemView.context as Activity).apply {
                    val backTo = Intent(this, MapActivity::class.java).apply {
                        putExtra("x", binding.locationResult?.locationPair?.latitude ?: -1.0)
                        putExtra("y", binding.locationResult?.locationPair?.longitude ?: -1.0)
                    }
                    this.setResult(AppCompatActivity.RESULT_OK, backTo)
                    this.finish()
                }
            }
        }

        //새로운 아이템 데이터와 뷰홀더를 바인드하는 함수
        fun bind(result: LocationResult){
            binding.locationResult = result
        }
    }

    /* 내부 아이템 리스트 변경을 확인하고 적용할 callback 클래스 */
    private class DiffUtilCallback
        (private val oldItems: List<LocationResult>, private val newItems: List<LocationResult>): DiffUtil.Callback(){
        //기존과 새 아이템 리스트 크기 구하는 함수들
        override fun getOldListSize() = oldItems.size
        override fun getNewListSize() = newItems.size

        //각 인덱스에 맞는 아이템이 서로 같은 아이템인지 확인하는 함수
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int)
                = oldItems[oldItemPosition].locationPair == newItems[newItemPosition].locationPair

        //areItemsTheSame -> true 나올 시 내부 내용도 같은지 확인하는 함수
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean
                = oldItems[oldItemPosition] == newItems[newItemPosition]
    }
}