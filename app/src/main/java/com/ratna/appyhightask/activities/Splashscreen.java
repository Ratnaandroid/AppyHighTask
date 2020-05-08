package com.ratna.appyhightask.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ratna.appyhightask.location.GPSTracker;
import com.ratna.myapplication.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Splashscreen extends AppCompatActivity {
    GPSTracker gps;
    public static Double latitude, longitude;

    String[] permissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int PERMISSION_REQUEST_USER = 23;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    public static String countryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        statusCheck();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //  Utility.showLog("hasPermissions", "onRequest: " + grantResults);
        if (requestCode == PERMISSION_REQUEST_USER) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setSplashScreen();
            } else {
                Toast.makeText(this, "Please enable Location", Toast.LENGTH_SHORT).show();
                finishAffinity();

            }
        }

    }

    private void setSplashScreen() {
        try {
            if (gps.canGetLocation()) {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                //    Log.e("latitude", "" + latitude);
                // Log.e("longitude", "" + longitude);
                getAddress(latitude, longitude);
                if (Double.compare(latitude, longitude) == 0) {
                    GPSTracker gps2 = new GPSTracker(Splashscreen.this);
                    latitude = gps2.getLatitude();
                    longitude = gps2.getLongitude();
                    getAddress(latitude, longitude);
                    Log.e("latitude", "" + latitude);
                    Log.e("longitude", "" + longitude);

                }
            }
        } catch (Exception ex) {
            ex.getStackTrace();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent home = new Intent(Splashscreen.this, HomeActivity.class);
                home.putExtra("code", countryCode);
                startActivity(home);


            }


        }, 2000);
    }

    private void updatePermissions() {
        gps = new GPSTracker(this);

        if (!hasPermissions(this, permissions)) {
            requestPermission();
        } else {

            if (gps.canGetLocation()) {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                Log.e("latitude", "" + latitude);
                Log.e("longitude", "" + longitude);

                if (Double.compare(latitude, longitude) == 0) {
                    GPSTracker gps2 = new GPSTracker(Splashscreen.this);
                    latitude = gps2.getLatitude();
                    longitude = gps2.getLongitude();
                    Log.e("latitude", "" + latitude);
                    Log.e("longitude", "" + longitude);

                }
            }
            setSplashScreen();
        }
    }

    private boolean hasPermissions(Context context, String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void getAddress(Double latitude, Double longitude) {
        try {
            Geocoder gcd = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);

            if (addresses.size() > 0) {
                countryCode = addresses.get(0).getCountryCode().toLowerCase();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void requestPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                permissions[0])) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.

            // Request the permission
            ActivityCompat.requestPermissions(this,
                    permissions,
                    PERMISSION_REQUEST_USER);

        } else {
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    permissions, PERMISSION_REQUEST_USER);
        }
    }

    public void statusCheck() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            Log.e("gpsexcep",""+ex);
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            Log.e("networkexcep",""+ex);
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            new AlertDialog.Builder(this)
                    .setMessage("Your GPS seems to be disabled, do you want to enable it?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            finishAffinity();
                        }

                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //  setSplashScreen();
                            finishAffinity();
                        }
                    }).setCancelable(false)

                    .show();
        } else {
            updatePermissions();
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }


}
