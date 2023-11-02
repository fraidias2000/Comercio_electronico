package com.example.practica3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.practica3.Datos.ListaProductos;
import com.example.practica3.Datos.Mensaje;
import com.example.practica3.Datos.Producto;
import com.example.practica3.Datos.ProductoSeleccionadoUsuario;
import com.example.practica3.Datos.Servidor;
import com.example.practica3.Datos.Usuario;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.List;

public class Productos extends AppCompatActivity {
    private final String ERROR_CANTIDAD_PRODUCTO = "No hay stock suficiente de este producto";
    private final String HUEVOS_JAULA_SELECCIONADOS = "Huevos Gallina Jaula";
    private final String HUEVOS_CAMPEROS_SELECCIONADOS = "Huevos Gallina Campera";
    private final String CLARA_HUEVOS_SELECCIONADOS = "Clara huevos gallina";
    private final String VARIABLE_TIPO_USUARIO = "tipoUsuario";
    private final String VARIABLE_TIPO_LISTA_PRODUCTOS = "listaProductos";
    private final String VACIO = " ";
    private ListaProductos listaProductos;
    private MobileServiceClient conexionServidorApi;
    private Context context;
    private Toast toast;
    private int duration = Toast.LENGTH_SHORT;
    private String respuestaServidor = "";
    private TextView nombresProductos[];
    private TextView descripcionesProductos[];
    private TextView preciosProductos[];
    private TextView cantidadProductos[];
    private Button botonesDecrementar[];
    private Button botonesIncrementar[];
    private Button botonesAnyadirArticulo[];
    private ImageView carrito;
    private Button tramitarPedido;
    private int cantidadHuevosCamperosSeleccionadaUsuario = 0;
    private int cantidadHuevosEnJaulaSeleccionadaUsuario = 0;
    private int cantidadClarasHuevoSeleccionadaUsuario = 0;
    private int cantidadHuevosCamperosBD = 0;
    private int cantidadHuevosEnJaulaBD  = 0;
    private int cantidadClarasHuevoBD  = 0;
    private ProductoSeleccionadoUsuario gallinaJaula;
    private ProductoSeleccionadoUsuario gallinaCampera;
    private ProductoSeleccionadoUsuario claraHuevo;
    private Usuario user;
    private Servidor servidor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        user = (Usuario) getIntent().getSerializableExtra(VARIABLE_TIPO_USUARIO);
        servidor = new Servidor();
        enlazarVistas();
        conectarServidor();
        obtenerProductos();
    }

    private void enlazarVistas(){

        carrito  = (ImageView) findViewById(R.id.imageCarritoCompra);
        tramitarPedido = (Button) findViewById(R.id.buttonTramitarPedido);

        nombresProductos = new TextView[3];
        nombresProductos[0] = (TextView) findViewById(R.id.nombreProducto1);
        nombresProductos[1] = (TextView) findViewById(R.id.nombreProducto2);
        nombresProductos[2] = (TextView) findViewById(R.id.nombreProducto3);

        descripcionesProductos = new TextView[3];
        descripcionesProductos[0] = (TextView) findViewById(R.id.descripcionProducto1);
        descripcionesProductos[1] = (TextView) findViewById(R.id.descripcionProducto2);
        descripcionesProductos[2] = (TextView) findViewById(R.id.descripcionProducto3);

        preciosProductos = new TextView[3];
        preciosProductos[0] = (TextView) findViewById(R.id.precioProducto1);
        preciosProductos[1] = (TextView) findViewById(R.id.precioProducto2);
        preciosProductos[2] = (TextView) findViewById(R.id.precioProducto3);

        botonesDecrementar  = new Button[3];
        botonesDecrementar[0] = (Button) findViewById(R.id.buttonDecrementarGallinaJaula);
        botonesDecrementar[1] = (Button) findViewById(R.id.buttonDecrementarGallinasCamperas  );
        botonesDecrementar[2] = (Button) findViewById(R.id.buttonDecrementarClaras);

        botonesIncrementar = new Button[3];
        botonesIncrementar[0] = (Button) findViewById(R.id.buttonIncrementarGallinasJaulas);
        botonesIncrementar[1] = (Button) findViewById(R.id.buttonIncrementarGallinasCamperas);
        botonesIncrementar[2] = (Button) findViewById(R.id.buttonIncrementarClaras);

        cantidadProductos = new TextView[3];
        cantidadProductos[0] = (TextView) findViewById(R.id.cantidadProducto1);
        cantidadProductos[1] = (TextView) findViewById(R.id.cantidadProducto2);
        cantidadProductos[2] = (TextView) findViewById(R.id.cantidadProducto3);

        botonesAnyadirArticulo = new Button[3];
        botonesAnyadirArticulo[0] = (Button) findViewById(R.id.buttonAnyadirCarritoGallinaJaula);
        botonesAnyadirArticulo[1] = (Button) findViewById(R.id.buttonAnyadirCarritoGallinaCampera);
        botonesAnyadirArticulo[2] = (Button) findViewById(R.id.buttonAnyadirCarritoClara);

        tramitarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTramintarPedido();
            }
        });

        carrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTramintarPedido();
            }
        });

        botonesAnyadirArticulo[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gallinaJaula.setCantidad(cantidadHuevosEnJaulaSeleccionadaUsuario);

            }
        });

        botonesAnyadirArticulo[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gallinaCampera.setCantidad(cantidadHuevosCamperosSeleccionadaUsuario);
            }
        });

        botonesAnyadirArticulo[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                claraHuevo.setCantidad(cantidadClarasHuevoSeleccionadaUsuario);
            }
        });

        botonesDecrementar[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cantidadHuevosEnJaulaSeleccionadaUsuario  > 0){
                    cantidadHuevosEnJaulaSeleccionadaUsuario--;
                    cantidadProductos[0].setText(String.valueOf(cantidadHuevosEnJaulaSeleccionadaUsuario));
                }
            }
        });

        botonesDecrementar[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cantidadHuevosCamperosSeleccionadaUsuario > 0){
                    cantidadHuevosCamperosSeleccionadaUsuario--;
                    cantidadProductos[1].setText(String.valueOf(cantidadHuevosCamperosSeleccionadaUsuario));
                }
            }
        });

        botonesDecrementar[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cantidadClarasHuevoSeleccionadaUsuario > 0){
                    cantidadClarasHuevoSeleccionadaUsuario--;
                    cantidadProductos[2].setText(String.valueOf(cantidadClarasHuevoSeleccionadaUsuario));
                }
            }
        });

        botonesIncrementar[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cantidadHuevosEnJaulaSeleccionadaUsuario < cantidadHuevosEnJaulaBD  ){
                    cantidadHuevosEnJaulaSeleccionadaUsuario++;
                    cantidadProductos[0].setText(String.valueOf(cantidadHuevosEnJaulaSeleccionadaUsuario));
                }else{
                    context = getApplicationContext();
                    toast = Toast.makeText(context,    ERROR_CANTIDAD_PRODUCTO, duration);
                    toast.show();
                }
            }
        });

        botonesIncrementar[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cantidadHuevosCamperosSeleccionadaUsuario  <  cantidadHuevosCamperosBD) {
                    cantidadHuevosCamperosSeleccionadaUsuario++;
                    cantidadProductos[1].setText(String.valueOf(cantidadHuevosCamperosSeleccionadaUsuario));
                }else{
                    context = getApplicationContext();
                    toast = Toast.makeText(context, ERROR_CANTIDAD_PRODUCTO, duration);
                    toast.show();
                }


            }
        });

        botonesIncrementar[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cantidadClarasHuevoSeleccionadaUsuario < cantidadClarasHuevoBD ) {
                    cantidadClarasHuevoSeleccionadaUsuario++;
                    cantidadProductos[2].setText(String.valueOf(cantidadClarasHuevoSeleccionadaUsuario));
                }else{
                    context = getApplicationContext();
                    toast = Toast.makeText(context, ERROR_CANTIDAD_PRODUCTO, duration);
                    toast.show();
                }
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
    public String obtenerProductos(){
        Mensaje mensajeServidor = new Mensaje(VACIO);
        final ListenableFuture<ListaProductos> peticionObtenerProductos =
                conexionServidorApi.invokeApi(servidor.API_OBTENER_PRODUCTOS, mensajeServidor, ListaProductos.class);
        Futures.addCallback(peticionObtenerProductos, new FutureCallback<ListaProductos>() {
            @Override
            public void onFailure(Throwable exc) {
                context = getApplicationContext();
                toast = Toast.makeText(context, "ERROR: " + exc, duration);
                toast.show();
            }
            @Override
            public void onSuccess(ListaProductos respuesta) {
                cantidadHuevosCamperosBD = respuesta.misProductos.get(0).getCantidadProducto();
                cantidadHuevosEnJaulaBD  = respuesta.misProductos.get(1).getCantidadProducto();
                cantidadClarasHuevoBD  = respuesta.misProductos.get(2).getCantidadProducto();
                listaProductos = respuesta;
                parsearInformacionProductos(listaProductos);
            }
        });
        return respuestaServidor;
    }

    private void parsearInformacionProductos(ListaProductos productos){
        for (int i = 0; i < 3; i++ ){
            nombresProductos[i].setText(productos.misProductos.get(i).getNombre());
            preciosProductos[i].setText("Precio: " + productos.misProductos.get(i).getPrecio());
            descripcionesProductos[i].setText(productos.misProductos.get(i).getDescripcion());
            gallinaJaula = new ProductoSeleccionadoUsuario(productos.misProductos.get(0).getId_producto(),
                                                           productos.misProductos.get(0).getNombre(),
                                                           productos.misProductos.get(0).getPrecio(), 0,cantidadHuevosEnJaulaBD );
            gallinaCampera = new ProductoSeleccionadoUsuario(productos.misProductos.get(1).getId_producto(),
                                                             productos.misProductos.get(1).getNombre(),
                                                             productos.misProductos.get(1).getPrecio(), 0, cantidadHuevosCamperosBD);
            claraHuevo = new ProductoSeleccionadoUsuario(productos.misProductos.get(2).getId_producto(),
                                                         productos.misProductos.get(2).getNombre(),
                                                         productos.misProductos.get(2).getPrecio(), 0, cantidadClarasHuevoBD);
        }
    }

     private void openTramintarPedido(){
        Intent activityTramitarPedido = new Intent(this, TramitarPedido.class);
        activityTramitarPedido.putExtra(HUEVOS_JAULA_SELECCIONADOS, (Serializable) gallinaJaula);
        activityTramitarPedido.putExtra(HUEVOS_CAMPEROS_SELECCIONADOS, (Serializable) gallinaCampera);
        activityTramitarPedido.putExtra(CLARA_HUEVOS_SELECCIONADOS, (Serializable) claraHuevo);
        activityTramitarPedido.putExtra(VARIABLE_TIPO_USUARIO, (Serializable) user);
         activityTramitarPedido.putExtra(VARIABLE_TIPO_LISTA_PRODUCTOS, (Serializable) listaProductos);
        startActivity(activityTramitarPedido);
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