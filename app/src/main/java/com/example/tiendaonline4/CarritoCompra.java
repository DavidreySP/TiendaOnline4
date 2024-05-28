package com.example.tiendaonline4;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class CarritoCompra extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout lista_productos, registro_clientes, carrito_compra, logout, geolocalizacion;
    Long total_precio = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carrito_compra);

        TextView title_toolbar = (TextView) findViewById(R.id.title_toolbar);
        title_toolbar.setText("Carrito de Compra");

        FirebaseApp.initializeApp(CarritoCompra.this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        FloatingActionButton add = findViewById(R.id.confirmar_compra);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View view1 = LayoutInflater.from(CarritoCompra.this).inflate(R.layout.cerrar_compra, null);

                AlertDialog alertDialog = new AlertDialog.Builder(CarritoCompra.this)
                        .setTitle("Cerrar Compra")
                        .setView(view1)
                        .setPositiveButton("Comprar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                    ProgressDialog dialog = new ProgressDialog(CarritoCompra.this);
                                dialog.setMessage("Cargando, por favor espere...");
                                dialog.show();
                                database.getReference().child("carrito").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {

                                    @Override
                                    public void onSuccess(Void unused) {
                                        dialog.dismiss();
                                        dialogInterface.dismiss();
                                        Toast.makeText(CarritoCompra.this, "Cierre de Compra Exitoso!", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialog.dismiss();
                                        Toast.makeText(CarritoCompra.this, "Ocurrio un error durante el proceso", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create();
                alertDialog.show();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_carrito_compra);

        database.getReference().child("carrito").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Producto> arrayList = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Producto producto = dataSnapshot.getValue(Producto.class);
                    Objects.requireNonNull(producto).setKey(dataSnapshot.getKey());
                    arrayList.add(producto);

                    total_precio += producto.getPrecio()*producto.getCantidad_seleccionada();
                }

                CarritoAdapter adapter = new CarritoAdapter(CarritoCompra.this, arrayList);
                recyclerView.setAdapter(adapter);

                TextView total_compra_TV = (TextView) findViewById(R.id.total_compra);
                total_compra_TV.setText("Total a pagar: $"+total_precio.toString());

                adapter.setOnItemClickListener(new CarritoAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Producto producto) {
                        View view = LayoutInflater.from(CarritoCompra.this).inflate(R.layout.add_producto_carrito, null);

                        TextView titulo_articulo_carrito;

                        titulo_articulo_carrito = view.findViewById(R.id.titulo_articulo_carrito);
                        titulo_articulo_carrito.setText(producto.getNombre());

                        TextInputLayout cantidad_pedidoLayout;
                        cantidad_pedidoLayout = view.findViewById(R.id.cantidad_pedidoLayout);

                        TextInputEditText cantidad_pedidoET;
                        cantidad_pedidoET = view.findViewById(R.id.cantidad_pedidoET);
                        cantidad_pedidoET.setText(producto.getCantidad_seleccionada().toString());

                        ProgressDialog progressDialog = new ProgressDialog(CarritoCompra.this);
                        AlertDialog alertDialog = new AlertDialog.Builder(CarritoCompra.this)
                                .setTitle("Editar")
                                .setView(view)
                                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (Objects.requireNonNull(cantidad_pedidoET.getText()).toString().equals("0")) {
                                            cantidad_pedidoLayout.setError("No pueden seleccionar cantidades negativas o iguales a 0");
                                            Toast.makeText(CarritoCompra.this, "No pueden seleccionar cantidades negativas o iguales a 0", Toast.LENGTH_SHORT).show();
                                        } else if(Objects.requireNonNull(cantidad_pedidoET.getText()).toString().isEmpty()) {
                                            cantidad_pedidoLayout.setError("No pueden seleccionar cantidades negativas o iguales a 0");
                                            Toast.makeText(CarritoCompra.this, "No pueden seleccionar cantidades negativas o iguales a 0", Toast.LENGTH_SHORT).show();
                                        } else {
                                            progressDialog.setMessage("Guardando...");
                                            progressDialog.show();

                                            Producto producto1 = new Producto();
                                            producto1.setNombre(producto.getNombre());
                                            producto1.setImagen(producto.getImagen());
                                            producto1.setPrecio(producto.getPrecio());
                                            producto1.setCantidad_seleccionada(Long.valueOf(cantidad_pedidoET.getText().toString()));

                                            database.getReference().child("carrito").child(producto.getKey()).setValue(producto1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    progressDialog.dismiss();
                                                    dialogInterface.dismiss();
                                                    total_precio=0L;
                                                    Toast.makeText(CarritoCompra.this, "Guardado Exitosamente", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(CarritoCompra.this, "Ocurrio un error al realizar el proceso", Toast.LENGTH_SHORT).show();
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
                                })
                                .setNegativeButton("Eliminar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        progressDialog.setTitle("Eliminando...");
                                        progressDialog.show();
                                        database.getReference().child("carrito").child(producto.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                total_precio=0L;
                                                Toast.makeText(CarritoCompra.this, "Eliminado exitosamente", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(CarritoCompra.this, "Error al realizar la eliminacion", Toast.LENGTH_SHORT).show();
                                            }
                                        });
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
