package com.Alikapp.alikappconductor;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.Alikapp.alikappconductor.models.acceptance_token.ParametrosAceptacion;
import com.Alikapp.alikappconductor.models.acceptance_token.WompiData;
import com.Alikapp.alikappconductor.models.acceptance_token.WompiRespuesta;
import com.Alikapp.alikappconductor.models.creditCardToken.CreditCardData;
import com.Alikapp.alikappconductor.models.creditCardToken.CreditCardRespose;
import com.Alikapp.alikappconductor.models.creditCardToken.CreditCardTokenizar;
import com.Alikapp.alikappconductor.models.transaction.Transaction;
import com.Alikapp.alikappconductor.models.transaction.responses.TransactionResponse;
import com.Alikapp.alikappconductor.models.transaction.responses.TransactionInformation;
import com.Alikapp.alikappconductor.wompiApi.WompiapiService;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class popupRecarga extends AppCompatActivity {

    private CheckBox checkBox;
    private Button mRecargar;
    private Button mCancela;
    private EditText mCantidad;

    private Boolean isEnabled = false;
    private Boolean isEnabledValue = false;

    private static final String NUMERO_TARJETA = "4242424242424242";
    private static final String CVC = "123";
    private static final String EXP_MONT = "08";
    private static final String EXP_YEAR = "28";
    private static final String CARD_HOLDER = "José Pérez";


    private String aceptanceToken;
    private String urlWompiTerminos;
    private double amount;

    private String tokenCreditCard;

    private static final String TAG = "WOMPI";
    private Retrofit retrofit;
    private WompiapiService service;
    private static final String URL_BASE_WOMPI = "https://sandbox.wompi.co/v1/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_popup_recarga);

        retrofit = new Retrofit.Builder()
                .baseUrl(URL_BASE_WOMPI)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(WompiapiService.class);

        mCantidad = findViewById(R.id.valorRecarga);
        mCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    amount = Double.parseDouble(mCantidad.getText().toString());
                    if(amount < 20000) {
                        isEnabledValue = false;
                        mCantidad.setTextColor(Color.parseColor("#c22828"));
                    } else {
                        isEnabledValue = true;
                        mCantidad.setTextColor(Color.parseColor("#FF000000"));
                    }
                } catch (Exception e) {
                    isEnabledValue = false;
                    mCantidad.setTextColor(Color.parseColor("#c22828"));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mCancela = findViewById(R.id.cancelarRecarga);
        mCancela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mRecargar = findViewById(R.id.aceptarRecarga);
        mRecargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEnabled) {
                    if (isEnabledValue) {
                        recagar();
                    } else {
                        Toast.makeText(popupRecarga.this, "Transacción inválida, el monto debe ser igual o superior a $20.000", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(popupRecarga.this, "Debes aceptar los términos y condiciones para efectuar el pago", Toast.LENGTH_LONG).show();
                }
            }
        });

        checkBox = findViewById(R.id.chk_terminosycondiciones);
        checkBox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent (Intent.ACTION_VIEW, Uri.parse(urlWompiTerminos));
                startActivity(intent);
                return false;
            }
        });
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(checkBox.isChecked()){
                    obtenerParametros();
                } else {
                    isEnabled = false;
                }
            }
        });
        obtenerParametros();
    }

    private void obtenerParametros() {
        Call<WompiData> wompiDataCall = service.obtenerParametros();

        wompiDataCall.enqueue(new Callback<WompiData>() {
            @Override
            public void onResponse(Call<WompiData> call, Response<WompiData> response) {
                if(response.isSuccessful()) {
                    WompiData wompiData = response.body();
                    WompiRespuesta wompiRespuesta = wompiData.getData();
                    ParametrosAceptacion parametrosAceptacion = wompiRespuesta.getPresigned_acceptance();
                    urlWompiTerminos = parametrosAceptacion.getPermalink();
                    if(checkBox.isChecked()){
                        aceptanceToken = parametrosAceptacion.getAcceptance_token();
                        System.out.println(aceptanceToken);
                    }
                    isEnabled = true;
                } else {
                    if(checkBox.isChecked()){
                        checkBox.setChecked(false);
                        Toast.makeText(popupRecarga.this, "Intentete nuevamente", Toast.LENGTH_SHORT).show();
                    } else {
                        obtenerParametros();
                    }
                    Log.e(TAG, "obtenerParametros onResponse: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<WompiData> call, Throwable t) {
                if(checkBox.isChecked()){
                    checkBox.setChecked(false);
                    Toast.makeText(popupRecarga.this, "Intentete nuevamente", Toast.LENGTH_SHORT).show();
                } else {
                    obtenerParametros();
                }
                Log.e(TAG, "obtenerParametros onFailure: " + t.getMessage());
            }
        });
    }

    public void tokenizarCreditCard(View view) {
        CreditCardTokenizar creditCardTokenizar = new CreditCardTokenizar(NUMERO_TARJETA, CVC, EXP_MONT, EXP_YEAR, CARD_HOLDER);
        Call<CreditCardData> creditCardDataCall = service.tokenizarTarjeta(creditCardTokenizar);

        creditCardDataCall.enqueue(new Callback<CreditCardData>() {
            @Override
            public void onResponse(Call<CreditCardData> call, Response<CreditCardData> response) {
                if(response.isSuccessful()) {
                    CreditCardData creditCard = response.body();
                    CreditCardRespose creditCardRespose = creditCard.getData();
                    tokenCreditCard = creditCardRespose.getId();
                    System.out.println(tokenCreditCard);
                } else {
                    Log.e(TAG, "tokenizarCreditCard onResponse: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<CreditCardData> call, Throwable t) {
                Log.e(TAG, "tokenizarCreditCard onFailure: " + t.getMessage());
            }
        });
    }

    private String idTransaccion;
    private void recagar() {
        HashMap creditCardPay = new HashMap();
        creditCardPay.put("type", "CARD");
        creditCardPay.put("installments", 2);
        creditCardPay.put("token", tokenCreditCard);
        Long timestamp = System.currentTimeMillis();
        String conductorUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String referencia = new StringBuilder()
                .append(timestamp)
                .append("//")
                .append(conductorUID)
                .toString();
        Transaction transaction = new Transaction(aceptanceToken, (int) (amount*100), "pepito_perez@example.com", referencia, creditCardPay);

        Call<TransactionResponse> transactionResponseCall = service.payTransaction(transaction);
        transactionResponseCall.enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                if(response.isSuccessful()){
                    TransactionResponse transaction = response.body();
                    TransactionInformation informationyeye = transaction.getData();
                    System.out.println(informationyeye.getId());
                    idTransaccion = informationyeye.getId();
                    System.out.println(informationyeye.getStatus());
                    verificarEstadoTransaccion();
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
                Log.e(TAG, "Recarga onFailure: " + t.getMessage());
            }
        });
    }

    private void verificarEstadoTransaccion() {
        Call<TransactionResponse> transaction = service.verificarEstadoTransaccion(idTransaccion);

        transaction.enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                if (response.isSuccessful()) {
                    TransactionResponse transaction = response.body();
                    TransactionInformation informationyeye = transaction.getData();
                    System.out.println(informationyeye.getId());
                    System.out.println(informationyeye.getStatus());
                } else {
                    Log.e(TAG, "Recarga onResponse: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<TransactionResponse> call, Throwable t) {
                Log.e(TAG, "verificarEstadoTransaccion onFailure: " + t.getMessage());
            }
        });
    }

}