package com.Alikapp.alikappconductor;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.google.firebase.database.core.Constants;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

public class splashScreen  extends AppCompatActivity {

    private TextView textAlik;
    private Typeface Roboc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splashscreen);

        //animacion de la pantalla
        Animation animacion1= AnimationUtils.loadAnimation(this, R.anim.desplazamiento_arriba);
        Animation animacion2= AnimationUtils.loadAnimation(this, R.anim.desplazamiento_abajo);
        TextView tvalikapp = findViewById(R.id.textAlikapp);
        ImageView logoAlikapp = findViewById(R.id.imagenlogo);

        tvalikapp.setAnimation(animacion2);
        logoAlikapp.setAnimation(animacion1);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent login = new Intent(splashScreen.this, CustomerLoginActivity.class);
                startActivity(login);
                finish();
            }
        }, 3000);



    }
}