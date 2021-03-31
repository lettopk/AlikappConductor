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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class LegalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal);

        Intent mPv = getIntent();
        boolean isPrimeraVez = mPv.getBooleanExtra("PrimeraVez",false);
        String Terminos = mPv.getStringExtra("Terminos");

        TextView mTerminos = findViewById(R.id.terminos);
        ConstraintLayout carga = findViewById(R.id.carga);
        ConstraintLayout scroll = findViewById(R.id.scroll);
        Button mAcpeto = findViewById(R.id.acepto);

        if (!isPrimeraVez){
            mAcpeto.setText("OK");
        }

        if(Terminos != null){
            mTerminos.setText(Terminos);
            scroll.setVisibility(View.VISIBLE);
            carga.setVisibility(View.GONE);
        }
        System.out.println(Terminos);
        mAcpeto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPrimeraVez){
                    Intent intent = new Intent(LegalActivity.this, CustomerSettingsActivity.class);
                    intent.putExtra("PrimeraVez", true);
                    startActivity(intent);
                }
                finish();
            }
        });
    }
}