package com.example.android.hackbvp;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.aitorvs.android.fingerlock.FingerprintDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity implements FingerprintDialog.Callback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int DOOR_REQUEST_CODE = 1;
    private static final int AC_REQUEST_CODE = 2;
    private static final String PREFS_KEY = "prefs";
    private static final String LATITUDE_KEY = "latitude";
    private static final String LONGITUDE_KEY = "longitude";
    private static final String TEN_METRE_GEOFENCE_KEY = "10m";
    private static final String FIVE_KM_GEOFENCE_KEY = "5km";
    private static final int PERMISSION_REQUEST_KEY = 123;

    private FusedLocationProviderClient mFusedLocationClient;
    private PendingIntent mGeofencePendingIntent;

    private String KEY_NAME = "scan_fingerprint";
    @BindView(R.id.btn_main_open_door)
    Button mOpenDoorButton;
    @BindView(R.id.btn_main_turn_on_ac)
    Button mTurnOnAcButton;
    private boolean mIsOpenDoorClick;
    private GeofencingClient mGeofencingClient;
    private ArrayList<Geofence> mGeofenceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mGeofenceList = new ArrayList<>();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mGeofencingClient = LocationServices.getGeofencingClient(this);

        mOpenDoorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsOpenDoorClick = true;
                new FingerprintDialog.Builder()
                        .with(MainActivity.this)    // context, must call
                        .setKeyName(KEY_NAME)// String key name, must call
                        .setCancelable(false)
                        .setRequestCode(DOOR_REQUEST_CODE)         // request code identifier, must call
                        .show();                    // show the dialog
            }
        });
        mTurnOnAcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsOpenDoorClick = false;
                new FingerprintDialog.Builder()
                        .with(MainActivity.this)    // context, must call
                        .setKeyName(KEY_NAME)       // String key name, must call
                        .setCancelable(false)
                        .setRequestCode(AC_REQUEST_CODE)         // request code identifier, must call
                        .show();                    // show the dialog
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_action_set_home_location:
                if (Build.VERSION.SDK_INT >= M) {
                    if (checkPermission()) {
                        setHomeLocation();
                    }
                }
                break;
        }
        return true;
    }


    public boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ) {
            requestPermission();
            return false;
        }
        return true;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                PERMISSION_REQUEST_KEY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_KEY: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setHomeLocation();
                } else {

                    Toast.makeText(this, "Location is needed for the app to function", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
        }
    }

    private void setHomeLocation() throws SecurityException {

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            SharedPreferences preferences = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
                            preferences.edit()
                                    .putString(LATITUDE_KEY, String.valueOf(latitude))
                                    .putString(LONGITUDE_KEY, String.valueOf(longitude))
                                    .apply();
                            Toast.makeText(MainActivity.this, String.valueOf(latitude) + "  " + String.valueOf(longitude), Toast.LENGTH_LONG).show();
                            Log.d("MainActivity", "Lat " + String.valueOf(latitude) + " Long " + String.valueOf(longitude));
                        }
                    }
                });
        SharedPreferences preferences = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        double mLatitude = Double.parseDouble(preferences.getString(LATITUDE_KEY, null));
        double mLongitude = Double.parseDouble(preferences.getString(LONGITUDE_KEY, null));
        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(TEN_METRE_GEOFENCE_KEY)
                .setCircularRegion(
                        mLatitude,
                        mLongitude,
                        10)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());
        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(FIVE_KM_GEOFENCE_KEY)
                .setCircularRegion(
                        mLatitude,
                        mLongitude,
                        5000)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Geofences added
                        // ...
                        Toast.makeText(MainActivity.this, "Geofences added", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to add geofences
                        // ...
                        Toast.makeText(MainActivity.this, "Failed to add Geofences", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onFingerprintDialogAuthenticated() {

        if (mIsOpenDoorClick) {
            Toast.makeText(this, "Opening door", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Turning on AC", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onFingerprintDialogVerifyPassword(FingerprintDialog fingerprintDialog, String s) {
        Toast.makeText(this, "onFPDverifyPassword", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFingerprintDialogStageUpdated(FingerprintDialog fingerprintDialog, FingerprintDialog.Stage stage) {
        Toast.makeText(this, "onFPDSUpdated", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFingerprintDialogCancelled() {
        Toast.makeText(this, "onFpDCanceled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().

        //TODO Check request code
        mGeofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }


}
