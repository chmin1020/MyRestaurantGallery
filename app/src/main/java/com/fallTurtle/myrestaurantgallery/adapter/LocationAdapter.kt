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
    var currentPage = 1
    var currentSearchString = ""

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomViewHolder {
        val v : View = LayoutInflater.from(parent.context).inflate(R.layout.map_list, parent, false)
        return CustomViewHolder(v)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.tvTitle.text = resultList[position].getName()
        holder.tvSubTitle.text = resultList[position].getFullAddr()
        holder.tvCategory.text = resultList[position].getCategory()
    }

    override fun getItemCount(): Int {
        return resultList.size
    }

    class CustomViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        var tvSubTitle: TextView = itemView.findViewById(R.id.tv_subtitle)
        var tvCategory: TextView = itemView.findViewById(R.id.tv_category)
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