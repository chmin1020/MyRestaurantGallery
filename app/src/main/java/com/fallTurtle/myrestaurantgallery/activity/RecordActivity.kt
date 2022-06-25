package com.fallTurtle.myrestaurantgallery.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.fallTurtle.myrestaurantgallery.etc.GlideApp
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.databinding.ActivityRecordBinding
import com.fallTurtle.myrestaurantgallery.item.Piece
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class RecordActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRecordBinding

    private var piece = Piece()

    private val db = Firebase.firestore
    private val docRef = db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.email.toString())
    private val str = Firebase.storage
    private val strRef = str.reference.child(FirebaseAuth.getInstance().currentUser!!.email.toString())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        //adapter 데이터 받기
        piece.setDBID(intent.getStringExtra("dbID").toString())
        piece.setName(intent.getStringExtra("name"))
        piece.setGenreNum(intent.getIntExtra("genreNum", 0))
        piece.setGenre(intent.getStringExtra("genre"))
        piece.setRate(intent.getIntExtra("rate",0))
        piece.setImage(intent.getStringExtra("image"))
        piece.setImgUsed(intent.getBooleanExtra("imgUsed", false))
        piece.setLocation(intent.getStringExtra("location"))
        piece.setMemo(intent.getStringExtra("memo"))
        piece.setDate(intent.getStringExtra("date"))
        piece.setLatitude(intent.getDoubleExtra("latitude", -1.0))
        piece.setLongitude(intent.getDoubleExtra("longitude", -1.0))

        binding.tvName.text = piece.getName()
        binding.tvGenre.text = piece.getGenre()
        binding.tvLocation.text = piece.getLocation()
        binding.tvMemo.text = piece.getMemo()
        binding.rbRatingBar.rating = piece.getRate()!!.toFloat()
        binding.tvDate.text = piece.getDate()

        if(piece.getImgUsed()) {
            GlideApp.with(this)
                .load(strRef.child(piece.getImage().toString())).into(binding.ivImage)
        }
        else {
            when (piece.getGenreNum()!!) {
                0 -> binding.ivImage.setImageResource(R.drawable.korean_food)
                1 -> binding.ivImage.setImageResource(R.drawable.chinese_food)
                2 -> binding.ivImage.setImageResource(R.drawable.japanese_food)
                3 -> binding.ivImage.setImageResource(R.drawable.western_food)
                4 -> binding.ivImage.setImageResource(R.drawable.coffee_and_drink)
                5 -> binding.ivImage.setImageResource(R.drawable.drink)
                6 -> binding.ivImage.setImageResource(R.drawable.etc)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> finish()
            R.id.delete_item -> {
                AlertDialog.Builder(this)
                    .setMessage(R.string.delete_message)
                    .setPositiveButton(R.string.yes) {dialog, which ->
                        if(piece.getImgUsed()){
                            strRef.child(piece.getImage().toString()).delete()
                        }
                        docRef.collection("restaurants").document(piece.getDBID().toString()).delete()
                        Toast.makeText(this, R.string.delete_complete, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .setNegativeButton(R.string.no) {dialog, which -> }
                    .show()
             }
            R.id.edit_item -> {
                val edit = Intent(this, AddActivity::class.java)
                edit.putExtra("isEdit", true)
                edit.putExtra("dbID",piece.getDBID())
                edit.putExtra("name",piece.getName())
                edit.putExtra("genreNum",piece.getGenreNum())
                edit.putExtra("genre",piece.getGenre())
                edit.putExtra("location",piece.getLocation())
                edit.putExtra("image",piece.getImage())
                edit.putExtra("imgUsed",piece.getImgUsed())
                edit.putExtra("memo",piece.getMemo())
                edit.putExtra("rate",piece.getRate())
                edit.putExtra("date",piece.getDate())
                edit.putExtra("latitude",piece.getLatitude())
                edit.putExtra("longitude",piece.getLongitude())
                finish()
                startActivity(edit)
                overridePendingTransition(R.anim.fadein, R.anim.fadeout)
             }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.record_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

}