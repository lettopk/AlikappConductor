package com.Alikapp.alikappconductor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerSettingsActivity extends AppCompatActivity {

    private EditText mNameField, mPhoneField, mCarField, mCedulaCiudadania;

    private Button mBack, mConfirm, mVerificarCedula, mVerificarPropiedad;

    private ImageView mProfileImage, mCedulaImage, mTarjetaPropiedad;

    private FirebaseAuth mAuth;
    private DatabaseReference mCustomerDatabase;

    private String URLImagen;

    private String userID;
    private String mName;
    private String mPhone;
    private String mCedula;
    private String mProfileImageUrl;

    private Uri resultUri;
    private Uri resultUriCedula;
    private Uri resultUriPropiedad;

    private String URLImagenPerfil;
    private String URLImagenCedula;
    private String URLImagenPropiedad;

    private RadioGroup mRadioGroup;

    private Boolean isCedula = false;
    private Boolean isPropiedad = false;


    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_settings);

        mNameField = (EditText) findViewById(R.id.name);
        mPhoneField = (EditText) findViewById(R.id.phone);
        mCedulaCiudadania = findViewById(R.id.cedula);

        mProfileImage = (ImageView) findViewById(R.id.profileImage);
        mCedulaImage = findViewById(R.id.cedulaImage);
        mTarjetaPropiedad = findViewById(R.id.tarjetaPropiedadImage);

        mBack = (Button) findViewById(R.id.back);
        mConfirm = (Button) findViewById(R.id.confirm);
        mVerificarCedula = findViewById(R.id.btnVerificarCedula);
        mVerificarPropiedad = findViewById(R.id.btnVerificarPropiedad);


        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userID);

        getUserInfo();

        mProfileImage.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/jpeg");
                i.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(Intent.createChooser(i, "selecciona una imagen"), 1);
            }
        });

        mCedulaImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 2);
            }
        });

        mTarjetaPropiedad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 3);
            }
        });

        mVerificarCedula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resultUriCedula != null) {
                    try {
                        runTextRecognation("Cedula", resultUriCedula);
                        mVerificarCedula.setEnabled(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(CustomerSettingsActivity.this, "No se ha cargado imagen de Cédula", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mVerificarPropiedad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resultUriPropiedad != null) {
                    try {
                        runTextRecognation("Pasado", resultUriPropiedad);
                        mVerificarPropiedad.setEnabled(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(CustomerSettingsActivity.this, "No se ha cargado imagen de Cédula", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mConfirm.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                guardarCedula();
                guardarPropiedad();
                saveUserInformation();
            }
        });

        mBack.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                finish();
                return;
            }
        });
    }


    private void runTextRecognation(String consulta, Uri resulturi) throws IOException {
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
                System.out.println("falla");
                e.printStackTrace();
            }
        });
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
            mVerificarCedula.setEnabled(true);
            mVerificarPropiedad.setEnabled(true);
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
                        if (A.equals("REPUBLICA DE COLOMBIA")) {
                            boolCedula1 = true;
                        } else if (A.equals("IDENTIFICACION PERSONAL")) {
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
                    }
                    /*for(int k = 0; k < elements.size(); k++) {
                    }*/
                }
            }
            System.out.println(parts);
            if (consulta.equals("Cedula")){
                verificarCedula(boolCedula1, boolCedula2, boolCedula3, parts);
            } else if (consulta.equals("Pasado")) {
                verificarPropiedad(boolPasado, parts);
            }
        }
    }

    private void verificarPropiedad(Boolean bool, ArrayList parts) {
        isPropiedad = false;
        if(bool) {
            Boolean isCorrect = false;
            String requisito1 = "N° ";
            String requisito2 = "No ";
            for(Object A: parts) {
                String B = A.toString();
                if(B.indexOf(requisito1) > -1){
                    String[] partes = B.split(requisito1);
                    String C = partes[1];
                    System.out.println(C);
                    if(C.equals(mCedulaCiudadania.getText().toString())) {
                        isCorrect = true;
                        break;
                    }
                }
                if(B.indexOf(requisito2) > -1){
                    String[] partes = B.split(requisito2);
                    String C = partes[1];
                    System.out.println(C);
                    if(C.equals(mCedulaCiudadania.getText().toString())) {
                        isCorrect = true;
                        break;
                    }
                }
            }
            if (!isCorrect) {
                mVerificarPropiedad.setEnabled(true);
                Toast.makeText(CustomerSettingsActivity.this, "El certificado no corresponde con el número de cédula", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CustomerSettingsActivity.this, "correcto", Toast.LENGTH_SHORT).show();
                mTarjetaPropiedad.setEnabled(false);
                isPropiedad = true;
            }

        } else {
            mVerificarPropiedad.setEnabled(true);
            Toast.makeText(CustomerSettingsActivity.this, "La imagen cargada no Corresponde a un certificado de pasado judicial, intente con una nueva imagen", Toast.LENGTH_SHORT).show();

        }
        checkDocumentos();
    }

    private void verificarCedula(Boolean boolCedula1, Boolean boolCedula2, Boolean boolCedula3, ArrayList parts) {
        isCedula = false;
        if (boolCedula1 && boolCedula2 && boolCedula3) {
            String D = "";
            String E = "";
            String G = "";
            for(int i = 0; i < parts.size(); i++) {
                if (parts.get(i).equals("APELLIDOS")) {
                    G = (String) parts.get(i + 1);
                }
                if (parts.get(i).equals("CEDULA DE CIUDADANIA")) {
                    D = (String) parts.get(i + 1);
                    E = (String) parts.get(i + 2);
                }
            }
            String[] partes = D.split(" ");
            String B = partes[1];
            String numCedula = "";
            String[] partesCedula = B.split(".");
            int contador = 0;
            while (B.indexOf(".") > -1) {
                numCedula = numCedula + B.substring(0, B.indexOf("."));
                B = B.substring(B.indexOf(".") + 1);
                contador++;
            }
            numCedula = numCedula + B;
            mNameField.setText(G + " " + E);
            mCedulaCiudadania.setText(numCedula);
            mNameField.setEnabled(true);
            mPhoneField.setEnabled(true);
            mCedulaImage.setEnabled(false);
            isCedula = true;
            Toast.makeText(CustomerSettingsActivity.this, "correcto", Toast.LENGTH_SHORT).show();
        } else {
            mVerificarCedula.setEnabled(true);
            Toast.makeText(CustomerSettingsActivity.this, "La imagen cargada no Corresponde a una cédula de ciudadanía colombiana, intente con una nueva imagen", Toast.LENGTH_SHORT).show();
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
                    }
                    if(map.get("phone")!=null){
                        mPhone = map.get("phone").toString();
                        mPhoneField.setText(mPhone);
                    }
                    if(map.get("cedula")!=null){
                        mCedula = map.get("cedula").toString();
                        mCedulaCiudadania.setText(mCedula);
                    }
                    if(map.get("profileImageUrl")!=null){
                        mProfileImageUrl = map.get("profileImageUrl").toString();
                        com.bumptech.glide.Glide.with(getApplication()).load(mProfileImageUrl).into(mProfileImage);
                    }
                    if(map.get("cedulaImageUrl")!=null){
                        URLImagenCedula = map.get("cedulaImageUrl").toString();
                        Glide.with(getApplication()).load(URLImagenCedula).into(mCedulaImage);
                        isCedula = true;
                    }
                    if(map.get("pasadoJudicialImageUrl")!=null){
                        URLImagenPropiedad = map.get("pasadoJudicialImageUrl").toString();
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
        if(isCedula && isPropiedad) {
            mConfirm.setVisibility(View.VISIBLE);
        } else {
            mConfirm.setVisibility(View.GONE);
        }
    }


    private void saveUserInformation() {
        mName = mNameField.getText().toString();
        mPhone = mPhoneField.getText().toString();
        mCedula = mCedulaCiudadania.getText().toString();

        java.util.Map userInfo = new HashMap();
        userInfo.put("name", mName);
        userInfo.put("phone", mPhone);
        userInfo.put("cedula", mCedula);
        mCustomerDatabase.updateChildren(userInfo);

        if(resultUri != null) {
            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("Images").child(userID).child("Profile_Image");
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(),resultUri);
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
            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("Images").child(userID).child("PasadoJudicial_Image");
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
                        newImage.put("pasadoJudicialImageUrl", URLImagenPropiedad);
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
                    newImage.put("pasadoJudicialImageUrl", URLImagenPropiedad);
                    mCustomerDatabase.updateChildren(newImage);
                }
            });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            Uri u = data.getData();
            resultUri = u;
            mProfileImage.setImageURI(resultUri);

        }
        if(requestCode == 2 && resultCode == Activity.RESULT_OK){
            Uri u = data.getData();
            resultUriCedula = u;
            mCedulaImage.setImageURI(resultUriCedula);
        }
        if(requestCode == 3 && resultCode == Activity.RESULT_OK){
            Uri u = data.getData();
            resultUriPropiedad = u;
            mTarjetaPropiedad.setImageURI(resultUriPropiedad);
        }
    }
}
