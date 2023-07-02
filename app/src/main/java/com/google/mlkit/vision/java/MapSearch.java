package com.google.mlkit.vision.demo.java;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import com.google.mlkit.vision.demo.R;


public class MapSearch {

    private static final String API_KEY = "AIzaSyBzuQUp8y_hvcPcgYZVV3hByDnIsIigbd4";
    private Context context;
    public String name ;
    public String address;
    public float reviews;

    public MapSearch(Context context) {
        this.context = context;
    }

    public void searchPlaces(String query) {
        GeoApiContext geoContext = new GeoApiContext.Builder()
                .apiKey(API_KEY)
                .build();

        try {
            PlacesSearchResponse response = PlacesApi.textSearchQuery(geoContext, query).await();

            for (PlacesSearchResult result : response.results) {
                System.out.println("Name: " + result.name);
                System.out.println("Address: " + result.formattedAddress);
                System.out.println("Rating: " + result.rating);
                System.out.println("-----");
                name=result.name;
                address=result.formattedAddress;
                reviews=result.rating;


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        showSearchResultsPopup(name, address, reviews);
    }

    private void showSearchResultsPopup(String name, String formattedAddress, float rating) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialogTheme);
        builder.setTitle("Search Results");
        builder.setMessage("Name: " + name + "\nAddress: " + formattedAddress + "\nRating: " + rating);
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}


