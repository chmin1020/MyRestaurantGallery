package com.fallTurtle.myrestaurantgallery.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.fallTurtle.myrestaurantgallery.etc.GlideApp
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.activity.RecordActivity
import com.fallTurtle.myrestaurantgallery.model.firebase.FirebaseHandler
import com.fallTurtle.myrestaurantgallery.model.firebase.Info
import java.util.ArrayList

/**
 * 각 맛집 데이터를 보여줄 리사이클러뷰를 위한 adapter
 **/
class ListAdapter : RecyclerView.Adapter<ListAdapter.CustomViewHolder>() {
    //--------------------------------------------
    // 해당 어댑터에서 사용할 뷰홀더
    //

    class CustomViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var ivImage:ImageView = itemView.findViewById(R.id.iv_image)
        var tvName:TextView = itemView.findViewById(R.id.tv_name)
        var tvGenre:TextView = itemView.findViewById(R.id.tv_genre)
        var tvRate:TextView = itemView.findViewById(R.id.tv_rate)
    }

    //--------------------------------------------
    // 프로퍼티 영역
    //

    //리사이클러뷰를 이루는 리스트 데이터를 저장하는 곳
    private var infoList: List<Info> = ArrayList()

    //Firebase
    private val docRef by lazy{ FirebaseHandler.getFirestoreRef() }
    private val strRef by lazy{ FirebaseHandler.getStorageRef() }


    //--------------------------------------------
    // 리사이클러뷰 필수 오버라이딩 함수 영역
    //

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val v : View = LayoutInflater.from(parent.context).inflate(R.layout.list, parent, false)
        return CustomViewHolder(v)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        //그리드 뷰 크기 조정
        val displayMetrics = DisplayMetrics()
        holder.itemView.context.display?.getRealMetrics(displayMetrics)
        holder.itemView.layoutParams.width = (displayMetrics.widthPixels)/7 * 3
        holder.itemView.layoutParams.height = (holder.itemView.layoutParams.width)/6 * 5
        holder.itemView.requestLayout()

        //뷰 항목 채우기
        if(infoList[position].imgUsed) {
            infoList[position].image?.let {
                GlideApp.with(holder.itemView).load(strRef.child(it)).into(holder.ivImage)
            }
        }
        else{
            when(infoList[position].categoryNum) {
                0 -> holder.ivImage.setImageResource(R.drawable.korean_food)
                1 -> holder.ivImage.setImageResource(R.drawable.chinese_food)
                2 -> holder.ivImage.setImageResource(R.drawable.japanese_food)
                3 -> holder.ivImage.setImageResource(R.drawable.western_food)
                4 -> holder.ivImage.setImageResource(R.drawable.coffee_and_drink)
                5 -> holder.ivImage.setImageResource(R.drawable.drink)
                6 -> holder.ivImage.setImageResource(R.drawable.etc)
            }
        }
        holder.tvName.text = infoList[position].name
        holder.tvGenre.text = infoList[position].category
        holder.tvRate.text = infoList[position].rate.toString()

        //항목 세부 내용 이동
        holder.itemView.setOnClickListener { v ->
            val record = Intent(v.context, RecordActivity::class.java)
            record.putExtra("info", infoList[position])

            v.context.startActivity(record)
        }
        //길게 누를 시 삭제 질의
        holder.itemView.setOnLongClickListener{ v->
            AlertDialog.Builder(v.context)
                .setMessage(R.string.delete_message)
                .setPositiveButton(R.string.yes) {dialog, which ->
                    if(infoList[position].imgUsed)
                        infoList[position].image?.let { strRef.child(it).delete() }

                    docRef.collection("restaurants").document(infoList[position].dbID).delete()
                    Toast.makeText(v.context, R.string.delete_complete, Toast.LENGTH_SHORT).show()
                    update(infoList)
                }
                .setNegativeButton(R.string.no) {dialog, which -> }
                .show()
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return infoList.size
    }


    //--------------------------------------------
    // 내부 함수 영역
    //

    @SuppressLint("NotifyDataSetChanged")
    fun update(items : List<Info>){
        this.infoList = items
        notifyDataSetChanged()
    }
}