package com.Alikapp.alikappconductor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.transition.ChangeImageTransform;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.paypal.android.sdk.u;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.Alikapp.alikappconductor.CustomerMapActivity.driver_ID;
import static java.nio.file.Paths.get;

public class Chat extends AppCompatActivity {
    private ImageView fotoPerfil;
    private TextView nombre;
    private RecyclerView rvMensajes;
    private EditText txtMensaje;
    private ImageButton btnEnviarFoto;
    private Button btnEnviar;
    private Button btnLlamar;
    // creacion de un adaptador
    private AdapterMensajes adapter;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private String NOMBRE_USUARIO;
    private String telefonoLlamar;
    private String conductorID;

    private static final int PHOTO_SEND =1;
    public static final String NODO_MENSAJES = "mensajes";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        fotoPerfil =(ImageView) findViewById(R.id.fotoPerfil);
        nombre = (TextView) findViewById(R.id.nombre);
        rvMensajes =(RecyclerView) findViewById(R.id.rvMensajes);
        txtMensaje = (EditText) findViewById(R.id.txtMensaje);
        btnEnviarFoto = (ImageButton) findViewById(R.id.btnEnviarFoto);
        btnEnviar = (Button) findViewById(R.id.btnEnviar);
        btnLlamar = (Button) findViewById(R.id.btnLlamar);

        fotoPerfil.setImageResource(R.mipmap.ic_default_user);
        conductorID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database =FirebaseDatabase.getInstance();
        //databaseReference = database.getReference(Chat.NODO_MENSAJES+"/"+conductorID+"/"+driver_ID);//Sala de chat
        databaseReference = database.getReference("Chat1");
        storage = FirebaseStorage.getInstance();




        adapter = new AdapterMensajes(this);
        LinearLayoutManager l = new LinearLayoutManager(this);
        /*
        Intent intent = getIntent();
        NOMBRE_USUARIO = intent.getStringExtra("nombrechat");
        nombre.setText(NOMBRE_USUARIO);
        */
        //seteado de layout
        rvMensajes.setLayoutManager(l);
        rvMensajes.setAdapter(adapter);
        btnLlamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:"+telefonoLlamar));
                if(ActivityCompat.checkSelfPermission(Chat.this,Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED)
                    return;
                startActivity(i);
            }
        });

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//enviado de mensaje
                databaseReference.push().setValue(new MensajeEnviar(txtMensaje.getText().toString(),nombre.getText().toString(),"","1", ServerValue.TIMESTAMP));
                txtMensaje.setText("");
            }
        });

        btnEnviarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/jpeg");
                i.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(Intent.createChooser(i,"selecciona una imagen"),PHOTO_SEND);
            }
        });
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                setScrollBar();
            }
        });

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded( DataSnapshot snapshot, String s) {
                MensajeRecibir m  = snapshot.getValue(MensajeRecibir.class);
                adapter.addMensaje(m);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        verifyStoragePermissions(this);
    }

        //solo chat
    private void setScrollBar(){
        rvMensajes.scrollToPosition(adapter.getItemCount()-1); //hace que la lista valla siempre al ultimo mensaje
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PHOTO_SEND && resultCode == RESULT_OK){
        Uri u = data.getData();
        storageReference =storage.getReference("Imagenes_chat1.1"); //lugar y nombre de la carpeta donde se van a guardar las imagenes
            final StorageReference fotoReferencia = storageReference.child(u.getLastPathSegment());
            fotoReferencia.putFile(u).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                    throw task.getException();
                    }
                    return fotoReferencia.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri uri =task.getResult();
                        MensajeEnviar m = new MensajeEnviar(NOMBRE_USUARIO+" te ha enviado una foto",uri.toString(), nombre.getText().toString(),"","2",ServerValue.TIMESTAMP);
                        databaseReference.push().setValue(m);
                    }
                }
            });

        }
    }
    public static boolean verifyStoragePermissions(Activity activity) {
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };
        int REQUEST_EXTERNAL_STORAGE = 1;
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return false;
        }else{
            return true;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
     getUserInfo();
    }

    private void getUserInfo (){

        DatabaseReference mConductorDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(driver_ID);
        mConductorDatabase.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    System.out.println("si entra");
                    java.util.Map<String, Object> map = (java.util.Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name") != null) {
                        NOMBRE_USUARIO = map.get("name").toString();
                        nombre.setText(NOMBRE_USUARIO);
                    }
                    if(map.get("phone")!=null){
                        telefonoLlamar = map.get("phone").toString();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
}