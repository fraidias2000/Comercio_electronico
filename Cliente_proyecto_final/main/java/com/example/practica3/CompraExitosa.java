package com.example.practica3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CompraExitosa extends AppCompatActivity {

    private Button botonAceptar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compra_exitosa);

        botonAceptar = (Button) findViewById(R.id.buttonAceptarActivityCompraExitosa);
        botonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityMain();
            }
        });
    }
    private void openActivityMain(){
        Intent activityMain = new Intent(this, MainActivity.class);
        startActivity(activityMain);
    }
}