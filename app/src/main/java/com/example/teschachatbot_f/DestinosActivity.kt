package com.example.teschachatbot_f

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class DestinosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destinos)


        // 🌍 Mapa de destinos
        val destinos = mapOf(
            R.id.btnBiblioteca to Pair("Biblioteca", Pair(19.2332447, -98.8424854)),
            R.id.btnAuditorio to Pair("Auditorio", Pair(19.2334900, -98.8417448)),
            R.id.btnGym to Pair("Gym", Pair(19.2332757, -98.8401254)),
            R.id.btnCafeteria to Pair("Cafetería", Pair(19.2333564, -98.8412603)),
            R.id.btnCanchas to Pair("Canchas", Pair(19.2336799, -98.8399960)),
            R.id.btnSorJuana to Pair("Edificio Sor Juana", Pair(19.2331431, -98.8419580)),
            R.id.btnBicentenario to Pair("Edificio Bicentenario", Pair(19.2328927, -98.8412992)),
            R.id.btnRevolucion to Pair("Edificio Revolución", Pair(19.2337803, -98.8413421)),
            R.id.btnNeza to Pair("Edificio Nezahualcóyotl", Pair(19.2333707, -98.8404751)),
            R.id.btnMorelos to Pair("Edificio Morelos", Pair(19.2324732, -98.8408224))
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
