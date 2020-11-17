package com.mmomeni.know_your_government;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    private TextView googleCivicLink;
    String gglURL = "https://developers.google.com/civic-information/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        googleCivicLink = findViewById(R.id.GoogleCivic);
        link();
    }


    private void link() {
        Linkify.addLinks(googleCivicLink, Linkify.ALL);
        googleCivicLink.setLinkTextColor(Color.WHITE);
    }
    public void googleCivic (View v){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(gglURL));
        startActivity(intent);
    }
}