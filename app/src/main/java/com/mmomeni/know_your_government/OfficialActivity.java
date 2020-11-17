package com.mmomeni.know_your_government;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class OfficialActivity extends AppCompatActivity {
    private TextView location;
    private TextView officeName;
    private TextView name;
    private TextView party;
    private ImageView mainPhoto;
    private ImageView logo;
    private TextView addressDetail;
    private TextView phoneDetail;
    private TextView urlDetail;
    private TextView emailDetail;
    private Official official;
    private ImageButton facebookButton;
    private ImageButton twitterButton;
    private ImageButton youtubeButton;
    private TextView address;
    private TextView phone;
    private TextView url;
    private TextView email;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);

        location = findViewById(R.id.location2);
        officeName = findViewById(R.id.OfficeName2);
        name = findViewById(R.id.Name2);
        party = findViewById(R.id.Party2);
        mainPhoto = findViewById(R.id.mainPhoto);
        logo = findViewById(R.id.logo);
        addressDetail = findViewById(R.id.AddressDetail);
        phoneDetail = findViewById(R.id.PhoneDetail);
        urlDetail = findViewById(R.id.UrlDetail);
        facebookButton = findViewById(R.id.FacebookBotton);
        twitterButton = findViewById(R.id.TwitterBotton);
        youtubeButton = findViewById(R.id.YoutubeBotton);
        emailDetail = findViewById(R.id.EmailDetail1);
        address = findViewById(R.id.Address);
        url = findViewById(R.id.Url);
        phone = findViewById(R.id.Phone);
        email = findViewById(R.id.Email);

        addressDetail.setMovementMethod(new ScrollingMovementMethod());
        phoneDetail.setMovementMethod(new ScrollingMovementMethod());
        urlDetail.setMovementMethod(new ScrollingMovementMethod());


        Intent intent = getIntent();
        official = (Official) intent.getSerializableExtra("official");
        layoutHandler(official);
    }

    public void layoutHandler(Official official) {
        location.setText(official.getLocation());
        officeName.setText(official.getOfficeName());
        name.setText(official.getName());
        party.setText("(" + official.getParty() + ")");

        if (official.getParty().equals("Democratic") || official.getParty().equals("Democratic Party")) {
            logo.setImageResource(R.drawable.dem_logo);
            getWindow().getDecorView().setBackgroundColor(Color.BLUE);
            facebookButton.setBackgroundColor(Color.BLUE);
            twitterButton.setBackgroundColor(Color.BLUE);
            youtubeButton.setBackgroundColor(Color.BLUE);
        } else if (official.getParty().equals("Republican") || official.getParty().equals("Republican Party")) {
            logo.setImageResource(R.drawable.rep_logo);
            getWindow().getDecorView().setBackgroundColor(Color.RED);
            facebookButton.setBackgroundColor(Color.RED);
            twitterButton.setBackgroundColor(Color.RED);
            youtubeButton.setBackgroundColor(Color.RED);
        } else {
            logo.setImageResource(0); //to set an image empty
            getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        }

        if (doNetCheck() == false) {
            mainPhoto.setImageResource(R.drawable.brokenimage);
        }
        else if (!official.getPhotoURL().isEmpty()) {
            Picasso.get().load(official.getPhotoURL())
                    .error(R.drawable.missing)
                    .placeholder(R.drawable.placeholder)
                    .into(mainPhoto);
        }

        if (official.getAddress() != null && !official.getAddress().isEmpty()) {
            addressDetail.setText(official.getAddress());
            Linkify.addLinks(addressDetail, Linkify.ALL);
        }
        else {
            address.setVisibility(View.INVISIBLE);
            addressDetail.setVisibility(View.INVISIBLE);
        }
        if (official.getPhone() != null && !official.getPhone().isEmpty()) {
            phoneDetail.setText(official.getPhone());
            Linkify.addLinks(phoneDetail, Linkify.ALL);
        }
        else {
            phone.setVisibility(View.INVISIBLE);
            phoneDetail.setVisibility(View.INVISIBLE);
        }
        if (official.getURL() != null && !official.getURL().isEmpty()) {
            urlDetail.setText(official.getURL());
            Linkify.addLinks(urlDetail, Linkify.ALL);
        }
        else {
            url.setVisibility(View.INVISIBLE);
            urlDetail.setVisibility(View.INVISIBLE);
        }

        if (official.getEmail() != null && !official.getEmail().isEmpty()) {
            emailDetail.setText(official.getEmail());
            Linkify.addLinks(emailDetail, Linkify.ALL);
        }
        else {
            email.setVisibility(View.INVISIBLE);
            emailDetail.setVisibility(View.INVISIBLE);
        }


        if (official.getChannel() == null) {
            facebookButton.setVisibility(View.INVISIBLE);
            twitterButton.setVisibility(View.INVISIBLE);
            youtubeButton.setVisibility(View.INVISIBLE);

        } else {
            if (official.getChannel().getFacebookId() == null || official.getChannel().getFacebookId().equals(""))
                facebookButton.setVisibility(View.INVISIBLE);
            if (official.getChannel().getTwitterId() == null || official.getChannel().getTwitterId().equals(""))
                twitterButton.setVisibility(View.INVISIBLE);
            if (official.getChannel().getYoutubeId() == null || official.getChannel().getYoutubeId().equals(""))
                youtubeButton.setVisibility(View.INVISIBLE);
        }
    }

    public void photoDetailActivity(View v) {
        if (official.getPhotoURL() != null && !official.getPhotoURL().equals("")){
            Intent intent = new Intent(this, PhotoDetailActivity.class);
            intent.putExtra("objectFile",official);
            startActivity(intent);
        }
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


    public void twitterClicked(View v) {
        Intent intent = null;
        String name = official.getChannel().getTwitterId();
        try {
            // get the Twitter app if possible
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
        }
        startActivity(intent);
    }

    public void facebookClicked(View v) {
        String FACEBOOK_URL = "https://www.facebook.com/" + official.getChannel().getFacebookId();
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                urlToUse = "fb://page/" + official.getChannel().getFacebookId();
            }
        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL; //normal web url
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }


    public void youTubeClicked(View v) {
        String name = official.getChannel().getYoutubeId();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + name)));

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