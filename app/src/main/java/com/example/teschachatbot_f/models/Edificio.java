package com.example.teschachatbot_f.models;

public class Edificio {
    private String nombre;
    private String descripcion;
    private double[] coordenadas;

    // Constructor vacío
    public Edificio() {}

    // Getter y Setter para nombre
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Getter y Setter para descripcion
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // Getter y Setter para coordenadas
    public double[] getCoordenadas() {
        return coordenadas;
    }
    public void setCoordenadas(double[] coordenadas) {
        this.coordenadas = coordenadas;
    }
}
