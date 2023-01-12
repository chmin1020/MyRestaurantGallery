package com.fallTurtle.myrestaurantgallery.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.activity.MapActivity
import com.fallTurtle.myrestaurantgallery.model.etc.LocationResult

/**
 * 위치 정보 검색 결과에 대한 리스트를 위한 recylcerView 전용 어댑터
 **/
class LocationAdapter(val context: Context): RecyclerView.Adapter<LocationAdapter.CustomViewHolder>(){
    //--------------------------------------------
    // 해당 어댑터에서 사용할 뷰홀더
    //

    class CustomViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        var tvSubTitle: TextView = itemView.findViewById(R.id.tv_subtitle)
        var tvCategory: TextView = itemView.findViewById(R.id.tv_category)
    }


    //--------------------------------------------
    // 프로퍼티 영역
    //

    private var resultList: List<LocationResult> = listOf()
    var currentPage = 1
    var isEnd = false
    var currentSearchString = ""


    //--------------------------------------------
    // 리사이클러뷰 필수 오버라이딩 함수 영역
    //

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomViewHolder {
        val v : View = LayoutInflater.from(parent.context).inflate(R.layout.map_list, parent, false)
        return CustomViewHolder(v)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.tvTitle.text = resultList[position].name
        holder.tvSubTitle.text = resultList[position].fullAddress
        holder.tvCategory.text = resultList[position].category

        holder.itemView.setOnClickListener {
            val activity:Activity = context as Activity
            val backTo = Intent(context, MapActivity::class.java).apply {
                putExtra("x", resultList[position].locationPair.latitude.toDouble())
                putExtra("y", resultList[position].locationPair.longitude.toDouble())
            }
            activity.setResult(AppCompatActivity.RESULT_OK, backTo)
            activity.finish()
        }
    }

    override fun getItemCount(): Int {
        return resultList.size
    }


    //--------------------------------------------
    // 내부 함수 영역
    //

    @SuppressLint("NotifyDataSetChanged")
    fun addList(searchResultList: List<LocationResult>) {
        this.resultList = this.resultList + searchResultList
        notifyDataSetChanged()
    }

    fun clearList(){
        resultList = listOf()
    }
}