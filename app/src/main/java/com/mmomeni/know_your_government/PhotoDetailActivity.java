package com.mmomeni.know_your_government;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class PhotoDetailActivity extends AppCompatActivity {
    private Official official;
    private TextView location;
    private TextView officeName;
    private TextView name;
    private ImageView mainPhoto;
    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        Intent intent = getIntent();
        official =(Official) intent.getSerializableExtra("objectFile");

        location = findViewById(R.id.location3);
        officeName = findViewById(R.id.OfficeName3);
        name = findViewById(R.id.Name3);
        mainPhoto = findViewById(R.id.mainPhoto2);
        logo = findViewById(R.id.Logo2);




        if (official.getParty().equals("Democratic") || official.getParty().equals("Democratic Party")) {
            logo.setImageResource(R.drawable.dem_logo);
            getWindow().getDecorView().setBackgroundColor(Color.BLUE);
        } else if (official.getParty().equals("Republican") || official.getParty().equals("Republican Party")) {
            logo.setImageResource(R.drawable.rep_logo);
            getWindow().getDecorView().setBackgroundColor(Color.RED);
        } else {
            logo.setImageResource(0); //to set an image empty
            getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        }
        if (doNetCheck() == false) {
            mainPhoto.setImageResource(R.drawable.brokenimage);
        }
        else {
            Picasso.get().load(official.getPhotoURL())
                    .error(R.drawable.missing)
                    .placeholder(R.drawable.placeholder)
                    .into(mainPhoto);
        }

        location.setText(official.getLocation());
        officeName.setText(official.getOfficeName());
        name.setText(official.getName());


    }

    public void logoClick(View v){
        if (official.getParty().equals("Democratic") || official.getParty().equals("Democratic Party")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://democrats.org/"));
            startActivity(intent);
        } else if (official.getParty().equals("Republican") || official.getParty().equals("Republican Party")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gop.com/"));
            startActivity(intent);
        } else {
        }
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
}