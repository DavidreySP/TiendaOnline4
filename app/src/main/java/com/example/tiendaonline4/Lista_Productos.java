package com.example.tiendaonline4;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class Lista_Productos extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout lista_productos, registro_clientes, carrito_compra, logout, geolocalizacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_productos);

        TextView title_toolbar = (TextView) findViewById(R.id.title_toolbar);
        title_toolbar.setText("Lista de Productos");

        FirebaseApp.initializeApp(Lista_Productos.this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        RecyclerView recyclerView = findViewById(R.id.recycler_lista_producto);

        Button sumar_cantidad = findViewById(R.id.sumar_cantidad);
        Button restar_cantidad = findViewById(R.id.restar_cantidad);

        sumar_cantidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView titulo_articulo_carrito = findViewById(R.id.titulo_articulo_carrito);

                int aux_val = Integer.parseInt(titulo_articulo_carrito.getText().toString());

                aux_val++;
                titulo_articulo_carrito.setText(aux_val);
            }
        });

        restar_cantidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView titulo_articulo_carrito = findViewById(R.id.titulo_articulo_carrito);

                int aux_val = Integer.parseInt(titulo_articulo_carrito.getText().toString());

                if(aux_val != 0){
                    aux_val--;
                    titulo_articulo_carrito.setText(aux_val);
                }
            }
        });

        database.getReference().child("productos").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Producto> arrayList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Producto producto = dataSnapshot.getValue(Producto.class);
                    Objects.requireNonNull(producto).setKey(dataSnapshot.getKey());
                    arrayList.add(producto);
                }

                ProductoAdapter adapter = new ProductoAdapter(Lista_Productos.this, arrayList);
                recyclerView.setAdapter(adapter);

                adapter.setOnItemClickListener(new ProductoAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Producto producto) {
                        View view = LayoutInflater.from(Lista_Productos.this).inflate(R.layout.add_producto_carrito, null);

                        TextView titulo_articulo_carrito, cantidad_articulo;

                        titulo_articulo_carrito = view.findViewById(R.id.titulo_articulo_carrito);
                        titulo_articulo_carrito.setText(producto.getNombre());

                        cantidad_articulo = view.findViewById(R.id.cantidad_articulo);

                        ProgressDialog progressDialog = new ProgressDialog(Lista_Productos.this);

                        AlertDialog alertDialog = new AlertDialog.Builder(Lista_Productos.this)
                            .setTitle("Hacer Pedido")
                            .setView(view)
                            .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (Integer.parseInt(cantidad_articulo.getText().toString()) >= 0) {
                                        Toast.makeText(Lista_Productos.this, "No pueden seleccionar cantidades negativas o iguales a 0", Toast.LENGTH_SHORT).show();
                                    } else {
                                        progressDialog.setMessage("Guardando...");
                                        progressDialog.show();

                                        Producto producto1 = new Producto();
                                        producto1.setNombre(producto.getNombre());
                                        producto1.setImagen(producto.getImagen());
                                        producto1.setPrecio(producto.getPrecio());
                                        producto1.setCantidad_seleccionada(Integer.parseInt(cantidad_articulo.getText().toString()));

                                        ProgressDialog dialog = new ProgressDialog(Lista_Productos.this);
                                        dialog.setMessage("Cargando, por favor espere...");

                                        database.getReference().child("carrito").push().setValue(producto1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                dialog.dismiss();
                                                dialogInterface.dismiss();
                                                Toast.makeText(Lista_Productos.this, "Guardado Exitosamente!", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                dialog.dismiss();
                                                Toast.makeText(Lista_Productos.this, "Ocurrio un error durante el proceso", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            })
                            .setNeutralButton("Cerrar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).create();
                        alertDialog.show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
                recreate();
            }
        });

        registro_clientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                redirectActivity(Lista_Productos.this, RegistrosClientes.class);
            }
        });

        carrito_compra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                redirectActivity(Lista_Productos.this, CarritoCompra.class);
            }
        });

        geolocalizacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                redirectActivity(Lista_Productos.this, Geolocalizacion.class);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Toast.makeText(getApplicationContext(), "Sesion cerrada", Toast.LENGTH_SHORT).show();
                redirectActivity(Lista_Productos.this, Login.class);
            }
        });
    }

    public void sumar(){

    }

    public void restar(){

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
