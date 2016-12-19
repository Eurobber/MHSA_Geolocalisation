package com.ece.iceageophone.main.listener;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsMessage;
import android.util.Log;

import com.ece.iceageophone.main.BuildConfig;
import com.ece.iceageophone.main.activity.LocateActivity;
import com.ece.iceageophone.main.activity.alertdialog.DisabledGPSAlertDialogActivity;
import com.ece.iceageophone.main.exception.MessageFormatException;
import com.ece.iceageophone.main.util.Command;
import com.ece.iceageophone.main.util.CommandFormatter;
import com.ece.iceageophone.main.util.CommandSender;
import com.ece.iceageophone.main.util.PreferenceChecker;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.SENSOR_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class SmsListener extends BroadcastReceiver implements LocationListener, SensorEventListener {

    private static final String TAG = "SmsListener";

    private static final long LOCATION_MIN_TIME = 1000;
    private static final float LOCATION_MIN_DISTANCE = 1;
    private static final float LOCATION_MAX_ACCURACY = 150;

    private static final long RING_DURATION = 10000;

    private final float alpha = (float) 0.8;
    private float gravity[] = {0f, 0f, 0f};
    private float magnetic[] = {0f, 0f, 0f};

    private Context context = null;
    private LocationManager locationManager = null;
    private SensorManager sensorManager = null;

    private String senderNum = null;
    private String sentPassword = null;

    /**
     * On SMS received
     * @param context
     * @param intent
     */
    @TargetApi(BuildConfig.MIN_SDK_VERSION)
    public void onReceive(Context context, Intent intent) {
        // Used to share data between activities
        final Bundle bundle = intent.getExtras();

        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                // If SDK version > Marshmallow, get format
                String format = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    format = bundle.getString("format");
                }

                // Looping on PDUs because a large message can be broken into multiple PDUs
                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage;
                    if (format == null) {
                        currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    } else {
                        currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i], format);
                    }
                    // Get sender number and message
                    String senderNum = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();

                    Log.d(TAG, "Received message \"" + message + "\" from " + senderNum);

                    // Handle message only is it a command
                    if (CommandFormatter.isCommand(message, context.getApplicationContext())) {
                        String[] splitMessage = message.split(CommandFormatter.SEPARATOR);
                        handleCommand(context, senderNum, splitMessage);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Can not handle received message", e);
        }
    }

    /**
     * Handle received command depending on type
     * @param context
     * @param senderNum
     * @param splitMessage
     */
    private void handleCommand(Context context, String senderNum, String[] splitMessage) {
        try {
            switch (Command.getCommand(splitMessage[2])) {
                case GET_GPS_LOCATION:
                    handleGetGpsLocation(context, senderNum, splitMessage);
                    break;
                case GET_GEOMAGNETIC_LOCATION:
                    handleGetGeomagneticLocation(context, senderNum, splitMessage);
                    break;
                case VIBRATE:
                    vibrate(context);
                    break;
                case RING:
                    ring(context);
                    break;
                default:
                    break;
            }
        } catch (NullPointerException e) {
            Log.d(TAG, "Missing argument", e);
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "No match found for received command " + splitMessage[2]);
        }
    }

    /**
     * Handle received GPS location command : either A request location from B, or B receives location from A
     * @param context
     * @param senderNum
     * @param splitMessage
     */
    private void handleGetGpsLocation(Context context, String senderNum, String[] splitMessage) {
        // Request the location
        if (splitMessage.length == 3) {
            requestLocation(context, senderNum, splitMessage[1]);
        }
        // Process the response
        else if (splitMessage.length == 4) {
            processLocation(context, splitMessage[3]);
        } else {
            Log.d(TAG, "Message formatted incorrectly");
        }
    }

    /**
     * Handle received geomagnetic location command : either A request location from B, or B receives location from A
     * @param context
     * @param senderNum
     * @param splitMessage
     */
    private void handleGetGeomagneticLocation(Context context, String senderNum, String[] splitMessage) {
        // Request the location
        if (splitMessage.length == 3) {
            requestGeomagneticLocation(context, senderNum, splitMessage[1]);
        }
        // Process the response
        else if (splitMessage.length == 4) {
            processLocation(context, splitMessage[3]);
        } else {
            Log.d(TAG, "Message formatted incorrectly");
        }
    }

    /**
     * Start listening for location updates, even if GPS is not enabled beforehand
     * @param context
     * @param senderNum
     * @param sentPassword
     */
    private void requestLocation(Context context, String senderNum, String sentPassword) {
        this.context = context;
        this.senderNum = senderNum;
        this.sentPassword = sentPassword;
        locationManager = (LocationManager) context.getApplicationContext().getSystemService(LOCATION_SERVICE);
        // Is GPS is not enabled, request user to change settings
        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            showSettingsNetWorkAlert(context);
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "No permission to access location");
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_MIN_TIME, LOCATION_MIN_DISTANCE, this);
    }

    private void requestGeomagneticLocation(Context context, String senderNum, String sentPassword) {
        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);

        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor geomagnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        sensorManager.registerListener(this, geomagnetic, SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
    }

    /**
     * Prompt the user to enable GPS on settings
     * @param context
     */
    public static void showSettingsNetWorkAlert(Context context) {
        Intent intent = new Intent(context, DisabledGPSAlertDialogActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Start new activity to display location on map
     * @param context
     * @param locationMessage
     */
    private void processLocation(Context context, String locationMessage) {
        try {
            Location location = CommandFormatter.parseLocationMessage(locationMessage);

            // Store values in cache
            PreferenceChecker.setRemoteLat(context, String.valueOf(location.getLatitude()));
            PreferenceChecker.setRemoteLong(context, String.valueOf(location.getLongitude()));
            PreferenceChecker.setRemoteAlt(context, String.valueOf(location.getAltitude()));

            Log.d(TAG, "Show location on map");

            // Show location on locate screen's map
            Intent intent = new Intent(context, LocateActivity.class);
            intent.putExtra(LocateActivity.LOCATION, location);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (MessageFormatException e) {
            Log.d(TAG, "Can not process location", e);
        }
    }

    /**
     * Vibrate the phone, works independently from the set ringer mode
     * @param context
     */
    private void vibrate(Context context) {
        Log.d(TAG, "Vibrating phone");
        AudioManager audiomanager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        Vibrator vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        long[] pattern = {0, 2000, 1000, 2000, 1000, 2000, 1000};
        vibrator.vibrate(pattern, -1);
    }

    /**
     * Ring the phone with loudest volume, works independently from the set ringer mode
     * @param context
     */
    private void ring(Context context) {
        Log.d(TAG, "Ringing phone");

        // Increase ringing volume at max
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        audioManager.setStreamVolume(AudioManager.STREAM_RING, maxVolume, AudioManager.FLAG_ALLOW_RINGER_MODES);

        // Ring
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        MediaPlayer mp = MediaPlayer.create(context.getApplicationContext(), notification);
        mp.start();

        // Program stop ringing after set duration
        Timer timer = new Timer();
        timer.schedule(new StopRingTimerTask(timer, mp), RING_DURATION);
    }

    /**
     * Timer task used to stop the phone ring after a set duration
     */
    private class StopRingTimerTask extends TimerTask {

        private Timer timer = null;
        private MediaPlayer mp = null;

        public StopRingTimerTask(Timer timer, MediaPlayer mp) {
            this.timer = timer;
            this.mp = mp;
        }

        @Override
        public void run() {
            // Stop ringing
            if (mp != null) {
                Log.d(TAG, "Stop ringing");
                mp.stop();
                mp.release();
            }
            if (timer != null) {
                timer.cancel();
            }
        }
    }

    /**
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        // Process changed location only if accuracy < 50m
        if (location.hasAccuracy()) {
            Log.d(TAG, "Location accuracy " + location.getAccuracy());
            if (location.getAccuracy() < LOCATION_MAX_ACCURACY) {
                if (location != null && locationManager != null && context != null && senderNum != null && sentPassword != null) {
                    // Send location via SMS
                    CommandSender.sendCommand(Command.GET_GPS_LOCATION, senderNum, sentPassword, location);

                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "No permission to access location");
                        return;
                    }

                    // Stop listening to location updates
                    this.locationManager.removeUpdates(this);
                    this.locationManager = null;
                    this.senderNum = null;
                    this.context = null;
                } else {
                    // Else no location found, retry
                    Log.d(TAG, "No GPS location found");
                }
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
//            Log.d(TAG, "Unreliable sensor status : " + event.values[0] + ";" + event.values[1] + ";" + event.values[2]);
//            return;
//        }
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity[0] = event.values[0];
            gravity[1] = event.values[1];
            gravity[2] = event.values[2];
            Log.d(TAG, "OnSensorChanged gravity:" + gravity[0] + " " + gravity[1] + " " + gravity[2]);
        } else if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magnetic[0] = event.values[0];
            magnetic[1] = event.values[1];
            magnetic[2] = event.values[2];
            Log.d(TAG, "OnSensorChanged magnetic:" + magnetic[0] + " " + magnetic[1] + " " + magnetic[2]);

            float[] R = new float[9];
            float[] I = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, gravity, magnetic);

            if (success) {
                Log.d(TAG, "Get location from magnetic field not implemented");
//                float[] A_D = event.values.clone();
//                float[] A_W = new float[3];
//                A_W[0] = R[0] * A_D[0] + R[1] * A_D[1] + R[2] * A_D[2];
//                A_W[1] = R[3] * A_D[0] + R[4] * A_D[1] + R[5] * A_D[2];
//                A_W[2] = R[6] * A_D[0] + R[7] * A_D[1] + R[8] * A_D[2];
//
//                float[] A_W2 = new float[3];
//                float[] A_D = gravity.clone();
//                A_W2[0] = I[0] * A_D[0] + I[1] * A_D[1] + I[2] * A_D[2];
//                A_W2[1] = I[3] * A_D[0] + I[4] * A_D[1] + I[5] * A_D[2];
//                A_W2[2] = I[6] * A_D[0] + I[7] * A_D[1] + I[8] * A_D[2];
//
//                float[] outR = new float[9];
//                SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_X, SensorManager.AXIS_Y, outR);
//
//                Log.d(TAG, "R: " + outR[0] + " " + outR[1] + " " + outR[2]);
//                Log.d(TAG, "Gravity: " + gravity[0] + " " + gravity[1] + " " + gravity[2]);
//                Log.d(TAG, "Magnetic: " + magnetic[0] + " " + magnetic[1] + " " + magnetic[2]);
//                Log.d("Field","\nX :"+A_W[0]+"\nY :"+A_W[1]+"\nZ :"+A_W[2]);
//                Log.d("Field2","\nX :"+A_W2[0]+"\nY :"+A_W2[1]+"\nZ :"+A_W2[2]);

//                float orientationData[] = new float[3];
//                SensorManager.getOrientation(R, orientationData);
//                float yaw = orientationData[0];
//                float pitch = orientationData[1];
//                float roll = orientationData[2];
//
//                yaw = (float) Math.toDegrees(orientationData[0]);
//                pitch = (float) Math.toDegrees(orientationData[1]);
//                roll = (float) Math.toDegrees(orientationData[2]);
//
//                double x = cos(yaw) * cos(pitch);
//                double y = sin(yaw) * cos(pitch);
//                double z = sin(pitch);
//
//                Log.d("Field3","\nX :"+x+"\nY :"+y+"\nZ :"+z);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
