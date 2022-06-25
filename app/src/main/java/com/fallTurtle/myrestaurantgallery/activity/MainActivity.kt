package com.fallTurtle.myrestaurantgallery.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
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
import com.google.firebase.storage.ktx.storage
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission


class MainActivity : AppCompatActivity() {
    //binding
    private lateinit var binding:ActivityMainBinding

    //time
    private var bpTime:Long = 0

    //recyclerview & Firebase
    private val listAdapter = ListAdapter()
    private var list  = ArrayList<Piece>()
    private val mAuth = FirebaseAuth.getInstance()
    private var docRef: DocumentReference? = null
    private val db = Firebase.firestore

    private val str = Firebase.storage
    private val strRef = str.reference.child(FirebaseAuth.getInstance().currentUser!!.email.toString())

    //앱 실행 전 권한을 받기 위한 다이얼로그
    private fun showPermissionDialog() {
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() { }
            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(this@MainActivity, "권한이 없으면 레시피 저장 기능 사용이 불가능합니다.", Toast.LENGTH_SHORT)
                    .show()
                finishAffinity()
            }
        }
        TedPermission.with(this)
            .setPermissionListener(permissionListener)
            .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE).check()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //permission
        showPermissionDialog()

        //recyclerView setting
        binding.recyclerView.layoutManager = GridLayoutManager(this,2)
        binding.recyclerView.adapter = listAdapter
        updateDB()

        //logout and withdrawal with toolbar_menu
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        //검색 기능 textWatcher를 통해 구현
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                listAdapter.filter.filter(binding.etSearch.text)
            }
        })

        //검색 설정 초기화 버튼
        binding.ivReset.setOnClickListener {
            binding.etSearch.text.clear()
        }
    }

    override fun onResume(){
        super.onResume()
        binding.etSearch.setText("")
        updateDB()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> {
                FirebaseAuth.getInstance().signOut()
                val progress = Intent(this, ProgressActivity::class.java)
                progress.putExtra("endCode",2)
                startActivity(progress)
                finish()
            }
            R.id.menu_withdrawal -> {
                AlertDialog.Builder(this)
                    .setMessage(R.string.withdrawal_ask)
                    .setPositiveButton(R.string.yes) {dialog, which ->
                        for(item in list){
                            //storage 내부 이미지 삭제부터
                            if(item.getImgUsed()){
                                strRef.child(item.getImage()!!).delete()
                            }
                            db.collection("users")
                                .document(FirebaseAuth.getInstance().currentUser!!.email.toString())
                                .collection("restaurants")
                                .document(item.getDBID()!!).delete()
                        }
                        db.collection("users")
                            .document(FirebaseAuth.getInstance().currentUser!!.email.toString()).delete()

                        FirebaseAuth.getInstance().currentUser!!.delete().addOnCompleteListener{ task->
                            if(task.isSuccessful){
                                FirebaseAuth.getInstance().signOut()
                            }
                            else{
                                Toast.makeText(this,"error",Toast.LENGTH_SHORT).show()
                            }
                        }
                        val progress = Intent(this, ProgressActivity::class.java)
                        progress.putExtra("endCode",3)
                        startActivity(progress)
                        finish()
                    }
                    .setNegativeButton(R.string.no){dialog, which ->}
                    .show()
            }
            R.id.add_item ->{
                val addIntent = Intent(this@MainActivity, AddActivity::class.java)
                addIntent.putExtra("isEdit", false)
                startActivity(addIntent)
                overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.account_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        val curTime = System.currentTimeMillis()
        val timeGap = curTime - bpTime

        if(timeGap in 0..2000)
            finish()
        else{
            bpTime = curTime
            Toast.makeText(this, "'뒤로' 버튼을 한 번 더 누르시면 종료됩니다.",Toast.LENGTH_SHORT).show()
        }

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