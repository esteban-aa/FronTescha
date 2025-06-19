package com.example.teschachatbot_f

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class DestinosActivity : AppCompatActivity() {

    private lateinit var tipoUsuario: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destinos)

        // Recibir tipo de usuario desde LoginActivity
        tipoUsuario = intent.getStringExtra("tipoUsuario") ?: "usuario"

        val destinos = mapOf(
            R.id.btnBiblioteca to "Biblioteca",
            R.id.btnAuditorio to "Auditorio Principal",
            R.id.btnGym to "Gymnasio",
            R.id.btnCafeteria to "Cafetería",
            R.id.btnCanchas to "Canchas",
            R.id.btnSorJuana to "Sor Juana",
            R.id.btnBicentenario to "Bicentenario",
            R.id.btnRevolucion to "Revolución",
            R.id.btnNeza to "Nezahualcoyotl",
            R.id.btnMorelos to "Morelos"
        )

        destinos.forEach { (btnId, nombreEdificio) ->
            findViewById<Button>(btnId).setOnClickListener {
                val intent = Intent(this, LocationInfoActivity::class.java)
                intent.putExtra("nombreEdificio", nombreEdificio)
                intent.putExtra("tipoUsuario", tipoUsuario) // ← envías el tipo correcto
                startActivity(intent)
            }
        }
    }
}
