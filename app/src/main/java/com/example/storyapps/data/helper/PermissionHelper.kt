package com.example.storyapps.data.helper

import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

fun requestPermissionLauncher(context: AppCompatActivity, permission: String)  {

    context.registerForActivityResult(ActivityResultContracts.RequestPermission()){
            isGranted: Boolean ->
        if (isGranted) {

        } else {
            Toast.makeText(context, "Permission $permission request denied", Toast.LENGTH_LONG).show()
        }
    }.launch(permission)
}