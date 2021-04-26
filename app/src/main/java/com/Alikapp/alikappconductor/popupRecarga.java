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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.Alikapp.alikappconductor.models.acceptance_token.ParametrosAceptacion;
import com.Alikapp.alikappconductor.models.acceptance_token.WompiData;
import com.Alikapp.alikappconductor.models.acceptance_token.WompiRespuesta;
import com.Alikapp.alikappconductor.models.creditCardToken.CreditCardData;
import com.Alikapp.alikappconductor.models.creditCardToken.CreditCardRespose;
import com.Alikapp.alikappconductor.models.creditCardToken.CreditCardTokenizar;
import com.Alikapp.alikappconductor.models.pseBanks.PseData;
import com.Alikapp.alikappconductor.models.pseBanks.PseResponse;
import com.Alikapp.alikappconductor.models.transaction.Transaction;
import com.Alikapp.alikappconductor.models.transaction.responses.PaymentMethod;
import com.Alikapp.alikappconductor.models.transaction.responses.ResponseExtra;
import com.Alikapp.alikappconductor.models.transaction.responses.TransactionResponse;
import com.Alikapp.alikappconductor.models.transaction.responses.TransactionInformation;
import com.Alikapp.alikappconductor.wompiApi.WompiapiService;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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

    private Spinner opcionesTipoPersonasPSE, opcionesTipoDocumentoPSE, opcionesBancoPSE;

    private static String EMAIL;
    private static String NUMTELEFONOCEL;
    private static String NOMBRE;
    private static int TIPOPERSONA;
    private static String TIPOID;
    private static String NUMCC;
    private static String BANCO;
    private static final String DEScPAGO ="Pago por recarga para servicios y creditos ALIKAPP";

    private Dialog pagoNqui;
    private EditText emailNequi, nombreNequi, numCelularNequi;
    private Button btnConfirNQ, btnCancelNQ;


    private Dialog pagoPSE;
    private EditText emailPSE, nombrePSE, numDocumentoPSE;

    private Button btnConfirPSE, btnCancelPSE;


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


        pagoNqui = new Dialog(this);
        pagoNqui.setContentView(R.layout.layout_popup_pago_nequi);

        pagoPSE = new Dialog(this);
        pagoPSE.setContentView(R.layout.layout_popup_pago_pse);



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

        btnPagoTarjeta = (ToggleButton) findViewById(R.id.metPagoCard);
        btnPagoBancolo = (ToggleButton) findViewById(R.id.metPagoBancolo);
        btnPagoNequi = (ToggleButton) findViewById(R.id.metPagoNequi);
        btnPagoPSE = (ToggleButton) findViewById(R.id.metPagoPSE);


        emailBancolo = (EditText) transferenciaBancolo.findViewById(R.id.emailBancolo);
        nombreBancolo = (EditText) transferenciaBancolo.findViewById(R.id.nombreApellidosBancolo);
        numCelularBancolo = (EditText) transferenciaBancolo.findViewById(R.id.numeroCelularBancolo);
        btnConfirBC = (Button) transferenciaBancolo.findViewById(R.id.btnConfirBC);
        btnCancelBC = (Button) transferenciaBancolo.findViewById(R.id.btnCancelBC);

        emailNequi = (EditText) pagoNqui.findViewById(R.id.emailNequi);
        numCelularNequi = (EditText) pagoNqui.findViewById(R.id.numeroCelularNequi);
        btnConfirNQ = (Button) pagoNqui.findViewById(R.id.btnConfirNQ);
        btnCancelNQ = (Button) pagoNqui.findViewById(R.id.btnCancelNQ);

        opcionesBancoPSE = (Spinner) pagoPSE.findViewById(R.id.spinBancoPSE);
        opcionesTipoDocumentoPSE = (Spinner) pagoPSE.findViewById(R.id.spinTipoDocumentoPSE);
        opcionesTipoPersonasPSE = (Spinner) pagoPSE.findViewById(R.id.spinTipoPersonaPSE);
        emailPSE = (EditText) pagoPSE.findViewById(R.id.emailPSE);
        numDocumentoPSE = (EditText) pagoPSE.findViewById(R.id.nunDocumentoPSE);
        nombrePSE = (EditText) pagoPSE.findViewById(R.id.nombreApellidosPSE);
        btnConfirPSE = (Button) pagoPSE.findViewById(R.id.btnConfirPSE);
        btnCancelPSE = (Button) pagoPSE.findViewById(R.id.btnCancelPSE);

        mCantidad = findViewById(R.id.valorRecarga);
        mCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    amount = Double.parseDouble(mCantidad.getText().toString());
                    if(amount < 400) {
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
                if(!isEnabled) {
                    Toast.makeText(popupRecarga.this, "Debes aceptar los términos y condiciones para efectuar el pago", Toast.LENGTH_LONG).show();
                } else if (!isEnabledValue) {
                    Toast.makeText(popupRecarga.this, "Transacción inválida, el monto debe ser igual o superior a $20.000", Toast.LENGTH_LONG).show();
                } else {
                    recagar();
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
                    checkBox.setEnabled(false);
                    Toast.makeText(popupRecarga.this, "Manten pulsado para leer terminos y condiciones", Toast.LENGTH_SHORT).show();
                } else {
                    isEnabled = false;
                }
            }
        });

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
                     desactivarBotonRecarga();

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
                  showPopupPagoBancolo();

              }

              else {

                  quitarFondobtnPagoBancolombia();
                  desactivarBotonRecarga();

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
                    showPopupPagoNequi();

                }

                else {

                    quitarFondobtnPagoNequi();
                    desactivarBotonRecarga();

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
                    getPseBancos();
                    showPopupPagoPSE();

                }

                else {

                    quitarFondobtnPagoPSE();
                    desactivarBotonRecarga();
                }

            }
        });


    }

    private void desactivarBotonRecarga() {

        mRecargar.setBackgroundResource(R.drawable.btn_recarga_desactivado);
        mRecargar.setEnabled(false);

    }

    private void activarBotonRecarga() {

        mRecargar.setBackgroundResource(R.drawable.btn_recargar);
        mRecargar.setEnabled(true);
    }

    private void showPopupPagoPSE() {
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.opcionesTipoDocumento, android.R.layout.simple_spinner_item);
        opcionesTipoDocumentoPSE.setAdapter(adapter1);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.opcionesTipoPersona, android.R.layout.simple_spinner_item);
        opcionesTipoPersonasPSE.setAdapter(adapter2);

        ArrayList<String> prepre = new ArrayList<String>();
        for(int i = 0; i < repustaApiBancosPSE.size(); i++) {
            PseResponse pse = repustaApiBancosPSE.get(i);
            prepre.add(pse.getFinancial_institution_name());
        }
        String[] bancosApiPSE = prepre.toArray(new String[0]);
        ArrayAdapter<String> bancosArray = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, bancosApiPSE);
        opcionesBancoPSE.setAdapter(bancosArray);

        btnConfirPSE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tipoDocumento= opcionesTipoDocumentoPSE.getSelectedItem().toString();
                System.out.println(tipoDocumento);

                Long tipoPersona= opcionesTipoPersonasPSE.getSelectedItemId();
                System.out.println(tipoPersona);

                String banco = opcionesBancoPSE.getSelectedItem().toString();
                System.out.println(banco);

                String code = "";
                for (int i = 0; i < repustaApiBancosPSE.size();i++) {
                    PseResponse pse = repustaApiBancosPSE.get(i);
                    if (pse.getFinancial_institution_name().equals(banco)){
                        code = pse.getFinancial_institution_code();
                    }
                }
                System.out.println(code);

                if (nombrePSE.getText().length()>0 && emailPSE.getText().length()>0
                        && numDocumentoPSE.toString().length()>0 && !code.equals("0")){
                    NOMBRE = nombrePSE.getText().toString();
                    EMAIL = emailPSE.getText().toString();
                    NUMCC = numDocumentoPSE.getText().toString();
                    TIPOID = tipoDocumento;
                    TIPOPERSONA = Math.toIntExact(tipoPersona);
                    BANCO = code;
                    pagoPSE.dismiss();
                    Toast.makeText(popupRecarga.this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(popupRecarga.this, "Diligencia todos los campos disponibles", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancelPSE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pagoPSE.dismiss();
            }
        });

        pagoPSE.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pagoPSE.show();
    }
  

    private void showPopupPagoNequi() {

        btnConfirNQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailNequi.getText().length()>0 && numCelularNequi.getText().length()>0){
                    EMAIL = emailNequi.getText().toString();
                    NUMTELEFONOCEL = numCelularNequi.getText().toString();
                    Toast.makeText(popupRecarga.this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
                    pagoNqui.dismiss();
                    activarBotonRecarga();
                } else {
                    Toast.makeText(popupRecarga.this, "Diligencia todos lo campos disponibles", Toast.LENGTH_SHORT).show();
                }

            }
        });

        pagoNqui.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pagoNqui.show();
    }

    private void showPopupPagoBancolo() {
        btnConfirBC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nombreBancolo.getText().length()>0 && emailBancolo.getText().length()>0 && numCelularBancolo.getText().length()>0){
                    NOMBRE = nombreBancolo.getText().toString();
                    EMAIL = emailBancolo.getText().toString();
                    NUMTELEFONOCEL = numCelularBancolo.getText().toString();
                    transferenciaBancolo.dismiss();
                    activarBotonRecarga();
                    Toast.makeText(popupRecarga.this, "Datos guardados correctamente", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(popupRecarga.this, "Diligencia todo los campos disponibles", Toast.LENGTH_LONG).show();
                }

            }
        });

        btnCancelBC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transferenciaBancolo.dismiss();
            }
        });

        transferenciaBancolo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        transferenciaBancolo.show();
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

    /**
     * permite visualizar los campos para diligenciar la terjeta dde crédito**/
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
                            activarBotonRecarga();
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
                    checkBox.setEnabled(true);
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
        Transaction transaction = new Transaction(aceptanceToken, (int) (amount*100), EMAIL, referencia, metPago());

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
            metPago.put("payment_description", DEScPAGO);
            //metPago.put("sandbox_status", "APPROVED");

        }

        else if (btnPagoNequi.isChecked()) {

            metPago.put("type", "NEQUI");
            metPago.put("phone_number", NUMTELEFONOCEL);
        }

        else if (btnPagoPSE.isChecked()){

            metPago.put("type", "PSE");
            metPago.put("user_type", TIPOPERSONA);
            metPago.put("user_legal_id_type", TIPOID);
            metPago.put("user_legal_id", NUMCC);
            metPago.put("financial_institution_code", BANCO);
            metPago.put("payment_description", DEScPAGO);

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
                            System.out.println(metodoResponse.getAsync_payment_url());
                            //Intent intent = new Intent(popupRecarga.this, LayoutWebview.class);
                            //intent.putExtra("pack", metodoResponse.getAsync_payment_url());
                            //startActivity(intent);
                            Intent intent = new Intent (Intent.ACTION_VIEW, Uri.parse(metodoResponse.getAsync_payment_url()));
                            startActivity(intent);

                        } else if (metodoPago.getExtra() == null && btnPagoBancolo.isChecked()){
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    verificarEstadoTransaccion();
                                }
                            }, 3000);
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

    private ArrayList<PseResponse> repustaApiBancosPSE;
    private void getPseBancos() {
        Call<PseData> pseDataCall = service.getPseBancos();

        pseDataCall.enqueue(new Callback<PseData>() {
            @Override
            public void onResponse(Call<PseData> call, Response<PseData> response) {
                if (response.isSuccessful()) {
                    PseData data = response.body();
                    repustaApiBancosPSE = data.getData();
                    showPopupPagoPSE();
                } else {
                    try {
                        Log.e(TAG, "getPseBancos onResponse: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<PseData> call, Throwable t) {
                Log.e(TAG, "getPseBancos onFailure: " + t.getMessage());
            }
        });
    }

}