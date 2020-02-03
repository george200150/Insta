package com.parse.starter;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DownloadPasswordTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {
        Log.i("URL", urls[0]);
        HttpsURLConnection urlConnection = null;

        try {
            StringBuilder result = new StringBuilder();
            URL url = new URL(urls[0]);
            urlConnection = (HttpsURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);
            int data = reader.read();

            while(data != -1){
                char current = (char) data;
                result.append(current);//more efficient -> the String itself will rebuild each iteration
                data = reader.read();
            }
            return result.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;//failed
    }
}
