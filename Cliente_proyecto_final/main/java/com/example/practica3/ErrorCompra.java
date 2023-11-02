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

        botonVolver = (Button) findViewById(R.id.buttonErrorCompra);
        botonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityMain();
            }
        });
    }

    private void openActivityMain(){
        Intent activityComprarProducto = new Intent(this, MainActivity.class);
        startActivity(activityComprarProducto);
    }
}