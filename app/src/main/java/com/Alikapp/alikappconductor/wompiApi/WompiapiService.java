package com.Alikapp.alikappconductor.wompiApi;

import com.Alikapp.alikappconductor.models.acceptance_token.WompiData;

import retrofit2.Call;
import retrofit2.http.GET;

public interface WompiapiService {

    static final String URL_WOMPI_ACEPTACION = "merchants/";
    static final String LLAVE_PUBLICA_WOMPI = "pub_test_7CZ8Yhe2xeaFd6Z1FZxip3nHlwQzgIvR";

    @GET(URL_WOMPI_ACEPTACION + LLAVE_PUBLICA_WOMPI)
    Call<WompiData> obtenerParametros();
}
