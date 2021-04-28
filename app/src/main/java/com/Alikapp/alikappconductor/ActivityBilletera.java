package com.Alikapp.alikappconductor;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ActivityBilletera extends AppCompatActivity {

    private Button btnRecarga;
    private TextView copDisponib;

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

        btnRecarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( ActivityBilletera.this, popupRecarga.class);
                startActivity(intent);
            }
        });

        getUserInfo();
    }

    private String idTransaccion, estTransaccion;
    private String conductorUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private void getUserInfo(){
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

                            verificarEstadoTransaccion();

                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void setearDineroPantalla() {

        String cantidad = (cantDineroDisponible);

     

        copDisponib.setText(cantidad);

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

                            if (metodoResponse.getAsync_payment_url() != null) {
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
                            }

                        } else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    verificarEstadoTransaccion();
                                }
                            }, 10000);
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




