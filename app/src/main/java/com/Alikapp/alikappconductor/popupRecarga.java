package com.Alikapp.alikappconductor;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.Alikapp.alikappconductor.models.acceptance_token.ParametrosAceptacion;
import com.Alikapp.alikappconductor.models.acceptance_token.WompiData;
import com.Alikapp.alikappconductor.models.acceptance_token.WompiRespuesta;
import com.Alikapp.alikappconductor.models.creditCardToken.CreditCardData;
import com.Alikapp.alikappconductor.models.creditCardToken.CreditCardRespose;
import com.Alikapp.alikappconductor.models.creditCardToken.CreditCardTokenizar;
import com.Alikapp.alikappconductor.models.transaction.Transaction;
import com.Alikapp.alikappconductor.models.transaction.responses.PaymentMethod;
import com.Alikapp.alikappconductor.models.transaction.responses.ResponseExtra;
import com.Alikapp.alikappconductor.models.transaction.responses.TransactionResponse;
import com.Alikapp.alikappconductor.models.transaction.responses.TransactionInformation;
import com.Alikapp.alikappconductor.wompiApi.WompiapiService;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class popupRecarga extends AppCompatActivity {

    private CheckBox checkBox;
    private Button mRecargar, mCancela;
    private ToggleButton btnPagoTarjeta, btnPagoBancolo, btnPagoNequi, btnPagoPSE;
    private EditText mCantidad;

    private Dialog transferenciaBancolo;
    private EditText emailBancolo, nombreBancolo, numCelularBancolo, tipoPersona;
    private Button btnConfirBC, btnCancelBC;


    private Dialog myDialog;
    private View linearLayout3;
    private EditText mCardNumber, mNameCard, mNumCcv, mDateExpiry;
    private Button mConfir;
    private TextView frontCardNUmber, fromCardName, fromCardExpiry, cvvNumber;
    private View creditCardFront, creditCardBack;
    private Boolean a1 = false, a2 = false, a3 = false, a4 = false;

    private Boolean isEnabled = false;
    private Boolean isEnabledValue = false;

    private static String NUMERO_TARJETA;
    private static String CVC;
    private static String EXP_MONT;
    private static String EXP_YEAR;
    private static String CARD_HOLDER;


    private String aceptanceToken;
    private String urlWompiTerminos;
    private double amount;

    private String tokenCreditCard;

    private static final String TAG = "WOMPI";
    private Retrofit retrofit;
    private WompiapiService service;
    private static final String URL_BASE_WOMPI = "https://production.wompi.co/v1/";

    private String actualMonth, actualYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_popup_recarga);

        Calendar currentTime = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
        SimpleDateFormat dateFormatYear = new SimpleDateFormat("yy");
        actualMonth = dateFormat.format(currentTime.getTime());
        actualYear = dateFormatYear.format(currentTime.getTime());

        retrofit = new Retrofit.Builder()
                .baseUrl(URL_BASE_WOMPI)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(WompiapiService.class);

        myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.layout_popup_submit_card_view);
        transferenciaBancolo = new Dialog(this);
        transferenciaBancolo.setContentView(R.layout.layout_popup_pago_bancolombia);

        creditCardFront = myDialog.findViewById(R.id.Creditcardfront);
        creditCardBack = myDialog.findViewById(R.id.Creditcardback);

        frontCardNUmber = myDialog.findViewById(R.id.front_card_number);
        fromCardName = myDialog.findViewById(R.id.front_card_name);
        fromCardExpiry = myDialog.findViewById(R.id.front_card_expiry);
        cvvNumber = myDialog.findViewById(R.id.cvvNumber);

        mCardNumber = (EditText) myDialog.findViewById(R.id.numTc);
        mNameCard = (EditText) myDialog.findViewById(R.id.nomComp);
        mNumCcv = (EditText) myDialog.findViewById(R.id.numCcv);
        mDateExpiry = (EditText) myDialog.findViewById(R.id.fecVenc);
        mConfir = (Button) myDialog.findViewById(R.id.btnConfirTC);
        linearLayout3 =  myDialog.findViewById(R.id.linearLayout3);

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
        btnPagoTarjeta = (ToggleButton) findViewById(R.id.metPagoCard);
        btnPagoBancolo = (ToggleButton) findViewById(R.id.metPagoBancolo);
        btnPagoNequi = (ToggleButton) findViewById(R.id.metPagoNequi);
        btnPagoPSE = (ToggleButton) findViewById(R.id.metPagoPSE);

        obtenerParametros();

        btnPagoTarjeta.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                 if (isChecked){

                     cambiarFondobtnPagoTarjeta();
                     quitarFondobtnPagoNequi();
                     quitarFondobtnPagoBancolombia();
                     quitarFondobtnPagoPSE();
                     showPopup();

                 }

                 else {

                     quitarFondoPagoTarjeta();

                }
            }

        });

      btnPagoBancolo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
              if (isChecked){

                  cambiarFondobtnPagoBancolombia();
                  quitarFondoPagoTarjeta();
                  quitarFondobtnPagoNequi();
                  quitarFondobtnPagoPSE();

              }

              else {

                  quitarFondobtnPagoBancolombia();

              }

          }
      });

        btnPagoNequi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){

                    cambiarFondobtnPagoNequi();
                    quitarFondoPagoTarjeta();
                    quitarFondobtnPagoBancolombia();
                    quitarFondobtnPagoPSE();

                }

                else {

                    quitarFondobtnPagoNequi();

                }

            }
        });

        btnPagoPSE.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){

                    cambiarFondobtnPagoPSE();
                    quitarFondoPagoTarjeta();
                    quitarFondobtnPagoBancolombia();
                    quitarFondobtnPagoNequi();

                }

                else {

                    quitarFondobtnPagoPSE();

                }

            }
        });

    }

    /**
     * Lanza un background cuando el boton pago con tarjeta queda enclavado**/
    public void cambiarFondobtnPagoTarjeta(){

        btnPagoTarjeta.setBackgroundResource(R.drawable.btn_met_pgo_seleccion);
        btnPagoBancolo.setChecked(false);
        btnPagoNequi.setChecked(false);
        btnPagoPSE.setChecked(false);
    }

    public void quitarFondoPagoTarjeta(){

        this.btnPagoTarjeta.setBackgroundResource(R.drawable.btn_auxiliar_recargas);
    }

    public void cambiarFondobtnPagoBancolombia(){

        btnPagoBancolo.setBackgroundResource(R.drawable.btn_met_pgo_seleccion);
        btnPagoTarjeta.setChecked(false);
        btnPagoNequi.setChecked(false);
        btnPagoPSE.setChecked(false);
    }

    public void quitarFondobtnPagoBancolombia(){

        this.btnPagoBancolo.setBackgroundResource(R.drawable.btn_auxiliar_recargas);
    }

    public void cambiarFondobtnPagoNequi(){

        btnPagoNequi.setBackgroundResource(R.drawable.btn_met_pgo_seleccion);
        btnPagoTarjeta.setChecked(false);
        btnPagoBancolo.setChecked(false);
        btnPagoPSE.setChecked(false);
    }

    public void quitarFondobtnPagoNequi(){

        this.btnPagoNequi.setBackgroundResource(R.drawable.btn_auxiliar_recargas);
    }

    public void cambiarFondobtnPagoPSE(){

        btnPagoPSE.setBackgroundResource(R.drawable.btn_met_pgo_seleccion);
        btnPagoTarjeta.setChecked(false);
        btnPagoNequi.setChecked(false);
        btnPagoBancolo.setChecked(false);
    }

    public void quitarFondobtnPagoPSE(){

        this.btnPagoPSE.setBackgroundResource(R.drawable.btn_auxiliar_recargas);
    }


    private void showPopup() {
        mNameCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fromCardName.setText(mNameCard.getText().toString());
                creditCardFront.setVisibility(View.VISIBLE);
                creditCardBack.setVisibility(View.GONE);
                a1 = mNameCard.getText().length() > 5;
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (mCardNumber.getText().length() <= 4 ) {
                    frontCardNUmber.setText(mCardNumber.getText().toString());
                }
                else if (mCardNumber.getText().length() > 4 && mCardNumber.getText().length()<= 8){
                    frontCardNUmber.setText(mCardNumber.getText().toString().substring(0, 4) + "  " + mCardNumber.getText().toString().substring(4));
                }

                else if (mCardNumber.getText().length() > 8 && mCardNumber.getText().length()<= 12){
                    frontCardNUmber.setText(mCardNumber.getText().toString().substring(0, 4) + "  " +
                            mCardNumber.getText().toString().substring(4, 8) + "  " +
                            mCardNumber.getText().toString().substring(8));
                }

                else if (mCardNumber.getText().length() > 12 && mCardNumber.getText().length()<= 16){
                    frontCardNUmber.setText(mCardNumber.getText().toString().substring(0, 4) + "  " +
                            mCardNumber.getText().toString().substring(4, 8) + "  " +
                            mCardNumber.getText().toString().substring(8, 12) + "  " +
                            mCardNumber.getText().toString().substring(12));
                }

                a2 = mCardNumber.getText().length() == 16;
                creditCardFront.setVisibility(View.VISIBLE);
                creditCardBack.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mDateExpiry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                creditCardFront.setVisibility(View.VISIBLE);
                creditCardBack.setVisibility(View.GONE);
                switch (mDateExpiry.getText().length()) {
                    case 0: fromCardExpiry.setText("MM/AA");
                        break;
                    case 1: fromCardExpiry.setText(mDateExpiry.getText().toString()+"M/AA");
                        break;
                    case 2: fromCardExpiry.setText(mDateExpiry.getText().toString()+"/AA");
                        break;
                    case 3: fromCardExpiry.setText(mDateExpiry.getText().toString().substring(0, 2) + "/"+
                                mDateExpiry.getText().toString().substring(2)+ "A" );
                        break;
                    case 4: fromCardExpiry.setText(mDateExpiry.getText().toString().substring(0,2) + "/"+ mDateExpiry.getText().toString().substring(2));
                        break;
                }
                a3 = mDateExpiry.getText().length() == 4;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mDateExpiry.getText().length() == 2) {
                    int i = Integer.parseInt(mDateExpiry.getText().toString().substring(0, 2));
                    if (i > 12){
                        mDateExpiry.setText("");
                        Toast.makeText(getBaseContext(),"El mes no puede ser mayor a 12", Toast.LENGTH_LONG).show();
                    }
                } else if (mDateExpiry.getText().length() == 4) {
                    int date = Integer.parseInt(mDateExpiry.getText().toString().substring(2) + mDateExpiry.getText().toString().substring(0, 2));
                    int dateActual = Integer.parseInt(actualYear + actualMonth);
                    if (date < dateActual) {
                        mDateExpiry.setText("");
                        Toast.makeText(getBaseContext(),"La fecha de expiración debe ser posterior a la fecha actual: " + actualMonth + "/" + actualYear, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        mNumCcv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cvvNumber.setText(mNumCcv.getText().toString());
                creditCardFront.setVisibility(View.GONE);
                creditCardBack.setVisibility(View.VISIBLE);
                a4 = mNumCcv.getText().length() == 3;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mNumCcv.getText().length() == 3) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            creditCardFront.setVisibility(View.VISIBLE);
                            creditCardBack.setVisibility(View.GONE);
                        }
                    }, 2000);
                }

            }
        });

        mConfir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (a1 && a2 && a3 && a4) {
                    while (true) {
                        StringBuilder sb = new StringBuilder(mNameCard.getText().toString());
                        if (mNameCard.getText().toString().substring(mNameCard.getText().length()-1).equals(" ")) {
                            sb.deleteCharAt(mNameCard.getText().length()-1);
                            mNameCard.setText(sb.toString());
                        } else {
                            NUMERO_TARJETA = mCardNumber.getText().toString();
                            CVC = mNumCcv.getText().toString();
                            CARD_HOLDER = mNameCard.getText().toString();
                            EXP_MONT = mDateExpiry.getText().toString().substring(0,2);
                            EXP_YEAR = mDateExpiry.getText().toString().substring(2);
                            tokenizarCreditCard();
                            break;

                        }
                    }
                } else {
                    Toast.makeText(popupRecarga.this, "Debes diligencir todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
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

    public void tokenizarCreditCard() {
        CreditCardTokenizar creditCardTokenizar = new CreditCardTokenizar(NUMERO_TARJETA, CVC, EXP_MONT, EXP_YEAR, CARD_HOLDER);
        Call<CreditCardData> creditCardDataCall = service.tokenizarTarjeta(creditCardTokenizar);

        creditCardDataCall.enqueue(new Callback<CreditCardData>() {
            @Override
            public void onResponse(Call<CreditCardData> call, Response<CreditCardData> response) {
                if(response.isSuccessful()) {
                    CreditCardData creditCard = response.body();
                    CreditCardRespose creditCardRespose = creditCard.getData();
                    tokenCreditCard = creditCardRespose.getId();
                    myDialog.dismiss();
                    Toast.makeText(popupRecarga.this, "Tarjeta válida", Toast.LENGTH_SHORT).show();
                    System.out.println(tokenCreditCard);
                    linearLayout3.setVisibility(View.GONE);
                } else {
                    Toast.makeText(popupRecarga.this, "Datos de tarjeta erróneos", Toast.LENGTH_LONG).show();
                    try {
                        Log.e(TAG, "tokenizarCreditCard onResponse: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
        Long timestamp = System.currentTimeMillis();
        String conductorUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String referencia = new StringBuilder()
                .append(timestamp)
                .append("//")
                .append(conductorUID)
                .toString();
        Transaction transaction = new Transaction(aceptanceToken, (int) (amount*100), "pepito_perez@example.com", referencia, metPago());

        Call<TransactionResponse> transactionResponseCall = service.payTransaction(transaction);
        transactionResponseCall.enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                if(response.isSuccessful()){
                    TransactionResponse transaction = response.body();
                    TransactionInformation informationyeye = transaction.getData();
                    PaymentMethod metodoPago = informationyeye.getPayment_method();
                    System.out.println(informationyeye.getPayment_method());
                    if (metodoPago.getExtra()!= null) {

                        System.out.println(metodoPago.getExtra());
                        ResponseExtra metodoResponse = metodoPago.getExtra();
                        Intent intent = new Intent(popupRecarga.this, LayoutWebview.class);
                        intent.putExtra("pack", metodoResponse.getAsync_payment_url());
                        startActivity(intent);
                    }
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

    private HashMap metPago() {

        HashMap metPago = new HashMap();

        if (btnPagoTarjeta.isChecked()) {

            metPago.put("type", "CARD");
            metPago.put("installments", 2);
            metPago.put("token", tokenCreditCard);

        }
        else if (btnPagoBancolo.isChecked()){

            metPago.put("type", "BANCOLOMBIA_TRANSFER");
            metPago.put("user_type", "PERSON");
            metPago.put("payment_description", "Pago por creditos Alikapp");
            //metPago.put("sandbox_status", "APPROVED");

        }

        else if (btnPagoNequi.isChecked()) {



        }
        else if (btnPagoPSE.isChecked()){



        }

        return metPago;

    }

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
                            Intent intent = new Intent(popupRecarga.this, LayoutWebview.class);
                            intent.putExtra("pack", metodoResponse.getAsync_payment_url());
                            startActivity(intent);
                        }

                        else if (metodoPago.getExtra() == null && btnPagoBancolo.isChecked()){

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    verificarEstadoTransaccion();

                                }
                            }, 500);

                        }

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

}