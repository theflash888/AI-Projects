package com.example.devin.mobiledevicesproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Info;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker prevMarker;
    public static final int REQUEST_DIRECTIONS = 0x4870;

    private HashMap<String, ArrayList<Object>> routeHashMap = new HashMap<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                startDirectionsActivity(view);
            }
        });
    }

    private void startDirectionsActivity(View view) {
        Intent i = new Intent(this, Directions.class);
        mMap.clear();
        startActivityForResult(i, REQUEST_DIRECTIONS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_DIRECTIONS){
                boolean isImperial = data.getBooleanExtra(Directions.UNITS_BOOLEAN, false);
                String vClass = data.getStringExtra(Directions.VEHICLE_CLASS);
                float gasPrice = data.getFloatExtra(Directions.FUEL_COST, 0.00f);
                handleRoutes(Directions.globalDirs, isImperial, vClass, gasPrice);
            }
        }
    }


    private void drawOnMap(Route route) {
        mMap.clear();
        Leg leg = route.getLegList().get(0);

        ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
        PolylineOptions polylineOptions = DirectionConverter.createPolyline(this, directionPositionList, 5, Color.RED);
        mMap.addPolyline(polylineOptions);

        List<Step> stepList = leg.getStepList();
        Step startStep = stepList.get(0);
        Step endStep = stepList.get(stepList.size() - 1);

        LatLng start = startStep.getStartLocation().getCoordination();
        LatLng end = endStep.getEndLocation().getCoordination();

        mMap.addMarker(new MarkerOptions().position(start).title("Starting Location"));
        mMap.addMarker(new MarkerOptions().position(end).title("Ending Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 11.5f));
    }

    private void handleRoutes(Direction direction, boolean isImperial, String vClass, float gasPrice) {
        ArrayList<String> routeArray = new ArrayList<>();

        Route route1 = direction.getRouteList().get(0);
        Leg leg1 = route1.getLegList().get(0);
        Info distanceInfo1 = leg1.getDistance();
        double dist1 = Double.parseDouble(distanceInfo1.getValue());
        float cost1 = getCostOfTrip(dist1, isImperial, vClass, gasPrice);
        String entry1 = "Route 1: $" + String.format(Locale.CANADA, "%.2f", cost1) + ", " + String.format(Locale.CANADA, "%.2f", dist1/1000) + " km";
        routeArray.add(entry1);
        ArrayList<Object> data1 = new ArrayList<>();
        data1.add(route1);
        data1.add(dist1);
        data1.add(cost1);
        routeHashMap.put(entry1, data1);
        Log.i("Cost1","$" + String.format("%.2f", cost1));

        if (direction.getRouteList().size() >= 2) {
            Route route2 = direction.getRouteList().get(1);
            Leg leg2 = route2.getLegList().get(0);
            Info distanceInfo2 = leg2.getDistance();
            double dist2 = Double.parseDouble(distanceInfo2.getValue());
            float cost2 = getCostOfTrip(dist2, isImperial, vClass, gasPrice);
            String entry2 = "Route 2: $" + String.format(Locale.CANADA, "%.2f", cost2) + ", " + String.format(Locale.CANADA, "%.2f", dist2/1000) + " km";
            routeArray.add(entry2);
            ArrayList<Object> data2 = new ArrayList<>();
            data2.add(route2);
            data2.add(dist2);
            data2.add(cost2);
            routeHashMap.put(entry2, data2);
            Log.i("Cost2","$" + String.format("%.2f", cost2));
        }

        if (direction.getRouteList().size() >= 3) {
            Route route3 = direction.getRouteList().get(2);
            Leg leg3 = route3.getLegList().get(0);
            Info distanceInfo3 = leg3.getDistance();
            double dist3 = Double.parseDouble(distanceInfo3.getValue());
            float cost3 = getCostOfTrip(dist3, isImperial, vClass, gasPrice);
            String entry3 = "Route 3: $" + String.format(Locale.CANADA, "%.2f", cost3) + ", " + String.format(Locale.CANADA, "%.2f", dist3/1000) + " km";
            routeArray.add(entry3);
            ArrayList<Object> data3 = new ArrayList<>();
            data3.add(route3);
            data3.add(dist3);
            data3.add(cost3);
            routeHashMap.put(entry3, data3);
            Log.i("Cost3","$" + String.format("%.2f", cost3));
        }

        if (direction.getRouteList().size() >= 4) {
            Route route4 = direction.getRouteList().get(3);
            Leg leg4 = route4.getLegList().get(0);
            Info distanceInfo4 = leg4.getDistance();
            double dist4 = Double.parseDouble(distanceInfo4.getValue());
            float cost4 = getCostOfTrip(dist4, isImperial, vClass, gasPrice);
            String entry4 = "Route 4: $" + String.format(Locale.CANADA, "%.2f", cost4) + ", " + String.format(Locale.CANADA, "%.2f", dist4/1000) + " km";
            routeArray.add(entry4);
            ArrayList<Object> data4 = new ArrayList<>();
            data4.add(route4);
            data4.add(dist4);
            data4.add(cost4);
            routeHashMap.put(entry4, data4);
            Log.i("Cost3","$" + String.format("%.2f", cost4));
        }

        Spinner routeSpin = (Spinner)findViewById(R.id.routeOptions);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, routeArray);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        routeSpin.setAdapter(arrayAdapter);
        routeSpin.setEnabled(true);

        Button planBtn = (Button)findViewById(R.id.routeFind);
        planBtn.setVisibility(View.VISIBLE);
        planBtn.setEnabled(true);

    }

    public void drawRoute(View view){
        Log.i("Routes", routeHashMap.toString());
        Spinner routeSpinner = (Spinner)findViewById(R.id.routeOptions);
        String sRoute = routeSpinner.getSelectedItem().toString();
        ArrayList<Object> data = routeHashMap.get(sRoute);

        Route route = (Route)data.get(0);
        double distance = (double) data.get(1);
        float cost = (float) data.get(2);


        TextView costTxt = (TextView)findViewById(R.id.costOfTrip);
        String txtMsg = "Cost of Trip:\n$" + String.format(Locale.CANADA, "%.2f", cost);
        costTxt.setText(txtMsg);

        TextView distTxt = (TextView)findViewById(R.id.distance);
        String distTxtMsg = (distance/1000) + " km";
        distTxt.setText(distTxtMsg);

        drawOnMap(route);
        Log.i("Map", "Drawing on map...");
    }

    public float getCostOfTrip(double distance, boolean isImperial, String sVClass, float gasPrice){
        DBHelper dbHelper = new DBHelper(this);
        float efficiency = dbHelper.getEfficiency(sVClass);


        if (isImperial) {
            gasPrice = convertToMetric(gasPrice);
        }

        float costPerKm = (gasPrice * efficiency) / 100; // in cents
        float dollarCostPerKm = costPerKm/100; // in dollars
        float distanceInKms = (float) (distance/1000);

        return dollarCostPerKm * distanceInKms;

    }

    private float convertToMetric(float priceInDollarPerGal) {
        //For simplicity's sake, we're going to assume currency based on region that the user is in.
        return 100*(priceInDollarPerGal*0.264172f);
    }

    public void launchGasPrices(View view){
        String url = "https://www.gasbuddy.com/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void launchWeather(View view){
        Intent i = new Intent(this, WeatherActivity.class);
        startActivity(i);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                                                PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // permission not granted, maybe ask?
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission granted
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                                                        PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
            } else {
            }
        }
    }
}
