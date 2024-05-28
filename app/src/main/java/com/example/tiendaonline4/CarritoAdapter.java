package com.example.tiendaonline4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CarritoAdapter extends RecyclerView.Adapter<CarritoAdapter.ViewHolder>{

    Context context;
    ArrayList<Producto> arrayList;
    CarritoAdapter.OnItemClickListener onItemClickListener;

    public CarritoAdapter(Context context, ArrayList<Producto> arrayList){
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public CarritoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.producto_carrito_list_item, parent, false);
        return new CarritoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarritoAdapter.ViewHolder holder, int position) {
        holder.nombre.setText(arrayList.get(position).getNombre());
        holder.precio.setText("Precio: "+arrayList.get(position).getPrecio().toString());
        holder.cantidad.setText(arrayList.get(position).getCantidad_seleccionada().toString());
        Glide.with(context).load(arrayList.get(position).getImagen()).into(holder.imagen);

        holder.itemView.setOnClickListener(view -> onItemClickListener.onClick(arrayList.get(position)));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void setOnItemClickListener(CarritoAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, precio, cantidad;
        ImageView imagen;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.list_productos_nombre);
            precio = itemView.findViewById(R.id.list_productos_precio);
            imagen = itemView.findViewById(R.id.list_productos_imagen);
            cantidad = itemView.findViewById(R.id.list_productos_cantidad);
        }
    }

    public interface OnItemClickListener {
        void onClick(Producto producto);
    }
}
