package com.Alikapp.alikappconductor;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.Alikapp.alikappconductor.models.transaction.responses.PaymentMethod;
import com.Alikapp.alikappconductor.models.transaction.responses.ResponseExtra;
import com.Alikapp.alikappconductor.models.transaction.responses.TransactionInformation;
import com.Alikapp.alikappconductor.models.transaction.responses.TransactionResponse;
import com.Alikapp.alikappconductor.wompiApi.WompiapiService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.internal.cache.DiskLruCache;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ActivityBilletera extends AppCompatActivity {

    private Button btnRecarga;
    private TextView copDisponib;
    private TextView credDisponib;
    private Boolean newPago = false;

    private static final String TAG = "WOMPI";
    private Retrofit retrofit;
    private WompiapiService service;
    private static final String URL_BASE_WOMPI = "https://production.wompi.co/v1/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billetera);

        retrofit = new Retrofit.Builder()
                .baseUrl(URL_BASE_WOMPI)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(WompiapiService.class);


        btnRecarga = (Button) findViewById(R.id.btnRecarga);
        copDisponib = (TextView) findViewById(R.id.copDisponib);
        credDisponib = (TextView) findViewById(R.id.credDisponib);

        btnRecarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPago = true;
                Intent intent = new Intent( ActivityBilletera.this, popupRecarga.class);
                startActivity(intent);
            }
        });

        getUserInfo();
    }

    private String idTransaccion, estTransaccion;
    private String conductorUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private int costoCredito = 0;

    private void getUserInfo(){

        DatabaseReference valorCreditoReference =  FirebaseDatabase.getInstance().getReference().child("CostoPorCredito");
        valorCreditoReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();


                    if (map.get("Usuario") != null){

                        costoCredito = Integer.parseInt(map.get("Usuario").toString());
                        setearDineroPantalla();
                     }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(conductorUID);
        mDriverDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    if (map.get("dineroDisponible") != null){

                        cantDineroDisponible = map.get("dineroDisponible").toString();
                        setearDineroPantalla();

                    }
                    if (map.get("idUltimaTransaccion") != null){

                        idTransaccion = map.get("idUltimaTransaccion").toString();
                    }

                    if (map.get("estadoUltimaTransaccion") != null){

                        estTransaccion = map.get("estadoUltimaTransaccion").toString();

                        if (estTransaccion.equals("PENDING")){
                            if(!newPago){ verificarEstadoTransaccion(); }

                        } else { newPago = true; }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    String v1 = "" ;
    @SuppressLint("SetTextI18n")
    private void setearDineroPantalla() {

        v1 = "";
        if(cantDineroDisponible.length()>3) {
            int j = 0;
            for (int i = 0; i < cantDineroDisponible.length(); i++) {
                System.out.println("cantDineroDisponible.length()" + cantDineroDisponible.length());
                if (j == 3) {
                    String a = new StringBuilder().append(".").append(v1).toString();
                    System.out.println("valor de a " + a);
                    v1 = a;
                    j = 0;
                }
                j++;
                String b = new StringBuilder().append(cantDineroDisponible.charAt(cantDineroDisponible.length()-(i+1))).append(v1).toString();
                v1 = b;
            }
        } else {
            v1 = cantDineroDisponible;
        }

        int v2 = Integer.parseInt(cantDineroDisponible);

        if (costoCredito > 0) {

            int v3 = v2/ costoCredito;
            credDisponib.setText(v3 + " Creditos");
            }

        copDisponib.setText("$" + v1 +" COP");

    }

    private String cantDineroDisponible="0";

    private void verificarEstadoTransaccion() {
        Call<TransactionResponse> transaction = service.verificarEstadoTransaccion(idTransaccion);

        transaction.enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                if (response.isSuccessful()) {
                    TransactionResponse transaction = response.body();
                    TransactionInformation informationyeye = transaction.getData();

                    if (informationyeye.getStatus().equals("PENDING")) {
                        PaymentMethod metodoPago = informationyeye.getPayment_method();

                        if (metodoPago.getExtra()!= null) {
                            ResponseExtra metodoResponse = metodoPago.getExtra();

                            if (metodoResponse.getAsync_payment_url() != null && (metodoPago.getType().equals("PSE") || metodoPago.getType().equals("BANCOLOMBIA_TRANSFER"))) {
                                System.out.println(metodoResponse.getAsync_payment_url());

                                if (metodoPago.getType().equals("PSE")) {
                                    Intent intent = new Intent(ActivityBilletera.this, LayoutWebview.class);
                                    intent.putExtra("pack", metodoResponse.getAsync_payment_url());
                                    startActivity(intent);
                                }
                                else {

                                    Intent intent = new Intent (Intent.ACTION_VIEW, Uri.parse(metodoResponse.getAsync_payment_url()));
                                    startActivity(intent);

                                }
                            } else {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        verificarEstadoTransaccion();
                                    }
                                }, 5000);
                            }

                        } else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    verificarEstadoTransaccion();
                                }
                            }, 5000);
                        }
                    }

                    else {

                        if (informationyeye.getStatus().equals("APPROVED")){
                            int dineroEntrante = informationyeye.getAmount_in_cents() / 100 ;
                            int dinerActual = Integer.parseInt(cantDineroDisponible);

                            cantDineroDisponible = (dineroEntrante + dinerActual)+ "";
                        }
                        estTransaccion = informationyeye.getStatus();
                        guardarInormacion();
                    }

                    System.out.println(informationyeye.getId());
                    System.out.println(informationyeye.getStatus());
                } else {
                    try {
                        Log.e(TAG, "Recarga onResponse: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<TransactionResponse> call, Throwable t) {
                Log.e(TAG, "verificarEstadoTransaccion onFailure: " + t.getMessage());
            }
        });
    }

    private void guardarInormacion(){

        DatabaseReference enableReference = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(conductorUID);

        Map usuarioInfo = new HashMap();

            usuarioInfo.put("estadoUltimaTransaccion", estTransaccion);
            usuarioInfo.put("idUltimaTransaccion", idTransaccion);
            usuarioInfo.put("dineroDisponible", cantDineroDisponible);
        enableReference.updateChildren(usuarioInfo);
    }
}




