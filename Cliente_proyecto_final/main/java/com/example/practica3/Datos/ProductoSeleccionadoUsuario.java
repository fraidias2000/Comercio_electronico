package com.example.practica3.Datos;

import java.io.Serializable;

public class ProductoSeleccionadoUsuario implements Serializable {

    private int id;
    private String nombre;
    private double precio;
    private int cantidad;
    private int cantidadBD;


    public ProductoSeleccionadoUsuario(int id, String nombre, double precio, int cantidad, int cantidadBD) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.cantidadBD = cantidadBD;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public int getCantidadBD() {
        return cantidadBD;
    }

    public double getPrecio() {
        return precio;
    }
}
