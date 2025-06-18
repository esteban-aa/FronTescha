package com.example.teschachatbot_f;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.IOException;

public class ReadFileActivity extends AppCompatActivity {

    private TextView textViewContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_file);

        textViewContent = findViewById(R.id.textViewContent);

        String filename = "ejemplo.txt"; // Nombre del archivo que leerás

        String content = readFile(filename);
        if (content != null) {
            textViewContent.setText(content);
        } else {
            textViewContent.setText("No se pudo leer el archivo.");
        }
    }

    private String readFile(String filename) {
        FileInputStream fis = null;
        try {
            fis = openFileInput(filename);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            return new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
