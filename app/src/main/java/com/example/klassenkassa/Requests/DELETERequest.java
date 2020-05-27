package com.example.klassenkassa.Requests;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DELETERequest extends AsyncTask<String, Integer, String> {


    private String URL;
    private String jsonResponse="";

    public DELETERequest(String URL) {
        this.URL = URL;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(URL).openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Content-Type", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                jsonResponse = readResponseStream(reader);
            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                jsonResponse = readResponseStream(reader);
            }
        } catch (IOException e) {
        }
        return jsonResponse;
    }

    private String readResponseStream(BufferedReader reader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String line = "";
        while ( (line=reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

    public String getJsonResponse()
    {
        return jsonResponse;
    }
}
