package com.fallTurtle.myrestaurantgallery.ui.locationList

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fallTurtle.myrestaurantgallery.ui.map.MapActivity
import com.fallTurtle.myrestaurantgallery.databinding.ListItemLocationBinding
import com.fallTurtle.myrestaurantgallery.data.etc.LATITUDE
import com.fallTurtle.myrestaurantgallery.data.etc.LONGITUDE
import com.fallTurtle.myrestaurantgallery.data.etc.UNDECIDED_LOCATION
import com.fallTurtle.myrestaurantgallery.data.etc.RESTAURANT_NAME
import com.fallTurtle.myrestaurantgallery.data.retrofit.value_object.LocationInfo
import com.fallTurtle.myrestaurantgallery.ui.AdapterDiffCallback

/**
 * 위치 정보 검색 결과에 대한 리스트를 위한 recyclerView 전용 어댑터
 **/
class LocationAdapter: RecyclerView.Adapter<LocationAdapter.CustomViewHolder>(){
    //리사이클러뷰를 이루는 리스트 데이터를 저장하는 컬렉션
    private val itemList: MutableList<LocationInfo> = mutableListOf()

    //검색할 페이지와 키워드
    var currentPage = 1
        private set
    var currentKeyword = ""
        private set

    //검색해도 더 나오는 내용이 없는지 여부 (검색 끝)
    var isEnd = false
        private set


    //--------------------------------------------
    // 리사이클러뷰 필수 오버라이딩 함수 영역

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding = ListItemLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount() = itemList.size


    //--------------------------------------------
    // 함수 영역

    /* 새로운 검색을 위한 설정 초기화 */
    fun searchSettingReset(keyword: String){
        currentPage = 1 //페이지 초기화
        currentKeyword = keyword //현재 검색 키워드 백업
        isEnd = false //리스트 검색 활동 초기화
    }

    /* 연속 검색을 위한 페이지 증가 */
    fun readyToNextPage(){
        currentPage++
    }

    /* 리스트 갱신 함수 */
    fun update(items: List<LocationInfo>?) {
        if(items?.size == itemCount) isEnd = true

        items?.let{
            //diff 연산
            val diffResult = DiffUtil.calculateDiff(DiffUtilCallback(this.itemList, it))

            //결과에 따라 리스트 갱신
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
    class CustomViewHolder(private val binding: ListItemLocationBinding) : RecyclerView.ViewHolder(binding.root){
        //초기화 블록 (리스너 설정)
        init {
            itemView.setOnClickListener {
                //뷰홀더가 가진 데이터를 인텐트로 보내며 해당 액티비티 종료
                (itemView.context as Activity).apply {
                    val backTo = Intent(this, MapActivity::class.java).apply {
                        putExtra(RESTAURANT_NAME, binding.locationResult?.name)
                        putExtra(LATITUDE, binding.locationResult?.locationPair?.latitude ?: UNDECIDED_LOCATION)
                        putExtra(LONGITUDE, binding.locationResult?.locationPair?.longitude ?: UNDECIDED_LOCATION)
                    }
                    this.setResult(AppCompatActivity.RESULT_OK, backTo)
                    this.finish()
                }
            }
        }

        //새로운 아이템 데이터와 뷰홀더를 바인드하는 함수
        fun bind(result: LocationInfo){
            binding.locationResult = result
        }
    }

    /* 내부 아이템 리스트 변경을 확인하고 적용할 callback 클래스 */
    private class DiffUtilCallback(private val oldItems: List<LocationInfo>, private val newItems: List<LocationInfo>)
        : AdapterDiffCallback<LocationInfo>(oldItems, newItems){
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int)
                = oldItems[oldItemPosition].locationPair == newItems[newItemPosition].locationPair
    }
}