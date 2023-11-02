package com.example.practica3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import java.net.MalformedURLException;

public class MenuIngresarDatosUsuarioCompraProducto extends AppCompatActivity {

    private MobileServiceClient conexionServidorApi;
    private final String URL_SERVIDOR = "http://appservicecomercioelectronico.azurewebsites.net/";
    private final String API_TOKEN = "GetToken";
    private final int DROP_IN_REQUEST_CODE = 111;
    private final String VARIABLE_TIPO_NONCE = "tipoNonce";
    private final String MENSAJE_CREANDO_USUARIO = "Creando usuario, un momento...";
    private final String ERROR_CREAR_USUARIO = "Error al crear usuario";

    private String nonceUsuario = "";
    private String nombre = " ";
    private String apellidos = " ";
    private String correoElectronico = " ";
    private String cantidadProducto = "";
    private CustomerUser customerUser = null;

    private EditText editTextNombre;
    private EditText editTextApellidos;
    private EditText editTextCorreoElectronico;

    private Button botonTramitarPedido;
    private Context context;
    private int duration = Toast.LENGTH_SHORT;
    private Toast toastCrearUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_ingresar_datos_usuario_comprar_producto);
        enlazarVistas();
        probarConexionServidor();
    }

    private void enlazarVistas(){
        editTextNombre = findViewById(R.id.editextNombreUsuario);
        editTextApellidos = findViewById(R.id.editextApellido);
        editTextCorreoElectronico = findViewById(R.id.editextCorreoElectronico);
        botonTramitarPedido = (Button) findViewById(R.id.botonTramitarPedido);
        botonTramitarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nombre = editTextNombre.getText().toString();
                apellidos = editTextApellidos.getText().toString();
                correoElectronico = editTextCorreoElectronico.getText().toString();
                customerUser = new CustomerUser(nombre, apellidos, correoElectronico);

                context = getApplicationContext();
                toastCrearUsuario = Toast.makeText(context,MENSAJE_CREANDO_USUARIO, duration);
                toastCrearUsuario.show();
                getToken(customerUser);
            }
        });
    }

    private void probarConexionServidor(){
        conexionServidorApi = null;
        try{
            conexionServidorApi = new MobileServiceClient(URL_SERVIDOR,this);
        }catch (MalformedURLException e){
        }
    }

    private void getToken(CustomerUser customerUser){
        final ListenableFuture<String> peticion =
                conexionServidorApi.invokeApi(API_TOKEN, customerUser, String.class);

        Futures.addCallback(peticion, new FutureCallback<String>() {
            @Override
            public void onFailure(Throwable exc) {
                toastCrearUsuario = Toast.makeText(context,ERROR_CREAR_USUARIO, duration);
            }
            @Override
            public void onSuccess(String token) {
                nonceUsuario = token;
                openActivityComprarProducto();
            }
        });
    }

    private void openActivityComprarProducto(){
        Intent activityComprarProducto = new Intent(this, ComprarProducto.class);
        activityComprarProducto.putExtra(VARIABLE_TIPO_NONCE, nonceUsuario);
        startActivity(activityComprarProducto);
    }

}