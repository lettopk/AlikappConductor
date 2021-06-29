package com.Alikapp.alikappconductor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class SoporteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soporte);

        Button acepto = findViewById(R.id.acepto);
        acepto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LinearLayout enlaceFacebook = findViewById(R.id.enlaceFacebook);
        enlaceFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (Intent.ACTION_VIEW, Uri.parse("https://m.facebook.com/AlikappUM/"));
                startActivity(intent);
            }
        });

        LinearLayout enlaceInstagram = findViewById(R.id.enlaceInstagram);
        enlaceInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/alikapp_um/"));
                startActivity(intent);
            }
        });
    }
}