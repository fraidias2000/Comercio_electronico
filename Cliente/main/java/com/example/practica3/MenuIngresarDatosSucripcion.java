package com.example.practica3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.DropInClient;
import com.braintreepayments.api.DropInRequest;
import com.braintreepayments.api.DropInResult;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import java.net.MalformedURLException;

public class MenuIngresarDatosSucripcion extends AppCompatActivity {

    private MobileServiceClient conexionServidorApi;
    private final String URL_SERVIDOR = "http://appservicecomercioelectronico.azurewebsites.net/";
    private final String API_TOKEN = "GetToken";
    private final String API_NONCE_SUSCRIPCION = "NonceSuscripcion";
    private final int DROP_IN_REQUEST_CODE = 111;
    private final String VARIABLE_TIPO_NONCE = "tipoNonce";
    private final String ERROR_CREAR_USUARIO = "Error al crear usuario";
    private final String SUSCRIPCION_MENSUAL = "Mensual";
    private final String SUSCRIPCION_ANUAL = "Anual";
    private String nonceUsuario = "";
    private String nombre = " ";
    private String apellidos = " ";
    private String correoElectronico = " ";
    private String cantidadProducto = "";
    private CustomerUser customerUser = null;
    private EditText editTextNombre;
    private EditText editTextApellidos;
    private EditText editTextCorreoElectronico;
    private Button botonTramitarSuscripcion;
    private Context context = null;
    private int duration = Toast.LENGTH_SHORT;
    private Toast toastCrearUsuario;
    private String tipoSuscripcion = "";
    String paymentMethodNonce;

    private CheckBox suscripcionMensual, suscripcionAnual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_ingresar_datos_sucripcion);
        relacionarVistas();
        probarConexionServidor();
    }

    private void probarConexionServidor(){
        conexionServidorApi = null;
        try{
            conexionServidorApi = new MobileServiceClient(URL_SERVIDOR,this);
        }catch (MalformedURLException e){

        }

    }

    private void relacionarVistas(){
        editTextNombre = findViewById(R.id.editextNombreUsuarioSuscripcion);
        editTextApellidos = findViewById(R.id.editextApellidoSuscripcion);
        editTextCorreoElectronico = findViewById(R.id.editextCorreoElectronicoSuscripcion);
        suscripcionMensual = findViewById(R.id.checkBoxSuscripcionMensual);
        suscripcionAnual = findViewById(R.id.checkBoxSuscripcionAnual);

        botonTramitarSuscripcion = (Button) findViewById(R.id.botonTramitarSuscripcion);
        botonTramitarSuscripcion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nombre = editTextNombre.getText().toString();
                apellidos = editTextApellidos.getText().toString();
                correoElectronico = editTextCorreoElectronico.getText().toString();
                customerUser = new CustomerUser(nombre, apellidos, correoElectronico);
                getToken(customerUser);
            }
        });

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
                onBraintreeSubmit(token);
            }
        });
    }
    public void onBraintreeSubmit(String token) {
        DropInRequest dropInRequest = new DropInRequest();
        DropInClient dropInClient = new DropInClient(this, token, dropInRequest);
        dropInClient.launchDropInForResult(this, DROP_IN_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DROP_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                paymentMethodNonce = result.getPaymentMethodNonce().getString();
                sendNonce(paymentMethodNonce);
            } else if (resultCode == RESULT_CANCELED) {
                Exception exception = (Exception) data.getSerializableExtra(DropInResult.EXTRA_ERROR);
            } else {
                Exception exception = (Exception) data.getSerializableExtra(DropInResult.EXTRA_ERROR);
            }
        }
    }

    private void sendNonce(String nonce){
        if(suscripcionMensual.isChecked()){
            tipoSuscripcion = SUSCRIPCION_MENSUAL;

        }else if(suscripcionAnual.isChecked()){
            tipoSuscripcion = SUSCRIPCION_ANUAL;
        }
        TokenNonceSuscripcion nonceSuscripcion = new TokenNonceSuscripcion(nonce, tipoSuscripcion);
        final ListenableFuture<String> peticionNonce =
                conexionServidorApi.invokeApi(API_NONCE_SUSCRIPCION, nonceSuscripcion, String.class);

        Futures.addCallback(peticionNonce, new FutureCallback<String>() {
            @Override
            public void onFailure(Throwable exc) {
                openActivityErrorCompra();

            }
            @Override
            public void onSuccess(String respuesta) {
                openActivityExitoCompra();

            }
        });
    }
    private void openActivityErrorCompra(){
        Intent activityErrorCompra = new Intent(this, ErrorCompra.class);
        startActivity(activityErrorCompra);
    }
    private void openActivityExitoCompra(){
        Intent activityExitoCompra = new Intent(this, CompraExitosa.class);
        startActivity(activityExitoCompra);
    }


    private void openActivityComprarProducto(){
        Intent activityComprarProducto = new Intent(this, ComprarProducto.class);
        activityComprarProducto.putExtra(VARIABLE_TIPO_NONCE, nonceUsuario);
        startActivity(activityComprarProducto);
    }
}
