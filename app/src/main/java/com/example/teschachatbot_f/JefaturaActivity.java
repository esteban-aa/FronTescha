package com.example.teschachatbot_f;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class JefaturaActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jefatura);

        TextView textView = findViewById(R.id.textoJefatura);
        textView.setText("Bienvenido a la sección de Jefatura de Carrera.");
    }
}
