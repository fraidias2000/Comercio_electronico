package com.example.practica3.Datos;

import java.io.Serializable;

public class Producto implements Serializable {
    public int id_producto;
    public String nombre;
    public double precio ;
    public String descripcion;
    public int cantidadProducto;


    public int getId_producto() {
        return id_producto;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getCantidadProducto() {
        return cantidadProducto;
    }

    public void setCantidadProducto(int cantidadProducto) {
        this.cantidadProducto = cantidadProducto;
    }

    public void setId_producto(int id_producto) {
        this.id_producto = id_producto;
    }
}
