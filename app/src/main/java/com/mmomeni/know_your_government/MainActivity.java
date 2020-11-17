package com.mmomeni.know_your_government;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements View.OnClickListener { //in order to make methods listeners we need
    //to import these View.OnClickListener, View.OnLongClickListener

    private final List<Official> officialList = new ArrayList<>();
    //private final List<Official> rawStockList = new ArrayList<>();//we should have this final in order to make changes to noteList in the other parts of code, otherwise
    //adapter always draws the old list and not the new one
    private RecyclerView recyclerView;
    private final String TITLEBAR = "Know Your Government";
    private TextView location;

    private static int MY_LOCATION_REQUEST_CODE_ID = 111;
    private LocationManager locationManager;
    private Criteria criteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //this line also is an inflator
        location = findViewById(R.id.location);


        if (doNetCheck()){
            locatorHelper();
        } else {
            noNetworkDialog(null);
            location.setText("No Data For Location");
        }
        updateTitleNoteCount();
    }

    public void locatorHelper(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        criteria = new Criteria();

        // use gps for location
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        // use network for location
        //criteria.setPowerRequirement(Criteria.POWER_LOW);
        //criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);

        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    MY_LOCATION_REQUEST_CODE_ID);
        } else {
            setLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull
            String[] permissions, @NonNull
                    int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_LOCATION_REQUEST_CODE_ID) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PERMISSION_GRANTED) {
                setLocation();
                return;
            }
        }

    }

    @SuppressLint("MissingPermission")
    private void setLocation() {

        String bestProvider = locationManager.getBestProvider(criteria, true);


        Location currentLocation = null;
        if (bestProvider != null) {
            currentLocation = locationManager.getLastKnownLocation(bestProvider);
        }
        if (currentLocation != null) {

            Geocoder geocoder = new Geocoder(this);

            try {
                List<Address> adrs = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                String zipcode = adrs.get(0).getPostalCode() == null ? "" : adrs.get(0).getPostalCode();
                getDataThread(zipcode);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

        }


    }

    public void recheckLocation(View v) {
        Toast.makeText(this, "Rechecking Location", Toast.LENGTH_SHORT).show();
        setLocation();
    }

    private boolean doNetCheck() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void updateTitleNoteCount() {
        setTitle(TITLEBAR + " (" + officialList.size() + ")");
    }

    public void updateData1(List<Official> cList) {
        officialList.clear();
        officialList.addAll(cList);
        Collections.reverse(officialList);
        location.setText(officialList.get(0).getLocation());
        updateRecycler();
    }

    public void downloadFailed() {
        officialList.clear();
        Toast.makeText(this, "Download failed!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){ //this is the only code we have for menues
        //the menu we pass here is the actual menu we have made in layout
        //inflating means to build live objects
        getMenuInflater().inflate(R.menu.first_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_add:
                if(doNetCheck() == false){
                    noNetworkDialog(null);
                    location.setText("No Data For Location");
                }
                else {
                    firstDialog(null);
                }
                return true;
            case R.id.menu_about:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void firstDialog(View v) {
        // Single input value dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Create an edittext and set it to be the builder's view
        final EditText et = new EditText(this); //these 3 lines are for building a textview
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setGravity(Gravity.CENTER_HORIZONTAL);
        //et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        builder.setView(et);
        builder.setPositiveButton("FIND", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getDataThread(et.getText().toString());
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(MainActivity.this, "You changed your mind!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setMessage("Please enter zipcode/city and state");
        builder.setTitle("Locator");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void getDataThread(String input){
        OfficialLoaderRunnable officialLoaderRunnable = new OfficialLoaderRunnable(this, input);
        new Thread(officialLoaderRunnable).start();
        updateRecycler();
    }

    public void noNetworkDialog(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Cannot load the data without a Network connection.");
        builder.setTitle("No Network Connection");
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void updateRecycler(){
        recyclerView = findViewById(R.id.recycler);
        OfficialAdapter vh = new OfficialAdapter(officialList, this);
        recyclerView.setAdapter(vh);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        updateTitleNoteCount();
    }

    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        Official m = officialList.get(pos);
        Intent data = new Intent(this, OfficialActivity.class); // this is explicit intent
        data.putExtra("official",m);
        startActivity(data);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        updateRecycler();
        super.onResume();
    }
}