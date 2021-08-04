package com.fallTurtle.myrestaurantgallery.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.adapter.ListAdapter
import com.fallTurtle.myrestaurantgallery.databinding.ActivityMainBinding
import com.fallTurtle.myrestaurantgallery.item.Piece
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    //binding
    private var mBinding:ActivityMainBinding? = null
    private val binding get()= mBinding!!

    //recyclerview & Firebase
    private val listAdapter = ListAdapter()
    private var list  = ArrayList<Piece>()
    private val mAuth = FirebaseAuth.getInstance()
    private var docRef: DocumentReference? = null
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //recyclerView setting
        binding.recyclerView.layoutManager = GridLayoutManager(this,2)
        binding.recyclerView.adapter = listAdapter
        updateDB()

        //add new things
        binding.ivAddPic.setOnClickListener{
            val addIntent = Intent(this, AddActivity::class.java)
            startActivity(addIntent)
        }

        //logout and withdrawal with toolbar_menu
        binding.toolbar.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    Toast.makeText(this, R.string.logout_success, Toast.LENGTH_SHORT).show()
                    val login = Intent(this, LoginActivity::class.java)
                    startActivity(login)
                    finish()
                    true
                }
                R.id.menu_withdrawal -> {
                    AlertDialog.Builder(this)
                        .setMessage(R.string.withdrawal_ask)
                        .setPositiveButton(R.string.yes) {dialog, which ->
                            db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.email.toString()).delete()
                            FirebaseAuth.getInstance().currentUser!!.delete()
                            Toast.makeText(this, R.string.withdrawal_success, Toast.LENGTH_SHORT).show()
                            finishAffinity()
                        }
                        .setNegativeButton(R.string.no){dialog, which ->}
                        .show()
                   true
                }
                else -> false
            }
        }
    }

    //database를 갱신하는 메소드
    fun updateDB() {
        if (mAuth.currentUser != null) docRef =
            db.collection("users").document(mAuth.currentUser!!.email.toString())
        listAdapter.update(list)
        if (mAuth.currentUser != null) {
            docRef!!.collection("restaurants")
                .addSnapshotListener { value, e ->
                    list.clear()
                    if (value != null) {
                        for (doc in value) {
                            list.add(doc.toObject(Piece::class.java))
                        }
                    }
                    listAdapter.update(list)
                }
        }
    }
}