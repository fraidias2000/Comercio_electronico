package com.example.practica3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ErrorCompra extends AppCompatActivity {
    private Button botonVolver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_compra);
        enlazarVistas();
    }

    private void enlazarVistas(){
        botonVolver = (Button) findViewById(R.id.buttonAceptarActivityCompraExitosa);
        botonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityComprarProducto();
            }
        });
    }

    private void openActivityComprarProducto(){
        Intent activityComprarProducto = new Intent(this, ComprarProducto.class);
        startActivity(activityComprarProducto);
    }
}