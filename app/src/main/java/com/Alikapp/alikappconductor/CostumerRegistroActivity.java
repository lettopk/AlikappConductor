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

import static com.paypal.android.sdk.ey.v;

public class CostumerRegistroActivity  extends AppCompatActivity {

    private EditText mEmail, mPassword;
    private Button mRegistration;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    mAuth = FirebaseAuth.getInstance();

    firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@androidx.annotation.NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user!=null){
                Intent intent = new Intent(CustomerLoginActivity.this, CustomerMapActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        }
    };

    mEmail = (EditText) findViewById(R.id.email);
    mPassword = (EditText) findViewById(R.id.password);


    mRegistration = (Button) findViewById(R.id.registration);


}
