package com.fallTurtle.myrestaurantgallery.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import com.fallTurtle.myrestaurantgallery.R
import com.fallTurtle.myrestaurantgallery.adapter.ListAdapter
import com.fallTurtle.myrestaurantgallery.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    //binding
    private var mBinding:ActivityMainBinding? = null
    private val binding get()= mBinding!!

    //recyclerview adapter
    private val listAdapter = ListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = GridLayoutManager(this,2)
        binding.recyclerView.adapter = listAdapter

        binding.ivAddPic.setOnClickListener{
            val addIntent = Intent(this, AddActivity::class.java)
            startActivity(addIntent)
        }

        binding.toolbar.setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener { item: MenuItem ->
            when(item.itemId){ //??????
                R.id.menu_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    Toast.makeText(this,R.string.logout_success,Toast.LENGTH_SHORT).show()
                    val login = Intent(this,LoginActivity::class.java)
                    startActivity(login)
                    finish()
                    true
                }
                R.id.menu_withdrawal -> {
                    FirebaseAuth.getInstance().currentUser!!.delete()
                    Toast.makeText(this,R.string.withdrawal_success,Toast.LENGTH_SHORT).show()
                    val login = Intent(this,LoginActivity::class.java)
                    startActivity(login)
                    true
                }
                else -> false
            }
        })
    }


}