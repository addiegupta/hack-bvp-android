package com.example.android.habitatspi;

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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.aitorvs.android.fingerlock.FingerprintDialog;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
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

public class MainActivity extends AppCompatActivity implements FingerprintDialog.Callback {

    private static final int DOOR_REQUEST_CODE = 1;
    private static final String PREFS_KEY = "prefs";
    private static final String LATITUDE_KEY = "latitude";
    private static final String LONGITUDE_KEY = "longitude";
    private static final String TEN_METRE_GEOFENCE_KEY = "10m";
    private static final String FIVE_KM_GEOFENCE_KEY = "5km";
    private static final int PERMISSION_REQUEST_KEY = 123;
    private static String BASE_URL;
    private static String CHANGE_URL;

    private FusedLocationProviderClient mFusedLocationClient;
    private PendingIntent mGeofencePendingIntent;

    private String KEY_NAME = "scan_fingerprint";
    private GeofencingClient mGeofencingClient;
    private ArrayList<Geofence> mGeofenceList;

    @BindView(R.id.iv_extras)
    ImageView mExtrasImageView;
    @BindView(R.id.iv_turn_on_ac)
    ImageView mTurnOnAcImageView;
    @BindView(R.id.iv_unlock_home)
    ImageView mUnlockHomeImageView;
    @BindView(R.id.iv_mood_light)
    ImageView mMoodLightImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        BASE_URL = getString(R.string.api_base_url);
        CHANGE_URL = BASE_URL + "changemeapi/currentbool?status=true";

        mGeofenceList = new ArrayList<>();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mGeofencingClient = LocationServices.getGeofencingClient(this);

        String action = getIntent().getAction();
        if (action.equals(GeofenceTransitionsIntentService.ACTION_AC)) {
            Toast.makeText(MainActivity.this, "Turning on AC", Toast.LENGTH_SHORT).show();
            turnOnAC();
        } else if (action.equals(GeofenceTransitionsIntentService.ACTION_HOME)) {
            new FingerprintDialog.Builder()
                    .with(MainActivity.this)    // context, must call
                    .setKeyName(KEY_NAME)// String key name, must call
                    .setCancelable(true)
                    .setRequestCode(DOOR_REQUEST_CODE)         // request code identifier, must call
                    .show();                    // show the dialog

        }

        mUnlockHomeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FingerprintDialog.Builder()
                        .with(MainActivity.this)    // context, must call
                        .setKeyName(KEY_NAME)// String key name, must call
                        .setCancelable(true)
                        .setRequestCode(DOOR_REQUEST_CODE)         // request code identifier, must call
                        .show();                    // show the dialog
            }
        });
        mTurnOnAcImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Turning on AC", Toast.LENGTH_SHORT).show();
                //TODO Request 2
                turnOnAC();
            }
        });
        mMoodLightImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MoodLightActivity.class));
            }
        });
        mExtrasImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ExtrasActivity.class));
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
            case R.id.menu_action_reset:
                resetEverything();
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
                }
            }
        }
    }

    private void handleLocation(Location location) throws SecurityException {
        // Logic to handle location object
        double mLatitude = location.getLatitude();
        double mLongitude = location.getLongitude();
        SharedPreferences preferences = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        preferences.edit()
                .putString(LATITUDE_KEY, String.valueOf(mLatitude))
                .putString(LONGITUDE_KEY, String.valueOf(mLongitude))
                .apply();
        Toast.makeText(MainActivity.this, String.valueOf(mLatitude) + "  " + String.valueOf(mLongitude), Toast.LENGTH_LONG).show();
        Log.d("MainActivity", "Lat " + String.valueOf(mLatitude) + " Long " + String.valueOf(mLongitude));

        mGeofencingClient.removeGeofences(getGeofencePendingIntent());

        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(TEN_METRE_GEOFENCE_KEY)
                .setCircularRegion(
                        mLatitude,
                        mLongitude,
                        25)
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
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to add geofences
                        // ...
                    }
                });


    }


    private void setHomeLocation() throws SecurityException {

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            handleLocation(location);
                        }
                    }
                });
    }

    @Override
    public void onFingerprintDialogAuthenticated() {

        Toast.makeText(this, "Opening door", Toast.LENGTH_SHORT).show();
        unlockDoor();
    }

    private void unlockDoor() {
        RequestQueue queue = Volley.newRequestQueue(this);
        queue = QueryUtils.addVolleyHttpRequest(queue, false, CHANGE_URL);
        String url = BASE_URL + "doorapi/edit?status=1";
        queue = QueryUtils.addVolleyHttpRequest(queue, false, url);

    }

    private void turnOnAC() {
        RequestQueue queue = Volley.newRequestQueue(this);
        queue = QueryUtils.addVolleyHttpRequest(queue, false, CHANGE_URL);
        String url = BASE_URL + "acapi/edit?status=1";
        queue = QueryUtils.addVolleyHttpRequest(queue, false, url);

    }

    @Override
    public void onFingerprintDialogVerifyPassword(FingerprintDialog fingerprintDialog, String s) {
    }

    @Override
    public void onFingerprintDialogStageUpdated(FingerprintDialog fingerprintDialog, FingerprintDialog.Stage stage) {
    }

    @Override
    public void onFingerprintDialogCancelled() {
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

    private void resetEverything() {
        RequestQueue queue = Volley.newRequestQueue(this);
        queue = QueryUtils.addVolleyHttpRequest(queue, false, CHANGE_URL);

        String resetUrl1 = BASE_URL + "lightapi/reset";
        queue = QueryUtils.addVolleyHttpRequest(queue, true, resetUrl1);

        String resetUrl2 = BASE_URL + "doorapi/edit?status=0";
        queue = QueryUtils.addVolleyHttpRequest(queue, false, resetUrl2);

        String resetUrl3 = BASE_URL + "acapi/edit?status=0";
        queue = QueryUtils.addVolleyHttpRequest(queue, false, resetUrl3);
        Toast.makeText(this, "All devices reset!", Toast.LENGTH_SHORT).show();
    }


}
