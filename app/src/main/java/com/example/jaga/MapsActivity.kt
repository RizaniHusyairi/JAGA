package com.example.jaga

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.jaga.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geofencingClient: GeofencingClient


    private val centerLat = -6.923229
    private val centerLng = 107.617154
    private val geofenceRadius = 200.0

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        intent.action = GeofenceBroadcastReceiver.ACTION_GEOFENCE_EVENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigasiView.background = null

        val isRecord = intent.getStringExtra(SUCCESS_RECORD)
        if(isRecord?.isEmpty() == false){
            Toast.makeText(applicationContext,isRecord,Toast.LENGTH_SHORT).show()
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        supportActionBar?.hide()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }


    private fun isMicrophonePresent(): Boolean {
        return this.packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)
    }
    private fun getMicrophonePermission(){
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.RECORD_AUDIO)
            == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,  Array<String>(10) { Manifest.permission.RECORD_AUDIO },
                MICROPHONE_PERMISSION_CODE
            )
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        getMyLocation()
        mMap.uiSettings.isMyLocationButtonEnabled = false
        if (isMicrophonePresent()){
            getMicrophonePermission()
        }

        binding.btnMyLoc.setOnClickListener {

        }
        val stanford = LatLng(centerLat, centerLng)
        //mMap.addMarker(MarkerOptions().position(stanford).title("Stanford University"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(stanford, 15f))

        mMap.addCircle(
            CircleOptions()
                .center(stanford)
                .radius(geofenceRadius)
                .fillColor(0x22FF0000)
                .strokeColor(Color.YELLOW)
                .strokeWidth(3f)
        )

        getMyLocation()
        addGeofence()

    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
//            val latLng = LatLng(loc, getLongitude().toDouble())
//            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18f)
//            mMap.animateCamera(cameraUpdate)
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ).toString()
            )
        }
    }

    fun btnRecordPressed( v: View){
        val intent: Intent = Intent(this,RecordActivity::class.java)
        startActivity(intent)
    }

    fun btnQuestionnaire(item: MenuItem) {
        val intent: Intent = Intent(this,QuisionerActivity::class.java)
        startActivity(intent)
    }


    companion object{
        private const val MICROPHONE_PERMISSION_CODE = 200
        const val SUCCESS_RECORD = "success_record"
    }


}