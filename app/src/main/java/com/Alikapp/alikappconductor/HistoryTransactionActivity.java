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

public class HistoryTransactionActivity extends AppCompatActivity {

    private String conductorUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private ListView listTransactions;

    private ArrayList HistoryTransactions = new ArrayList<String>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_transaction);

        listTransactions = findViewById(R.id.historyTransactions);
        listTransactions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(parent.getItemAtPosition(position).toString());
                String[] parts = parent.getItemAtPosition(position).toString().split("       ");
                Intent intent = new Intent(HistoryTransactionActivity.this, DetailTransactionActivity.class);
                intent.putExtra("idTransaction", parts[0]);
                intent.putExtra("date", parts[1]);
                startActivity(intent);
            }
        });

        getUserHistoryTransactions();
    }

    private void getUserHistoryTransactions() {
        DatabaseReference userHistoryDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(conductorUID).child("Transacciones");
        userHistoryDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot history : dataSnapshot.getChildren()){
                        String id = history.getKey();
                        Long timestamp = 0L;
                        timestamp = Long.valueOf(history.getValue().toString());
                        String date = getDate(timestamp);
                        HistoryTransactions.add(id + "       " + date);
                    }
                    adapter = new ArrayAdapter<String>(HistoryTransactionActivity.this, android.R.layout.simple_spinner_item, HistoryTransactions);
                    listTransactions.setAdapter(adapter);
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