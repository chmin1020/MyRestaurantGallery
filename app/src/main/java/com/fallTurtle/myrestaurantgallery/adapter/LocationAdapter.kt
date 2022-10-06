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
import com.fallTurtle.myrestaurantgallery.item.LocationResult

class LocationAdapter(val context: Context): RecyclerView.Adapter<LocationAdapter.CustomViewHolder>(){
    private var resultList: List<LocationResult> = listOf()
    var currentPage = 1
    var isEnd = false
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
        holder.tvTitle.text = resultList[position].name
        holder.tvSubTitle.text = resultList[position].fullAddress
        holder.tvCategory.text = resultList[position].category

        holder.itemView.setOnClickListener {
            val activity:Activity = context as Activity
            val backTo = Intent(context, MapActivity::class.java).apply {
                putExtra("x", resultList[position].lp.latitude.toDouble())
                putExtra("y", resultList[position].lp.longitude.toDouble())
            }
            activity.setResult(AppCompatActivity.RESULT_OK, backTo)
            activity.finish()
        }
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
    fun setList(searchResultList: List<LocationResult>) {
        this.resultList = this.resultList + searchResultList
        notifyDataSetChanged()
    }

    fun clearList(){
        resultList = listOf()
    }
}