package com.example.miapputn2026.models;

public class Ingredient {
    public int id;
    public String nombre;
    public double calorias;
    public boolean estado;

    @Override
    public String toString() {
        return nombre;
    }
}