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

public class RegistrosClientes extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout lista_productos, registro_clientes, carrito_compra, logout, geolocalizacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_clientes);

        TextView title_toolbar = (TextView) findViewById(R.id.title_toolbar);
        title_toolbar.setText("Registro de Clientes");

        FirebaseApp.initializeApp(RegistrosClientes.this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        FloatingActionButton add = findViewById(R.id.addCliente);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View view1 = LayoutInflater.from(RegistrosClientes.this).inflate(R.layout.add_registro_cliente_dialog, null);

                TextInputLayout nombreLayout, emailLayout, claveLayout;
                nombreLayout = view1.findViewById(R.id.nombre_clienteLayout);
                emailLayout = view1.findViewById(R.id.email_cliente_Layout);
                claveLayout = view1.findViewById(R.id.clave_cliente_Layout);

                TextInputEditText nombreET, emailET, claveET;
                nombreET = view1.findViewById(R.id.nombre_clienteET);
                emailET = view1.findViewById(R.id.email_clienteET);
                claveET = view1.findViewById(R.id.clave_clienteET);

                AlertDialog alertDialog = new AlertDialog.Builder(RegistrosClientes.this)
                        .setTitle("Añadir")
                        .setView(view1)
                        .setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (Objects.requireNonNull(nombreET.getText()).toString().isEmpty()) {
                                    nombreLayout.setError("Campo Requerido");
                                } else if (Objects.requireNonNull(emailET.getText()).toString().isEmpty()) {
                                    emailLayout.setError("Campo Requerido");
                                } else if (Objects.requireNonNull(claveET.getText()).toString().isEmpty()) {
                                    claveLayout.setError("Campo Requerido");
                                } else {
                                    ProgressDialog dialog = new ProgressDialog(RegistrosClientes.this);
                                    dialog.setMessage("Cargando, por favor espere...");
                                    dialog.show();
                                    Cliente cliente = new Cliente();
                                    cliente.setNombre(nombreET.getText().toString());
                                    cliente.setEmail(emailET.getText().toString());
                                    cliente.setClave(claveET.getText().toString());
                                    database.getReference().child("clientes").push().setValue(cliente).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            dialog.dismiss();
                                            dialogInterface.dismiss();
                                            Toast.makeText(RegistrosClientes.this, "Guardado Exitosamente!", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            dialog.dismiss();
                                            Toast.makeText(RegistrosClientes.this, "Ocurrio un error durante el proceso", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
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

        TextView empty = findViewById(R.id.vacio);

        RecyclerView recyclerView = findViewById(R.id.recycler);

        database.getReference().child("clientes").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Cliente> arrayList = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Cliente cliente = dataSnapshot.getValue(Cliente.class);
                    Objects.requireNonNull(cliente).setKey(dataSnapshot.getKey());
                    arrayList.add(cliente);
                }

                if (arrayList.isEmpty()) {
                    empty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    empty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

                ClienteAdapter adapter = new ClienteAdapter(RegistrosClientes.this, arrayList);
                recyclerView.setAdapter(adapter);

                adapter.setOnItemClickListener(new ClienteAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Cliente cliente) {
                        View view = LayoutInflater.from(RegistrosClientes.this).inflate(R.layout.add_registro_cliente_dialog, null);

                        TextInputLayout nombreLayout, emailLayout, claveLayout;
                        nombreLayout = view.findViewById(R.id.nombre_clienteLayout);
                        emailLayout = view.findViewById(R.id.email_cliente_Layout);
                        claveLayout = view.findViewById(R.id.clave_cliente_Layout);

                        TextInputEditText nombreET, emailET, claveET;
                        nombreET = view.findViewById(R.id.nombre_clienteET);
                        emailET = view.findViewById(R.id.email_clienteET);
                        claveET = view.findViewById(R.id.clave_clienteET);

                        nombreET.setText(cliente.getNombre());
                        emailET.setText(cliente.getEmail());
                        claveET.setText(cliente.getClave());

                        ProgressDialog progressDialog = new ProgressDialog(RegistrosClientes.this);

                        AlertDialog alertDialog = new AlertDialog.Builder(RegistrosClientes.this)
                                .setTitle("Editar")
                                .setView(view)
                                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (Objects.requireNonNull(nombreET.getText()).toString().isEmpty()) {
                                            nombreLayout.setError("Campo requerido");
                                        } else if (Objects.requireNonNull(emailET.getText()).toString().isEmpty()) {
                                            emailLayout.setError("Campo requerido");
                                        } else if (Objects.requireNonNull(claveET.getText()).toString().isEmpty()) {
                                            claveLayout.setError("Campo requerido");
                                        } else {
                                            progressDialog.setMessage("Guardando...");
                                            progressDialog.show();
                                            Cliente cliente1 = new Cliente();
                                            cliente1.setNombre(nombreET.getText().toString());
                                            cliente1.setEmail(emailET.getText().toString());
                                            cliente1.setClave(claveET.getText().toString());

                                            database.getReference().child("clientes").child(cliente.getKey()).setValue(cliente1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    progressDialog.dismiss();
                                                    dialogInterface.dismiss();
                                                    Toast.makeText(RegistrosClientes.this, "Guardado Exitosamente", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(RegistrosClientes.this, "Ocurrio un error al realizar el proceso", Toast.LENGTH_SHORT).show();
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
                                        database.getReference().child("clientes").child(cliente.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Toast.makeText(RegistrosClientes.this, "Eliminado exitosamente", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(RegistrosClientes.this, "Error al realizar la eliminacion", Toast.LENGTH_SHORT).show();
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
                redirectActivity(RegistrosClientes.this, Lista_Productos.class);
            }
        });

        registro_clientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                recreate();
            }
        });

        carrito_compra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                redirectActivity(RegistrosClientes.this, CarritoCompra.class);
            }
        });

        geolocalizacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                redirectActivity(RegistrosClientes.this, Geolocalizacion.class);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Toast.makeText(getApplicationContext(), "Sesion cerrada", Toast.LENGTH_SHORT).show();
                redirectActivity(RegistrosClientes.this, Login.class);
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
