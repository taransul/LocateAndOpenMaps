package com.example.locateandopenmaps

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val LOCATION_PERMISSION_REQ_CODE = 100
        private const val RESPONSE_TO_AN_ERROR = "Не удалось получить текущее местоположение"
        private const val LATITUDE = "Ширина:"
        private const val LONGITUDE = "Долгота:"
        private const val RESPONSE_TO_REFUSAL_OF_PERMISSION =
            "Вам необходимо предоставить разрешение на доступ к местоположению"
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        btGetLocation.setOnClickListener {
            getCurrentLocation()
        }

        btOpenMap.setOnClickListener {
            openMap()
        }
    }

    private fun getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQ_CODE
            )
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                latitude = location.latitude
                longitude = location.longitude

                "$LATITUDE ${location.latitude}".also { tvLatitude.text = it }
                "$LONGITUDE ${location.longitude}".also { tvLongitude.text = it }


                btOpenMap.visibility = View.VISIBLE
            }
            .addOnFailureListener {
                Toast.makeText(
                    this, RESPONSE_TO_AN_ERROR,
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQ_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                } else {
                    Toast.makeText(
                        this, RESPONSE_TO_REFUSAL_OF_PERMISSION,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun openMap() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("geo:${latitude},${longitude}")
        }
        startActivity(Intent.createChooser(intent, "map"))
    }
}