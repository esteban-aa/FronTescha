package com.example.teschachatbot_f

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class DestinosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destinos)

        val destinos = mapOf(
            R.id.btnBiblioteca to Pair("Biblioteca", Pair(19.5045, -98.8793)),
            R.id.btnAuditorio to Pair("Auditorio", Pair(19.5040, -98.8790)),
            R.id.btnGym to Pair("Gym", Pair(19.5038, -98.8795)),
            R.id.btnCafeteria to Pair("Cafetería", Pair(19.5042, -98.8791)),
            R.id.btnCanchas to Pair("Canchas", Pair(19.5043, -98.8797)),
            R.id.btnSorJuana to Pair("Edificio Sor Juana", Pair(19.5041, -98.8792)),
            R.id.btnBicentenario to Pair("Edificio Bicentenario", Pair(19.5046, -98.8789)),
            R.id.btnRevolucion to Pair("Edificio Revolución", Pair(19.5047, -98.8788)),
            R.id.btnNeza to Pair("Edificio Nezahualcóyotl", Pair(19.5048, -98.8794)),
            R.id.btnMorelos to Pair("Edificio Morelos", Pair(19.5049, -98.8796))
        )

        destinos.forEach { (btnId, data) ->
            findViewById<Button>(btnId).setOnClickListener {
                val intent = Intent(this, MapsActivity::class.java)
                intent.putExtra("nombre", data.first)
                intent.putExtra("lat", data.second.first)
                intent.putExtra("lng", data.second.second)
                startActivity(intent)
            }
        }
    }
}
