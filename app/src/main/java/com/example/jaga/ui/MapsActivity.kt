package com.example.jaga.ui

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.jaga.*
import com.example.jaga.R
import com.example.jaga.databinding.ActivityMapsBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geofencingClient: GeofencingClient
    private lateinit var mapsViewModel: LoginViewModel
    private lateinit var dbFirestore: FirebaseFirestore
    private val data= ArrayList<ZoneDanger>()

    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.rotate_open_anim)}
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.rotate_close_anim)}
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.from_bottom_anim)}
    private val to_bottom: Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.to_bottom_anim)}
    private var clicked = false


    private val centerLat = -0.493349
    private val centerLng = 117.147487
    private val geofenceRadius = 100.0

    private val centerLat2 = -0.495242
    private val centerLng2 = 117.148974

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
        setupViewModel()
        binding.bottomNavigasiView.background = null
        mapsViewModel.getUser().observe(this){
            iduser = it.id
        }
        val isRecord = intent.getStringExtra(SUCCESS_RECORD)
        if(isRecord?.isEmpty() == false){
            Toast.makeText(applicationContext,isRecord,Toast.LENGTH_SHORT).show()
        }

        val radius: Float = resources.getDimension(R.dimen.activity_horizontal_margin)

        val bottomBarBackground: MaterialShapeDrawable = binding.bottomAppBar.background as MaterialShapeDrawable
        bottomBarBackground.shapeAppearanceModel = bottomBarBackground.shapeAppearanceModel
            .toBuilder()
            .setTopLeftCorner(CornerFamily.ROUNDED, radius)
            .setTopRightCorner(CornerFamily.ROUNDED, radius)
            .setBottomRightCorner(CornerFamily.ROUNDED, radius)
            .setBottomLeftCorner(CornerFamily.ROUNDED, radius)

            .build()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        supportActionBar?.hide()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.fab.setOnLongClickListener {
            val intent = Intent(this@MapsActivity, RecordActivity::class.java)
            if (checkForegroundAndBackgroundLocationPermission() && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                mMap.isMyLocationEnabled = true
                fusedLocationClient.lastLocation.addOnSuccessListener { location:Location? ->
                    if (location != null){
                        intent.putExtra(RecordActivity.LATITUDE,location.latitude)
                        intent.putExtra(RecordActivity.LONGITUDE,location.longitude)
                        startActivity(intent)
                    }
                }
            } else {
                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            false


        }

        binding.btnSetting.setOnClickListener {
            val profilPage = Intent(this@MapsActivity,SettingActivity::class.java)
            startActivity(profilPage)
        }

        binding.btnSource.setOnClickListener {
            val sourcePage = Intent(this@MapsActivity,SourceActivity::class.java)
            startActivity(sourcePage)
        }

        binding.btnPlus.setOnClickListener{
            onAddButtonCLicked()
        }
    }

    private fun onAddButtonCLicked(){
        setVisibility(clicked)
        setAnimation(clicked)
        clicked = !clicked
    }

    private fun setVisibility(clicked: Boolean){
        if(!clicked){
            binding.btnSetting.visibility = View.VISIBLE
            binding.btnSource.visibility = View.VISIBLE
            binding.btnSetting.isEnabled = true
            binding.btnSource.isEnabled = true

        }else{
            binding.btnSetting.visibility = View.INVISIBLE
            binding.btnSource.visibility = View.INVISIBLE
            binding.btnSetting.isEnabled = false
            binding.btnSource.isEnabled = false


        }
    }
    private fun setupViewModel() {
        mapsViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]
    }

    private fun setAnimation(clicked: Boolean){
        if(!clicked){
            binding.btnSetting.startAnimation(fromBottom)
            binding.btnSource.startAnimation(fromBottom)
            binding.btnPlus.startAnimation(rotateOpen)
        }else{
            binding.btnSetting.startAnimation(to_bottom)
            binding.btnSource.startAnimation(to_bottom)
            binding.btnPlus.startAnimation(rotateClose)
        }
    }




    private fun isMicrophonePresent(): Boolean {
        return this.packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)
    }

    private fun getMicrophonePermission(){
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.RECORD_AUDIO)
            == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,  arrayOf(Manifest.permission.RECORD_AUDIO),
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

        dbFirestore = Firebase.firestore

        dbFirestore.collection("datajaga9").get().addOnSuccessListener { it ->


            it.documents.forEach { doc ->
                val temp = doc.toObject(ZoneDanger::class.java)
                if (temp != null) {
                    data.add(temp)
                }
            }

            data.forEach { zone ->
                val stanford = LatLng( zone.y!!,zone.x!!)
                mMap.addCircle(
                    CircleOptions()
                        .center(stanford)
                        .radius(geofenceRadius)
                        .fillColor(0x22FF0000)
                        .strokeColor(Color.RED)
                        .strokeWidth(3f)
                )
                addGeofence(zone.y!!,zone.x!!, )
                Log.e("tesdata",zone.toString())
            }

                Log.e("tesdata2",data[1].x.toString())

        }

        mMap.uiSettings.isMyLocationButtonEnabled = false
        if (isMicrophonePresent()){
            getMicrophonePermission()
        }

        binding.btnMyLoc.setOnClickListener {
            getMyLocation()
        }



        val stanford2 = LatLng(centerLat2, centerLng2)


        getMyLocation()

    }

    @SuppressLint("MissingPermission")
    private fun addGeofence(x:Double, y:Double) {
        geofencingClient = LocationServices.getGeofencingClient(this)
        val geofence = Geofence.Builder()
            .setRequestId("kampus")
            .setCircularRegion(
                x,
                y,
                geofenceRadius.toFloat()
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_ENTER)
            .setLoiteringDelay(5000)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        geofencingClient.removeGeofences(geofencePendingIntent).run {
            addOnCompleteListener {
                geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
                    addOnSuccessListener {
//                        showToast("Geofencing added")
                    }
                    addOnFailureListener {
//                        showToast("Geofencing not added : ${it.message}")
                    }
                }
            }
        }

    }

    private fun showToast(text: String) {
        Toast.makeText(this@MapsActivity, text, Toast.LENGTH_SHORT).show()
    }


    private val requestBackgroundLocationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private val runningQOrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    @TargetApi(Build.VERSION_CODES.Q)
    private val requestLocationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                if (runningQOrLater) {
                    requestBackgroundLocationPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                } else {
                    getMyLocation()
                }
            }
        }

    @TargetApi(Build.VERSION_CODES.Q)
    private fun checkForegroundAndBackgroundLocationPermission(): Boolean {
        val foregroundLocationApproved = checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        val backgroundPermissionApproved =
            if (runningQOrLater) {
                checkPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            } else {
                true
            }
        return foregroundLocationApproved && backgroundPermissionApproved
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun getMyLocation() {
        if (checkForegroundAndBackgroundLocationPermission() && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location:Location? ->
                if (location != null){
                    val latLng = LatLng(location.latitude, location.longitude)
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18f)
                    mMap.animateCamera(cameraUpdate)
                }
            }
        } else {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }



    fun btnContact(item: MenuItem){
        val intent: Intent = Intent(this, ContactActivity::class.java)
        startActivity(intent)
    }

    fun btnQuestionnaire(item: MenuItem) {
        val intent: Intent = Intent(this, QuisionerActivity::class.java)
        startActivity(intent)
    }



    companion object{
        private const val MICROPHONE_PERMISSION_CODE = 200
        const val SUCCESS_RECORD = "success_record"
        var iduser:String? = null

    }


}