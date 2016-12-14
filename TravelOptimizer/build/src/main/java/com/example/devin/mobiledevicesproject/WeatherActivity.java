package com.example.devin.mobiledevicesproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class WeatherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        WebView webView = new WebView(this);
        setContentView(webView);
        webView.loadUrl("https://weather.gc.ca/city/pages/on-143_metric_e.html");
    }
}
