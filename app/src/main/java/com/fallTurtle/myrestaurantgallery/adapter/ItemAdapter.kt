package com.fallTurtle.myrestaurantgallery.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fallTurtle.myrestaurantgallery.activity.RecordActivity
import com.fallTurtle.myrestaurantgallery.databinding.EachItemBinding
import com.fallTurtle.myrestaurantgallery.etc.Configurations
import com.fallTurtle.myrestaurantgallery.model.room.RestaurantInfo

/**
 * 각 맛집 데이터를 보여줄 리사이클러뷰를 위한 adapter
 **/
class ItemAdapter(windowWidth: Int) : RecyclerView.Adapter<ItemAdapter.CustomViewHolder>() {
    //각 아이템뷰의 길이 (가로, 세로)
    private val holderWidth = windowWidth/7 * 3
    private val holderHeight = holderWidth/6 * 5

    //리사이클러뷰를 이루는 리스트 데이터를 저장하는 컬렉션
    private val itemList = mutableListOf<RestaurantInfo>()


    //--------------------------------------------
    // 리사이클러뷰 필수 오버라이딩 함수 영역

    /* 새로운 뷰홀더 만들어질 때 실행되는 callback 함수 */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding = EachItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }

    /* 뷰홀더와 새 항목(position 확인)을 연결할 때 실행되는 callback 함수 */
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    /* 리스트 생성을 위해 아이템 개수를 파악할 때 실행되는 callback 함수 */
    override fun getItemCount(): Int = itemList.size


    //--------------------------------------------
    // 함수 영역

    /* 리스트 내역을 새롭게 갱신하는 함수 */
    fun update(items : List<RestaurantInfo>?){
        items?.let {
            //기존 리스트와 새 리스트 차이점을 파악하기 위한 diff 연산
            val diffResult = DiffUtil.calculateDiff(DiffUtilCallback(this.itemList, it))

            //결과에 따라 아이템 리스트 새롭게 갱신
            this.itemList.run {
                diffResult.dispatchUpdatesTo(this@ItemAdapter)
                clear()
                addAll(it)
            }
        }
    }


    //----------------------------
    //관련 클래스

    /* 해당 리사이클러뷰에서 사용하는 뷰홀더 클래스 */
    inner class CustomViewHolder(private val binding: EachItemBinding) : RecyclerView.ViewHolder(binding.root) {
        //초기화 블록 (아이템뷰 크기 및 리스너 설정)
        init {
            //크기 설정
            itemView.layoutParams.width = holderWidth
            itemView.layoutParams.height = holderHeight

            //클릭 리스너 설정 (id에 따른 정보를 가지고 record 화면으로 넘어감)
            itemView.setOnClickListener { v ->
                Intent(v.context, RecordActivity::class.java).let {
                    it.putExtra(Configurations.ITEM_ID, binding.info?.dbID)
                    v.context.startActivity(it)
                }
            }
        }

        //새로운 아이템 데이터와 뷰홀더를 바인드하는 함수
        fun bind(item:RestaurantInfo){
            binding.info = item
        }
    }

    /* 내부 아이템 리스트 변경을 확인하고 적용할 callback 클래스 */
    private class DiffUtilCallback(private val oldItems: List<RestaurantInfo>, private val newItems: List<RestaurantInfo>)
        : AdapterDiffCallback<RestaurantInfo>(oldItems, newItems){
        //각 인덱스에 맞는 아이템이 서로 같은 아이템인지 확인하는 함수
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int)
                = oldItems[oldItemPosition].dbID == newItems[newItemPosition].dbID
    }
}