package com.example.teschachatbot_f.models;

import java.util.List;

public class Profesor {
    private String nombre;
    private List<String> horarios;
    private String edificio;
    private String salon;
    private List<Double> coordenadas;  // [latitud, longitud]

    // Getters y setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public List<String> getHorarios() { return horarios; }
    public void setHorarios(List<String> horarios) { this.horarios = horarios; }

    public String getEdificio() { return edificio; }
    public void setEdificio(String edificio) { this.edificio = edificio; }

    public String getSalon() { return salon; }
    public void setSalon(String salon) { this.salon = salon; }

    public List<Double> getCoordenadas() { return coordenadas; }
    public void setCoordenadas(List<Double> coordenadas) { this.coordenadas = coordenadas; }
}
