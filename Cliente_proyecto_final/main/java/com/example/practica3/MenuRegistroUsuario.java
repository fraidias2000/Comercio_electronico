package com.example.practica3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.practica3.Datos.Servidor;
import com.example.practica3.Datos.Usuario;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import java.net.MalformedURLException;

public class MenuRegistroUsuario extends AppCompatActivity {
    private MobileServiceClient conexionServidorApi;
    private String ERROR_TERMINOS_CONDICIONES = "Debe aceptar los terminos y condiciones";
    private String nombre = "";
    private String apellidos = "";
    private String correoElectronico = "";
    private String contrasenia = "";
    private EditText editTextNombre;
    private EditText editTextApellidos;
    private EditText editTextCorreoElectronico;
    private EditText editTextContrasenia;
    private Button botonTramitarPedido;
    private final String ERROR_REGISTRAR_USUARIO= "Error al crear usuario";
    private Context context;
    private Toast toast;
    private int duration = Toast.LENGTH_SHORT;
    private Usuario usuario;
    private CheckBox checkBox;
    private Servidor servidor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_registro_usuario);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        servidor = new Servidor();
        enlazarVistar();
        conexionServidor();
    }

    private void enlazarVistar(){
        editTextNombre = findViewById(R.id.editextNombreUsuarioActivityIngresarDatosUsuasio);
        editTextApellidos = findViewById(R.id.editextApellidoActivityIngresarDatosUsuasio);
        editTextCorreoElectronico = findViewById(R.id.editextCorreoElectronicoActivityIngresarDatosUsuasio);
        editTextContrasenia = findViewById(R.id.editextContraseniaUsuarioActivityIngresarDatosUsuasio);
        botonTramitarPedido = (Button) findViewById(R.id.botonTramitarPedidoActivityIngresarDatosUsuasio);

        checkBox = (CheckBox) findViewById(R.id.checkBoxTerminosCondiciones);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityTerminosCondiciones();
            }
        });

        botonTramitarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionalidadBotonTramitarPedido();
            }
        });
    }

    private void conexionServidor(){
        conexionServidorApi = null;
        try{
            conexionServidorApi = new MobileServiceClient(servidor.URL_SERVIDOR,this);
        }catch (MalformedURLException e){
        }
    }
    public void registrarUsuario(Usuario usuario){
        final ListenableFuture<Usuario> peticionLogin =
                conexionServidorApi.invokeApi(servidor.API_REGISTRO_USUARIO, usuario, Usuario.class);
        Futures.addCallback(peticionLogin, new FutureCallback<Usuario>() {
            @Override
            public void onFailure(Throwable exc) {
                context = getApplicationContext();
                toast = Toast.makeText(context, "" + exc, duration);
                toast.show();
            }
            @Override
            public void onSuccess(Usuario respuesta) {
                if(respuesta != null){
                    openActivityMain();
                }else{
                    context = getApplicationContext();
                    toast = Toast.makeText(context, ERROR_REGISTRAR_USUARIO, duration);
                    toast.show();
                }
            }
        });
    }
    private void funcionalidadBotonTramitarPedido(){
        if(checkBox.isChecked()){
            nombre = editTextNombre.getText().toString();
            apellidos = editTextApellidos.getText().toString();
            correoElectronico = editTextCorreoElectronico.getText().toString();
            contrasenia = editTextContrasenia.getText().toString();
            usuario = new Usuario(0,nombre,apellidos,correoElectronico,contrasenia);
            registrarUsuario(usuario);
        }else{
            context = getApplicationContext();
            toast = Toast.makeText(context, ERROR_TERMINOS_CONDICIONES, duration);
            toast.show();
        }
    }

    private void openActivityMain(){
        Intent activityMain = new Intent(this, MainActivity.class);
        startActivity(activityMain);
    }
    private void openActivityTerminosCondiciones(){
        Intent activityTerminosCondiciones = new Intent(this, TerminosCondiciones.class);
        startActivity(activityTerminosCondiciones);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}