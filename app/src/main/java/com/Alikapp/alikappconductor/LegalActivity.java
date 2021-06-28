package com.Alikapp.alikappconductor;

import androidx.annotation.NonNull;
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

import java.util.HashMap;
import java.util.Map;

public class LegalActivity extends AppCompatActivity {

    private String Terminos;
    private TextView mTerminos;
    private ConstraintLayout carga;
    private ConstraintLayout scroll;
    private Button mAcpeto;
    private String email;

    private String conductorUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal);

        Intent mPv = getIntent();
        boolean isPrimeraVez = mPv.getBooleanExtra("PrimeraVez",false);
        if(mPv.getStringExtra("Terminos") != null && !mPv.getStringExtra("Terminos").isEmpty()){
            Terminos = mPv.getStringExtra("Terminos");
        } else {
            getTermininos();
        }
        if(mPv.getStringExtra("email") != null && !mPv.getStringExtra("email").isEmpty()){
            email = mPv.getStringExtra("email");
        } else {
            getEmail();
        }

        mTerminos = findViewById(R.id.terminos);
        carga = findViewById(R.id.carga);
        scroll = findViewById(R.id.scroll);
        mAcpeto = findViewById(R.id.acepto);

        if (!isPrimeraVez){
            mAcpeto.setText("OK");
        }

        System.out.println(Terminos);
        String finalEmail = email;
        mAcpeto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPrimeraVez){
                    saveConfirmation();
                    Intent intent = new Intent(LegalActivity.this, CustomerSettingsActivity.class);
                    intent.putExtra("PrimeraVez", true);
                    if(finalEmail != null && !finalEmail.isEmpty()){
                        intent.putExtra("email", finalEmail);
                    }
                    startActivity(intent);
                }
                finish();
            }
        });
        setearTerminsPantalla();
    }

    private void saveConfirmation() {
        DatabaseReference enableReference = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(conductorUID);
        Map usuarioInfo = new HashMap();
        usuarioInfo.put("AceptaTerminosYCondiciones", true);
        enableReference.updateChildren(usuarioInfo);
    }

    private void getEmail() {
        DatabaseReference mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(conductorUID);
        mDriverDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("email") != null) {
                        email = map.get("email").toString();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setearTerminsPantalla() {
        if(Terminos!= null && !Terminos.isEmpty()){
            mTerminos.setText(Terminos);
            scroll.setVisibility(View.VISIBLE);
            carga.setVisibility(View.GONE);
        } else {
            getTermininos();
        }
    }

    private void getTermininos() {
        DatabaseReference m = FirebaseDatabase.getInstance().getReference().child("TerminosCondiciones");
        m.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("Driver")!=null){
                        Terminos = map.get("Driver").toString();
                        setearTerminsPantalla();
                    }
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
            }
        });
    }
}