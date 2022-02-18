package com.example.mydocumenter

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import kotlin.properties.Delegates

class InsertActivity : AppCompatActivity() {

    // storage 권한 처리에 필요한 변수
    val CAMERA = arrayOf(   Manifest.permission.CAMERA )
    val STORAGE = arrayOf(  Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE )
    val CAMERA_CODE = 98
    val STORAGE_CODE = 99

    // 권한처리
    lateinit var locationPermission: ActivityResultLauncher<Array<String>>

    // 위치 서비스가 GPS 사용하여 위치 확인
    lateinit var fusedLocationClient: FusedLocationProviderClient

    // 위치 값 요청에 대한 갱신 정보를 받는 변수
    lateinit var locationCallback: LocationCallback

    // 위도와 경도
    var latitude:Double = 0.0
    var longitude:Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert)

        // 위치 권한
        locationPermission = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions() ){ results->
            if(!results.all { it.value }){
                Toast.makeText(this, "권한 승인이 필요합니다", Toast.LENGTH_LONG).show()
            }
        }

        // 권한 요청
        locationPermission.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // 위치 정보를 실시간으로 받기 위한 함수를 호출
        updateLocation()



        // 카메라 촬영
        val cameraBtn = findViewById<Button>(R.id.cameraBtn)
        cameraBtn.setOnClickListener {
            CallCamera()
        }


        // 이미지 경로
        val imagePath = findViewById<TextView>(R.id.imagePathText)
        // 내용
        val editContent = findViewById<EditText>(R.id.editContent)
        // 저장 버튼
        val save = findViewById<Button>(R.id.saveBtn)
        save.setOnClickListener {
            // 이미지 경로, 현재의 위치 주소, 내용
            val address = getAddress(latitude, longitude)

            var message = "위도:$latitude 경도:$longitude \n" +
                    "이미지경로:${imagePath.text} \n" +
                    "주소:$address \n" +
                    "위도:$latitude 경도:$longitude \n" +
                    "내용:${editContent.text} "

            // 우선은 출력해보자!
            /*AlertDialog.Builder(this@InsertActivity)
                .setTitle("대화 상자")
                .setMessage(message)
                .setCancelable(false)
                .setNeutralButton("닫기", DialogInterface.OnClickListener { dialog, which ->
                }).show()*/

            DBHelper.getInstance(this, "myDoc.db")
                    .insert(
                        DocumentDto(0,
                                    imagePath.text.toString(),
                                    address,
                                    editContent.text.toString(),
                                    ""))

            Toast.makeText(this, "저장되었습니다", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    // 위도, 경도 -> 주소
    fun getAddress(lat:Double, lon:Double):String{
        val geocoder:Geocoder = Geocoder(this)
        var list: List<Address>? = null
        var address = ""
        try {
            list = geocoder.getFromLocation(
                lat,  // 위도
                lon,  // 경도
                10
            ) // 얻어올 값의 개수
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생")
        }
        if (list != null) {
            if (list.isEmpty()) {
                Toast.makeText(this, "해당되는 주소 정보는 없습니다", Toast.LENGTH_LONG).show()
            } else {
                address = list[0].getAddressLine(0).toString()
            }
        }
        return address
    }

    // 카메라 권한, 저장소 권한
    // 요청 권한
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            CAMERA_CODE -> {
                for (grant in grantResults){
                    if(grant != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "카메라 권한을 승인해 주세요", Toast.LENGTH_LONG).show()
                    }
                }
            }
            STORAGE_CODE -> {
                for(grant in grantResults){
                    if(grant != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "저장소 권한을 승인해 주세요", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
    // 다른 권한등도 확인이 가능하도록
    fun checkPermission(permissions: Array<out String>, type:Int):Boolean{
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            for (permission in permissions){
                if(ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, permissions, type)
                    return false
                }
            }
        }
        return true
    }
    // 카메라 촬영 - 권한 처리
    fun CallCamera(){
        if(checkPermission(CAMERA, CAMERA_CODE) && checkPermission(STORAGE, STORAGE_CODE)){
            val itt = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(itt, CAMERA_CODE)
        }
    }
    // 사진 저장
    fun saveFile(fileName:String, mimeType:String, bitmap: Bitmap): Uri?{

        var CV = ContentValues()

        // MediaStore 에 파일명, mimeType 을 지정
        CV.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        CV.put(MediaStore.Images.Media.MIME_TYPE, mimeType)

        // 안정성 검사
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            CV.put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        // MediaStore 에 파일을 저장
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, CV)
        if(uri != null){
            var scriptor = contentResolver.openFileDescriptor(uri, "w")

            val fos = FileOutputStream(scriptor?.fileDescriptor)

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.close()

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                CV.clear()
                // IS_PENDING 을 초기화
                CV.put(MediaStore.Images.Media.IS_PENDING, 0)
                contentResolver.update(uri, CV, null, null)
            }
        }
        return uri
    }

    // 결과
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val imageView = findViewById<ImageView>(R.id.imageView)
        val imagePath = findViewById<TextView>(R.id.imagePathText)

        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                CAMERA_CODE -> {
                    if(data?.extras?.get("data") != null){
                        val img = data?.extras?.get("data") as Bitmap
                        val uri = saveFile(RandomFileName(), "image/png", img)
                        imageView.setImageURI(uri)
                        Toast.makeText(this, "$uri", Toast.LENGTH_LONG).show()
                        println("이미지 경로: $uri")

                        // 이미지 경로를 TextView 에 저장
                        imagePath.text = getPath(uri)
                        println("실제 이미지 경로:" + getPath(uri))
                    }
                }
                STORAGE_CODE -> {
                    val uri = data?.data
                    imageView.setImageURI(uri)
                }
            }
        }
    }

    // 파일명을 날짜 저장
    @SuppressLint("SimpleDateFormat")
    fun RandomFileName() : String{
        val fileName = SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis())
        return fileName
    }

    // 실제 경로로 변경해 주는 함수
    // Uri -> String
    fun getPath(uri: Uri?): String {
        val projection = arrayOf<String>(MediaStore.Images.Media.DATA)
        val cursor: Cursor = managedQuery(uri, projection, null, null, null)
        startManagingCursor(cursor)
        val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(columnIndex)
    }



    // 위치정보를 실시간으로 받기 위한 함수
    @SuppressLint("MissingPermission")
    fun updateLocation(){
        val locationRequest = LocationRequest.create()
        locationRequest.run {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 1000
        }

        // 실시간으로 자신의 위치를 취득
        locationCallback = object :LocationCallback(){
            // 1초에 한번씩 변경된 위치 정보가 onLocationResult 로 전달된다
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.let {
                    for (location in it.locations){
                        //Log.d( "위치정보", " - 위도:${location.latitude} 경도:${location.longitude}")
                        // 변수에 저장한다
                        latitude = location.latitude
                        longitude = location.longitude
                    }
                }
            }
        }

        // 권한 처리
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
    }

}