package com.example.tiendaonline4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout lista_productos, registro_clientes, carrito_compra, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        lista_productos = findViewById(R.id.opt_menu_lista_productos);
        registro_clientes = findViewById(R.id.opt_menu_registro_clientes);
        carrito_compra = findViewById(R.id.opt_menu_carrito_compra);
        logout = findViewById(R.id.opt_menu_sign_out);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                openDrawer(drawerLayout);
            }
        });

        lista_productos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                redirectActivity(MainActivity.this, Lista_Productos.class);
            }
        });

        registro_clientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                redirectActivity(MainActivity.this, RegistrosClientes.class);
            }
        });

        carrito_compra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                redirectActivity(MainActivity.this, CarritoCompra.class);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Toast.makeText(getApplicationContext(), "Sesion cerrada", Toast.LENGTH_SHORT).show();
                redirectActivity(MainActivity.this, Login.class);
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