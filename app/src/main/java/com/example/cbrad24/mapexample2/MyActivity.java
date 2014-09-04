package com.example.cbrad24.mapexample2;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MyActivity extends Activity {
    GPS gps;
    GoogleMap map;
    TextView loctxt;
    Marker patient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        gps = new GPS(MyActivity.this);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        loctxt = (TextView) findViewById(R.id.header);

        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            LatLng position = new LatLng(latitude, longitude);
            patient = map.addMarker(new MarkerOptions().position(position).title("Patient"));
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(position, 16);
            map.animateCamera( update );
            loctxt.setText( "Latitude:" +  latitude  + ", Longitude:"+ longitude );
        }
    }

    public void locate_onClick(View v) {
        gps.getLocation();
        double latitude = gps.getLatitude();
        double longitude = gps.getLongitude();
        LatLng position = new LatLng(latitude, longitude);
        patient.setPosition(position);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(position, 16);
        map.animateCamera( update );
        loctxt.setText( "Latitude:" +  latitude  + ", Longitude:"+ longitude );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}