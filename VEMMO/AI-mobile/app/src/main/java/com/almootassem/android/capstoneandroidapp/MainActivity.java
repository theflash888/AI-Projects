package com.almootassem.android.capstoneandroidapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.almootassem.android.capstoneandroidapp.Listeners.NearestRoadListener;
import com.almootassem.android.capstoneandroidapp.Models.Lane;
import com.almootassem.android.capstoneandroidapp.Models.Sign;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, NearestRoadListener {

    private final String TAG = "MainActivity";

    FirebaseDatabase db;

    private GoogleApiClient mApiClient;

    private GoogleMap mMap;
    private MarkerOptions trafficLightMarkerOptions;
    private Marker trafficLightMarker;

    private Location oldLocation;
    private Location currentLocation;

    private TextView speedometer;
    private CardView card;
    private TextView cardMessage;
    private TextView cardDistance;
    private ImageView cardImage;

    private View laneWarningLeft, laneWarningRight;

    private ArrayList<Sign> signs;
    private ArrayList<Marker> markers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        speedometer = (TextView) findViewById(R.id.speed_text);
        card = (CardView) findViewById(R.id.card);
        cardMessage = (TextView) findViewById(R.id.card_message);
        cardDistance = (TextView) findViewById(R.id.card_distance);
        cardImage = (ImageView) findViewById(R.id.card_image);

        laneWarningLeft = findViewById(R.id.lane_warning_left);
        laneWarningRight = findViewById(R.id.lane_warning_right);

        signs = new ArrayList<>();
        markers = new ArrayList<>();

        db = FirebaseDatabase.getInstance();
//        db.getReference("android_connection_test").setValue("Working");

/*        String key = db.getReference("signs").push().getKey();
        Sign sign = new Sign("Stop Sign", "10", Side.LEFT, false);
        Map<String, Object> signValues = sign.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, signValues);
        db.getReference("signs").updateChildren(childUpdates);*/

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_explore);
        mapFragment.getMapAsync(this);
    }

    protected void onStart() {
        super.onStart();
        mApiClient.connect();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    protected void onStop() {
        // Disconnecting the client invalidates it.
        LocationServices.FusedLocationApi.removeLocationUpdates(mApiClient, this);

        // only stop if it's connected, otherwise we crash
        if (mApiClient != null) {
            mApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setPadding(0, getStatusBarHeight(), 0, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_LOCATION_PERMISSION);
        }

        // Watch signs node where received is false in Firebase
        db.getReference("signs").orderByChild("received").equalTo(false).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    Sign sign = childSnapshot.getValue(Sign.class);
                    signs.add(sign);
                    if (processSigns(signs)){
                        Map<String, Object> updateMap = new HashMap<>();
                        updateMap.put("received", true);
                        updateMap.put("real_time_testing", "oooo");
                        db.getReference("signs").child(childSnapshot.getKey()).updateChildren(updateMap);
                    }
//                    Log.d(TAG, "Value is: " + sign.getType());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        // Watch lane node in Firebase
        db.getReference("lane").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Lane lane = dataSnapshot.getValue(Lane.class);
                displayLaneDepartureWarning(lane);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void displayLaneDepartureWarning(Lane lane){
        if (lane.getStatus().equals(Lane.UNSAFE)){
            if (lane.getDirection().equals(Lane.LEFT)){
                laneWarningRight.setVisibility(View.GONE);
                laneWarningLeft.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.Flash)
                        .duration(1000)
                        .playOn(laneWarningLeft);
            } else if (lane.getDirection().equals(Lane.RIGHT)){
                laneWarningRight.setVisibility(View.VISIBLE);
                laneWarningLeft.setVisibility(View.GONE);
                YoYo.with(Techniques.Flash)
                        .duration(1000)
                        .playOn(laneWarningRight);
            }
        } else {
            laneWarningRight.setVisibility(View.GONE);
            laneWarningLeft.setVisibility(View.GONE);
        }
    }

    private boolean processSigns(ArrayList<Sign> signs){
        if (signs.size() == 0) return false;
//        Log.v(TAG, "List size: " + signs.size());
        for (Sign sign: signs){
            LatLng signPlacement = getSignPlacement(sign);
            new GetNearestRoad(MainActivity.this, this, sign).execute(signPlacement);
            signs.remove(sign);
//            Log.v(TAG, "List size: " + signs.size());
        }
        return true;
    }

    private LatLng getSignPlacement(Sign sign){
        LatLng latLng;
        double distanceKM = sign.getDistance() / 1000.0;
        double dist = distanceKM/Constants.RADIUS_EARTH_KM;
        double bearing = Math.toRadians(currentLocation.getBearing());
        double curLat = Math.toRadians(currentLocation.getLatitude());
        double curLong = Math.toRadians(currentLocation.getLongitude());

        double signLat = Math.asin(Math.sin(curLat) * Math.cos(dist) + Math.cos(curLat) * Math.sin(dist) * Math.cos(bearing));
        double a = Math.atan2(Math.sin(bearing) * Math.sin(dist) * Math.cos(curLat), Math.cos(dist) - Math.sin(curLat) * Math.sin(signLat));
        double signLong = curLong + a;
        signLong = (signLong + 3 * Math.PI) % (2 * Math.PI) - Math.PI;

        latLng = new LatLng(Math.toDegrees(signLat), Math.toDegrees(signLong));
        Log.v(TAG, "Position LAT: " + currentLocation.getLatitude() + " LONG: " + currentLocation.getLongitude() + " BEARING: " + currentLocation.getBearing());
        Log.v(TAG, "Sign LAT: " + Math.toDegrees(signLat) + " LONG: " + Math.toDegrees(signLong) + " DIST: " + dist + " KM: " + distanceKM);

        return latLng;
    }

    private void updateMap(final Location location) {
        //Permission Check
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (location == null) return;
        currentLocation = location;
        if (!mMap.isMyLocationEnabled())
            mMap.setMyLocationEnabled(true);
//        new GetNearestRoad(this, this).execute(location);

        try{
            final float bearing;
            if (location.getBearing() != 0){
                oldLocation = location;
                bearing = location.getBearing();
            } else {
                bearing = oldLocation.getBearing();
            }

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 20), new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder(mMap.getCameraPosition())
                            .bearing(bearing)
                            .tilt(60)
                            .build()));
                }

                @Override
                public void onCancel() {
                }
            });
        } catch (NullPointerException e){}
        //Log.v(TAG, location.getSpeed()*3.6f + "");
        int speed = (int) (location.getSpeed()*3.6f);
        speedometer.setText(String.valueOf(speed));
        setUpCard(location);
    }

    public static float getDistanceToSign(float lat1, float lng1, float lat2, float lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }

    private void setUpCard(Location location){
        LatLngBounds bounds= mMap.getProjection().getVisibleRegion().latLngBounds;
        Marker closestMarker = null;

        for (Marker marker: markers){
            if (bounds.contains(marker.getPosition())){
                if (closestMarker == null){
                    closestMarker = marker;
                } else {
                    float[] resultCurrent = new float[1];
                    float[] resultClosest = new float[1];
                    Location.distanceBetween(location.getLatitude(), location.getLongitude(), marker.getPosition().latitude, marker.getPosition().longitude, resultCurrent);
                    Location.distanceBetween(location.getLatitude(), location.getLongitude(), closestMarker.getPosition().latitude, closestMarker.getPosition().longitude, resultClosest);
                    if (resultCurrent[0] < resultClosest[0]){
                        closestMarker = marker;
                    }
                }
            }
        }
        if (closestMarker != null){
            cardMessage.setText(closestMarker.getTitle());
            float[] resultSign = new float[1];
            Location.distanceBetween(location.getLatitude(), location.getLongitude(), closestMarker.getPosition().latitude, closestMarker.getPosition().longitude, resultSign);
            //Log.v(TAG, "Distance: " + resultSign[0]);
            String distanceStr = String.format(Locale.getDefault(), "%.2f", resultSign[0]) + getString(R.string.meters);
            cardDistance.setText(distanceStr);
            int signType = setCardImage(cardImage, closestMarker.getTitle());
            switch(signType){
                case R.drawable.ic_stop_sign:
                    card.setBackgroundColor(ContextCompat.getColor(this, R.color.colorRedLight));
                    break;
                case R.drawable.ic_red_light:
                    card.setBackgroundColor(ContextCompat.getColor(this, R.color.colorRedLight));
                    break;
                case R.drawable.ic_amber_light:
                    card.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAmberLight));
                    break;
                case R.drawable.ic_green_light:
                    card.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGreenLight));
                    break;
            }
            if (card.getVisibility() == View.GONE || !cardMessage.getText().equals(closestMarker.getTitle())){
                card.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.BounceInUp)
                        .duration(700)
                        .playOn(card);
            }
        } else {
                YoYo.with(Techniques.ZoomOutDown)
                        .duration(300)
                        .playOn(card);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    card.setVisibility(View.GONE);
                }
            }, 300);
        }
    }

    private int setCardImage(ImageView imageView, String title){
        if (title.equals(getString(R.string.stop))){
            imageView.setImageResource(R.drawable.ic_stop_sign);
            return R.drawable.ic_stop_sign;
        } else if (title.equals(getString(R.string.red_light))){
            imageView.setImageResource(R.drawable.ic_light);
            return R.drawable.ic_red_light;
        } else if (title.equals(getString(R.string.amber_light))){
            imageView.setImageResource(R.drawable.ic_light);
            return R.drawable.ic_amber_light;
        } else if (title.equals(getString(R.string.green_light))){
            imageView.setImageResource(R.drawable.ic_light);
            return R.drawable.ic_green_light;
        }
        return 0;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_LOCATION_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
                    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        Location location;
                        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        } else {
                            location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        }
                        oldLocation = location;
                        updateMap(location);
                    }
                } else {
                    Log.v(TAG, "REJECTED");
                    Snackbar.make(findViewById(R.id.coordinator_layout), R.string.location_permission_snackbar, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.grant_permission, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_LOCATION_PERMISSION);
                                    }
                                }
                            }).setCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            super.onDismissed(snackbar, event);
                            if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                                Toast.makeText(MainActivity.this, R.string.app_closing_permission, Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                    }).show();
                }
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationRequest mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(Constants.UPDATE_INTERVAL)
                .setFastestInterval(Constants.FASTEST_INTERVAL);
        LocationServices.FusedLocationApi.requestLocationUpdates(mApiClient, mLocationRequest, this);
    }

    public void onLocationChanged(Location location) {
        // New location has now been determined
        String msg = "Updated Location: " + Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude());
