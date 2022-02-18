package com.example.mydocumenter

import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val dto = intent.getParcelableExtra<DataVo>("data")

        val detailImageView = findViewById<ImageView>(R.id.detailImageView)
        val detailContentText = findViewById<TextView>(R.id.detailContentText)
        val detailDateText = findViewById<TextView>(R.id.detailDateText)

        val myBitmap = BitmapFactory.decodeFile(dto?.path) // 이미지파일 경로를 비트맵으로 변환
        detailImageView.setImageBitmap(myBitmap)  // 이미지뷰에 비트맵이미지를 뿌려준다.

        detailContentText.text = dto?.content
        detailDateText.text = dto?.reg

        // map
        val fm = supportFragmentManager
        val fragmentTransaction = fm.beginTransaction()
        val mapsFragment = MapsFragment(this)
        fragmentTransaction.add(R.id.content, mapsFragment) // content랑 mapsFragment 연결
        fragmentTransaction.commit()

        val geocoder: Geocoder = Geocoder(this)

        var list:List<Address>? = geocoder.getFromLocationName(
            dto?.address,
            10
        )

        val mapBtn = findViewById<Button>(R.id.mapBtn)
        val content = findViewById<FrameLayout>(R.id.content)
        content.visibility = View.INVISIBLE

        mapBtn.setOnClickListener {
            content.visibility = View.VISIBLE
            mapsFragment.setLocation(list!![0].latitude, list!![0].longitude)
        }
    }
}