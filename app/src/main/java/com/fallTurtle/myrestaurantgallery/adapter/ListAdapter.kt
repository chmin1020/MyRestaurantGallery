package com.fallTurtle.myrestaurantgallery.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.activity.RecordActivity
import com.fallTurtle.myrestaurantgallery.item.Piece
import java.util.ArrayList

class ListAdapter : RecyclerView.Adapter<ListAdapter.CustomViewHolder>() {
    //리사이클러뷰를 이루는 리스트 데이터를 저장하는 곳
    private var UfList: ArrayList<Piece>? = ArrayList()
    private var FList: ArrayList<Piece>? = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val v : View = LayoutInflater.from(parent.context).inflate(R.layout.list, parent, false)
        return CustomViewHolder(v)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.ivImage.setImageBitmap(FList?.get(position)?.getImage())
        holder.tvName.text = FList?.get(position)?.getName()
        holder.tvGenre.text = FList?.get(position)?.getGenre()
        holder.tvRate.text = FList?.get(position)?.getRate().toString()

        holder.itemView.setOnClickListener { v ->
            val record = Intent(v.context, RecordActivity::class.java)
            record.putExtra("dbID", FList?.get(position)?.getDBID())
            record.putExtra("name", FList?.get(position)?.getName())
            record.putExtra("genre", FList?.get(position)?.getGenre())
            record.putExtra("rate", FList?.get(position)?.getRate())
            record.putExtra("imgUsed", FList?.get(position)?.getImgUsed())
            record.putExtra("date", FList?.get(position)?.getDate())
            record.putExtra("location", FList?.get(position)?.getLocation())
            record.putExtra("memo", FList?.get(position)?.getMemo())
            v.context.startActivity(record)
        }
    }

    override fun getItemCount(): Int {
        if(FList == null) return 0
        else return FList!!.size
    }

    fun update(item : ArrayList<Piece>?){
        this.FList = item
        this.UfList = item
        notifyDataSetChanged()
    }

    class CustomViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var ivImage:ImageView = itemView.findViewById(R.id.iv_image)
        var tvName:TextView = itemView.findViewById(R.id.tv_name)
        var tvGenre:TextView = itemView.findViewById(R.id.tv_genre)
        var tvRate:TextView = itemView.findViewById(R.id.tv_rate)
    }
}