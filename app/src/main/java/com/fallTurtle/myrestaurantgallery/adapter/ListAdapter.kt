package com.fallTurtle.myrestaurantgallery.adapter

import android.app.Activity
import android.content.Intent
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.activity.RecordActivity
import com.fallTurtle.myrestaurantgallery.item.Piece
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.ArrayList

class ListAdapter : RecyclerView.Adapter<ListAdapter.CustomViewHolder>() {
    //리사이클러뷰를 이루는 리스트 데이터를 저장하는 곳
    private var UfList: ArrayList<Piece>? = ArrayList()
    private var FList: ArrayList<Piece>? = ArrayList()

    private val db = Firebase.firestore
    private val docRef = db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.email.toString())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val v : View = LayoutInflater.from(parent.context).inflate(R.layout.list, parent, false)
        return CustomViewHolder(v)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        //그리드 뷰 크기 조정
        val displayMetrics = DisplayMetrics()
        holder.itemView.context.display!!.getRealMetrics(displayMetrics)

        holder.itemView.layoutParams.width = (displayMetrics.widthPixels)/7 * 3
        holder.itemView.layoutParams.height = (holder.itemView.layoutParams.width)/6 * 5
        holder.itemView.requestLayout()

        //뷰 항목 채우기
        if(FList?.get(position)?.getImgUsed() == false){
            when(FList?.get(position)?.getGenreNum()) {
                0 -> holder.ivImage.setImageResource(R.drawable.korean_food)
                1 -> holder.ivImage.setImageResource(R.drawable.chinese_food)
                2 -> holder.ivImage.setImageResource(R.drawable.japanese_food)
                3 -> holder.ivImage.setImageResource(R.drawable.western_food)
                4 -> holder.ivImage.setImageResource(R.drawable.coffee_and_drink)
                5 -> holder.ivImage.setImageResource(R.drawable.drink)
                6 -> holder.ivImage.setImageResource(R.drawable.etc)
            }
        }
        holder.tvName.text = FList?.get(position)?.getName()
        holder.tvGenre.text = FList?.get(position)?.getGenre()
        holder.tvRate.text = FList?.get(position)?.getRate().toString()

        //항목 세부 내용 이동
        holder.itemView.setOnClickListener { v ->
            val record = Intent(v.context, RecordActivity::class.java)
            record.putExtra("dbID", FList?.get(position)?.getDBID())
            record.putExtra("name", FList?.get(position)?.getName())
            record.putExtra("genreNum", FList?.get(position)?.getGenreNum())
            record.putExtra("genre", FList?.get(position)?.getGenre())
            record.putExtra("rate", FList?.get(position)?.getRate())
            record.putExtra("imgUsed", FList?.get(position)?.getImgUsed())
            record.putExtra("location", FList?.get(position)?.getLocation())
            record.putExtra("memo", FList?.get(position)?.getMemo())
            v.context.startActivity(record)
        }
        //길게 누를 시 삭제 질의
        holder.itemView.setOnLongClickListener{ v->
            AlertDialog.Builder(v.context)
                .setMessage(R.string.delete_message)
                .setPositiveButton(R.string.yes) {dialog, which ->
                    docRef.collection("restaurants").document(FList?.get(position)?.getDBID().toString()).delete()
                    Toast.makeText(v.context, R.string.delete_complete, Toast.LENGTH_SHORT).show()
                    update(FList)
                }
                .setNegativeButton(R.string.no) {dialog, which -> }
                .show()
            return@setOnLongClickListener true
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