//        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        if (mMap != null){
            updateMap(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(this, "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void nearestRoadFound(JSONObject road, final Sign sign) throws JSONException {
        Log.v(TAG, road.toString());
        float interpolatedLat = Float.parseFloat(road.getJSONArray("snappedPoints").getJSONObject(0).getJSONObject("location").getString("latitude"));
        float interpolatedLong = Float.parseFloat(road.getJSONArray("snappedPoints").getJSONObject(0).getJSONObject("location").getString("longitude"));
        switch (sign.getType()){
            case Sign.STOP_SIGN:
                markers.add(mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(interpolatedLat, interpolatedLong))
                        .title(getString(R.string.stop))
                        .icon(getMarkerIconFromDrawable(ContextCompat.getDrawable(this, R.drawable.ic_stop_sign)))
                        .anchor(0.5f, 0.5f)));
                break;
            default:
                if (sign.isTrafficLight()){
                    setUpTrafficLight(sign, new LatLng(interpolatedLat, interpolatedLong));
                }
                break;
        }
        Log.v(TAG, markers.toString());
    }

    private void setUpTrafficLight(Sign sign, LatLng latLng){
        if (trafficLightMarkerOptions == null){
            trafficLightMarkerOptions = new MarkerOptions()
                    .position(latLng)
                    .anchor(0.5f, 0.5f);
        } else {
            if (trafficLightMarker != null){
                trafficLightMarker.remove();
            }
        }
        switch (sign.getType()){
            case Sign.RED_LIGHT:
                trafficLightMarkerOptions.icon(getMarkerIconFromDrawable(ContextCompat.getDrawable(this, R.drawable.ic_red_light)));
                trafficLightMarkerOptions.title(getString(R.string.red_light));
                break;
            case Sign.AMBER_LIGHT:
                trafficLightMarkerOptions.icon(getMarkerIconFromDrawable(ContextCompat.getDrawable(this, R.drawable.ic_amber_light)));
                trafficLightMarkerOptions.title(getString(R.string.amber_light));
                break;
            case Sign.GREEN_LIGHT:
                trafficLightMarkerOptions.icon(getMarkerIconFromDrawable(ContextCompat.getDrawable(this, R.drawable.ic_green_light)));
                trafficLightMarkerOptions.title(getString(R.string.green_light));
                break;
        }
        trafficLightMarker = mMap.addMarker(trafficLightMarkerOptions);
        markers.add(trafficLightMarker);
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
