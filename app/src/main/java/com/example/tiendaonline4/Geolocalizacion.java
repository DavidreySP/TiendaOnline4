package com.example.tiendaonline4;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class Geolocalizacion extends AppCompatActivity implements OnMapReadyCallback{

    private final int FINE_PERMISSION_CODE = 1;
    private GoogleMap myMap;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout lista_productos, registro_clientes, carrito_compra, logout, geolocalizacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.geolocalizacion);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        TextView title_toolbar = (TextView) findViewById(R.id.title_toolbar);
        title_toolbar.setText("Geolocalizacion");

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
                redirectActivity(Geolocalizacion.this, Lista_Productos.class);
            }
        });

        registro_clientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                redirectActivity(Geolocalizacion.this, RegistrosClientes.class);
            }
        });

        carrito_compra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                redirectActivity(Geolocalizacion.this, CarritoCompra.class);
            }
        });

        geolocalizacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                recreate();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Toast.makeText(getApplicationContext(), "Sesion cerrada", Toast.LENGTH_SHORT).show();
                redirectActivity(Geolocalizacion.this, Login.class);
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

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    currentLocation = location;

                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

                    mapFragment.getMapAsync(Geolocalizacion.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        myMap = googleMap;

        LatLng localPoint = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
        myMap.addMarker(new MarkerOptions().position(localPoint).title("Su localizacion"));
        myMap.moveCamera(CameraUpdateFactory.newLatLng(localPoint));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == FINE_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }else{
                Toast.makeText(this, "Permisos de localizacion denegados, por favor habilitar el permiso", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
