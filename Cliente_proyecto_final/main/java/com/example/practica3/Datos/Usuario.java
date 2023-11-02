package com.example.practica3.Datos;

import java.io.Serializable;

public class Usuario implements Serializable {

    public int id;
    public String nombre;
    public String apellidos;
    public String email;
    public String contrasenia;

    public Usuario(int id, String nombre, String apellidos, String email, String contrasenia) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.contrasenia = contrasenia;
    }
}
