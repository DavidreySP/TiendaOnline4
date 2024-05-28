package com.example.tiendaonline4;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;


public class Producto {

    String key;
    String nombre;
    Long precio;
    String imagen;
    Long cantidad_seleccionada;

    public Long getCantidad_seleccionada() {
        return cantidad_seleccionada;
    }

    public void setCantidad_seleccionada(Long cantidad_seleccionada) {
        this.cantidad_seleccionada = cantidad_seleccionada;
    }

    public Producto(){}

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getPrecio() {
        return precio;
    }

    public void setPrecio(Long precio) {
        this.precio = precio;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
