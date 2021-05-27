package com.Alikapp.alikappconductor;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
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

        mRegistration = (Button) findViewById(R.id.btnRegusuario);
        mRegistration.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                final String password = mPassword.getText().toString();
                final String ConfirmarPassword = mConfirmarPassword.getText().toString();
                if(mPassword.getText().toString().contains(" ")) {
                    Toast.makeText(CostumerRegistroActivity.this, "La contraseña no debe tener espacios, intenta nuevamente", Toast.LENGTH_SHORT).show();
                } else if(!password.equals(ConfirmarPassword)) {
                    Toast.makeText(CostumerRegistroActivity.this, "Error al confirmar contraseña, intenta nuevamente", Toast.LENGTH_SHORT).show();
                } else {
                    final String email = mEmail.getText().toString();
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(CostumerRegistroActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@androidx.annotation.NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(CostumerRegistroActivity.this, "sign up error", Toast.LENGTH_LONG).show();
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