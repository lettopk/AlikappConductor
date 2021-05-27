package com.Alikapp.alikappconductor;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.Alikapp.alikappconductor.historyRecyclerView.HistoryObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HistoryTransactionActivity extends AppCompatActivity {

    private String conductorUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private ListView listTransactions;

    private ArrayList HistoryTransactions = new ArrayList<String>();
    private List <individualTransactionView> indTransactionView;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_transaction);
        listTransactions = findViewById(R.id.historyTransactions);
        listTransactions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                individualTransactionView extraerDatos = indTransactionView.get(position);
                Intent intent = new Intent(HistoryTransactionActivity.this, DetailTransactionActivity.class);
                intent.putExtra("idTransaction", extraerDatos.getIdTransaction());
                intent.putExtra("date",  extraerDatos.getFechaTransaction());
                startActivity(intent);

               /* System.out.println(parent.getItemAtPosition(position).toString());
                System.out.println(parent.getItemAtPosition(position).toString());
                String[] parts = parent.getItemAtPosition(position).toString().split("/");
                Intent intent = new Intent(HistoryTransactionActivity.this, DetailTransactionActivity.class);
                intent.putExtra("idTransaction", parts[0]);
                intent.putExtra("date", parts[1]);
                startActivity(intent);*/
            }
        });

        getUserHistoryTransactions();
    }


    private List<individualTransactionView> getData() {

        indTransactionView = new ArrayList<>();
        for (int i=0; i<HistoryTransactions.size(); i++){

            String[] hola = HistoryTransactions.get(i).toString().split("/");
            indTransactionView.add(new individualTransactionView(getImage(hola[2]),hola[1], hola[0]));
        }

        return indTransactionView;
    }

    private int getImage(String s) {

        int respuesta= 0;
        if (s.equals("CARD")){

            respuesta= R.mipmap.ic_tarjetacreditoicononegro;
        }

        else if (s.equals("BANCOLOMBIA_TRANSFER")){

            respuesta = R.mipmap.ic_bancolombia;
        }

        else if (s.equals("NEQUI")){

            respuesta = R.mipmap.ic_nequi;
        }

        else if (s.equals("PSE")){

            respuesta = R.mipmap.ic_pse;
        }

        else if (s.equals("BANCOLOMBIA_COLLECT")){

            respuesta = R.mipmap.ic_pagoefectivo;
        }
        else {

            respuesta = R.mipmap.ic_tarjetacredito;
        }

        return respuesta;
    }

    private void getUserHistoryTransactions() {
        DatabaseReference userHistoryDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(conductorUID);
        userHistoryDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    dataSnapshot.child("Transacciones");
                    DataSnapshot data = dataSnapshot.child("Transacciones");
                    for(DataSnapshot history : data.getChildren()){
                        String id = history.getKey();
                        Long timestamp = 0L;
                        String B = history.getValue().toString();
                        String[] A = B.split("-");
                        timestamp = Long.valueOf(A[0]);
                        String date = getDate(timestamp);
                        HistoryTransactions.add(id + "/" + date + "/" + A[1]);
                    }
                    individualTransactionAdapter i= new individualTransactionAdapter(HistoryTransactionActivity.this, getData());
                    listTransactions.setAdapter(i);
                    //adapter = new ArrayAdapter<String>(HistoryTransactionActivity.this, android.R.layout.simple_spinner_item, HistoryTransactions);
                    //listTransactions.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private String getDate(Long time) {
        Calendar cal = Calendar.getInstance(java.util.Locale.getDefault());
        cal.setTimeInMillis(time);
        String date = DateFormat.format("MM-dd-yyyy hh:mm", cal).toString();
        return date;
    }
}