package com.example.practica3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.braintreepayments.api.DropInClient;
import com.braintreepayments.api.DropInRequest;
import com.braintreepayments.api.DropInResult;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import java.net.MalformedURLException;

public class ComprarProducto extends AppCompatActivity {

    private final String URL_SERVIDOR = "http://appservicecomercioelectronico.azurewebsites.net/";
    private final String API_NONCE =  "Nonce";
    private final String VARIABLE_TIPO_NONCE = "tipoNonce";
    private final int DROP_IN_REQUEST_CODE = 111;
    private TextView textViewCantidadProducto;
    private TextView textViewResumenCompra;
    private Button buttonIncrementar;
    private Button buttonDecrementar;
    private Button buttonMostrarInformacionCompra;
    private Button buttonTramitarPedido;
    private int cantidadProducto = 0;
    private int PrecioProducto = 3;
    private int cuentaUsuario = 0;
    private MobileServiceClient conexionServidorApi = null;
    private String paymentMethodNonce = "";
    private String nonceUsuario = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprar_producto);
        enlazarVistas();
        probarConexionServidor();
    }
    private void probarConexionServidor(){
        conexionServidorApi = null;
        try{
            conexionServidorApi = new MobileServiceClient(URL_SERVIDOR,this);
        }catch (MalformedURLException e){

        }
    }
    private void enlazarVistas(){
        textViewCantidadProducto = (TextView) findViewById(R.id.textViewcantidadProducto);
        textViewResumenCompra = (TextView) findViewById(R.id.textViewMostrarInformacionProducto);
        buttonIncrementar = (Button) findViewById(R.id.buttonIncrementar);
        buttonDecrementar = (Button) findViewById(R.id.buttonDecrementar);
        buttonMostrarInformacionCompra = (Button) findViewById(R.id.botonAceptarComprar);
        buttonTramitarPedido = (Button) findViewById(R.id.buttonConfirmarCompra);
        nonceUsuario = getIntent().getStringExtra(VARIABLE_TIPO_NONCE);

        buttonIncrementar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cantidadProducto ++;
                textViewCantidadProducto.setText(String.valueOf(cantidadProducto));
            }
        });

        buttonDecrementar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cantidadProducto --;
                textViewCantidadProducto.setText(String.valueOf(cantidadProducto));
            }
        });

        buttonMostrarInformacionCompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewResumenCompra.setVisibility(View.VISIBLE);
                cuentaUsuario = cantidadProducto * PrecioProducto;
                textViewResumenCompra.setText("La cantidad a pagar por " + String.valueOf(cantidadProducto) +
                        " docenas es de " + String.valueOf(cuentaUsuario) + "€");
            }
        });

        buttonTramitarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBraintreeSubmit(nonceUsuario);
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
                //test.setText("nonce canceled");
            } else {
                //test.setText("nonce exception");
                Exception exception = (Exception) data.getSerializableExtra(DropInResult.EXTRA_ERROR);
            }
        }
    }

    private void sendNonce(String nonce){ //llamadaApi()
        TokenNonce nonceCompra = new TokenNonce(nonce, cantidadProducto);
        //Creamos peticion
        final ListenableFuture<String> peticionNonce =
                conexionServidorApi.invokeApi(API_NONCE, nonceCompra, String.class);

        //Lanzamos peticion
        Futures.addCallback(peticionNonce, new FutureCallback<String>() {
            @Override
            // Acciones a realizar si la llamada devuelve un error
            public void onFailure(Throwable exc) {
                openActivityErrorCompra();

            }
            @Override
            // Acciones a realizar si la llamada devuelve un ok
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

}