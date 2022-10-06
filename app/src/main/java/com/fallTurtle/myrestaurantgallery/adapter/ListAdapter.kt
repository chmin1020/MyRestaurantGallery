package com.fallTurtle.myrestaurantgallery.adapter

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
import com.fallTurtle.myrestaurantgallery.item.Piece
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.ArrayList

class ListAdapter : RecyclerView.Adapter<ListAdapter.CustomViewHolder>(), Filterable {
    //리사이클러뷰를 이루는 리스트 데이터를 저장하는 곳
    private var ufList: List<Piece> = ArrayList()
    private var fList: List<Piece> = ArrayList()

    private val db = Firebase.firestore
    private val docRef = db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.email.toString())
    private val str = Firebase.storage
    private val strRef = str.reference.child(FirebaseAuth.getInstance().currentUser!!.email.toString())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val v : View = LayoutInflater.from(parent.context).inflate(R.layout.list, parent, false)
        return CustomViewHolder(v)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        //그리드 뷰 크기 조정
        val displayMetrics = DisplayMetrics()
        holder.itemView.context.display!!.getRealMetrics(displayMetrics)
        holder.itemView.layoutParams.width = (displayMetrics.widthPixels)/7 * 3
        holder.itemView.layoutParams.height = (holder.itemView.layoutParams.width)/6 * 5
        holder.itemView.requestLayout()

        //뷰 항목 채우기
        if(fList[position].getImgUsed()) {
                GlideApp.with(holder.itemView)
                .load(strRef.child(fList[position].getImage().toString())).into(holder.ivImage)
        }
        else{
            when(fList[position].getGenreNum()) {
                0 -> holder.ivImage.setImageResource(R.drawable.korean_food)
                1 -> holder.ivImage.setImageResource(R.drawable.chinese_food)
                2 -> holder.ivImage.setImageResource(R.drawable.japanese_food)
                3 -> holder.ivImage.setImageResource(R.drawable.western_food)
                4 -> holder.ivImage.setImageResource(R.drawable.coffee_and_drink)
                5 -> holder.ivImage.setImageResource(R.drawable.drink)
                6 -> holder.ivImage.setImageResource(R.drawable.etc)
            }
        }
        holder.tvName.text = fList[position].getName()
        holder.tvGenre.text = fList[position].getGenre()
        holder.tvRate.text = fList[position].getRate().toString()

        //항목 세부 내용 이동
        holder.itemView.setOnClickListener { v ->
            val record = Intent(v.context, RecordActivity::class.java)
            record.putExtra("dbID", fList[position].getDBID())
            record.putExtra("name", fList[position].getName())
            record.putExtra("genreNum", fList[position].getGenreNum())
            record.putExtra("genre", fList[position].getGenre())
            record.putExtra("rate", fList[position].getRate())
            record.putExtra("image",fList[position].getImage())
            record.putExtra("imgUsed", fList[position].getImgUsed())
            record.putExtra("location", fList[position].getLocation())
            record.putExtra("memo", fList[position].getMemo())
            record.putExtra("date", fList[position].getDate())
            record.putExtra("latitude", fList[position].getLatitude())
            record.putExtra("longitude", fList[position].getLongitude())
            v.context.startActivity(record)
        }
        //길게 누를 시 삭제 질의
        holder.itemView.setOnLongClickListener{ v->
            AlertDialog.Builder(v.context)
                .setMessage(R.string.delete_message)
                .setPositiveButton(R.string.yes) {dialog, which ->
                    if(fList[position].getImgUsed()){
                        strRef.child(fList[position].getImage().toString()).delete()
                    }
                    docRef.collection("restaurants").document(fList[position].getDBID().toString()).delete()
                    Toast.makeText(v.context, R.string.delete_complete, Toast.LENGTH_SHORT).show()
                    update(fList)
                }
                .setNegativeButton(R.string.no) {dialog, which -> }
                .show()
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return fList.size
    }

    fun update(item : List<Piece>){
        this.fList = item
        this.ufList = item
        notifyDataSetChanged()
    }

    class CustomViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var ivImage:ImageView = itemView.findViewById(R.id.iv_image)
        var tvName:TextView = itemView.findViewById(R.id.tv_name)
        var tvGenre:TextView = itemView.findViewById(R.id.tv_genre)
        var tvRate:TextView = itemView.findViewById(R.id.tv_rate)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                fList =
                    if (constraint == null || constraint.isEmpty())  //검색 창에 입력된 내용이 없을 시 전체 리스트 출력
                        ufList
                    else{
                        val filteringList: MutableList<Piece> = ArrayList<Piece>()
                        val chk = constraint.toString().trim { it <= ' ' }
                        for (i in ufList.indices) {  //필터되지 않은 전체 리스트에서 조건에 맞는 것만 filteringList 내에 추가
                            if (ufList[i].getName()?.contains(chk)!!) {
                                ufList[i].let { filteringList.add(it) }
                            }
                            else if(ufList[i].getLocation()?.contains(chk)!!){
                                ufList[i].let { filteringList.add(it) }
                            }
                            else if(ufList[i].getGenre()?.contains(chk)!!) {
                                ufList[i].let { filteringList.add(it) }
                            }
                            else if(ufList[i].getMemo()?.contains(chk)!!){
                                ufList[i].let { filteringList.add(it) }
                            }
                        }
                        filteringList
                    }
                val filterResults = FilterResults()
                filterResults.values = fList
                return filterResults
            }
            //완성된 filterResults 출력
            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                fList = results.values as ArrayList<Piece>
                notifyDataSetChanged()
            }
        }
    }
}