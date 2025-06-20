package com.example.teschachatbot_f.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "usuario")
public class Usuario {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String rol;           // "Usuario" o "Estudiante"
    private String identificador; // correo o matrícula
    private String password;

    private String idBackend;     // ID en el backend (MongoDB)

    public Usuario(String rol, String identificador, String password) {
        this.rol = rol;
        this.identificador = identificador;
        this.password = password;
    }

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getIdentificador() { return identificador; }
    public void setIdentificador(String identificador) { this.identificador = identificador; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getIdBackend() { return idBackend; }
    public void setIdBackend(String idBackend) { this.idBackend = idBackend; }


}
