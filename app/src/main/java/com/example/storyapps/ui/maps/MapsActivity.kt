package com.example.storyapps.ui.maps

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.example.storyapps.R
import com.example.storyapps.data.helper.AuthDataBundle
import com.example.storyapps.data.helper.DetailData
import com.example.storyapps.data.response.ListStoryItem
import com.example.storyapps.data.response.StoryResponse
import com.example.storyapps.databinding.ActivityMapsBinding
import com.example.storyapps.ui.ViewModelFactoryStory
import com.example.storyapps.ui.detail_story.DetailStoryActivity
import com.example.storyapps.ui.story_list.StoryListViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var storyViewModel: StoryListViewModel
    private lateinit var story: StoryResponse
    private var token: AuthDataBundle? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        storyViewModel = ViewModelProvider(this, ViewModelFactoryStory(this, token?.token))[StoryListViewModel::class.java]

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        token = if (Build.VERSION.SDK_INT >= 33) {
            intent.getSerializableExtra(MAPS_ACTIVITY_INTENT_KEY, AuthDataBundle::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(MAPS_ACTIVITY_INTENT_KEY) as AuthDataBundle
        }
        storyViewModel.getAll(token?.token ?: "")
        storyViewModel.listOurStory.observe(this){
            if (it != null){
                Log.d("ERROR", it.toString())
                if (it.error == true){
                    Toast.makeText(this, "Invalid credentials, please log in again", Toast.LENGTH_LONG).show()
                } else {
                    story = it
                    populateMarker(story)
                }

            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
            }
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        val data = storyViewModel.listOurStory.value
        if (data != null) {
            populateMarker(data)

        }

        mMap.setOnMarkerClickListener {
            mMap.animateCamera((CameraUpdateFactory.newLatLngZoom(it.position, 15f)))
            it.showInfoWindow()
            true
        }
        mMap.setOnInfoWindowClickListener {
            val markerData = it.tag as ListStoryItem
            val intentDetail = Intent(this, DetailStoryActivity::class.java)
            intentDetail.putExtra(DetailStoryActivity.EXTRA_ID, DetailData(nama = markerData.name!!, image = markerData.photoUrl!!, description = markerData.description!! ))
            startActivity(intentDetail)
            true
        }
        val Indonesia = LatLng(-0.789275, 113.921327)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Indonesia, 5f))

        getLocation()

    }

    private fun populateMarker(story: StoryResponse){
        story.listStory.forEach { data ->
            val latLng = LatLng(data.lat?.toDouble() ?: 0.0, data.lon?.toDouble() ?: 0.0)

            mMap.addMarker(MarkerOptions()
                .position(latLng)
                .title(data.name)
                .icon(vectorToBitmap(R.drawable.baseline_pin))
                .snippet(data.description))?.apply {
                tag = data
            }
        }
    }
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getLocation()
            }
        }
    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    private fun vectorToBitmap(@DrawableRes id: Int): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
        if (vectorDrawable == null) {
            Log.e("BitmapHelper", "Resource not found")
            return BitmapDescriptorFactory.defaultMarker()
        }
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
    companion object {
        const val MAPS_ACTIVITY_INTENT_KEY = "KEY_MAPS"
    }

}


