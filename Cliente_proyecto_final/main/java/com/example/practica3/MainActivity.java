package com.example.practica3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.practica3.Datos.Servidor;
import com.example.practica3.Datos.Usuario;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import java.io.Serializable;
import java.net.MalformedURLException;

public class MainActivity extends AppCompatActivity {
    private Button botonIniciarSesion;
    private TextView textViewRegistro;
    private EditText editTextUsuario;
    private EditText editTextContrasenia;
    private String usuarioIntroducido = "";
    private String contraseniaIntroducida = "";
    private final String VARIABLE_TIPO_USUARIO = "tipoUsuario";
    private final int ID_VACIO = 0;
    private final String CAMPO_VACIO = " ";
    private MobileServiceClient conexionServidorApi;
    private final String ERROR_USUARIO_NO_ENCONTRADO= "Error, usuario no encontrado";
    private Context context;
    private Toast toast;
    private int duration = Toast.LENGTH_SHORT;
    private Servidor servidor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        servidor = new Servidor();
        conectarServidor();
        enlazarVistas();
    }

    private void enlazarVistas(){
        editTextUsuario = (EditText) findViewById(R.id.editTextUsuario);
        editTextContrasenia = (EditText) findViewById(R.id.editTextContrasenia);

        botonIniciarSesion = (Button) findViewById(R.id.botonIniciarSesion);
        botonIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionalidadBotonLogin();
            }
        });
        textViewRegistro = findViewById(R.id.textClickRegistroUsuario);
        textViewRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityRegistroUsuario();
            }
        });
    }

    private void conectarServidor(){
        conexionServidorApi = null;
        try{
            conexionServidorApi = new MobileServiceClient(servidor.URL_SERVIDOR,this);
        }catch (MalformedURLException e){
        }
    }

    public void comprobarLoginUsuario(String nombreUsuario, String contrasenia){
        Usuario usuario = new Usuario(ID_VACIO,nombreUsuario,CAMPO_VACIO,CAMPO_VACIO,contrasenia);
        final ListenableFuture<Usuario> peticionLogin =
                conexionServidorApi.invokeApi(servidor.API_LOGIN_USUARIO, usuario, Usuario.class);

        Futures.addCallback(peticionLogin, new FutureCallback<Usuario>() {
            @Override
            public void onFailure(Throwable exc) {
                context = getApplicationContext();
                toast = Toast.makeText(context, ERROR_USUARIO_NO_ENCONTRADO, duration);
                toast.show();
            }
            @Override
            public void onSuccess(Usuario respuesta) {
                if(respuesta != null){
                    openActivityProductos(respuesta);
                }else{
                    context = getApplicationContext();
                    toast = Toast.makeText(context, ERROR_USUARIO_NO_ENCONTRADO, duration);
                    toast.show();
                }
            }
        });
    }

    private void funcionalidadBotonLogin(){
        usuarioIntroducido = editTextUsuario.getText().toString();
        contraseniaIntroducida = editTextContrasenia.getText().toString();
        comprobarLoginUsuario(usuarioIntroducido, contraseniaIntroducida);
    }

    private void openActivityRegistroUsuario(){
        Intent activityRegistroUsuario = new Intent(this, MenuRegistroUsuario.class);
        startActivity(activityRegistroUsuario);
    }

    private void openActivityProductos(Usuario usuario){
        Intent activityProductos = new Intent(this, Productos.class);
        activityProductos.putExtra(VARIABLE_TIPO_USUARIO, (Serializable) usuario);
        startActivity(activityProductos);
    }
}
