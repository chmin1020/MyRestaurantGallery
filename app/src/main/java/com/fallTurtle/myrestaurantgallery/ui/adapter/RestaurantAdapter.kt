package com.fallTurtle.myrestaurantgallery.ui.adapter

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fallTurtle.myrestaurantgallery.ui.record.RecordActivity
import com.fallTurtle.myrestaurantgallery.databinding.ListItemRestaurantBinding
import com.fallTurtle.myrestaurantgallery.data.etc.ITEM_ID
import com.fallTurtle.myrestaurantgallery.data.room.RestaurantInfo

/**
 * 각 맛집 데이터를 보여줄 리사이클러뷰를 위한 adapter
 **/
class RestaurantAdapter(windowWidth: Int) : RecyclerView.Adapter<RestaurantAdapter.CustomViewHolder>() {
    //각 뷰의 길이 (가로, 세로)
    private val holderWidth = windowWidth/9 * 4
    private val holderHeight = holderWidth/6 * 5

    //리사이클러뷰를 이루는 리스트 데이터를 저장하는 컬렉션
    private val itemList = mutableListOf<RestaurantInfo>()


    //--------------------------------------------
    // 리사이클러뷰 필수 오버라이딩 함수 영역

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding = ListItemRestaurantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int = itemList.size


    //--------------------------------------------
    // 함수 영역

    /* 리스트 내역을 갱신 */
    fun update(items : List<RestaurantInfo>?){
        items?.let {
            //diff 연산
            val diffResult = DiffUtil.calculateDiff(DiffUtilCallback(this.itemList, it))

            //결과에 따라 아이템 리스트 새롭게 갱신
            this.itemList.run {
                diffResult.dispatchUpdatesTo(this@RestaurantAdapter)
                clear()
                addAll(it)
            }
        }
    }


    //----------------------------
    //관련 클래스

    /* 해당 리사이클러뷰에서 사용하는 뷰홀더 클래스 */
    inner class CustomViewHolder(private val binding: ListItemRestaurantBinding) : RecyclerView.ViewHolder(binding.root) {
        //초기화 블록 (아이템뷰 크기 및 리스너 설정)
        init {
            //크기 설정
            itemView.layoutParams.width = holderWidth
            itemView.layoutParams.height = holderHeight

            //클릭 리스너 설정 (id에 따른 정보를 가지고 record 화면으로 넘어감)
            itemView.setOnClickListener { v ->
                Intent(v.context, RecordActivity::class.java).let {
                    it.putExtra(ITEM_ID, binding.info?.dbID)
                    val options = ActivityOptions.makeSceneTransitionAnimation(
                        v.context as Activity, binding.layoutImage, binding.layoutImage.transitionName)
                    v.context.startActivity(it, options.toBundle())
                }
            }
        }

        //새로운 아이템 데이터와 뷰홀더를 바인드하는 함수
        fun bind(item: RestaurantInfo){
            binding.info = item
        }
    }

    /* 내부 아이템 리스트 변경을 확인하고 적용할 callback 클래스 */
    private class DiffUtilCallback(private val oldItems: List<RestaurantInfo>, private val newItems: List<RestaurantInfo>)
        : AdapterDiffCallback<RestaurantInfo>(oldItems, newItems){
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int)
                = oldItems[oldItemPosition].dbID == newItems[newItemPosition].dbID
    }
}