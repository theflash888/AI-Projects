package com.example.devin.mobiledevicesproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.constant.Unit;
import com.akexorcist.googledirection.model.Direction;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Directions extends AppCompatActivity implements PriceListener{

    private LatLng from;
    private LatLng to;
    private boolean fromFilled = false;
    private boolean toFilled = false;

    private double trip1Distance = 0;
    private double trip2Distance = 0;
    private double trip3Distance = 0;

    public static final String UNITS_BOOLEAN = "com.example.devin.mobiledevicesproject.UNIT";
    public static final String VEHICLE_CLASS = "com.example.devin.mobiledevicesproject.CLASS";
    public static final String FUEL_COST = "com.example.devin.mobiledevicesproject.FUEL";


    public static Direction globalDirs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        //=============== FROM LISTENER ===============================================\\

        PlaceAutocompleteFragment fromAutocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.fromSearch);

        fromAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                from = place.getLatLng();
                String name = place.getName().toString();
                if(!name.isEmpty()){
                    fromFilled = true;
                    if(toFilled){
                        Button proceed = (Button)findViewById(R.id.btnGetDirections);
                        proceed.setEnabled(true);
                    }
                }
                else{
                    fromFilled = false;
                }
            }

            @Override
            public void onError(Status status) {}
        });
        //=============== END FROM LISTENER ===========================================//


        //=============== TO LISTENER =================================================\\
        PlaceAutocompleteFragment toAutocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.toSearch);

        toAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                to = place.getLatLng();
                String name = place.getName().toString();
                if(!name.isEmpty()){
                    toFilled = true;
                    if(fromFilled){
                        Button proceed = (Button)findViewById(R.id.btnGetDirections);
                        proceed.setEnabled(true);
                    }
                }
                else{
                    toFilled = false;
                }
            }

            @Override
            public void onError(Status status) {}
        });
        //============== END TO LISTENER ==============================================//

        DownloadPriceTask task = new DownloadPriceTask(this);
        task.execute("STUB");
        Log.i("DirectionsCreation", "Getting price");
    }

    @Override
    public void handlePrice(String price) {
        EditText priceField = (EditText)findViewById(R.id.priceOfGas);
        priceField.setText(price);
    }

    public void getDirections(View view){
        GoogleDirection.withServerKey("AIzaSyDzFwlffLplZKK22mxdIqUBj5M_bUSwgZI")
                .from(from)
                .to(to)
                .alternativeRoute(true)
                .unit(Unit.METRIC)
                .avoid(AvoidType.FERRIES)
                .avoid(AvoidType.TOLLS)
                .execute(new DirectionCallback(){

                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if(direction.isOK()){
                            Log.i("Directions", rawBody);
                            Spinner units = (Spinner)findViewById(R.id.units);
                            String[] unitArray = getResources().getStringArray(R.array.units);
                            boolean impUnits = false; //in metric
                            if(units.getSelectedItem().toString().equals(unitArray[1])) {
                                impUnits = true;
                            }
                            Spinner vClass = (Spinner)findViewById(R.id.vClass);
                            String sVClass = vClass.getSelectedItem().toString();
                            EditText price = (EditText)findViewById(R.id.priceOfGas);
                            float gasPrice = Float.parseFloat(price.getText().toString());
                            sendDataToMap(direction, impUnits, sVClass, gasPrice);
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        Log.i("Direction failure", "ERROR");
                    }
                });
    }

    private void sendDataToMap(Direction direction, boolean isImperial, String vehicleClass, float gasPrice) {
        Intent i = new Intent(this, MainActivity.class);
        setResult(RESULT_OK, i);
        globalDirs = direction;
        i.putExtra(UNITS_BOOLEAN, isImperial);
        i.putExtra(VEHICLE_CLASS, vehicleClass);
        i.putExtra(FUEL_COST, gasPrice);
        finish();
    }

    class DownloadPriceTask extends AsyncTask<String, Void, String>{

        private String price;
        private PriceListener listener;
        private Exception exception = null;

        public DownloadPriceTask(PriceListener listener) {
            this.listener = listener;
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                Document doc = Jsoup.connect("https://www.gasbuddy.com/")
                        .maxBodySize(0)
                        .timeout(0)
                        .get();
                Elements prices = doc.getElementsByClass("gb-price-lg");
                String cheapestPrice = null;
                if (!prices.isEmpty()) {
                    Element cheapestPriceElement = prices.get(1);
                    cheapestPrice = cheapestPriceElement.html();
                    price = cheapestPrice;
                    Log.i("Element", cheapestPrice);
                }
                else {
                    price = "0";
                }
                Log.i("Price", price);
            } catch (IOException e) {
                e.printStackTrace();
                price = "0";
            }
            return price;
        }

        protected void onPostExecute(String result) {
            // handle any error
            if (exception != null) {
                exception.printStackTrace();
                return;
            }

            // show the data
            listener.handlePrice(result);
        }
    }
}
