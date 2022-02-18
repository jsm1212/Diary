package com.example.mydocumenter

import android.Manifest
import android.app.Activity
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment(val activity: Activity) : Fragment(), OnMapReadyCallback{

    lateinit var locationPermission: ActivityResultLauncher<Array<String>>
    lateinit var fusedLocationClient: FusedLocationProviderClient

    private var mMap: GoogleMap? = null // 초기화를 반드시 해주도록 한다. 아니면 onMapReady가 자동 호출되지 않는다

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        locationPermission = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions() ){ results->
            if(!results.all { it.value }){
                Toast.makeText(activity, "권한 승인이 필요합니다", Toast.LENGTH_LONG).show()
            }
        }

        // 권한 요청
        locationPermission.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )

        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
    }

    fun setLocation(latitude:Double, longitude:Double){
        val LATLNG = LatLng(latitude, longitude)

        val markerOptions = MarkerOptions()
            .position(LATLNG)
            .title("Here!")

        val cameraPosition = CameraPosition.Builder()
            .target(LATLNG)
            .zoom(15.0f)
            .build()

        mMap?.clear()
        mMap?.addMarker(markerOptions)
        mMap?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }
}