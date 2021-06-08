package com.Alikapp.alikappconductor;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.Alikapp.alikappconductor.CustomerMapActivity.conductorUID;

public class CustomerSettingsActivity extends AppCompatActivity {

    private EditText mNameField, mPhoneField, mCedulaCiudadania,mNumPlaca, mvehiculo, mEmail;
    private TextView mNombre1;

    private Button mBack, mConfirm;

    private ImageView mCedulaImage,mPasadoJudicialImage, mTarjetaPropiedad;
    private CircleImageView mProfileImage;

    private FirebaseAuth mAuth;
    private DatabaseReference mCustomerDatabase;

    private String URLImagen;

    private String userID;
    private String mName;
    private String mPhone;
    private String mCedula;
    private String mNumPropiedad;
    private String mNumeroPlaca;
    private String mVehiculoTotal;
    private String mProfileImageUrl;
    private String mEmailString;

    private Uri resultUriPerfil;
    private Uri resultUriCedula;
    private Uri resultUriPropiedad;
    private Uri resultUriPasado;

    private String URLImagenCedula;
    private String URLImagenPropiedad;
    private String URLImagenPasado;


    private Boolean isCedula = false;
    private Boolean isPropiedad = false;
    private Boolean isPasado = false;
    private Boolean isPerfil = false;
    private Boolean isPrimeraVez;


    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_settings);

        if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
            }
        }

        mNameField = (EditText) findViewById(R.id.name);
        mPhoneField = (EditText) findViewById(R.id.phone);
        mCedulaCiudadania = findViewById(R.id.cedula);
        mNumPlaca = (EditText) findViewById(R.id.numeroPlaca);
        mvehiculo =(EditText) findViewById(R.id.vehiculo);
        mEmail = (EditText) findViewById(R.id.email);
        mNombre1 = findViewById(R.id.name1);

        mProfileImage = (CircleImageView) findViewById(R.id.profileImage);
        mCedulaImage = findViewById(R.id.cedulaImage);
        mTarjetaPropiedad = findViewById(R.id.tarjetaPropiedadImage);
        mPasadoJudicialImage = findViewById(R.id.pasadoJudicialImage);

        mBack = (Button) findViewById(R.id.back);
        mConfirm = (Button) findViewById(R.id.confirm);


        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userID);

        getUserInfo();

        mProfileImage.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File imagenArchivo = null;
                try {
                    imagenArchivo = crearImagen("perfil");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (imagenArchivo != null){
                    resultUriPerfil = FileProvider.getUriForFile(CustomerSettingsActivity.this, "com.Alikapp.alikappconductor.fileprovider", imagenArchivo);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, resultUriPerfil);
                    startActivityForResult(intent, 1);
                }
            }
        });

        mCedulaImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File imagenArchivo = null;
                try {
                    imagenArchivo = crearImagen("cedula");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (imagenArchivo != null){
                    resultUriCedula = FileProvider.getUriForFile(CustomerSettingsActivity.this, "com.Alikapp.alikappconductor.fileprovider", imagenArchivo);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, resultUriCedula);
                    startActivityForResult(intent, 101);
                }
            }
        });
        mPasadoJudicialImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 3);
            }
        });
        mTarjetaPropiedad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File imagenArchivo = null;
                try {
                    imagenArchivo = crearImagen("propiedad");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (imagenArchivo != null) {
                    resultUriPropiedad = FileProvider.getUriForFile(CustomerSettingsActivity.this, "com.Alikapp.alikappconductor.fileprovider", imagenArchivo);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, resultUriPropiedad);
                    startActivityForResult(intent, 4);
                }
            }
        });

        mConfirm.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if(mNameField.getText() != null && mPhoneField.getText() != null &&  mvehiculo.getText() != null
                        && mCedulaCiudadania.getText() != null){
                    guardarCedula();
                    guardarPropiedad();
                    guardarPasado();
                    saveUserInformation();
                    if(isPrimeraVez){
                        Intent intent = new Intent(CustomerSettingsActivity.this, CustomerMapActivity.class);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(CustomerSettingsActivity.this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mBack.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                finish();
                return;
            }
        });
        Intent mPv = getIntent();
        isPrimeraVez = mPv.getBooleanExtra("PrimeraVez",true);
        if(isPrimeraVez){
            mBack.setVisibility(View.GONE);
            dineroObseqio();
        }
    }

    private String dinero;
    private void dineroObseqio() {

        DatabaseReference valorCreditoReference =  FirebaseDatabase.getInstance().getReference().child("dineroObsequio");
        valorCreditoReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if (map.get("Usuario") != null){
                        dinero= map.get("Usuario").toString();
                        subirDinero();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void subirDinero(){
        DatabaseReference enableReference = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(conductorUID);
        Map usuarioInfo = new HashMap();
        usuarioInfo.put("dineroDisponible", dinero);
        enableReference.updateChildren(usuarioInfo);
    }


    private void runTextRecognation(String consulta, Uri resulturi) throws IOException {
        if (!consulta.equals("Cedula")) {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resulturi);
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
            FirebaseVision firebaseVision = FirebaseVision.getInstance();
            FirebaseVisionTextRecognizer director = firebaseVision.getOnDeviceTextRecognizer();
            Task<FirebaseVisionText> task = director.processImage(image);
            task.addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                @Override
                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                    processTextRecognitionResult(firebaseVisionText, consulta);
                }
            });
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
        }else {
                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmapCedula);
                FirebaseVision firebaseVision = FirebaseVision.getInstance();
                FirebaseVisionTextRecognizer director = firebaseVision.getOnDeviceTextRecognizer();
                Task<FirebaseVisionText> task = director.processImage(image);
                task.addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        processTextRecognitionResult(firebaseVisionText, consulta);
                    }
                });
                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
            }
    }

    private void processTextRecognitionResult(FirebaseVisionText texts, String consulta) {
        String A = "";
        ArrayList parts = new ArrayList();
        Boolean boolCedula1 = false;
        Boolean boolCedula2 = false;
        Boolean boolCedula3 = false;
        Boolean boolPasado = false;
        List<FirebaseVisionText.TextBlock> blocks = texts.getTextBlocks();
        if(blocks.size() == 0){
            Toast.makeText(CustomerSettingsActivity.this, "No se pudo identificar la imagen, intente nuevamente", Toast.LENGTH_SHORT).show();
            Toast.makeText(CustomerSettingsActivity.this, "La imagen debe encontrarse al derecho y visualizarse con claridad", Toast.LENGTH_SHORT).show();
        } else {
            for(int i = 0; i < blocks.size(); i++) {
                List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
                for(int j = 0; j < lines.size(); j++) {
                    List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
                    if (consulta.equals("Cedula")){
                        A = lines.get(j).getText();
                        parts.add(lines.get(j).getText());
                        if (A.equals("REPUBLICA DE COLOMBIA") || A.equals("REPUBCICA DE COLOMBIA") || A.equals("REPUBLICA DE COLOMBLA") ||
                                A.equals("REPUBLICA DE cOLOMBIA") || A.equals("REPUBLICA DE COLOMBTA") || A.equals("REPUBLCA DE COLOMBIA") ||
                                A.equals("REPUBLICA DE cOLOMBLA")) {
                            boolCedula1 = true;
                        } else if (A.equals("IDENTIFICACION PERSONAL") || A.equals("IDENTIFCACION PERSONAL") || A.equals("1DENTIFICACION PERSONAL") ||
                                A.equals("DENTIFICACION PERSONAL") || A.equals("IDENTIRICACION PERSONAL") || A.equals("OENTIFICACION PERSONAL")) {
                            boolCedula2 = true;
                        } else if (A.equals("CEDULA DE CIUDADANIA")) {
                            boolCedula3 = true;
                        }
                    } else if (consulta.equals("Pasado")) {
                        A = lines.get(j).getText();
                        parts.add(lines.get(j).getText());
                        if (A.equals("Consulta en linea de Antecedentes Penales y Requerimientos Judiciales")) {
                            boolPasado = true;
                        } else if (A.equals("Consulta en linea de Antecedentes")) {
                            boolPasado = true;
                        } else if (A.equals("La Policía Nacional de Colombia informa:")) {
                            boolPasado = true;
                        } else if (A.equals("La Policia Nacional de Colombia informa:")) {
                            boolPasado = true;
                        }
                    }else if (consulta.equals("propiedad")) {
                        A = lines.get(j).getText();
                        parts.add(lines.get(j).getText());
                        if (A.equals("MINISTERIO DE TRANSPORTE")) {
                            boolPasado = true;
                        } else if (A.equals("LICENCIA DE TRANSITO")) {
                            boolPasado = true;
                        } else if (A.equals("PLACA")) {
                            boolPasado = true;
                        } else if (A.equals("CLASE DE VEHICULO")) {
                            boolPasado = true;
                        }
                    }
                    /*for(int k = 0; k < elements.size(); k++) {
                    }*/
                }
            }
            System.out.println(parts);
            System.out.println(boolPasado);
            if (consulta.equals("Cedula")){
                verificarCedula(boolCedula1, boolCedula2, boolCedula3, parts);
            }else if (consulta.equals("Pasado")) {
                verificarPasado(boolPasado, parts);
            } else if (consulta.equals("propiedad")) {
                verificarPropiedad(boolPasado, parts);
            }
        }
    }
    private void verificarPasado(Boolean bool, ArrayList parts) {
        isPasado = false;
        if(bool) {
            Boolean isCorrect = false;

            for(Object A: parts) {
                String B = A.toString();
                if(B.contains(mCedulaCiudadania.getText().toString())){
                    isCorrect = true;
                    break;
                }
            }
            if (!isCorrect) {
                Toast.makeText(CustomerSettingsActivity.this, "El certificado no corresponde con el número de cédula", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CustomerSettingsActivity.this, "correcto", Toast.LENGTH_SHORT).show();
                mPasadoJudicialImage.setEnabled(false);
                mCedulaImage.setEnabled(false);
                isPasado = true;
            }

        }else {
            Toast.makeText(CustomerSettingsActivity.this, "La imagen cargada no Corresponde a un certificado de pasado judicial, intente con una nueva imagen", Toast.LENGTH_SHORT).show();

        }
        checkDocumentos();
    }

    private void verificarCedula(Boolean boolCedula1, Boolean boolCedula2, Boolean boolCedula3, ArrayList parts) {
        isCedula = false;
        Boolean caso1 = boolCedula1 && boolCedula2;
        Boolean caso2 = boolCedula1 && boolCedula3;
        if (caso1 || caso2) {
            String D = "";
            String E = "";
            String G = "";
            for(int i = 0; i < parts.size(); i++) {
                if (parts.get(i).equals("IDENTIFICACION PERSONAL") || parts.get(i).equals("IDENTIFCACION PERSONAL") || parts.get(i).equals("1DENTIFICACION PERSONAL") ||
                        parts.get(i).equals("DENTIFICACION PERSONAL") || parts.get(i).equals("IDENTIRICACION PERSONAL") || parts.get(i).equals("OENTIFICACION PERSONAL")) {

                    G = (String) parts.get(parts.size() - 3);
                    E = (String) parts.get(i + 3);
                    D = (String) parts.get(i + 2);
                    break;
                } else if (parts.get(i).equals("REPUBLICA DE COLOMBIA") || parts.get(i).equals("REPUBCICA DE COLOMBIA") || parts.get(i).equals("REPUBLICA DE COLOMBLA") ||
                        parts.get(i).equals("REPUBLICA DE cOLOMBIA") || parts.get(i).equals("REPUBLICA DE COLOMBTA") || parts.get(i).equals("REPUBLCA DE COLOMBIA") ||
                        parts.get(i).equals("REPUBLICA DE cOLOMBLA")) {
                    G = (String) parts.get(parts.size() - 3);
                    E = (String) parts.get(i + 4);
                    D = (String) parts.get(i + 3);
                    break;
                }
            }
            try {
                String B = "";
                if (D.contains(" ")) {
                    B = D.substring(D.indexOf(" "));
                } else {
                    B = D;
                }
                String numCedula = "";
                while (B.contains(".")) {
                    numCedula = numCedula + B.substring(0, B.indexOf("."));
                    B = B.substring(B.indexOf(".") + 1);
                }
                numCedula = numCedula + B;
                while (numCedula.contains(" ")) {
                    StringBuilder stringBuilder = new StringBuilder(numCedula);
                    stringBuilder.deleteCharAt(numCedula.indexOf(" "));
                    numCedula = stringBuilder.toString();
                }
                mNameField.setText(G + " " + E);
                mCedulaCiudadania.setText(numCedula);
            } catch(Exception e) {
                e.printStackTrace();
            }
            mNameField.setEnabled(true);
            mPhoneField.setEnabled(true);
            isCedula = true;
            isPasado = false;
            mPasadoJudicialImage.setImageResource(R.mipmap.ic_default_user);
            Toast.makeText(CustomerSettingsActivity.this, "correcto", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(CustomerSettingsActivity.this, "La imagen cargada no Corresponde a una cédula de ciudadanía colombiana, intente con una nueva imagen", Toast.LENGTH_SHORT).show();
        }
        checkDocumentos();
    }

    private void verificarPropiedad(Boolean bool, ArrayList parts) {
        isPropiedad = false;
        if (bool) {
            Boolean isCorrect = false;
            String placa = "";
            String marca = "";
            String linea = "";
            String modelo = "";
            for (int i = 0; i < parts.size(); i++) {
                if (parts.get(i).toString().contains("PLACA")) {

                    placa = (String) parts.get(i + 4);
                }
                if (parts.get(i).toString().contains("MARCA")) {

                    marca = (String) parts.get(i + 4);
                }

                if (parts.get(i).toString().contains("LINEA") || parts.get(i).toString().contains("LÍNEA")) {


                    linea = (String) parts.get(i + 4);
                }
                if (parts.get(i).toString().contains("MODELO")) {

                    modelo = (String) parts.get(i + 4);
                }

            }
            mNumeroPlaca= placa;
            mVehiculoTotal=marca+" "+linea+" "+modelo;

            mNumPlaca.setText(mNumeroPlaca);
            mvehiculo.setText(mVehiculoTotal);



            Toast.makeText(CustomerSettingsActivity.this, "correcto", Toast.LENGTH_SHORT).show();
            isPropiedad = true;

        }else {
            Toast.makeText(CustomerSettingsActivity.this, "La imagen cargada no Corresponde a una tarjeta de propiedad, intente con una nueva imagen", Toast.LENGTH_SHORT).show();

        }
        checkDocumentos();
    }

    public void getUserInfo(){
        mCustomerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    java.util.Map<String, Object> map = (java.util.Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("name")!=null){
                        mName = map.get("name").toString();
                        mNameField.setText(mName);
                        String nombre = mName;
                        String[] nombSeparado = nombre.split(" ");
                        if (nombSeparado.length>=3){
                            nombre = nombSeparado[0] + " " + nombSeparado[2];
                        }
                        mNombre1.setText(nombre);
                    }
                    if(map.get("phone")!=null){
                        mPhone = map.get("phone").toString();
                        mPhoneField.setText(mPhone);
                    }
                    if(map.get("email")!=null){
                        mEmailString = map.get("email").toString();
                        mEmail.setText(mEmailString);
                    }
                    if(map.get("cedula")!=null){
                        mCedula = map.get("cedula").toString();
                        mCedulaCiudadania.setText(mCedula);
                    }
                    if(map.get("placa")!=null){
                        mNumeroPlaca = map.get("placa").toString();
                        mNumPlaca.setText(mNumeroPlaca);
                    }
                    if(map.get("vehiculo")!=null){
                        mVehiculoTotal = map.get("vehiculo").toString();
                        mvehiculo.setText(mVehiculoTotal);
                    }
                    if(map.get("profileImageUrl")!=null){
                        mProfileImageUrl = map.get("profileImageUrl").toString();
                        com.bumptech.glide.Glide.with(getApplication()).load(mProfileImageUrl).into(mProfileImage);
                        isPerfil = true;
                    }
                    if(map.get("cedulaImageUrl")!=null){
                        URLImagenCedula = map.get("cedulaImageUrl").toString();
                        Glide.with(getApplication()).load(URLImagenCedula).into(mCedulaImage);
                        isCedula = true;
                    }
                    if(map.get("pasadoJudicialImageUrl")!=null){
                        URLImagenPasado = map.get("pasadoJudicialImageUrl").toString();
                        Glide.with(getApplication()).load(URLImagenPasado).into(mPasadoJudicialImage);
                        isPasado = true;
                    }
                    if(map.get("tarjetapropiedadImageUrl")!=null){
                        URLImagenPropiedad = map.get("tarjetapropiedadImageUrl").toString();
                        Glide.with(getApplication()).load(URLImagenPropiedad).into(mTarjetaPropiedad);
                        isPropiedad = true;
                    }
                    checkDocumentos();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private void checkDocumentos() {
        if(isCedula && isPropiedad && isPasado && isPerfil) {
            mConfirm.setVisibility(View.VISIBLE);
        } else {
            mConfirm.setVisibility(View.GONE);
        }
    }


    private void saveUserInformation() {
        mName = mNameField.getText().toString();
        mPhone = mPhoneField.getText().toString();
        mCedula = mCedulaCiudadania.getText().toString();
        mNumeroPlaca= mNumPlaca.getText().toString();

        java.util.Map userInfo = new HashMap();
        userInfo.put("name", mName);
        userInfo.put("phone", mPhone);
        userInfo.put("email", mEmailString);
        userInfo.put("cedula", mCedula);
        userInfo.put("placa", mNumeroPlaca);
        userInfo.put("vehiculo", mVehiculoTotal);
        mCustomerDatabase.updateChildren(userInfo);

        if(resultUriPerfil != null) {
            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("Images").child(userID).child("Profile_Image");
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(),resultUriPerfil);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri uri = task.getResult();
                        URLImagen = uri.toString();
                        Map newImage = new HashMap();
                        newImage.put("profileImageUrl", URLImagen);
                        mCustomerDatabase.updateChildren(newImage);
                    }
                }
            });

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                    return;
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    UploadTask.TaskSnapshot downloadUrl = taskSnapshot.getTask().getResult();
                }
            });
        }else {
            finish();
        }

    }


    protected void guardarCedula(){
        if(resultUriCedula != null) {
            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("Images").child(userID).child("Cedula_Image");
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUriCedula);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri uri = task.getResult();
                        URLImagenCedula = uri.toString();
                        Map newImage = new HashMap();
                        newImage.put("cedulaImageUrl", URLImagenCedula);
                        mCustomerDatabase.updateChildren(newImage);
                    }
                }
            });

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    UploadTask.TaskSnapshot downloadUrl = taskSnapshot.getTask().getResult();
                    Map newImage = new HashMap();
                    newImage.put("cedulaImageUrl", URLImagenCedula);
                    mCustomerDatabase.updateChildren(newImage);
                }
            });
        }
    }

    protected void guardarPropiedad() {
        if(resultUriPropiedad != null) {
            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("Images").child(userID).child("Tarjeta_propiedad_Image");
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUriPropiedad);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri uri = task.getResult();
                        URLImagenPropiedad= uri.toString();
                        Map newImage = new HashMap();
                        newImage.put("tarjetapropiedadImageUrl", URLImagenPropiedad);
                        mCustomerDatabase.updateChildren(newImage);
                    }
                }
            });

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    UploadTask.TaskSnapshot downloadUrl = taskSnapshot.getTask().getResult();
                    Map newImage = new HashMap();
                    newImage.put("tarjetapropiedadImageUrl", URLImagenPropiedad);
                    mCustomerDatabase.updateChildren(newImage);
                }
            });
        }
    }

    protected void guardarPasado() {
        if(resultUriPasado != null) {
            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("Images").child(userID).child("PasadoJudicial_Image");
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUriPasado);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri uri = task.getResult();
                        URLImagenPasado = uri.toString();
                        Map newImage = new HashMap();
                        newImage.put("pasadoJudicialImageUrl", URLImagenPasado);
                        mCustomerDatabase.updateChildren(newImage);
                    }
                }
            });

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    UploadTask.TaskSnapshot downloadUrl = taskSnapshot.getTask().getResult();
                    Map newImage = new HashMap();
                    newImage.put("pasadoJudicialImageUrl", URLImagenPasado);
                    mCustomerDatabase.updateChildren(newImage);
                }
            });
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            Bitmap bitmapPerfil = BitmapFactory.decodeFile(rutaPerfilImagen);
            mProfileImage.setImageBitmap(bitmapPerfil);
            isPerfil = true;
            checkDocumentos();
        }
        if(requestCode == 3 && resultCode == Activity.RESULT_OK){
            resultUriPasado = data.getData();
            mPasadoJudicialImage.setImageURI(resultUriPasado);
            try {
                runTextRecognation("Pasado", resultUriPasado);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(requestCode == 101 && resultCode == Activity.RESULT_OK){
            bitmapCedula = BitmapFactory.decodeFile(rutaImagen);
            mCedulaImage.setImageBitmap(bitmapCedula);
            try {
                runTextRecognation("Cedula", resultUriCedula);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(requestCode == 4 && resultCode == Activity.RESULT_OK){
            Bitmap bitmapPropiedad = BitmapFactory.decodeFile(rutaPropiedadImagen);
            mTarjetaPropiedad.setImageBitmap(bitmapPropiedad);
            try {
                runTextRecognation("propiedad", resultUriPropiedad);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap bitmapCedula;
    private String rutaImagen;
    private String rutaPropiedadImagen;
    private String rutaPerfilImagen;

    private File crearImagen(String consulta) throws IOException {
        String nombreImagen = "foto_";
        File directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imagen = File.createTempFile(nombreImagen, ".jpg", directorio);

        if (consulta.equals("cedula")){
            rutaImagen = imagen.getAbsolutePath();
        } else if (consulta.equals("propiedad")){
            rutaPropiedadImagen = imagen.getAbsolutePath();
        }
        else if (consulta.equals("perfil")){
            rutaPerfilImagen = imagen.getAbsolutePath();
        }
        return imagen;
    }
}
