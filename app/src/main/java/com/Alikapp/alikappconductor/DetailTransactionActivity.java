package com.Alikapp.alikappconductor;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.Alikapp.alikappconductor.models.transaction.responses.PaymentMethod;
import com.Alikapp.alikappconductor.models.transaction.responses.ResponseExtra;
import com.Alikapp.alikappconductor.models.transaction.responses.TransactionInformation;
import com.Alikapp.alikappconductor.models.transaction.responses.TransactionResponse;
import com.Alikapp.alikappconductor.wompiApi.WompiapiService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailTransactionActivity extends AppCompatActivity {

    private TextView mCantidad, mTipo, mRef, mId, mEstado, mDate;
    private Button mVolver;

    private static final String TAG = "WOMPI";
    private Retrofit retrofit;
    private WompiapiService service;
    private static final String URL_BASE_WOMPI = "https://production.wompi.co/v1/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaction);

        retrofit = new Retrofit.Builder()
                .baseUrl(URL_BASE_WOMPI)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(WompiapiService.class);

        mCantidad = findViewById(R.id.amountDetalle);
        mTipo = findViewById(R.id.tipoPagoDetalle);
        mRef = findViewById(R.id.referenciaPagoDetalle);
        mId = findViewById(R.id.idPagoDetalle);
        mEstado = findViewById(R.id.estadoPagoDetalle);
        mDate = findViewById(R.id.fechaDetalle);

        mVolver = findViewById(R.id.btnHistorial);
        mVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent get = getIntent();
        String id = get.getStringExtra("idTransaction");
        String date = get.getStringExtra("date");

        verifyTransaction(id, date);
    }

    private void verifyTransaction(String id, String date) {
        Call<TransactionResponse> transaction = service.verificarEstadoTransaccion(id);

        transaction.enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                if (response.isSuccessful()) {
                    TransactionResponse transaction = response.body();
                    TransactionInformation informationyeye = transaction.getData();

                    if(informationyeye.getStatus() != null){
                        mEstado.setText(getEstadoPago(informationyeye.getStatus()));
                    }

                    if(informationyeye.getAmount_in_cents() > 0){
                        int A = informationyeye.getAmount_in_cents()/100;
                        mCantidad.setText(setearDineroPantalla(A + ""));
                    }

                    if(informationyeye.getPayment_method_type() != null){
                        mTipo.setText(getMetPago(informationyeye.getPayment_method_type()));
                    }

                    if(informationyeye.getReference() != null){
                        mRef.setText(informationyeye.getReference());
                    }

                    mId.setText(id);
                    mDate.setText(date);

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

    String v1 = "" ;
    @SuppressLint("SetTextI18n")
    private String setearDineroPantalla(String amount) {

        v1 = "";
        if(amount.length()>3) {
            int j = 0;
            for (int i = 0; i < amount.length(); i++) {
                System.out.println("cantDineroDisponible.length()" + amount.length());
                if (j == 3) {
                    String a = new StringBuilder().append(".").append(v1).toString();
                    System.out.println("valor de a " + a);
                    v1 = a;
                    j = 0;
                }
                j++;
                String b = new StringBuilder().append(amount.charAt(amount.length()-(i+1))).append(v1).toString();
                v1 = b;
            }
        } else {
            v1 = amount;
        }
        return "$ " + v1 + " COP";
    }

    private  String getMetPago(String s) {

        String respuesta= "";

        if (s.equals("CARD")){

            respuesta= "Tarjeta de Crédito";
        }

        else if (s.equals("BANCOLOMBIA_TRANSFER")){

            respuesta = "Transferencia Bancolombia";
        }

        else if (s.equals("NEQUI")){

            respuesta = "NEQUI";
        }

        else if (s.equals("PSE")){

            respuesta = "Pagos en Linea";
        }

        else if (s.equals("BANCOLOMBIA_COLLECT")){

            respuesta = "Corresponsal Bancario";
        }
        else {

            respuesta = "Pago Exitoso";
        }

        return respuesta;
    }

    private  String getEstadoPago(String s) {

        String respuesta= "";

        if (s.equals("APPROVED")){

            respuesta= "Aprobado";
        }

        else {

            respuesta = "Exitoso";
        }

        return respuesta;
    }
}