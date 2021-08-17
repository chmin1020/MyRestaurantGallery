package com.fallTurtle.myrestaurantgallery.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission


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

    //앱 실행 전 권한을 받기 위한 다이얼로그
    private fun showPermissionDialog() {
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                val addIntent = Intent(this@MainActivity, AddActivity::class.java)
                addIntent.putExtra("isEdit", false)
                startActivity(addIntent)
            }
            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(this@MainActivity, "권한이 없으면 레시피 저장 기능 사용이 불가능합니다.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        TedPermission.with(this)
            .setPermissionListener(permissionListener)
            .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE).check()
    }

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
            showPermissionDialog()
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
                            for(item in list){
                                db.collection("users")
                                    .document(FirebaseAuth.getInstance().currentUser!!.email.toString())
                                    .collection("restaurants")
                                    .document(item.getDBID()!!).delete()
                            }
                            db.collection("users")
                                .document(FirebaseAuth.getInstance().currentUser!!.email.toString()).delete()

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

        //검색 기능 textWatcher를 통해 구현
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                listAdapter.filter.filter(binding.etSearch.text)
            }
        })
    }

    override fun onResume(){
        super.onResume()
        updateDB()
    }

    //database를 갱신하는 메소드
    private fun updateDB() {
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