package com.example.practica3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button botonComprarColeccion;
    private Button botonComprarServicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        enlazarVistas();
    }

    private void enlazarVistas(){
        botonComprarColeccion = (Button) findViewById(R.id.botonCompraColeccion);
        botonComprarColeccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openActivityComprarProducto();
            }
        });

        botonComprarServicio = (Button) findViewById(R.id.botonCompraServicio);
        botonComprarServicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivitySuscripcion();
            }
        });
    }
    private void openActivityComprarProducto(){
        Intent activityPago = new Intent(this, MenuIngresarDatosUsuarioCompraProducto.class);
        startActivity(activityPago);
    }
    private void openActivitySuscripcion(){
        Intent activitySuscripcion = new Intent(this, MenuIngresarDatosSucripcion.class);
        startActivity(activitySuscripcion);
    }

}
