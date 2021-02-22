package com.Alikapp.alikappconductor;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.Alikapp.alikappconductor.notifyFirebase.tokeng;

public class CustomerMapActivity extends FragmentActivity implements OnMapReadyCallback, RoutingListener {

    private GoogleMap mMap;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    private FusedLocationProviderClient mFusedLocationClient;

    private Button mLogout, mRequest, mSettings, mHistory, mDesplegar, mChat;

    private EditText mDescripcion;

    private TextView mLongDescrip;

    private LatLng pickupLocation;

    private Boolean requestBol = false;

    private Marker pickupMarker;

    private SupportMapFragment mapFragment;

    private String destination, requestService;

    private LatLng destinationLatLng, tallerLatLng;

    private LinearLayout mDriverInfo;

    public ImageView mDriverProfileImage;

    private android.widget.TextView mDriverName, mDriverPhone, mDriverCar;

    private RadioGroup mRadioGroup;

    private RatingBar mRatingBar;
    private AppBarConfiguration mAppBarConfiguration;
    private BottomSheetBehavior mBottomSheetBehavior;
    private String token1;
    private String titulo1;
    private String detalle1;
    private String info1;

    public static String conductorUID;
    public static String clicknotify="";

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_costumer_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        destinationLatLng = new LatLng(0.0,0.0);


        mDriverInfo = (LinearLayout) findViewById(R.id.driverInfo);

        mDriverProfileImage = (ImageView) findViewById(R.id.driverProfileImage);

