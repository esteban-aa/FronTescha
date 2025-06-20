package com.example.teschachatbot_f.models;

import java.util.List;

public class Ventanilla {
    private String ubicacion;
    private List<String> horarios;
    private List<String> tramites;

    // Getters y setters

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public List<String> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<String> horarios) {
        this.horarios = horarios;
    }

    public List<String> getTramites() {
        return tramites;
    }

    public void setTramites(List<String> tramites) {
        this.tramites = tramites;
    }
}
