package com.example.tiendaonline4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class CarritoCompra extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout lista_productos, registro_clientes, carrito_compra, logout, geolocalizacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carrito_compra);

        TextView title_toolbar = (TextView) findViewById(R.id.title_toolbar);
        title_toolbar.setText("Carrito de Compra");

        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        lista_productos = findViewById(R.id.opt_menu_lista_productos);
        registro_clientes = findViewById(R.id.opt_menu_registro_clientes);
        carrito_compra = findViewById(R.id.opt_menu_carrito_compra);
        logout = findViewById(R.id.opt_menu_sign_out);
        geolocalizacion = findViewById(R.id.opt_menu_geolocalizacion);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                openDrawer(drawerLayout);
            }
        });

        lista_productos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                redirectActivity(CarritoCompra.this, Lista_Productos.class);
            }
        });

        registro_clientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                redirectActivity(CarritoCompra.this, RegistrosClientes.class);
            }
        });

        carrito_compra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                recreate();
            }
        });

        geolocalizacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                redirectActivity(CarritoCompra.this, Geolocalizacion.class);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Toast.makeText(getApplicationContext(), "Sesion cerrada", Toast.LENGTH_SHORT).show();
                redirectActivity(CarritoCompra.this, Login.class);
            }
        });
    }

    public static void openDrawer(DrawerLayout drawerLayout){
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer(DrawerLayout drawerLayout){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }
    public static void redirectActivity(Activity activity, Class secondActivity){
        Intent intent = new Intent(activity, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    protected void onPause(){
        super.onPause();
        closeDrawer(drawerLayout);
    }
}
