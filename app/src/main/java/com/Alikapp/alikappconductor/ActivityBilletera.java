package com.Alikapp.alikappconductor;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.Alikapp.alikappconductor.models.acceptance_token.ParametrosAceptacion;
import com.Alikapp.alikappconductor.models.acceptance_token.WompiData;
import com.Alikapp.alikappconductor.models.acceptance_token.WompiRespuesta;
import com.Alikapp.alikappconductor.wompiApi.WompiapiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActivityBilletera extends AppCompatActivity {

    private static final String TAG = "WOMPI";
    private Retrofit retrofit;
    private static final String URL_BASE_WOMPI = "https://sandbox.wompi.co/v1/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billetera);

        retrofit = new Retrofit.Builder()
                .baseUrl(URL_BASE_WOMPI)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        obtenerParametros();
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
                } else {
                    Log.e(TAG, "onResponse: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<WompiData> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
}
