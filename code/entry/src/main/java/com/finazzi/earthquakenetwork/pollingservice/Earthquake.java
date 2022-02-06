package com.finazzi.earthquakenetwork.pollingservice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Earthquake
{
    private int Id;                 //Earthquake informations
    private double latitude;
    private double longitude;
    private LocalDateTime dateTime;
    private String location;

    public Earthquake(String line) throws JSONException //Parsing the line obteined from the server
    {
        JSONArray jArr = new JSONArray(line);
        JSONObject jObj = jArr.getJSONObject(0);

        Id = jObj.getInt("ID");

        latitude = jObj.getDouble("latitude");
        longitude = jObj.getDouble("longitude");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        dateTime = LocalDateTime.parse(jObj.getString("date"), formatter);

        location = jObj.getString("location");
    }

    //get methods
    public int getId() {return Id;}
    public double getLatitude() {return latitude;}
    public double getLongitude() {return longitude;}
    public String getLocation() {return location;}
    public LocalDateTime getEarthquakeTime() {return dateTime;}
}
