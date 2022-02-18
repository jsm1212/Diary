package com.example.mydocumenter

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    lateinit var locationPermission: ActivityResultLauncher<Array<String>>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationPermission = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()) { results ->
            if(!results.all{ it.value }) {
                Toast.makeText(this
                    , "권한 승인이 필요합니다."
                    , Toast.LENGTH_LONG).show()
            }
        }
        locationPermission.launch(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
        )

        val insertBtn = findViewById<Button>(R.id.insertBtn)

        insertBtn.setOnClickListener {
            val intent = Intent(this, InsertActivity::class.java)
            startActivity(intent)
        }

        // DB 로부터 list
        val list = DBHelper.getInstance(this, "myDoc.db").select()
        // 확인!
        for (i in list.indices){
            println(list[i].toString())
        }

        val docAdapter = DocumentAdapter(this, list)
        var recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = docAdapter

        val layout = LinearLayoutManager(this)
        recyclerView.layoutManager = layout
        recyclerView.setHasFixedSize(true)
    }

}