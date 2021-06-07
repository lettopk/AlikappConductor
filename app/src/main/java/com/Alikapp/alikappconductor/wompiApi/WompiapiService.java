package com.Alikapp.alikappconductor.wompiApi;

import com.Alikapp.alikappconductor.models.acceptance_token.WompiData;
import com.Alikapp.alikappconductor.models.creditCardToken.CreditCardData;
import com.Alikapp.alikappconductor.models.creditCardToken.CreditCardTokenizar;
import com.Alikapp.alikappconductor.models.pseBanks.PseData;
import com.Alikapp.alikappconductor.models.transaction.Transaction;
import com.Alikapp.alikappconductor.models.transaction.responses.TransactionResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface WompiapiService {

    static final String URL_WOMPI_TRANSACCION = "transactions";
    static final String URL_WOMPI_ACEPTACION = "merchants/";
    static final String URL_WOMPI_TOKENIZAR_TARJETA = "tokens/cards";
    static final String URL_WOMPI_PSE_BANCOS = "pse/financial_institutions";

    static final String LLAVE_PUBLICA_WOMPI = "pub_prod_BScxnsM92Qp3te8VRfvONb3ikvqLr5X1";

    @GET(URL_WOMPI_ACEPTACION + LLAVE_PUBLICA_WOMPI)
    Call<WompiData> obtenerParametros();

    @GET(URL_WOMPI_TRANSACCION + "/{id}")
    Call<TransactionResponse> verificarEstadoTransaccion(@Path ("id") String id);

    @Headers({"Authorization: Bearer " + LLAVE_PUBLICA_WOMPI})
    @GET(URL_WOMPI_PSE_BANCOS)
    Call<PseData> getPseBancos();

    @Headers({"Authorization: Bearer " + LLAVE_PUBLICA_WOMPI})
    @POST(URL_WOMPI_TOKENIZAR_TARJETA)
    Call<CreditCardData> tokenizarTarjeta(@Body CreditCardTokenizar creditCardTokenizar);

    @Headers({"Authorization: Bearer " + LLAVE_PUBLICA_WOMPI})
    @POST(URL_WOMPI_TRANSACCION)
    Call<TransactionResponse> payTransaction(@Body Transaction transaction);
}