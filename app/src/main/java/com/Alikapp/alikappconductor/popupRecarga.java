package com.Alikapp.alikappconductor;


import android.annotation.SuppressLint;
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
import com.Alikapp.alikappconductor.wompiApi.WompiapiService;

import com.Alikapp.alikappconductor.models.acceptance_token.ParametrosAceptacion;
import com.Alikapp.alikappconductor.models.acceptance_token.WompiData;
import com.Alikapp.alikappconductor.models.acceptance_token.WompiRespuesta;
import com.Alikapp.alikappconductor.wompiApi.WompiapiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Url;

public class popupRecarga extends AppCompatActivity {

    private CheckBox checkBox;
    private Button mRecargar;
    private Button mCancela;
    private EditText mCantidad;

    private Boolean isEnabled = false;

    private static final String TAG = "WOMPI";
    private static final String URL_TERMINOS_WOMPI = "https://wompi.co/wp-content/uploads/2019/09/TERMINOS-Y-CONDICIONES-DE-USO-USUARIOS-WOMPI.pdf";
    private Retrofit retrofit;
    private static final String URL_BASE_WOMPI = "https://sandbox.wompi.co/v1/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_popup_recarga);

        retrofit = new Retrofit.Builder()
                .baseUrl(URL_BASE_WOMPI)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mCantidad = findViewById(R.id.valorRecarga);
        mCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    double i = Double.parseDouble(mCantidad.getText().toString());
                    if(i < 20000) {
                        mCantidad.setTextColor(Color.parseColor("#c22828"));
                    } else {
                        mCantidad.setTextColor(Color.parseColor("#FF000000"));
                    }
                } catch (Exception e) {
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

                } else {
                    Toast.makeText(popupRecarga.this, "Debes aceptar los términos y condiciones para efectuar el pago", Toast.LENGTH_LONG).show();
                }
            }
        });

        checkBox = findViewById(R.id.chk_terminosycondiciones);
        checkBox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent (Intent.ACTION_VIEW, Uri.parse(URL_TERMINOS_WOMPI));
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
    }

    private void obtenerParametros() {
        WompiapiService service = retrofit.create(WompiapiService.class);
        Call<WompiData> wompiDataCall = service.obtenerParametros();

        wompiDataCall.enqueue(new Callback<WompiData>() {
            @Override
            public void onResponse(Call<WompiData> call, Response<WompiData> response) {
                if(response.isSuccessful()) {
                    WompiData wompiData = response.body();
                    WompiRespuesta wompiRespuesta = wompiData.getData();
                    ParametrosAceptacion parametrosAceptacion = wompiRespuesta.getPresigned_acceptance();
                    System.out.println(parametrosAceptacion.getAcceptance_token());
                    isEnabled = true;
                } else {
                    checkBox.setChecked(false);
                    Toast.makeText(popupRecarga.this, "Intentete nuevamente", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onResponse: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<WompiData> call, Throwable t) {
                checkBox.setChecked(false);
                Toast.makeText(popupRecarga.this, "Intentete nuevamente", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

}