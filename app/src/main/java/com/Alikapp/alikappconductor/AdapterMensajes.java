package com.Alikapp.alikappconductor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdapterMensajes extends RecyclerView.Adapter <HolderMensajes> {

//lista de mensajes
List<MensajeRecibir> listMensaje = new ArrayList<>();
    private Context c;

    public AdapterMensajes( Context c) {
        this.c = c;
    }

    //agregar mensajes a la lista
    public void addMensaje(MensajeRecibir m){
        listMensaje.add(m);
        notifyItemInserted(listMensaje.size());//envia notificacion cuando se envia un elemento
    }
    @NonNull
    @Override
    public HolderMensajes onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //crear una vista
        View v = LayoutInflater.from(c).inflate(R.layout.card_view_mensajes,parent,false);
        //enviar lista
        return new HolderMensajes(v);
    }

    @Override//que se setea cuando llega el objeto
    public void onBindViewHolder(@NonNull HolderMensajes holder, int position) {
        holder.getNombre().setText(listMensaje.get(position).getNombre());
        holder.getMensaje().setText(listMensaje.get(position).getMensaje());

        if (listMensaje.get(position).getType_mensaje().equals("2")) {
            holder.getFotoMensaje().setVisibility(View.VISIBLE);
            holder.getMensaje().setVisibility(View.VISIBLE);
            Glide.with(c).load(listMensaje.get(position).getUrlFoto()).into(holder.getFotoMensaje());
        }else if (listMensaje.get(position).getType_mensaje().equals("1")){
            holder.getFotoMensaje().setVisibility(View.GONE);
            holder.getMensaje().setVisibility(View.VISIBLE);
        }
        Long codigoHora = listMensaje.get(position).getHora();
        Date d = new Date(codigoHora);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        holder.getHora().setText(sdf.format(d));
    }

    @Override
    public int getItemCount() {
        return listMensaje.size();
    } //Tamaño de la lista
}