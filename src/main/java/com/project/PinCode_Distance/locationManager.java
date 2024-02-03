package com.project.PinCode_Distance;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;

import com.google.code.geocoder.model.GeocoderRequest;

import com.google.code.geocoder.Geocoder;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class locationManager implements Serializable {
    final Geocoder geocoder = new Geocoder();
    private static final String SERIALIZED_FILE_PATH = "C:\\Map\\loc.ser";
    private Map<String , DbManager.Pair<Double, Double> > locationMap= new HashMap<>();
    public locationManager() {
        try{
            BufferedReader br = new BufferedReader(new FileReader(SERIALIZED_FILE_PATH));
            if (!(br.readLine() == null)) {
                loadFromFile();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public double[] getCoordinates(String pincode) {
        if(locationMap.containsKey(pincode)){
            return new double[]{locationMap.get(pincode).getFirst() ,locationMap.get(pincode).getSecond() };
        }
        GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(pincode).getGeocoderRequest();
        GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);
        List results = geocoderResponse.getResults();
        double latitude = results.get(0).getGeometry().getLocation().getLat().floatValue();
        double longitude = results.get(0).getGeometry().getLocation().getLng().floatValue();
        DbManager.Pair<Double , Double> latLong = new DbManager.Pair<>(latitude , longitude);
        locationMap.put(pincode . latLong);
        saveToFile();
        return new double[]{latitude , longitude};
    }
    public void saveToFile() {
        try (FileOutputStream fileOut = new FileOutputStream(SERIALIZED_FILE_PATH);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DbManager loadFromFile() {
        try (FileInputStream fileIn = new FileInputStream(SERIALIZED_FILE_PATH);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            return (DbManager) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Haversine formula to calculate distance between two coordinates
    public static double calculateDistanceHaversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Radius of the Earth in kilometers

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        String apiKey = "";
        String urlString = "https://maps.googleapis.com/maps/api/distancematrix/json"
                + "?origins=" + lat1 + "," + lon1
                + "&destinations=" + lat2 + "," + lon2
                + "&key=" + apiKey;

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse JSON response
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(response.toString());
                String distanceText = (String) ((JSONObject) ((JSONObject) ((JSONObject) jsonObject.get("rows"))
                        .get(0)).get("elements"))
                        .get("distance")
                        .toString();


                double distance = Double.parseDouble(distanceText) / 1000;
                return distance;
            } else {

                return calculateDistanceHaversine(lat1 , lon1 , lat2 , lon2);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return calculateDistanceHaversine(lat1 , lon1 , lat2 , lon2);
        }
    }
}

