package com.example.geobird;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LocationParser {

    public static Map<String, double[]> parseLocations(Context context) {
        Map<String, double[]> locationMap = new HashMap<>();
        try {

            InputStream inputStream = context.getAssets().open("se.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            String jsonString = stringBuilder.toString();
            JSONObject obj = new JSONObject(jsonString);
            JSONArray arr = obj.getJSONArray("cities");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject cityObject = arr.getJSONObject(i);
                    String cityName = cityObject.getString("city");
                    double cityLat = cityObject.getDouble("lat");
                    double cityLong = cityObject.getDouble("lng");
                    locationMap.put(cityName, new double[]{cityLat, cityLong});

                }
            }
        catch (IOException | JSONException e) {
            Log.d(TAG, "failed to parse");
        }
        return locationMap;
    }

}
