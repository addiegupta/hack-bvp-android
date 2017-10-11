package com.example.android.hackbvp;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;


public class GeofenceTransitionsIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GeofenceTransitionsIntentService(String name) {
        super(name);
    }

    public GeofenceTransitionsIntentService() {
        super("empty constructor");
    }

    private static final String TAG = GeofenceTransitionsIntentService.class.getSimpleName();

    private NotificationManager mNotificationManager;
    public static final int NOTIFICATION_ID = 1;
    private static final String TEN_METRE_GEOFENCE_KEY = "10m";
    public static final String ACTION_AC = "turn_on_ac";
    public static final String ACTION_HOME = "unlock_home";


    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        Log.d("GeofencingService","Received geofencing event");

        //TODO Request 3
        //TODO Remove exit code
        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            boolean acTurnedOn = false;
            for (Geofence geofence : triggeringGeofences) {
                if (!acTurnedOn) {
                    acTurnedOn = true;
                    sendNotificationForAc();
                }

                String requestId = geofence.getRequestId();
                Log.d("dfs",requestId);
                if (requestId.equals(TEN_METRE_GEOFENCE_KEY)) {
                    // Send notification and log the transition details.
                    sendNotificationForHome();
                    break;
                }

            }

        } else {
            // Log the error.
            Log.e(TAG, "Geofence transition invalid type " +
                    geofenceTransition);
        }
    }

    private void sendNotificationForAc(){
        Intent turnOnAcIntent = new Intent
                (this,MainActivity.class)
                .setAction(ACTION_AC);


        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                turnOnAcIntent, 0);

        String message = "Want to turn on your AC?";
        String action = "Turn on AC";
        sendNotification(contentIntent,message,action,2);
    }
    private void sendNotificationForHome(){
       Intent homeUnlockIntent = new Intent
               (this,MainActivity.class)
               .setAction(ACTION_HOME);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                homeUnlockIntent, 0);

        String message = "Want to unlock your home?";
        String action = "Unlock Home";
        sendNotification(contentIntent,message,action,1);
    }
    private void sendNotification(PendingIntent contentIntent,String message,String action,int notifId) {

        Log.d("DDS","Sending notification " + message);
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("Home is nearby")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message))
                        .setContentText(message)
                        .addAction(new NotificationCompat.Action(R.mipmap.ic_launcher_round,action,contentIntent))
                        .setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(notifId, mBuilder.build());
    }
}
