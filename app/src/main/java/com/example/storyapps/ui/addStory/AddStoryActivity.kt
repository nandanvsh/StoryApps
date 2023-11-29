package com.example.storyapps.ui.addStory

import android.Manifest
import android.app.Activity
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.storyapps.data.helper.AuthDataBundle
import com.example.storyapps.data.helper.getImageUri
import com.example.storyapps.data.helper.requestPermissionLauncher
import com.example.storyapps.data.helper.uriToFile
import com.example.storyapps.databinding.ActivityAddStoryBinding
import com.example.storyapps.ui.decorations.Button
import com.example.storyapps.ui.story_list.StoryListViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import java.util.concurrent.TimeUnit

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var addStoryViewModel: AddStoryViewModel
    private var currentImageUri: Uri? = null
    private lateinit var customButton: Button
    private lateinit var fusedLocation: FusedLocationProviderClient
    private lateinit var requestLoc : LocationRequest
    private var locationToReq : Location? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermissionLauncher(this, REQUIRED_PERMISSION)
        customButton = binding.btnSubmit
        addStoryViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[AddStoryViewModel::class.java]
//        val storyViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[StoryListViewModel::class.java]
        val token = if (Build.VERSION.SDK_INT >= 33) {
            intent.getSerializableExtra(ADD_STORY_KEY, AuthDataBundle::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(ADD_STORY_KEY) as AuthDataBundle
        }
        addStoryViewModel.story.observe(this){ result ->
            setButtonEnable(true)
            if (result.error == true){
                Toast.makeText(this@AddStoryActivity, result.message, Toast.LENGTH_SHORT).show()
            } else {
//                storyViewModel.getAll(token?.token ?: "")
                Toast.makeText(this@AddStoryActivity, result.message, Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
        fusedLocation = LocationServices.getFusedLocationProviderClient(this)
        Log.d("tokenLoc", token.toString())

        binding.btnCamera.setOnClickListener {
            startCamera()
        }

        binding.btnGallery.setOnClickListener {
            startGallery()
        }
        addStoryViewModel.isLoading.observe(this){
            setButtonLoading(it)
        }
        binding.checkLoc.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                createLocationRequest()
            }
        }
        binding.btnSubmit.setOnClickListener {
            uploadImage(token?.token ?: "")
        }

    }
    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA

        private const val TAG = "MapsActivity"
        const val ADD_STORY_KEY = "ADD_STORY_KEY"
    }
    private fun setButtonEnable(value: Boolean) {
        customButton.isEnabled = value
    }
    private fun setButtonLoading(value: Boolean){
        setButtonEnable(false)
        customButton.setLoading(value)
    }
    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }
    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("ImageURI", "showImage: $it")
            binding.imgPreview.setImageURI(it)
        }
    }
    private fun uploadImage(token: String) {

        if (currentImageUri != null && binding.editDesc.text.isNotEmpty()){
            setButtonLoading(true)
            currentImageUri?.let { uri ->
                val imageFile = uriToFile(uri, this)
                Log.d("ImageFile", "showImage: ${imageFile.path}")
                val description =  binding.editDesc.text.toString()
                if (!binding.checkLoc.isChecked){
                    locationToReq = null
                }
                addStoryViewModel.uploadImage(imageFile, description, token = token, lon = locationToReq?.longitude, lat = locationToReq?.latitude,)
            }
        } else {
            Toast.makeText(this@AddStoryActivity, "Please select an image and fill in the description", Toast.LENGTH_SHORT).show()
        }

    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getLastLoc()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getLastLoc()
                }
                else -> {
                    // No location access granted.
                }
            }
        }

    private val resolutionLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        when (result.resultCode) {
            RESULT_OK ->
                Log.i(TAG, "onActivityResult: All location settings are satisfied.")
            RESULT_CANCELED ->
                Toast.makeText(
                    this,
                    "enable GPS to use this app!",
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

    private fun createLocationRequest(){

        requestLoc = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(1)
            maxWaitTime = TimeUnit.SECONDS.toMillis(1)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(requestLoc)
        val client = LocationServices.getSettingsClient(this)
        client.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                getLastLoc()
            }
            .addOnFailureListener { exception ->
                binding.checkLoc.isChecked = false
                if (exception is ResolvableApiException) {
                    try {
                        resolutionLauncher.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                        Toast.makeText(this, sendEx.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun getLastLoc() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val mLocationCallback: LocationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    if (locationResult == null) {
                        return
                    }
                    for (location in locationResult.locations) {
                        if (location != null) {
                        }
                    }
                }
            }
            fusedLocation.requestLocationUpdates(requestLoc, mLocationCallback, null)

            fusedLocation.lastLocation.addOnSuccessListener { location: Location? ->

                if (location != null) {
                    locationToReq = location
                    Toast.makeText(
                        this@AddStoryActivity,
                        "${location.toString()}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    binding.checkLoc.isChecked = false
                    Toast.makeText(
                        this@AddStoryActivity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

}