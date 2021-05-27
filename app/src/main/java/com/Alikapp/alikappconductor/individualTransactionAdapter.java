package com.Alikapp.alikappconductor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class individualTransactionAdapter extends BaseAdapter {

    Context context;
    List <individualTransactionView> listView;


    public individualTransactionAdapter(Context context, List<individualTransactionView> listView) {
        this.context = context;
        this.listView = listView;
    }

    @Override
    public int getCount() {

        return listView.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageTransaction;
        TextView idTransaccion;
        TextView fechaTransaccion;

        individualTransactionView position1 = listView.get(position);

        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_transaction, null);
        }
        imageTransaction =(ImageView) convertView.findViewById(R.id.imageTransaction);
        idTransaccion =(TextView) convertView.findViewById(R.id.idTransaccion);
        fechaTransaccion =(TextView) convertView.findViewById(R.id.fechaTransaccion);

        imageTransaction.setImageResource(position1.getImageMetodo());
        idTransaccion.setText(position1.getIdTransaction());
        fechaTransaccion.setText(position1.getFechaTransaction());

        return convertView;
    }
}
