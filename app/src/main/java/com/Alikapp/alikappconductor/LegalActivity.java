package com.Alikapp.alikappconductor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class LegalActivity extends AppCompatActivity {

    private DatabaseReference mDriverDatabase;
    private TextView mTerminos;
    private ConstraintLayout carga;
    private ConstraintLayout scroll;
    private ScrollView scrollView;
    private Button mAcpeto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal);

        mTerminos = findViewById(R.id.terminos);
        carga = findViewById(R.id.carga);
        scroll = findViewById(R.id.scroll);
        scrollView = findViewById(R.id.scrollView);
        mAcpeto = findViewById(R.id.acepto);

        mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers");
        mDriverDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("TerminosYCondicionesConductor")!=null){
                        mTerminos.setText(map.get("TerminosYCondicionesConductor").toString());
                        scroll.setVisibility(View.VISIBLE);
                        carga.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        Intent mPv = getIntent();
        Boolean isPrimeraVez = mPv.getBooleanExtra("PrimeraVez",false);

        mAcpeto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPrimeraVez){
                    Intent intent = new Intent(LegalActivity.this, CustomerSettingsActivity.class);
                    intent.putExtra("PrimeraVez", true);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(LegalActivity.this, CustomerMapActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}