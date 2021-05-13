package com.Alikapp.alikappconductor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;

import android.preference.PreferenceManager;
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

import com.addisonelliott.segmentedbutton.SegmentedButton;
import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.skyfishjy.library.RippleBackground;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.Alikapp.alikappconductor.notifyFirebase.tokeng;

public class CustomerMapActivity extends FragmentActivity implements OnMapReadyCallback, RoutingListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private GoogleMap mMap;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    private Dialog myDialog, myDialogTaller;

    private FusedLocationProviderClient mFusedLocationClient;

    private Button mRequest, mRequestt, mChat, mCancelar, mLogout;

    private FloatingActionButton mDesplegar;

    private EditText mDescripcion;

    private TextView mLongDescrip, mMenuNombre, mTerminosCondiciones;

    private LatLng pickupLocation;

    private Boolean requestBol = false;
    private Boolean isOnService = false;
    private Boolean outSideRequest = false;
    private Boolean isOutSide = false;

    private Marker pickupMarker = null;

    private SupportMapFragment mapFragment;

    private String destination, requestService;

    private LatLng destinationLatLng, tallerLatLng;

    private ConstraintLayout mDriverInfo;

    public ImageView mDriverProfileImage;
    private CircleImageView mImagenPerfil;

    private android.widget.TextView mDriverName, mDriverPhone, mDriverCar, mDriverDistance, mDriverTime;

    //private RadioGroup mRadioGroup;

    private SegmentedButtonGroup mSegmentedButtonGroup;

    private TextView mRatingBar;

    private AppBarConfiguration mAppBarConfiguration;

    private BottomSheetBehavior mBottomSheetBehavior;

    private String token1;

    private String titulo1;

    private String detalle1;

    private String info1;

    private RippleBackground rippleBackground, rippleBackgroundhelp, rippleBackgroundEspera;
    private ConstraintLayout constraintLayout;
    private CardView cardViewInicial, cardViewBusqueda, cardViewOutSide;
    private AVLoadingIndicatorView avi;

    public static String conductorUID;

    public static String clicknotify="";

    private DatabaseReference mDriverDatabase;
    private CoordinatorLayout mMain, mSecond;
    private DrawerLayout drawer;

    // Intancias del popup Talleres
    private TextView mNombreTaller;
    private TextView mDireccionTaller;
    private CircleImageView mImagenTaller;
    private CardView mCardViewTaller, mCardViewCarca;

    onAppKilled service = null;
    boolean mBound = false;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            onAppKilled.LocalBinder binder = (onAppKilled.LocalBinder) iBinder;
            service = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            service = null;
            mBound = false;
        }
    };

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_costumer_map);

        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, 106);
            }
        }

        bindService(new Intent(this, onAppKilled.class),
                mServiceConnection,
                Context.BIND_AUTO_CREATE);

        mMain = findViewById(R.id.mainCoordinator);
        mSecond = findViewById(R.id.secondCoodinator);
        mSecond.setVisibility(View.VISIBLE);
        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
        avi.show();
        mMain.setVisibility(View.GONE);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        destinationLatLng = new LatLng(0.0,0.0);

        myDialog = new Dialog(this);
        myDialogTaller = new Dialog(this);


        mRatingBar = (TextView) findViewById(R.id.driverRate);

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


        mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(conductorUID);
        getUserInfo();

        myDialog.setContentView(R.layout.layout_popup);
        myDialogTaller.setContentView(R.layout.layout_popup_taller);

        mDriverInfo = (ConstraintLayout) findViewById(R.id.driverInfo);
        mDriverProfileImage = (ImageView) findViewById(R.id.driverProfileImage);
        mDriverName = (android.widget.TextView) findViewById(R.id.driverName);
        mDriverPhone = (android.widget.TextView) findViewById(R.id.driverPhone);
        mDriverCar = (android.widget.TextView)findViewById(R.id.driverCar);
        mDriverDistance = findViewById(R.id.driverDistance);
        mDriverTime = findViewById(R.id.driverTiempo);
        mChat =(Button) findViewById(R.id.mChat);
        mLogout =(Button) findViewById(R.id.logout);
        mTerminosCondiciones = findViewById(R.id.TerminosCondiciones);
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        mRequestt = (Button) findViewById(R.id.request);
        mRequestt.setOnClickListener(new android.view.View.OnClickListener() {
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
                }
            }
        });

        mRequest = (Button) myDialog.findViewById(R.id.request);
        mRequest.setText("Pedir Ayuda");
        mDescripcion = myDialog.findViewById(R.id.descripcion);
        mLongDescrip = myDialog.findViewById(R.id.longDescrip);
        mSegmentedButtonGroup = (SegmentedButtonGroup) myDialog.findViewById(R.id.buttonGroup);
        mSegmentedButtonGroup.setPosition(0, true);
        cardViewInicial = myDialog.findViewById(R.id.carview_inicial);
        cardViewOutSide = myDialog.findViewById(R.id.cardViewOutSide);
        cardViewInicial.setVisibility(View.VISIBLE);
        cardViewBusqueda = myDialog.findViewById(R.id.cardViewBusqueda);
        cardViewBusqueda.setVisibility(View.GONE);
        mCancelar = myDialog.findViewById(R.id.cancelarPedido);
        // mRadioGroup = (RadioGroup) myDialog.findViewById(R.id.radioGroup);
        // mRadioGroup.check(R.id.Mecanico);
        myDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (isOutSide) {
                    cardViewBusqueda.setVisibility(View.GONE);
                    cardViewInicial.setVisibility(View.VISIBLE);
                    cardViewOutSide.setVisibility(View.GONE);
                    outSideRequest = true;
                }
            }
        });

        mCardViewTaller = myDialogTaller.findViewById(R.id.carview_cargando);
        mCardViewCarca = myDialogTaller.findViewById(R.id.carview_taller_info);
        mCardViewTaller.setVisibility(View.GONE);
        mCardViewCarca.setVisibility(View.VISIBLE);
        mNombreTaller = myDialogTaller.findViewById(R.id.nombreTaller);
        mImagenTaller = myDialogTaller.findViewById(R.id.imagenTaller);
        mDireccionTaller = myDialogTaller.findViewById(R.id.dirccionTaller);
        myDialogTaller.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                camaraEnMovimiento = true;
                mCardViewTaller.setVisibility(View.VISIBLE);
                mCardViewCarca.setVisibility(View.GONE);
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
                        rippleBackground.setVisibility(View.GONE);
                        constraintLayout.setVisibility(View.GONE);
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        mDesplegar.setVisibility(View.VISIBLE);
                        rippleBackground.setVisibility(View.VISIBLE);
                        constraintLayout.setVisibility(View.VISIBLE);
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

        rippleBackground = (RippleBackground)findViewById(R.id.content);
        rippleBackground.startRippleAnimation();
        constraintLayout = findViewById(R.id.contrainLayout);
        rippleBackgroundhelp = (RippleBackground) myDialog.findViewById(R.id.help);
        rippleBackgroundhelp.stopRippleAnimation();
        rippleBackgroundEspera = (RippleBackground)findViewById(R.id.espera);

        mDesplegar = findViewById(R.id.desplegarCuadro);
        mDesplegar.setVisibility(View.VISIBLE);
        mDesplegar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isOnService){
                    ShowPopup();

                } else {
                    mDesplegar.setVisibility(View.GONE);
                    rippleBackground.setVisibility(View.GONE);
                    constraintLayout.setVisibility(View.GONE);
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
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
        drawer = findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        mMenuNombre = headerView.findViewById(R.id.nombreManu);
        mImagenPerfil = headerView.findViewById(R.id.imageView);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_history, R.id.nav_gallery, R.id.nav_billetera, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        /*NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);*/
        NavigationUI.setupWithNavController(navigationView, navController);

        getTerminos();
        mTerminosCondiciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                Intent intent = new Intent(CustomerMapActivity.this, LegalActivity.class);
                intent.putExtra("PrimeraVez", false);
                intent.putExtra("Terminos", Terminos);
                startActivity(intent);
            }
        });

        isOnService();
    }
    //Finaliza el onCreate

    public void ShowPopup() {

        final int maximum_character = 250;
        mDescripcion.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maximum_character)});

        mDescripcion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mLongDescrip.setText(""+String.valueOf(maximum_character - mDescripcion.getText().length()));
                if (mLongDescrip.getText().equals("250")) {
                    rippleBackgroundhelp.stopRippleAnimation();
                    rippleBackgroundhelp.startRippleAnimation();
                    rippleBackgroundhelp.stopRippleAnimation();
                }
                else {
                    rippleBackgroundhelp.startRippleAnimation();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    endRide();
                } catch (Exception e) {
                    romper = true;
                    finRide();
                    CustomerMapActivity.super.onRestart();
                    Toast.makeText(CustomerMapActivity.this, "Solicitud Cancelada", Toast.LENGTH_SHORT).show();
                }
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
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

                        int selectId = mSegmentedButtonGroup.getPosition();
                        if (selectId == 0)
                        {
                            requestService = "Mecanico";
                        }
                        else
                            if ( selectId == 1)
                            {
                                requestService = "Taller";
                            }
                        /*int selectId = mRadioGroup.getCheckedRadioButtonId();

                        final RadioButton radioButton = (RadioButton) myDialog.findViewById(selectId);

                        if (radioButton.getText() == null){
                            return;
                        }

                        requestService = radioButton.getText().toString();*/

                        requestBol = true;

                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
                        GeoFire geoFire = new GeoFire(ref);
                        geoFire.setLocation(userId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                        //poner un try catch con un mensaje de permitir hubicación en el dispositivo
                        pickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                        if (pickupMarker == null) {
                            pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Estoy Aquí").icon(BitmapDescriptorFactory.fromResource(R.drawable.averiado)));
                        }

                        mRequest.setText("Buscando Mecanico");

                        getClosestDriver();
                        temporizador.continuarConteo();
                        tiempoEspera();
                        romper = false;

                        //mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference enableReference = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId);
                        Map usuarioInfo = new HashMap();
                        usuarioInfo.put("Descripcion", "" + mDescripcion.getText());
                        enableReference.updateChildren(usuarioInfo);

                        final RippleBackground rippleBackgroundEspera = (RippleBackground)myDialog.findViewById(R.id.espera);
                        cardViewInicial.setVisibility(View.GONE);
                        cardViewBusqueda.setVisibility(View.VISIBLE);
                        rippleBackgroundEspera.startRippleAnimation();
                    } else {
                       Toast.makeText(CustomerMapActivity.this  , "Escribe una breve descripción del problema", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();

    }

    private void guardartoken(String token) {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(conductorUID);
        ref.child("token").setValue(token);
    }

    private Temporizador temporizador = new Temporizador(0,4,0);
    private void tiempoEspera() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!(temporizador.getSegundosTotal() > 0) || !requestBol){
                    finalizarEspera();
                } else {
                    temporizador.conteoRegresivo();
                    tiempoEspera();
                }
            }
        }, 1000);
    }
    private Boolean romper = true;
    private void finalizarEspera() {
        if(!romper){
            finRide();
            romper = true;
            Toast.makeText(this,"No hay mecánicos cerca",Toast.LENGTH_LONG).show();
        }
    }

    final static float ZOOM_CAMARA = (float) 15.5;
    private void finRide() {
        requestBol = false;
        isOnService = false;
        cardViewInicial.setVisibility(View.VISIBLE);
        cardViewBusqueda.setVisibility(View.GONE);
        mDesplegar.setVisibility(View.VISIBLE);
        rippleBackground.setVisibility(View.VISIBLE);
        constraintLayout.setVisibility(View.VISIBLE);
        temporizador.reIniciarConteo();
        service.removeLocationUpdates();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), ZOOM_CAMARA));
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
            pickupMarker = null;
        }
        if (mDriverMarker != null){
            mDriverMarker.remove();
            mDriverMarker = null;
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
                        if(A.equals("Si")){
                            mDesplegar.setVisibility(View.GONE);
                            rippleBackground.setVisibility(View.GONE);
                            constraintLayout.setVisibility(View.GONE);
                            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            isOnService = true;
                            romper = true;
                            getDriverLocation();
                            getDriverInfo();
                            getHasRideEnded();
                            if(requestService.equals("Taller")) {
                                service.requestLocationUpdates();
                            }
                            driver_ID = driverFoundID;
                            mRequest.setText("Buscando la Ubicacion de su Mecanico....");
                            enServicio();
                            myDialog.dismiss();
                        } else if (A.equals("No")) {
                            driverFound = false;
                            requestBol = true;

                            int selectId = mSegmentedButtonGroup.getPosition();
                            if (selectId == 0)
                            {
                                requestService = "Mecanico";
                            }
                            else
                            if ( selectId == 1)
                            {
                                requestService = "Taller";
                            }

                           /* int selectId = mRadioGroup.getCheckedRadioButtonId();
                           final RadioButton radioButton = (RadioButton) findViewById(selectId);
                           if (radioButton.getText() == null){
                                return;
                            }
                            requestService = radioButton.getText().toString(); */
                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
                            GeoFire geoFire = new GeoFire(ref);
                            geoFire.setLocation(userId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                            pickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                            if (pickupMarker == null) {
                                pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Estoy Aquí").icon(BitmapDescriptorFactory.fromResource(R.drawable.averiado)));
                            }
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
                        mDriverMarker = null;
                    }
                    Location loc1 = new Location("");
                    loc1.setLatitude(pickupLocation.latitude);
                    loc1.setLongitude(pickupLocation.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(driverLatLng.latitude);
                    loc2.setLongitude(driverLatLng.longitude);

                    float distance = loc1.distanceTo(loc2);


                    if (distance >= 1000){
                        BigDecimal distanceShort = new BigDecimal((distance)/1000).setScale(1, RoundingMode.HALF_UP);
                        mDriverDistance.setText(String.valueOf(distanceShort)+" Km");
                        //mRequest.setText("Su Mecanico esta Aquí");
                    } else if (distance < 1000 && distance >= 50){
                        BigDecimal distanceShort = new BigDecimal(distance).setScale(1, RoundingMode.HALF_UP);
                        mDriverDistance.setText(String.valueOf((distanceShort))+" m");
                    } else {
                        mDriverDistance.setText("Aquí");
                    }

                    float speed = (float) 0.0;
                    boolean hasSpeed = false;
                    if(requestService.equals("Taller")){
                        speed = loc1.getSpeed();
                        hasSpeed = loc1.hasSpeed();
                        getRouteToMarker(new LatLng(DriverlocationLat, DriverlocationLng),
                                new  LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                    } else {
                        speed = loc2.getSpeed();
                        hasSpeed = loc2.hasSpeed();
                        getRouteToMarker( new  LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()),
                                new LatLng(DriverlocationLat, DriverlocationLng));
                    }

                    if(!hasSpeed){
                        speed = (float) 4.0;
                    }
                    int time = (int) ((distance/speed)/60);
                    mDriverTime.setText(time + " min");

                    if (mDriverMarker == null) {
                        mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLatLng).title("Su Mecanico")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mecanico)));
                    }
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
                        String nombre = dataSnapshot.child("name").getValue().toString();
                        String[] nombSeparado = nombre.split(" ");
                        if (nombSeparado.length>=3){
                            nombre = nombSeparado[0] + " " + nombSeparado[2];
                        }
                        mDriverName.setText(nombre);
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

                    if (dataSnapshot.child("rating").getValue() != null){
                        int ratingSum = 0;
                        float ratingsTotal = 0;
                        float ratingsAvg = 0;
                        for (DataSnapshot child : dataSnapshot.child("rating").getChildren()){
                            ratingSum = ratingSum + Integer.valueOf(child.getValue().toString());
                            ratingsTotal++;
                        }
                        if(ratingsTotal!= 0){
                            ratingsAvg = ratingSum/ratingsTotal;
                            mRatingBar.setText(ratingsAvg + "");
                        }
                    } else { mRatingBar.setText("5.0"); }

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
        isOnService = false;
        cardViewInicial.setVisibility(View.VISIBLE);
        cardViewBusqueda.setVisibility(View.GONE);
        mDesplegar.setVisibility(View.VISIBLE);
        rippleBackground.setVisibility(View.VISIBLE);
        constraintLayout.setVisibility(View.VISIBLE);
        temporizador.reIniciarConteo();
        service.removeLocationUpdates();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), ZOOM_CAMARA));
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
            pickupMarker = null;
        }
        if (mDriverMarker != null){
            mDriverMarker.remove();
            mDriverMarker = null;
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
                if(camaraEnMovimiento){
                    if(tallerMarker1 != null){
                        tallerMarker1.remove();
                        tallerMarker1 = null;
                    }
                    if(tallerMarker2 != null){
                        tallerMarker2.remove();
                        tallerMarker2 = null;
                    }
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
        try {
            mMap.setMyLocationEnabled(true);
        } catch (Exception e) {
            checkLocationPermission();
        }

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
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                camaraEnMovimiento = false;
                String id = "id";
                if(tallerMarker1 != null && tallerMarker1.getId().equals(marker.getId())) {
                    id = tallerKey1;
                } else if (tallerMarker2 != null && tallerMarker2.getId().equals(marker.getId())) {
                    id = tallerKey2;
                }
                if (marker.getTitle().equals("Taller Mecánico")) {
                    ShowPopupTaller(id);
                }
                return false;
            }
        });
        getTallerAround();
    }

    private Boolean camaraEnMovimiento = true;
    private void ShowPopupTaller(String id) {
        DatabaseReference talleres = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(id);
        talleres.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    java.util.Map<String, Object> map = (java.util.Map<String, Object>) snapshot.getValue();
                    if(map.get("name") != null) {
                        mNombreTaller.setText(map.get("name").toString());
                    }
                    if(map.get("email") != null) {
                        mDireccionTaller.setText(map.get("email").toString());
                    }
                    if(map.get("profileImageUrl")!=null){
                        com.bumptech.glide.Glide.with(getApplication()).load(map.get("profileImageUrl").toString()).into(mImagenTaller);
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mCardViewTaller.setVisibility(View.GONE);
                            mCardViewCarca.setVisibility(View.VISIBLE);
                        }
                    }, 2000);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myDialogTaller.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                myDialogTaller.show();
            }
        }, 1000);
    }

    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for(Location location : locationResult.getLocations()){
                if(getApplicationContext()!=null){
                    mLastLocation = location;

                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                    float lat = (float) location.getLatitude();
                    float lon = (float) location.getLongitude();
                    if (!(lat < 4.832224338445363 && lat > 4.484097960049635 && lon < -74.0104303881526 && lon > -74.21255730092525) && !outSideRequest) {
                        isOutSide = true;
                        cardViewBusqueda.setVisibility(View.GONE);
                        cardViewOutSide.setVisibility(View.VISIBLE);
                        ShowPopup();
                    }
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
                if(mDriverMarker == null){
                    mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLocation).title(key).icon(BitmapDescriptorFactory.fromResource(R.drawable.mecanico)));
                }
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
    private String tallerKey1, tallerKey2;
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
                if (key != null && camaraEnMovimiento) {
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
                                    tallerMarker1 = mMap.addMarker(new MarkerOptions().position(tallerLatLng).title("Taller Mecánico").icon(BitmapDescriptorFactory.fromResource(R.drawable.taller1)));
                                    tallerKey1 = key;
                                } else if(tallerMarker2 == null){
                                    tallerMarker2 = mMap.addMarker(new MarkerOptions().position(tallerLatLng).title("Taller Mecánico").icon(BitmapDescriptorFactory.fromResource(R.drawable.taller1)));
                                    tallerKey2 = key;
                                }
                                mMap.getUiSettings().setMapToolbarEnabled(true);
                                mMap.setPadding(0, 0, 0, 250);

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
                    Routing routing = new Routing.Builder()
                            .key("AIzaSyC5qe0PdRWO9qvCo4rNuyNrXyf8K06SbbI")
                            .travelMode(AbstractRouting.TravelMode.DRIVING)
                            .withListener(CustomerMapActivity.this)
                            .alternativeRoutes(false)
                            .waypoints(puntoB, puntoA)
                            .build();
                    routing.execute();
                    moverCamara(puntoA, puntoB);
                }
            }
        }, 1000);

    }

    Boolean terminado = false;
    private void moverCamara(LatLng puntoA, LatLng puntoB) {
        try {
            double lati = (puntoA.latitude + puntoB.latitude)/2;
            double longi = (puntoA.longitude + puntoB.longitude)/2;
            Location locationIn = new Location("");
            locationIn.setLatitude(puntoA.latitude);
            locationIn.setLongitude(puntoA.longitude);

            Location locationFn = new Location("");
            locationFn.setLatitude(puntoB.latitude);
            locationFn.setLongitude(puntoB.longitude);

            double A = Math.log(locationIn.distanceTo(locationFn)/2);
            double zoom = (-1)*((10000*A)-172247)/6967;

            if (isOnService && terminado) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lati, longi), (float) zoom));
            } else {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(puntoB.latitude, puntoB.longitude), 16));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(puntoA.latitude, puntoA.longitude), 16));
                    }
                }, 2000);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lati, longi), (float) zoom));
                        terminado = true;
                    }
                }, 4000);

            }
        } catch (Exception e) {

        }
    }

    private double DriverlocationLat = 0;
    private double DriverlocationLng = 0;
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.AlikappGris};
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
        isOnService = false;
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
                                                driverFoundID = B;
                                                driver_ID = B;
                                                mDescripcion.setText(C);
                                                requestBol = true;
                                                isOnService = true;
                                                requestService = D;
                                                if(D.equals("Taller")) {
                                                    mSegmentedButtonGroup.setPosition(1, true);
                                                    service.requestLocationUpdates();
                                                 //   mRadioGroup.check(R.id.Taller);
                                                } else if (D.equals("Mecanico")) {
                                                    mSegmentedButtonGroup.setPosition(0,true);
                                                   // mRadioGroup.check(R.id.Mecanico);
                                                }
                                                mRequest.setText("Buscando la Ubicacion de su Mecanico....");
                                                final Handler handler =new Handler();
                                                handler.postDelayed(new Runnable(){
                                                    @Override
                                                    public void run() {
                                                        pickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                                                        if (pickupMarker == null) {
                                                            pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Estoy Aquí").icon(BitmapDescriptorFactory.fromResource(R.drawable.averiado)));
                                                        }
                                                        getDriverLocation();
                                                    }
                                                }, 1000);
                                                getDriverInfo();
                                                getHasRideEnded();
                                                mDesplegar.setVisibility(View.GONE);
                                                rippleBackground.setVisibility(View.GONE);
                                                constraintLayout.setVisibility(View.GONE);
                                                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                                servicioPendiente = true;
                                            } else {
                                                isOnService = false;
                                                Map usuarioInfo = new HashMap();
                                                usuarioInfo.put("EnServicio", "No");
                                                usuarioInfo.put("MecanicoServicio", "");
                                                usuarioInfo.put("TipoServicio", "");
                                                enableReference.updateChildren(usuarioInfo);
                                                service.removeLocationUpdates();
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

    private void getUserInfo(){
        mDriverDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("name")!=null && map.get("cedula")!=null){
                        mMenuNombre.setText(map.get("name").toString());
                        if(map.get("profileImageUrl")!=null){
                            com.bumptech.glide.Glide.with(getApplication()).load(map.get("profileImageUrl").toString())
                                    .into(mImagenPerfil);

                        }
                        final Handler handler =new Handler();
                        handler.postDelayed(new Runnable(){
                            @Override
                            public void run() {
                                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                                mMain.setVisibility(View.VISIBLE);
                                mSecond.setVisibility(View.GONE);
                            }
                        }, 2000);
                    } else {
                        Intent intent = new Intent(CustomerMapActivity.this, LegalActivity.class);
                        intent.putExtra("PrimeraVez", true);
                        intent.putExtra("Terminos", Terminos);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private String Terminos;
    private void getTerminos() {
        DatabaseReference m = FirebaseDatabase.getInstance().getReference().child("TerminosCondiciones");
        m.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("Customer")!=null){
                        Terminos = map.get("Customer").toString();
                    }
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Common.KEY_REQUEST_LOCATION_UPDATES)) {
            // ver minuto 30
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        if (mBound) {
            unbindService(mServiceConnection);
            mBound = false;
        }
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListenLocation(SendLocationActivity event) {
        if (event != null) {
            String datoshechos = new StringBuilder()
                    .append(event.getLocation().getLatitude())
                    .append("/")
                    .append(event.getLocation().getLongitude())
                    .toString();
            //Toast.makeText(service, datoshechos, Toast.LENGTH_SHORT).show();
        }
    }
}




