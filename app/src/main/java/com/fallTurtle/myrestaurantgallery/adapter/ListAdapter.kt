package com.fallTurtle.myrestaurantgallery.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.item.Piece
import java.util.ArrayList

class ListAdapter : RecyclerView.Adapter<ListAdapter.CustomViewHolder>() {
    //리사이클러뷰를 이루는 리스트 데이터를 저장하는 곳
    private var UfList: List<Piece>? = ArrayList()
    private var FList: List<Piece>? = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val v : View = LayoutInflater.from(parent.context).inflate(R.layout.list, parent, false)
        return CustomViewHolder(v)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.ivImage.setImageBitmap(FList?.get(position)?.getImage())
        holder.tvName.text = FList?.get(position)?.getName()
        holder.tvGenre.text = FList?.get(position)?.getGenre()
        holder.tvRate.text = FList?.get(position)?.getRate().toString()
    }

    override fun getItemCount(): Int {
        if(FList == null) return 0
        else return FList!!.size
    }

    class CustomViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var ivImage:ImageView = itemView.findViewById(R.id.iv_image)
        var tvName:TextView = itemView.findViewById(R.id.tv_name)
        var tvGenre:TextView = itemView.findViewById(R.id.tv_genre)
        var tvRate:TextView = itemView.findViewById(R.id.tv_rate)
    }
}