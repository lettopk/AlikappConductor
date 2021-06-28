package com.Alikapp.alikappconductor;

import android.app.Dialog;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.royrodriguez.transitionbutton.TransitionButton;

import java.util.logging.LogRecord;

public class CustomerLoginActivity extends AppCompatActivity {

    private EditText mEmail, mPassword;
    private Button mRegistration;
    private TransitionButton transitionButton;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private LinearLayout login;
    private LinearLayout splash;

    private Dialog myDialogAlert;
    private Button mAceptaAlert;
    private TextView mTextAlert;

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);

        login = findViewById(R.id.linearLayout);
        login.setVisibility(View.GONE);
        splash = findViewById(R.id.splash);
        splash.setVisibility(View.VISIBLE);
        Animation animacion1= AnimationUtils.loadAnimation(this, R.anim.desplazamiento_arriba);
        Animation animacion2= AnimationUtils.loadAnimation(this, R.anim.desplazamiento_abajo);
        TextView tvalikapp = findViewById(R.id.textAlikapp);
        ImageView logoAlikapp = findViewById(R.id.imagenlogo);

        tvalikapp.setAnimation(animacion2);
        logoAlikapp.setAnimation(animacion1);

        myDialogAlert = new Dialog(this);
        myDialogAlert.setContentView(R.layout.layout_popup_alert);
        mAceptaAlert = myDialogAlert.findViewById(R.id.btnOkAlert);
        mTextAlert = myDialogAlert.findViewById(R.id.textAlert);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@androidx.annotation.NonNull FirebaseAuth firebaseAuth) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if(user!=null){
                            Intent intent = new Intent(CustomerLoginActivity.this, CustomerMapActivity.class);
                            startActivity(intent);
                            finish();
                            return;
                        } else {
                            login.setVisibility(View.VISIBLE);
                            splash.setVisibility(View.GONE);
                        }
                    }
                }, 3000);
            }
        };

        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);

        transitionButton = findViewById(R.id.logint);
        transitionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the loading animation when the user tap the button
                transitionButton.startAnimation();
                try {
                    final String email = mEmail.getText().toString();
                    final String password = mPassword.getText().toString();
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(CustomerLoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@androidx.annotation.NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                transitionButton.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                                showPopupAlert("Usuario o contraseña incorrectos, intente nuevamente");
                            }
                        }
                    });
                }catch (Exception e){
                    transitionButton.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                    Toast.makeText(CustomerLoginActivity.this, "Ingrese un usuario y contraseña", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //ir a pagina de registro mediante boton registro
        mRegistration =(Button)findViewById(R.id.registration);
        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent registrop = new Intent(CustomerLoginActivity.this, CostumerRegistroActivity.class);
                startActivity(registrop);
            }
        });
    }

    private void showPopupAlert(String AlertMessage){
        mTextAlert.setText(AlertMessage);
        mAceptaAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialogAlert.dismiss();
            }
        });

        if(myDialogAlert != null && !CustomerLoginActivity.this.isFinishing()) {
            myDialogAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialogAlert.show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }
    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }

}
