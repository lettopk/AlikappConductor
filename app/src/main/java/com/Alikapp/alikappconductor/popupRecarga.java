package com.Alikapp.alikappconductor;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class popupRecarga extends AppCompatActivity {

    private Button mMetRecarga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_popup_recarga);

        mMetRecarga = findViewById(R.id.metPagoBancolo);
        mMetRecarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent( popupRecarga.this, Submit_card_view.class);
                startActivity(intent);
            }
        });

    }

}