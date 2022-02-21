package com.fallTurtle.myrestaurantgallery.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.item.LocationResult

class LocationAdapter: RecyclerView.Adapter<LocationAdapter.CustomViewHolder>(){
    private var resultList: List<LocationResult> = listOf()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LocationAdapter.CustomViewHolder {
        val v : View = LayoutInflater.from(parent.context).inflate(R.layout.list, parent, false)
        return LocationAdapter.CustomViewHolder(v)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onBindViewHolder(holder: LocationAdapter.CustomViewHolder, position: Int) {
        //그리드 뷰 크기 조정
        val displayMetrics = DisplayMetrics()
        holder.itemView.context.display!!.getRealMetrics(displayMetrics)
        holder.itemView.layoutParams.width = (displayMetrics.widthPixels)/7 * 3
        holder.itemView.layoutParams.height = (holder.itemView.layoutParams.width)/6 * 5
        holder.itemView.requestLayout()

        holder.tvTitle.text = resultList[position].getName()
        holder.tvSubTitle.text = resultList[position].getFullAddr()
    }

    override fun getItemCount(): Int {
        return resultList.size
    }

    class CustomViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        var tvSubTitle: TextView = itemView.findViewById(R.id.tv_subtitle)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(
        searchResultList: List<LocationResult>) {
        this.resultList = this.resultList + searchResultList
        notifyDataSetChanged()
    }

    fun clearList(){
        resultList = listOf()
    }
}