        mDriverName = (android.widget.TextView) findViewById(R.id.driverName);
        mDriverPhone = (android.widget.TextView) findViewById(R.id.driverPhone);
        mDriverCar = (android.widget.TextView) findViewById(R.id.driverCar);

        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);

        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        mRadioGroup.check(R.id.Mecanico);

        mLogout = (Button) findViewById(R.id.logout);
        mChat =(Button) findViewById(R.id.mChat);
        mRequest = (Button) findViewById(R.id.request);
        mRequest.setText("Pedir Ayuda");
        mSettings = (Button) findViewById(R.id.settings);
        mHistory = (Button) findViewById(R.id.history);

        conductorUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {

                            Toast.makeText(CustomerMapActivity.this,"no se pudo llamar el token",Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        guardartoken(token);

                    }
                });




        View bottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View BottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        //mTextViewState.setText("collapsado");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        //mTextViewState.setText("Dragging...");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        mDesplegar.setVisibility(View.GONE);
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        mDesplegar.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        //mTextViewState.setText("settling...");
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        mDesplegar = findViewById(R.id.desplegarCuadro);
        mDesplegar.setVisibility(View.VISIBLE);
        mDesplegar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                mDesplegar.setVisibility(View.GONE);
            }
        });

        mDescripcion = findViewById(R.id.descripcion);
        mLongDescrip = findViewById(R.id.longDescrip);
        final int maximum_character = 250;
        mDescripcion.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maximum_character)});

        mDescripcion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mLongDescrip.setText(""+String.valueOf(maximum_character - mDescripcion.getText().length()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mLogout.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(CustomerMapActivity.this, CustomerLoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        mChat.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent intent = new Intent(CustomerMapActivity.this, Chat.class);
                //intent.putExtra("nombrechat", mName);
                CustomerMapActivity.this.startActivity(intent);
            }
        });

        mRequest.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {

                if (requestBol){
                    try {
                        endRide();

                    } catch (Exception e) {
                        romper = true;
                        finRide();
                        CustomerMapActivity.super.onRestart();
                        Toast.makeText(CustomerMapActivity.this, "Solicitud Cancelada", Toast.LENGTH_SHORT).show();
                    }
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }else{
                    if (!mLongDescrip.getText().equals("250")){
                        int selectId = mRadioGroup.getCheckedRadioButtonId();

                        final RadioButton radioButton = (RadioButton) findViewById(selectId);

                        if (radioButton.getText() == null){
                            return;
                        }

                        requestService = radioButton.getText().toString();

                        requestBol = true;

                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
                        GeoFire geoFire = new GeoFire(ref);
                        geoFire.setLocation(userId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

                        pickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                        pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Estoy Aquí").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_averiado)));

                        mRequest.setText("Buscando Mecanico");

                        getClosestDriver();
                        tiempoEspera();
                        romper = false;

                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference enableReference = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId);
                        Map usuarioInfo = new HashMap();
                        usuarioInfo.put("Descripcion", "" + mDescripcion.getText());
                        enableReference.updateChildren(usuarioInfo);
                    } else {
                        Toast.makeText(CustomerMapActivity.this, "Escribe una breve descripción del problema", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        mSettings.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent intent = new Intent(CustomerMapActivity.this, CustomerSettingsActivity.class);
                startActivity(intent);
                return;
            }
        });

        mHistory.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent intent = new Intent(CustomerMapActivity.this, HistoryActivity.class);
                intent.putExtra("customerOrDriver", "Customers");
                startActivity(intent);
                return;
            }
        });



        /*Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        com.google.android.material.floatingactionbutton.FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                onSupportNavigateUp();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_history, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        /*NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);*/
        NavigationUI.setupWithNavController(navigationView, navController);

        isOnService();
    }

    private void guardartoken(String token) {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(conductorUID);
        ref.child("token").setValue(token);
    }

    private void tiempoEspera() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finalizarEspera();
            }
        }, 240000);
    }
    private Boolean romper = true;
    private void finalizarEspera() {
        System.out.println("finalizarEspera");
        if(!romper){
            finRide();
            CustomerMapActivity.super.onRestart();
            romper = true;
            Toast.makeText(this,"No hay mecánicos cerca",Toast.LENGTH_LONG).show();
        }
    }

    private void finRide() {
        requestBol = false;
        try {
            geoQuery.removeAllListeners();
        } catch (Exception e){

        }

        if (driverFoundID != null){
            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
            driverRef.removeValue();
            driverFoundID = null;

        }
        driverFound = false;
        radius = 1;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);

        if(pickupMarker != null){
            pickupMarker.remove();
        }
        if (mDriverMarker != null){
            mDriverMarker.remove();
        }
        mRequest.setText("Pedir Ayuda");

        mDriverInfo.setVisibility(android.view.View.GONE);
        mDriverName.setText("");
        mDriverPhone.setText("");
        mDriverCar.setText("Destination: --");
        mDriverProfileImage.setImageResource(R.mipmap.ic_default_user);
        erasePolylines();
        servicioTermina();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }

    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                /*|| super.onSupportNavigateUp()*/;
    }

    private int radius = 1;
    private Boolean driverFound = false;
    private String driverFoundID;
    public static String driver_ID = "driver_ID";

    GeoQuery geoQuery;
    private void getClosestDriver(){
        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("driversAvailable");

        GeoFire geoFire = new GeoFire(driverLocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude, pickupLocation.longitude), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!driverFound && requestBol){
                    DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(key);
                    mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                                java.util.Map<String, Object> driverMap = (java.util.Map<String, Object>) dataSnapshot.getValue();
                                if (driverFound){
                                    return;
                                }

                                if(driverMap.get("service").equals(requestService)){
                                    driverFound = true;
                                    driverFoundID = dataSnapshot.getKey();

                                    DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
                                    String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    HashMap<String, Object> map = new HashMap<String, Object>();
                                    map.put("customerRideId", customerId);
                                    map.put("destination", destination);
                                    map.put("destinationLat", destinationLatLng.latitude);
                                    map.put("destinationLng", destinationLatLng.longitude);
                                    driverRef.updateChildren(map);
                                    //llamado a la base de datos por token.
                                    DatabaseReference tokenmecanico = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID);
                                    tokenmecanico.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists() ) {
                                                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                                if (map.get("token") != null) {
                                                    token1 =map.get("token").toString();
                                                    titulo1 ="Conductor en emergencia mecanica";
                                                    detalle1 = "Hechale una mano a este conductor";
                                                    info1 ="pedirservicio";

                                                    if(token1 != null){
                                                        notificacionServicio();
                                                    }
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });

                                    getConfirmacion();

                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!driverFound)
                {
                    radius++;
                    getClosestDriver();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void getConfirmacion() {
        try{
            DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID);
            mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists() && requestBol){
                        java.util.Map<String, Object> map = (java.util.Map<String, Object>) snapshot.getValue();
                        String A = map.get("Enable").toString();
                        System.out.println(A);
                        System.out.println(driverFoundID);
                        if(A.equals("Si")){
                            romper = true;
                            getDriverLocation();
                            getDriverInfo();
                            getHasRideEnded();
                            driver_ID = driverFoundID;
                            mRequest.setText("Buscando la Ubicacion de su Mecanico....");
                            enServicio();
                        } else if (A.equals("No")) {
                            driverFound = false;
                            requestBol = true;

                            int selectId = mRadioGroup.getCheckedRadioButtonId();
                            final RadioButton radioButton = (RadioButton) findViewById(selectId);
                            if (radioButton.getText() == null){
                                return;
                            }
                            requestService = radioButton.getText().toString();
                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
                            GeoFire geoFire = new GeoFire(ref);
                            geoFire.setLocation(userId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                            pickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                            pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Estoy Aquí").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_averiado)));
                            mRequest.setText("Buscando Mecanico....");

                            getClosestDriver();
                        } else {
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(A.equals("Espera")){
                                        getConfirmacion();
                                    }
                                }
                            }, 1000);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }catch (Exception e){
            CustomerMapActivity.super.onRestart();
            e.printStackTrace();
        }
    }
    private void notificacionServicio() {

        RequestQueue myrequest = Volley.newRequestQueue(getApplicationContext());
        JSONObject json = new JSONObject();
        try {
            String token = token1;
            json.put("to",token);
            JSONObject notificacion = new JSONObject();
            notificacion.put("titulo",titulo1);
            notificacion.put("detalle",detalle1);
            notificacion.put("info",info1);

            json.put("data",notificacion);

            String URL = "https://fcm.googleapis.com/fcm/send";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,URL,json,null,null){

                @Override
                public Map<String, String> getHeaders()  {
                    Map<String,String> header = new HashMap<>();

                    header.put("content-type","application/json");
                    header.put("authorization","key=AAAAgx1G4i8:APA91bGtUEgaCzuxbqqh33LzQL7Lp0WNatPWJCOQFImvTWZMoverV7huSCHpaYTqW0IPMBF876wqrKyUzokjNhYZcOYeG8dgHidJqZxYblF3OjlY_p19oAZglksDsrXSeJN7sOSaMhYV");
                    return header;
                }
            };
            myrequest.add(request);

        } catch (JSONException e){
            e.printStackTrace();
        }

    }


    /*-------------------------------------------- Map specific functions -----
    |  Function(s) getDriverLocation
    |
    |  Purpose:  Get's most updated driver location and it's always checking for movements.
    |
    |  Note:
    |	   Even tho we used geofire to push the location of the driver we can use a normal
    |      Listener to get it's location with no problem.
    |
    |      0 -> Latitude
    |      1 -> Longitudde
    |
    *-------------------------------------------------------------------*/
    private Marker mDriverMarker;
    private DatabaseReference driverLocationRef;
    private ValueEventListener driverLocationRefListener;
    private void getDriverLocation(){
        driverLocationRef = FirebaseDatabase.getInstance().getReference().child("driversWorking").child(driverFoundID).child("l");
        driverLocationRefListener = driverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && requestBol){
                    java.util.List<Object> map = (java.util.List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    if(map.get(0) != null){
                        locationLat = Double.parseDouble(map.get(0).toString());
                        DriverlocationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null){
                        locationLng = Double.parseDouble(map.get(1).toString());
                        DriverlocationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng driverLatLng = new LatLng(locationLat,locationLng);
                    if(mDriverMarker != null){
                        mDriverMarker.remove();
                    }
                    Location loc1 = new Location("");
                    loc1.setLatitude(pickupLocation.latitude);
                    loc1.setLongitude(pickupLocation.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(driverLatLng.latitude);
                    loc2.setLongitude(driverLatLng.longitude);

                    float distance = loc1.distanceTo(loc2);

                    if (distance<100){
                        mRequest.setText("Su Mecanico esta Aquí");
                    }else{

                        mRequest.setText("Mecanico Encontrado: " + String.valueOf((distance)/1000)+" Kms");

                    }


                    if(requestService.equals("Taller")){
                        getRouteToMarker(new LatLng(DriverlocationLat, DriverlocationLng),
                                new  LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                    } else {
                        getRouteToMarker( new  LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()),
                                new LatLng(DriverlocationLat, DriverlocationLng));
                    }

                    mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLatLng).title("Su Mecanico")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_mecanico)));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    /*-------------------------------------------- getDriverInfo -----
    |  Function(s) getDriverInfo
    |
    |  Purpose:  Get all the user information that we can get from the user's database.
    |
    |  Note: --
    |
    *-------------------------------------------------------------------*/
    private void getDriverInfo(){
        mDriverInfo.setVisibility(android.view.View.VISIBLE);
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    if(dataSnapshot.child("name")!=null){
                        mDriverName.setText(dataSnapshot.child("name").getValue().toString());
                    }
                    if(dataSnapshot.child("phone")!=null){
                        mDriverPhone.setText(dataSnapshot.child("phone").getValue().toString());
                    }
                    if(dataSnapshot.child("car")!=null){
                        mDriverCar.setText(dataSnapshot.child("car").getValue().toString());
                    }
                    if(dataSnapshot.child("profileImageUrl").getValue()!=null){
                        com.bumptech.glide.Glide.with(getApplication()).load(dataSnapshot.child("profileImageUrl").getValue().toString()).into(mDriverProfileImage);
                    }

                    int ratingSum = 0;
                    float ratingsTotal = 0;
                    float ratingsAvg = 0;
                    for (DataSnapshot child : dataSnapshot.child("rating").getChildren()){
                        ratingSum = ratingSum + Integer.valueOf(child.getValue().toString());
                        ratingsTotal++;
                    }
                    if(ratingsTotal!= 0){
                        ratingsAvg = ratingSum/ratingsTotal;
                        mRatingBar.setRating(ratingsAvg);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private DatabaseReference driveHasEndedRef;
    private ValueEventListener driveHasEndedRefListener;
    private void getHasRideEnded(){
        driveHasEndedRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest").child("customerRideId");
        driveHasEndedRefListener = driveHasEndedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                }else{
                    try {
                        endRide();
                    } catch (Exception e) {
                        finRide();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void endRide(){
        requestBol = false;
        try {
            geoQuery.removeAllListeners();
        } catch (Exception e){

        }
        driverLocationRef.removeEventListener(driverLocationRefListener);
        driveHasEndedRef.removeEventListener(driveHasEndedRefListener);

        if (driverFoundID != null){
            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
            driverRef.removeValue();
            driverFoundID = null;

        }
        driverFound = false;
        radius = 1;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);

        if(pickupMarker != null){
            pickupMarker.remove();
        }
        if (mDriverMarker != null){
            mDriverMarker.remove();
        }
        mRequest.setText("Pedir Ayuda");

        mDriverInfo.setVisibility(android.view.View.GONE);
        mDriverName.setText("");
        mDriverPhone.setText("");
        mDriverCar.setText("Destination: --");
        mDriverProfileImage.setImageResource(R.mipmap.ic_default_user);
        erasePolylines();
        servicioTermina();
    }

    /*-------------------------------------------- Map specific functions -----
    |  Function(s) onMapReady, buildGoogleApiClient, onLocationChanged, onConnected
    |
    |  Purpose:  Find and update user's location.
    |
    |  Note:
    |	   The update interval is set to 1000Ms and the accuracy is set to PRIORITY_HIGH_ACCURACY,
    |      If you're having trouble with battery draining too fast then change these to lower values
    |
    |
    *-------------------------------------------------------------------*/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.map_style));
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                if(tallerMarker1 != null){
                    tallerMarker1.remove();
                    tallerMarker1 = null;
                }
                if(tallerMarker2 != null){
                    tallerMarker2.remove();
                    tallerMarker2 = null;
                }
                final Handler handler =new Handler();
                handler.postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        getTallerAround();
                    }
                }, 1000);
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            }else{
                checkLocationPermission();
            }
        }

        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);


        float zoom = 11;

        LatLng sydney = new LatLng(4.6, -74.12);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,zoom));

        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                ajustarZoom();
                return false;
            }

            private void ajustarZoom() {
                CameraPosition cameraPosition = mMap.getCameraPosition();
                LatLng lat = cameraPosition.target;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lat,15));
            }
        });
        getTallerAround();
    }

    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for(Location location : locationResult.getLocations()){
                if(getApplicationContext()!=null){
                    mLastLocation = location;

                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    //mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                    if(!getDriversAroundStarted)
                        getDriversAround();
                }
            }
        }
    };

    /*-------------------------------------------- onRequestPermissionsResult -----
    |  Function onRequestPermissionsResult
    |
    |  Purpose:  Get permissions for our app if they didn't previously exist.
    |
    |  Note:
    |	requestCode: the nubmer assigned to the request that we've made. Each
    |                request has it's own unique request code.
    |
    *-------------------------------------------------------------------*/
    private void checkLocationPermission() {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    new android.app.AlertDialog.Builder(this)
                            .setTitle("give permission")
                            .setMessage("give permission message")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(CustomerMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                                }
                            })
                            .create()
                            .show();
                } else {
                    ActivityCompat.requestPermissions(CustomerMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            }
        }


    @Override
    public void onRequestPermissionsResult(int requestCode, @androidx.annotation.NonNull String[] permissions, @androidx.annotation.NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case 1:{
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }
                } else{
                    Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    boolean getDriversAroundStarted = false;
    java.util.List<Marker> markers = new ArrayList<Marker>();
    private void getDriversAround(){
        getDriversAroundStarted = true;
        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("driversAvailable");

        GeoFire geoFire = new GeoFire(driverLocation);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLongitude(), mLastLocation.getLatitude()), 999999999);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                for(Marker markerIt : markers){
                    if(markerIt.getTag().equals(key))
                        return;
                }

                LatLng driverLocation = new LatLng(location.latitude, location.longitude);

                Marker mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLocation).title(key).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_mecanico)));
                mDriverMarker.setTag(key);

                markers.add(mDriverMarker);


            }

            @Override
            public void onKeyExited(String key) {
                for(Marker markerIt : markers){
                    if(markerIt.getTag().equals(key)){
                        markerIt.remove();
                    }
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                for(Marker markerIt : markers){
                    if(markerIt.getTag().equals(key)){
                        markerIt.setPosition(new LatLng(location.latitude, location.longitude));
                    }
                }
            }

            @Override
            public void onGeoQueryReady() {
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }

    boolean getTallerStarted = false;
    private Marker tallerMarker1, tallerMarker2;
    GeoQuery geoRequest;
    private void getTallerAround(){
        DatabaseReference TalleresLocation = FirebaseDatabase.getInstance().getReference().child("driversAvailable");
        GeoFire geoFire = new GeoFire(TalleresLocation);
        float expo = (float) (17.2247 - 0.6867*mMap.getCameraPosition().zoom);
        float radio = (float) Math.exp(expo)/1000;
        LatLng latLatLng = new LatLng(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude);
        geoRequest = geoFire.queryAtLocation(new GeoLocation(latLatLng.latitude, latLatLng.longitude), radio);
        geoRequest.removeAllListeners();
        geoRequest.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                System.out.println(key);
                if (key != null) {
                    DatabaseReference Tallersitos = FirebaseDatabase.getInstance().getReference().child("driversAvailable").child(key).child("l");
                    Tallersitos.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                java.util.List<Object> map = (java.util.List<Object>) snapshot.getValue();
                                double locationLat = 0;
                                double locationLng = 0;
                                if (map.get(0) != null) {
                                    locationLat = Double.parseDouble(map.get(0).toString());

                                }
                                if (map.get(1) != null) {
                                    locationLng = Double.parseDouble(map.get(1).toString());

                                }

                                tallerLatLng = new LatLng(locationLat,locationLng);
                                if(tallerMarker1 == null){
                                    tallerMarker1 = mMap.addMarker(new MarkerOptions().position(tallerLatLng).title(" especialidad: ").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_taller)));
                                } else if(tallerMarker2 == null){
                                    tallerMarker2 = mMap.addMarker(new MarkerOptions().position(tallerLatLng).title(" especialidad: ").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_taller)));
                                }
                                mMap.getUiSettings().setMapToolbarEnabled(true);
                                mMap.setPadding(0, 0, 0, 250);
                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {
                                        Toast.makeText(CustomerMapActivity.this, "si funciona", Toast.LENGTH_LONG).show();
                                        return false;
                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

            }
                @Override
                public void onKeyExited(String key) {

                }

                @Override
                public void onKeyMoved(String key, GeoLocation location) {

                }

                @Override
                public void onGeoQueryReady() {

                }

                @Override
                public void onGeoQueryError(DatabaseError error) {

                }

        });
    }


    private void getRouteToMarker(LatLng puntoA, LatLng puntoB) {
        final Handler handler =new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run() {
                if (puntoA != null && puntoB != null && mLastLocation != null && requestBol) {
                    System.out.println(mLastLocation);
                    Routing routing = new Routing.Builder()
                            .key("AIzaSyC5qe0PdRWO9qvCo4rNuyNrXyf8K06SbbI")
                            .travelMode(AbstractRouting.TravelMode.DRIVING)
                            .withListener(CustomerMapActivity.this)
                            .alternativeRoutes(false)
                            .waypoints(puntoB, puntoA)
                            .build();
                    routing.execute();
                }
            }
        }, 1000);

    }
    private double DriverlocationLat = 0;
    private double DriverlocationLng = 0;
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.black};
    @Override
    public void onRoutingFailure(RouteException e) {
        if (e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if (polylines != null) {
            for (Polyline poly : polylines) {
                poly.remove();


            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            //Toast.makeText(getApplicationContext(), "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingCancelled() {

    }

    private void erasePolylines() {
        if(polylines != null){
            for (Polyline line : polylines) {
                line.remove();
            }
            polylines.clear();
        }
    }

    private void enServicio() {
        String conductorId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference enableReference = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(conductorId);
        Map usuarioInfo = new HashMap();
        usuarioInfo.put("EnServicio", "Si");
        usuarioInfo.put("MecanicoServicio", driverFoundID.toString());
        usuarioInfo.put("TipoServicio", requestService);
        enableReference.updateChildren(usuarioInfo);
    }
    private Boolean servicioPendiente = false;
    private void servicioTermina() {
        String conductorId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference enableReference = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(conductorId);
        Map usuarioInfo = new HashMap();
        usuarioInfo.put("EnServicio", "No");
        usuarioInfo.put("MecanicoServicio", "");
        usuarioInfo.put("TipoServicio", "");
        enableReference.updateChildren(usuarioInfo);
    }

    private void isOnService() {
        String conductorId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference enableReference = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(conductorId);
        enableReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && !servicioPendiente) {
                    try {
                        java.util.Map<String, Object> map = (java.util.Map<String, Object>) snapshot.getValue();
                        String A = map.get("EnServicio").toString();
                        String B = map.get("MecanicoServicio").toString();
                        String C = map.get("Descripcion").toString();
                        String D = map.get("TipoServicio").toString();
                        if(A != null && B != null  && C != null){
                            DatabaseReference driverReference = FirebaseDatabase.getInstance().getReference().child("driversWorking");
                            driverReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                                    if (datasnapshot.exists() && datasnapshot.getChildrenCount() > 0) {
                                        java.util.Map<String, Object> map = (java.util.Map<String, Object>) datasnapshot.getValue();
                                        if (map.get(B) != null) {
                                            if(A.equals("Si") && !requestBol){
                                                System.out.println("entra");
                                                driverFoundID = B;
                                                driver_ID = B;
                                                mDescripcion.setText(C);
                                                requestBol = true;
                                                pickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                                                pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Estoy Aquí").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_averiado)));
                                                requestService = D;
                                                if(D.equals("Taller")){
                                                    mRadioGroup.check(R.id.Taller);
                                                } else if (D.equals("Mecanico")) {
                                                    mRadioGroup.check(R.id.Mecanico);
                                                }
                                                mRequest.setText("Buscando la Ubicacion de su Mecanico....");
                                                final Handler handler =new Handler();
                                                handler.postDelayed(new Runnable(){
                                                    @Override
                                                    public void run() {
                                                        getDriverLocation();
                                                    }
                                                }, 1000);
                                                getDriverInfo();
                                                getHasRideEnded();
                                                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                                servicioPendiente = true;
                                            } else {
                                                Map usuarioInfo = new HashMap();
                                                usuarioInfo.put("EnServicio", "No");
                                                usuarioInfo.put("MecanicoServicio", "");
                                                usuarioInfo.put("TipoServicio", "");
                                                enableReference.updateChildren(usuarioInfo);
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    } catch (Exception e) {
                        Map usuarioInfo = new HashMap();
                        usuarioInfo.put("EnServicio", "No");
                        usuarioInfo.put("MecanicoServicio", "");
                        usuarioInfo.put("TipoServicio", "");
                        enableReference.updateChildren(usuarioInfo);
                        finRide();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}




