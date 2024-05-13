package com.example.tiendaonline4;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        FirebaseApp.initializeApp(getApplicationContext());
    }

    public void login_check(View view){

        TextInputEditText email = findViewById(R.id.email_loginET);
        TextInputEditText password = findViewById(R.id.clave_loginET);

        String email_text = email.getText().toString();
        String password_text = password.getText().toString();

        Intent i = new Intent(getApplicationContext(), RegistrosClientes.class);
        startActivity(i);
        finish();

        if(email_text.isEmpty() || password_text.isEmpty()){
            Toast.makeText(getApplicationContext(), "Por favor ingresar correo y contraseña", Toast.LENGTH_SHORT).show();
        }else{
            reference = FirebaseDatabase.getInstance().getReference("clientes");
            reference.child(email_text).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    Log.d("Usuario Ingresado", "el usuario ingresado fue: "+email_text);
                    if(task.isSuccessful()){
                        if(task.getResult().exists()){
                            DataSnapshot dataSnapshot = task.getResult();
                            String password_aux = String.valueOf(dataSnapshot.child("clave").getValue());

                            if(password_aux.equals(password_text)){
                                Toast.makeText(getApplicationContext(), "Inicio de sesion exitoso", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getApplicationContext(), RegistrosClientes.class);
                                startActivity(i);
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(), "Email o clave incorrectos", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Toast.makeText(getApplicationContext(), "Usuario ingresado no existe", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "Error al iniciar sesion", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
    }


}
