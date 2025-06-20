package com.example.teschachatbot_f

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.teschachatbot_f.models.Ventanilla
import com.example.teschachatbot_f.network.ApiService
import com.example.teschachatbot_f.network.RetrofitClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.text.Html


class HorariosControlEscolarActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var tvInfoVentanillas: TextView

    private val ventanillas = mutableListOf<Ventanilla>()

    // Mapea ubicaciones con coordenadas (ajusta o agrega las que tengas)
    private val ubicacionCoords = mapOf(
        "Control Escolar" to LatLng(19.2327762, -98.8406618),
        "Sor Juana" to LatLng(19.233, -98.841)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_horarios_control_escolar)

        tvInfoVentanillas = findViewById(R.id.tvInfoVentanillas)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        obtenerVentanillas()
    }

    private fun obtenerVentanillas() {
        val apiService = RetrofitClient.getInstance().create(ApiService::class.java)
        apiService.getVentanillas().enqueue(object : Callback<List<Ventanilla>> {
            override fun onResponse(
                call: Call<List<Ventanilla>>,
                response: Response<List<Ventanilla>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    ventanillas.clear()
                    ventanillas.addAll(response.body()!!)
                    mostrarMarcadoresEnMapa()
                    mostrarInfoVentanillas()
                } else {
                    Toast.makeText(
                        this@HorariosControlEscolarActivity,
                        "Error al obtener datos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Ventanilla>>, t: Throwable) {
                Toast.makeText(
                    this@HorariosControlEscolarActivity,
                    "Error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        mostrarMarcadoresEnMapa()
    }

    private fun mostrarMarcadoresEnMapa() {
        if (!::googleMap.isInitialized) return

        googleMap.clear()

        for (ventanilla in ventanillas) {
            val coords = ubicacionCoords[ventanilla.ubicacion]
            if (coords != null) {
                googleMap.addMarker(
                    MarkerOptions()
                        .position(coords)
                        .title(ventanilla.ubicacion)
                        .snippet(ventanilla.horarios.joinToString("\n"))
                )
            }
        }

        if (ventanillas.isNotEmpty()) {
            val primeraUbicacion = ubicacionCoords[ventanillas[0].ubicacion]
            if (primeraUbicacion != null) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(primeraUbicacion, 15f))
            }
        }
    }


    private fun mostrarInfoVentanillas() {
        val sb = StringBuilder()
        for (ventanilla in ventanillas) {
            sb.append("<h3>${ventanilla.ubicacion}</h3>")
            sb.append("<b>Horarios:</b><br>")
            ventanilla.horarios.forEach { horario ->
                sb.append("&emsp;• $horario<br>")
            }
            sb.append("<b>Trámites:</b><br>")
            ventanilla.tramites.forEach { tramite ->
                sb.append("&emsp;- $tramite<br>")
            }
            sb.append("<hr>") // Línea separadora entre ventanillas
        }

        tvInfoVentanillas.text = Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY)
    }
}
