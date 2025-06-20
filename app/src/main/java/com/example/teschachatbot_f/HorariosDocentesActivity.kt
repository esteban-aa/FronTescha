package com.example.teschachatbot_f

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.teschachatbot_f.models.Profesor
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

class HorariosDocentesActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var tablaHorarios: TableLayout
    private lateinit var googleMap: GoogleMap

    private val profesores = mutableListOf<Profesor>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_horarios_docentes)

        tablaHorarios = findViewById(R.id.tablaHorarios)

        // Configura el mapa
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        obtenerProfesores()
    }

    private fun obtenerProfesores() {
        val apiService = RetrofitClient.getInstance().create(ApiService::class.java)
        apiService.getProfesores().enqueue(object : Callback<List<Profesor>> {
            override fun onResponse(call: Call<List<Profesor>>, response: Response<List<Profesor>>) {
                if (response.isSuccessful && response.body() != null) {
                    profesores.clear()
                    profesores.addAll(response.body()!!)
                    agregarFilasDinamicamente(profesores)
                    mostrarMarcadoresEnMapa()
                } else {
                    Toast.makeText(this@HorariosDocentesActivity, "Error al obtener datos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Profesor>>, t: Throwable) {
                Toast.makeText(this@HorariosDocentesActivity, "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun agregarFilasDinamicamente(profesores: List<Profesor>) {
        // Limpia filas previas excepto encabezado
        val header = tablaHorarios.getChildAt(0)
        tablaHorarios.removeAllViews()
        tablaHorarios.addView(header)

        for (profesor in profesores) {
            val fila = TableRow(this)
            fila.layoutParams = TableRow.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            val nombre = profesor.nombre ?: "Sin nombre"
            val horarios = profesor.horarios?.joinToString("\n") ?: "Sin horarios"

            // Evitar mostrar "null" para edificio y salón
            val edificio = profesor.edificio ?: "Desconocido"
            val salon = profesor.salon ?: "Desconocido"
            val ubicacion = "$edificio / $salon"

            val tvNombre = crearCelda(nombre)
            val tvHorario = crearCelda(horarios)
            val tvUbicacion = crearCelda(ubicacion)

            fila.addView(tvNombre)
            fila.addView(tvHorario)
            fila.addView(tvUbicacion)

            tablaHorarios.addView(fila)
        }
    }

    private fun crearCelda(texto: String): TextView {
        val tv = TextView(this)
        tv.text = texto
        tv.setPadding(8, 8, 8, 8)
        tv.gravity = Gravity.CENTER
        tv.layoutParams = TableRow.LayoutParams(
            0,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            1f
        )
        return tv
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        mostrarMarcadoresEnMapa()
    }

    private fun mostrarMarcadoresEnMapa() {
        if (!::googleMap.isInitialized) return

        googleMap.clear()

        for (profesor in profesores) {
            val coords = profesor.coordenadas
            if (coords == null) {
                Log.w("HorariosDocentesActivity", "El profesor ${profesor.nombre} tiene coordenadas null")
                continue
            }
            if (coords.size < 2) {
                Log.w("HorariosDocentesActivity", "El profesor ${profesor.nombre} tiene coordenadas insuficientes")
                continue
            }

            val posicion = LatLng(coords[0], coords[1])
            googleMap.addMarker(
                MarkerOptions()
                    .position(posicion)
                    .title(profesor.nombre)
                    .snippet(profesor.horarios?.joinToString("\n") ?: "")
            )
        }

        if (profesores.isNotEmpty()) {
            val primeraUbicacionCoords = profesores[0].coordenadas
            if (primeraUbicacionCoords != null && primeraUbicacionCoords.size >= 2) {
                val primeraUbicacion = LatLng(primeraUbicacionCoords[0], primeraUbicacionCoords[1])
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(primeraUbicacion, 15f))
            }
        }
    }
}
