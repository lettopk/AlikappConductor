package com.Alikapp.alikappconductor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityBilletera extends AppCompatActivity {

    private Button btnRecarga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billetera);


        btnRecarga = (Button) findViewById(R.id.btnRecarga);
        btnRecarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( ActivityBilletera.this, popupRecarga.class);
                startActivity(intent);
            }
        });


    }


}