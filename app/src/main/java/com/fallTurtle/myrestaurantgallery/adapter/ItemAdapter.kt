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
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.activity.RecordActivity
import com.fallTurtle.myrestaurantgallery.model.room.Info
import java.io.File
import java.util.ArrayList

/**
 * 각 맛집 데이터를 보여줄 리사이클러뷰를 위한 adapter
 **/
class ItemAdapter(private val localPath: String) : RecyclerView.Adapter<ItemAdapter.CustomViewHolder>() {

    class CustomViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var ivImage:ImageView = itemView.findViewById(R.id.iv_image)
        var tvName:TextView = itemView.findViewById(R.id.tv_name)
        var tvGenre:TextView = itemView.findViewById(R.id.tv_genre)
        var tvRate:TextView = itemView.findViewById(R.id.tv_rate)
    }

    //리사이클러뷰를 이루는 리스트 데이터를 저장하는 곳
    private var infoList: List<Info> = ArrayList()


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
        holder.tvName.text = infoList[position].name
        holder.tvGenre.text = infoList[position].category
        holder.tvRate.text = infoList[position].rate.toString()
        fillImageView(infoList[position].image, infoList[position].categoryNum, holder.ivImage)

        //항목 세부 내용 이동
        holder.itemView.setOnClickListener { v ->
            val record = Intent(v.context, RecordActivity::class.java)
            record.putExtra("item_id", infoList[position].dbID)

            v.context.startActivity(record)
        }
    }

    override fun getItemCount(): Int = infoList.size


    //--------------------------------------------
    // 내부 함수 영역
    //

    @SuppressLint("NotifyDataSetChanged")
    fun update(items : List<Info>?){
        items?.let {
            this.infoList = it
            notifyDataSetChanged()
        }
    }

    private fun fillImageView(imagePath: String?, categoryNum: Int, imageView: ImageView){
        imagePath?.let {
            imageView.load(File("${localPath}/$it")){
                crossfade(true)
                placeholder(R.drawable.loading_food)
            }
        } ?:
        run{
            when(categoryNum) {
                0 -> imageView.setImageResource(R.drawable.korean_food)
                1 -> imageView.setImageResource(R.drawable.chinese_food)
                2 -> imageView.setImageResource(R.drawable.japanese_food)
                3 -> imageView.setImageResource(R.drawable.western_food)
                4 -> imageView.setImageResource(R.drawable.coffee_and_drink)
                5 -> imageView.setImageResource(R.drawable.drink)
                6 -> imageView.setImageResource(R.drawable.etc)
            }
        }

    }
}