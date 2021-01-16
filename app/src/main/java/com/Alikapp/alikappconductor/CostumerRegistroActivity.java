package com.Alikapp.alikappconductor;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.paypal.android.sdk.ey.v;

public class CostumerRegistroActivity  extends AppCompatActivity {

    private EditText mEmail, mPassword;
    private Button  mRegistro;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;


}
