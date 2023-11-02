package com.example.practica3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.DropInClient;
import com.braintreepayments.api.DropInRequest;
import com.braintreepayments.api.DropInResult;
import com.example.practica3.Datos.ListaProductos;
import com.example.practica3.Datos.Mail;
import com.example.practica3.Datos.Producto;
import com.example.practica3.Datos.ProductoSeleccionadoUsuario;
import com.example.practica3.Datos.Servidor;
import com.example.practica3.Datos.TokenNonce;
import com.example.practica3.Datos.Usuario;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Properties;

import javax.mail.Session;

public class TramitarPedido extends AppCompatActivity {
    private MobileServiceClient conexionServidorApi;

    private final String MENSAJE_GALLINA_JAULA = "Huevos de gallina en jaula:";
    private final String MENSAJE_GALLINA_CAMPERA = "Huevos de gallina campera:";
    private final String MENSAJE_CLARA_HUEVO= "Claras de huevo:";
    private final String ERROR_CANTIDAD_PRODUCTO = "No hay stock suficiente de este producto";
    private final String SUGERRIR_PRODUCTO = "Hemos visto que no has seleccionado este producto, ¿no te interesa?";
    private final String CABECERA_CORREO = "Informe sobre su pedido en integraciones agricolas SL";
    private final String CORREO_EXITO = "Exito al enviar correo";
    private final String CORREO_ERROR = "Error al enviar correo";
    private final String DESPEDIDA_CORREO = "Gracias por confiar en nosotros";
    private final String VARIABLE_TIPO_LISTA_PRODUCTOS = "listaProductos";
    private final String ERROR_CORREO = "Error al enviar el correo";
    private final String EXITO_CORREO = "Exito al enviar correo";
    private final String TEXTO_PRECIO_PAGAR = "Precio a pagar: ";
    private final String TEXTO_CORREO_PARTE_1 = "Buenas ";
    private final String TEXTO_CORREO_PARTE_2 = " su pedido es el siguiente: ";
    private final String TEXTO_CORREO_PARTE_3 = "Su importe es de ";
    private final String EURO_SIMBOLO = " €.";
    private final String MailApp = "MailApp";
    private final String ESPACIO = "                                         ";
    private int DROP_IN_REQUEST_CODE = 111;
    private String paymentMethodNonce = "";
    private final String HUEVOS_JAULA_SELECCIONADOS = "Huevos Gallina Jaula";
    private final String HUEVOS_CAMPEROS_SELECCIONADOS = "Huevos Gallina Campera";
    private final String CLARA_HUEVOS_SELECCIONADOS = "Clara huevos gallina";
    private final String VARIABLE_TIPO_USUARIO = "tipoUsuario";
    private ProductoSeleccionadoUsuario gallinaJaula;
    private ProductoSeleccionadoUsuario gallinaCampera;
    private ProductoSeleccionadoUsuario claraHuevo;
    private TextView nombresProductos[];
    private TextView cantidadesProductos[];
    private TextView sugerencias[];
    private TextView cantidadPagar;
    private Button decrementar[];
    private Button incrementar[];
    private Button botonPagar;
    private int huevosGallinaJaula = 0;
    private int huevosGallinaCampera = 0;
    private int claras = 0;
    private Context context;
    private Toast toast;
    private int duration = Toast.LENGTH_SHORT;
    private Usuario user;
    private double precioPagar = 0;
    private ListaProductos listaProductos;
    private Servidor servidor;
    private Producto productoAux;
    // Variables para enviar correo
    private String correo = "soportetecnicoteleahorro@gmail.com";
    private String contrasenia = "Unizar1234";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tramitar_pedido);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        productoAux = new Producto();
        servidor = new Servidor();
        conexionServidor();
        gallinaJaula = (ProductoSeleccionadoUsuario) getIntent().getSerializableExtra(HUEVOS_JAULA_SELECCIONADOS);
        gallinaCampera = (ProductoSeleccionadoUsuario) getIntent().getSerializableExtra(HUEVOS_CAMPEROS_SELECCIONADOS);
        claraHuevo = (ProductoSeleccionadoUsuario) getIntent().getSerializableExtra(CLARA_HUEVOS_SELECCIONADOS);
        user = (Usuario) getIntent().getSerializableExtra(VARIABLE_TIPO_USUARIO);
        listaProductos =  (ListaProductos) getIntent().getSerializableExtra(VARIABLE_TIPO_LISTA_PRODUCTOS);
        enlazarVistas();
    }

    private void enlazarVistas(){
        cantidadPagar = (TextView) findViewById(R.id.textViewPrecioFinal);
        botonPagar = (Button) findViewById(R.id.buttonPagar);

        sugerencias = new TextView[3];
        sugerencias[0] = (TextView) findViewById(R.id.textViewSugerenciaHuevoEnJaula);
        sugerencias[1] = (TextView) findViewById(R.id.textViewSugerenciaHuevoCamperos);
        sugerencias[2] = (TextView) findViewById(R.id.textViewSugerenciaClaraHuevo);

        nombresProductos = new TextView[3];
        nombresProductos[0]  = (TextView) findViewById(R.id.textViewCabeceraHuevosJaula);
        nombresProductos[1]  = (TextView) findViewById(R.id.textViewCabeceraHuevoCampero);
        nombresProductos[2]  = (TextView) findViewById(R.id.textViewCabeceraClaraHuevo);

        cantidadesProductos = new TextView[3];
        cantidadesProductos[0]  = (TextView) findViewById(R.id.textViewCantidadHuevoJaula);
        cantidadesProductos[1]  = (TextView) findViewById(R.id.textViewCantidadHuevoCampero);
        cantidadesProductos[2]  = (TextView) findViewById(R.id.textViewCantidadClaraHuevo);

        nombresProductos[0].setText(gallinaJaula.getNombre());
        nombresProductos[1].setText(gallinaCampera.getNombre());
        nombresProductos[2].setText(claraHuevo.getNombre());

        cantidadesProductos[0].setText(ESPACIO + String.valueOf(gallinaJaula.getCantidad()));
        cantidadesProductos[1].setText(ESPACIO + String.valueOf(gallinaCampera.getCantidad()));
        cantidadesProductos[2].setText( ESPACIO + String.valueOf(claraHuevo.getCantidad()));

        huevosGallinaJaula = gallinaJaula.getCantidad();
        huevosGallinaCampera = gallinaCampera.getCantidad();
        claras = claraHuevo.getCantidad();

         decrementar = new Button[3];
         decrementar [0] = (Button) findViewById(R.id.buttonDecrementarHuevoJaula);
         decrementar [1] = (Button) findViewById(R.id.buttonDecrementarHuevoCampero);
         decrementar [2] = (Button) findViewById(R.id.buttonDecrementarClaraHuevo);

        botonPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getToken(user);
            }
        });

        decrementar[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(huevosGallinaJaula  > 0){
                    huevosGallinaJaula --;
                    cantidadesProductos[0].setText(ESPACIO + String.valueOf(huevosGallinaJaula));
                    calcularPrecio();
                }
            }
        });

        decrementar[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(huevosGallinaCampera  > 0){
                    huevosGallinaCampera --;
                    cantidadesProductos[1].setText(ESPACIO + String.valueOf(huevosGallinaCampera));
                    calcularPrecio();
                }
            }
        });

        decrementar[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(claras  > 0){
                    claras --;
                    cantidadesProductos[2].setText(ESPACIO + String.valueOf(claras));
                    calcularPrecio();
                }
            }
        });

         incrementar = new Button[3];
         incrementar [0]= (Button) findViewById(R.id.buttonAumentarHuevoJaula);
         incrementar [1]= (Button) findViewById(R.id.buttonAumentarHuevoCampero);
         incrementar [2]= (Button) findViewById(R.id.buttonAumentarClaraHuevo);

        incrementar[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(huevosGallinaJaula < gallinaJaula.getCantidadBD()  ){
                    huevosGallinaJaula++;
                    cantidadesProductos[0].setText(ESPACIO + String.valueOf(huevosGallinaJaula));
                    calcularPrecio();
                }else{
                    context = getApplicationContext();
                    toast = Toast.makeText(context,    ERROR_CANTIDAD_PRODUCTO, duration);
                    toast.show();
                }
            }
        });

        incrementar[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(huevosGallinaCampera < gallinaCampera.getCantidadBD()  ){
                    huevosGallinaCampera++;
                    cantidadesProductos[1].setText(ESPACIO + String.valueOf(huevosGallinaCampera));
                    calcularPrecio();
                }else{
                    context = getApplicationContext();
                    toast = Toast.makeText(context,    ERROR_CANTIDAD_PRODUCTO, duration);
                    toast.show();
                }
            }
        });

        incrementar[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(claras < claraHuevo.getCantidadBD()  ){
                    claras++;
                    cantidadesProductos[2].setText(ESPACIO + String.valueOf(claras));
                    calcularPrecio();
                }else{
                    context = getApplicationContext();
                    toast = Toast.makeText(context,    ERROR_CANTIDAD_PRODUCTO, duration);
                    toast.show();
                }
            }
        });

        if(gallinaJaula.getCantidad() == 0){
            sugerencias[0].setText(SUGERRIR_PRODUCTO);
        }
        if(gallinaCampera.getCantidad() == 0){
            sugerencias[1].setText(SUGERRIR_PRODUCTO);

        }
        if(claraHuevo.getCantidad()==0){
            sugerencias[2].setText(SUGERRIR_PRODUCTO);
        }
        calcularPrecio();
    }

    private void conexionServidor(){
        conexionServidorApi = null;
        try{
            conexionServidorApi = new MobileServiceClient(servidor.URL_SERVIDOR,this);
        }catch (MalformedURLException e){
        }
    }

    private void calcularPrecio(){
        double precioProducto1 = 0;
        double precioProducto2 = 0;
        double precioProducto3= 0;
        double suma = 0;

        precioProducto1 = huevosGallinaJaula * gallinaJaula.getPrecio();
        precioProducto2 = huevosGallinaCampera * gallinaCampera.getPrecio();
        precioProducto3 = claras * claraHuevo.getPrecio();
        suma = precioProducto1 +precioProducto2 + precioProducto3;

        cantidadPagar.setText(TEXTO_PRECIO_PAGAR + String.valueOf(suma));
        precioPagar = suma;
    }

    private void getToken(Usuario customerUser){
        final ListenableFuture<String> peticion =
                conexionServidorApi.invokeApi(servidor.API_TOKEN, customerUser, String.class);

        Futures.addCallback(peticion, new FutureCallback<String>() {
            @Override
            public void onFailure(Throwable exc) {
                context = getApplicationContext();
                toast = Toast.makeText(context,    "ERROR " + exc, duration);
                toast.show();
            }
            @Override
            public void onSuccess(String token) {
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
            } else {
                Exception exception = (Exception) data.getSerializableExtra(DropInResult.EXTRA_ERROR);
            }
        }
    }

    private void sendNonce(String nonce){ //llamadaApi()
        TokenNonce nonceCompra = new TokenNonce(nonce, precioPagar);
        final ListenableFuture<String> peticionNonce =
                conexionServidorApi.invokeApi(servidor.API_NONCE, nonceCompra, String.class);
        Futures.addCallback(peticionNonce, new FutureCallback<String>() {
            @Override
            public void onFailure(Throwable exc) {
                openActivityErrorCompra();
            }
            @Override
            public void onSuccess(String respuesta) {
                listaProductos.misProductos.get(0).setCantidadProducto(huevosGallinaJaula);
                listaProductos.misProductos.get(1).setCantidadProducto(huevosGallinaCampera);
                listaProductos.misProductos.get(2).setCantidadProducto(claras);

                //Actualizar productos
              /*  if(huevosGallinaJaula > 0){
                    productoAux.setId_producto(1);
                    productoAux.setCantidadProducto(huevosGallinaJaula);
                    actualizarProductos(productoAux);
                }
                if(huevosGallinaCampera > 0){
                    productoAux.setId_producto(2);
                    productoAux.setCantidadProducto(huevosGallinaCampera);
                    actualizarProductos(productoAux);
                }
                if(claras > 0){
                    productoAux.setId_producto(3);
                    productoAux.setCantidadProducto(claras);
                    actualizarProductos(productoAux);
                }*/
                sendEmail();
                openActivityExitoCompra();
            }
        });
    }

    private String generarMensajeGemail(){
        String mensaje = TEXTO_CORREO_PARTE_1 + user.nombre + TEXTO_CORREO_PARTE_2 + '\n';
        if (huevosGallinaJaula > 0){
            mensaje += MENSAJE_GALLINA_JAULA + huevosGallinaJaula + '\n';
        }
        if (huevosGallinaCampera > 0){
            mensaje += MENSAJE_GALLINA_CAMPERA + huevosGallinaCampera + '\n';
        }
        if (claras > 0){
            mensaje += MENSAJE_CLARA_HUEVO + claras + '\n';
        }
        mensaje+= TEXTO_CORREO_PARTE_3 + precioPagar + EURO_SIMBOLO + '\n' +
                   DESPEDIDA_CORREO;
        return mensaje;
    }

    private void actualizarProductos(Producto producto){
        final ListenableFuture<String> peticion =
                conexionServidorApi.invokeApi(servidor.API_ACTUALIZAR_PRODUCTOS, producto, String.class);

        Futures.addCallback(peticion, new FutureCallback<String>() {
            @Override
            public void onFailure(Throwable exc) {
                context = getApplicationContext();
                toast = Toast.makeText(context, exc.toString(), duration);
                toast.show();
            }
            @Override
            public void onSuccess(String token) {
                onBraintreeSubmit(token);
            }
        });
    }

    private void sendEmail(){
        Mail m = new Mail(correo, contrasenia);
        String[] toArr = {user.email};
        m.set_to(toArr);
        m.set_from(correo); //FROM
        m.set_subject(CABECERA_CORREO); //CABECERA CORREO
        m.setBody(generarMensajeGemail()); //CUERPO EMAIL

        try {
            //  m.addAttachment("/sdcard/filelocation"); SIRVE PARA MANDAR ARCHIVOS
            if (m.send()) {
                context = getApplicationContext();
                toast = Toast.makeText(context,    EXITO_CORREO, duration);
                toast.show();
            } else {
                context = getApplicationContext();
                toast = Toast.makeText(context,    ERROR_CORREO, duration);
                toast.show();
            }
        } catch (Exception e) {
            //Toast.makeText(MailApp.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show();
            Log.e(MailApp,ERROR_CORREO , e);
        }

    }

    private void openActivityErrorCompra(){
        Intent activityErrorCompra = new Intent(this, ErrorCompra.class);
        startActivity(activityErrorCompra);
    }
    private void openActivityExitoCompra(){
        Intent activityExitoCompra = new Intent(this, CompraExitosa.class);
        startActivity(activityExitoCompra);
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