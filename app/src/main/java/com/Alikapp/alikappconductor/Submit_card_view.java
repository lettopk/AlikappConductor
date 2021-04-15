package com.Alikapp.alikappconductor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.creditcarddesign.CreditCardView;

import java.util.Calendar;
import java.util.Date;

public class Submit_card_view extends AppCompatActivity {


    private EditText mCardNumber, mNameCard, mNumCcv, mDateExpiry;
    private Button mConfir;
    private Object CreditCardView;
    private TextView frontCardNUmber, fromCardName, fromCardExpiry, cvvNumber;
    private View creditCardFront, creditCardBack;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_card_view);


        creditCardFront = findViewById(R.id.Creditcardfront);
        creditCardBack = findViewById(R.id.Creditcardback);

        frontCardNUmber = findViewById(R.id.front_card_number);
        fromCardName = findViewById(R.id.front_card_name);
        fromCardExpiry = findViewById(R.id.front_card_expiry);
        cvvNumber = findViewById(R.id.cvvNumber);


        CreditCardView = (CreditCardView) findViewById(R.id.card_1);
        mCardNumber = (EditText) findViewById(R.id.numTc);
        mNameCard = (EditText) findViewById(R.id.nomComp);
        mNumCcv = (EditText) findViewById(R.id.numCcv);
        mDateExpiry = (EditText) findViewById(R.id.fecVenc);
        mConfir = (Button) findViewById(R.id.btnConfir);

        Date currentTime = Calendar.getInstance().getTime();

        mNameCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                fromCardName.setText(mNameCard.getText().toString());
                creditCardFront.setVisibility(View.VISIBLE);
                creditCardBack.setVisibility(View.GONE);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (mCardNumber.getText().length() <= 4 )
                {

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

                creditCardFront.setVisibility(View.VISIBLE);
                creditCardBack.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        mDateExpiry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                creditCardFront.setVisibility(View.VISIBLE);
                creditCardBack.setVisibility(View.GONE);

                switch (mDateExpiry.getText().length()) {

                    case 0:

                        fromCardExpiry.setText("MM/AA");

                        break;


                    case 1:

                        fromCardExpiry.setText(mDateExpiry.getText().toString()+"M/AA");

                        break;


                    case 2:

                        fromCardExpiry.setText(mDateExpiry.getText().toString()+"/AA");

                        break;


                    case 3:

                        fromCardExpiry.setText(mDateExpiry.getText().toString().substring(0, 2) + "/"+
                                mDateExpiry.getText().toString().substring(2)+ "A" );

                        break;


                    case 4:

                        fromCardExpiry.setText(mDateExpiry.getText().toString().substring(0,2) + "/"+ mDateExpiry.getText().toString().substring(2));

                        break;


                }


            }

            @Override
            public void afterTextChanged(Editable s) {

                if (mDateExpiry.getText().length()>= 2) {

                    int i = Integer.parseInt(mDateExpiry.getText().toString().substring(0, 2));

                    if (i>12 ){

                        mDateExpiry.setText("");

                        Toast.makeText(getBaseContext(),"El mes no puede ser mayor a 12", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        mNumCcv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                cvvNumber.setText(mNumCcv.getText().toString());
                creditCardFront.setVisibility(View.GONE);
                creditCardBack.setVisibility(View.VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (mNumCcv.getText().length() == 3)
                {

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


    }

}