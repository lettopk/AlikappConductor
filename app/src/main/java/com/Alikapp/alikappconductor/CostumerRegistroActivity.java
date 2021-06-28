package com.Alikapp.alikappconductor;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CostumerRegistroActivity extends AppCompatActivity {

     EditText mEmail, mPassword, mConfirmarPassword;
     Button mRegistration;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    private Dialog myDialogAlert;
    private Button mAceptaAlert;
    private TextView mTextAlert;

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_costumer);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@androidx.annotation.NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    Intent intent = new Intent(CostumerRegistroActivity.this, CustomerMapActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        mEmail = (EditText) findViewById(R.id.et_correo);
        mPassword = (EditText) findViewById(R.id.et_newpass);
        mConfirmarPassword = (EditText) findViewById(R.id.Et_vnewpass);

        myDialogAlert = new Dialog(this);
        myDialogAlert.setContentView(R.layout.layout_popup_alert);
        mAceptaAlert = myDialogAlert.findViewById(R.id.btnOkAlert);
        mTextAlert = myDialogAlert.findViewById(R.id.textAlert);

        mRegistration = (Button) findViewById(R.id.btnRegusuario);
        mRegistration.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                final String password = mPassword.getText().toString();
                final String ConfirmarPassword = mConfirmarPassword.getText().toString();
                if(mPassword.getText().toString().contains(" ")) {
                    showPopupAlert("La contraseña no debe tener espacios, intenta nuevamente");
                } else if(!password.equals(ConfirmarPassword)) {
                    showPopupAlert("Error al confirmar contraseña, intenta nuevamente");
                } else if(mPassword.getText().toString().isEmpty() || mEmail.getText().toString().isEmpty() || mConfirmarPassword.getText().toString().isEmpty()) {
                    showPopupAlert("Debes diligenciar todos los campos");
                } else {
                    final String email = mEmail.getText().toString();
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(CostumerRegistroActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@androidx.annotation.NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(CostumerRegistroActivity.this, "Error de registro", Toast.LENGTH_LONG).show();
                            }else{
                                String user_id = mAuth.getCurrentUser().getUid();
                                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(user_id).child("email");
                                current_user_db.setValue(email);
                            }
                        }
                    });
                }
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

        if(myDialogAlert != null && !CostumerRegistroActivity.this.isFinishing()) {
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