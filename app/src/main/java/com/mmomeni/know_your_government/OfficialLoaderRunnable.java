package com.mmomeni.know_your_government;

import android.accessibilityservice.GestureDescription;
import android.net.Uri;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class OfficialLoaderRunnable implements Runnable {

    private static final String TAG = "CountryLoaderRunnable";
    private MainActivity mainActivity;
    private String address;
    private static final String DATA_URL = "https://www.googleapis.com/civicinfo/v2/representatives?key=AIzaSyBsCLmm6p1sk69lEXeBrQ3DnpmcoSSk4QU&address=";

    OfficialLoaderRunnable(MainActivity mainActivity, String s) {
        this.mainActivity = mainActivity;
        this.address = s;
    }


    @Override
    public void run() {

        Uri dataUri = Uri.parse(DATA_URL + address);
        String urlToUse = dataUri.toString();
        Log.d(TAG, "run: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "run: HTTP ResponseCode NOT OK: " + conn.getResponseCode());
                handleResults(null);
                return;
            }

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "run: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "run: ", e);
            handleResults(null);
            return;
        }

        handleResults(sb.toString());

    }

    private void handleResults(String s) {

        if (s == null) {
            Log.d(TAG, "handleResults: Failure in data download");
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.downloadFailed();
                }
            });
            return;
        }



        final List<Official> officialList = parseJSON(s);
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (officialList != null)
                mainActivity.updateData1(officialList);
            }
        });
    }

    private List<Official> parseJSON(String s) {

        List<Official> officialList = new ArrayList<>();

        try {
            JSONObject jObjMain = new JSONObject(s);

            JSONObject jNormalInput = jObjMain.getJSONObject("normalizedInput");
            JSONArray jArrayOffices = jObjMain.getJSONArray("offices");
            JSONArray jArrayOfficials = jObjMain.getJSONArray("officials");

            String location = jNormalInput.getString("city")+", "+jNormalInput.getString("state")+" "+jNormalInput.getString("zip");

            int length = jArrayOffices.length();

            for (int i = 0; i<length; i++){
                JSONObject jObj = jArrayOffices.getJSONObject(i);
                String officeName = jObj.getString("name");

                JSONArray indicesStr = jObj.getJSONArray("officialIndices");
                //ArrayList<Integer> indices = new ArrayList<>();

                for (int j = 0; j<indicesStr.length(); j++){
                    int pos = Integer.parseInt(indicesStr.getString(j));
                    JSONObject jOfficial = jArrayOfficials.getJSONObject(pos);

                    String name = jOfficial.getString("name");

                    String address = "";
                    if (jOfficial.has("address")) {

                        JSONArray jAddresses = jOfficial.getJSONArray("address");
                        JSONObject jAddress = jAddresses.getJSONObject(0);

                        if (jAddress.has("line1")) address += jAddress.getString("line1") + '\n';
                        if (jAddress.has("line2")) address += jAddress.getString("line2") + '\n';
                        if (jAddress.has("line3")) address += jAddress.getString("line3") + '\n';
                        if (jAddress.has("city")) address += jAddress.getString("city") + ", ";
                        if (jAddress.has("state")) address += jAddress.getString("state") + ' ';
                        if (jAddress.has("zip")) address += jAddress.getString("zip");
                    }

                    String party = "";
                    String phone = "";
                    String url = "";
                    String email = "";
                    String photoURL = "";

                    if (jOfficial.has("party")) party = jOfficial.getString("party");
                    if (jOfficial.has("phones")) phone = jOfficial.getJSONArray("phones").getString(0);
                    if (jOfficial.has("urls")) url = jOfficial.getJSONArray("urls").getString(0);
                    if (jOfficial.has("emails")) email = jOfficial.getJSONArray("emails").getString(0);
                    if (jOfficial.has("photoUrl")) photoURL = jOfficial.getString("photoUrl");

                    Channel channel = new Channel();

                    if (jOfficial.has("channels")){


                        JSONArray jChannels = jOfficial.getJSONArray("channels");

                        for (int k = 0; k < jChannels.length(); k++){
                            JSONObject jChannel = jChannels.getJSONObject(k);
                            if (jChannel.getString("type").equals("Facebook")) channel.setFacebookId(jChannel.getString("id"));
                            if (jChannel.getString("type").equals("Twitter")) channel.setTwitterId(jChannel.getString("id"));
                            if (jChannel.getString("type").equals("YouTube")) channel.setYoutubeId(jChannel.getString("id"));
                        }

                    }
                    officialList.add(0 ,new Official(location, officeName, name, address, party, phone, url, email, photoURL, channel));

                }

            }
            //return officialList;


        }  catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return officialList;
    }


}
