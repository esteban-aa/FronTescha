package com.example.teschachatbot_f

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class HorariosControlEscolarActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_horarios_control_escolar)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Coordenadas de Control Escolar (puedes cambiarlo a las reales)
        val controlEscolar = LatLng(19.2327762, -98.8406618)
        googleMap.addMarker(
            MarkerOptions()
                .position(controlEscolar)
                .title("Control Escolar")
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(controlEscolar, 17f))
    }
}



