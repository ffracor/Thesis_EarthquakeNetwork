package com.finazzi.earthquakenetwork.pollingservice;

import ohos.app.Context;
import ohos.location.Location;
import ohos.location.Locator;
import ohos.location.LocatorCallback;
import ohos.location.RequestParam;

public class Geolocator
{
    private Locator locator;                            //location objects
    private RequestParam requestParam;
    private MyLocatorCallback locatorCallback;

    private double deviceLatitude;                      //device positon
    private double deviceLongitude;
    private double maxDistance = 300; //km

    private Earthquake lastRelevantEarthquake;          //last earthquake appened near the device
    private static Geolocator instance = null;          //singleton pattern

    private Context context;                            //context of EarthquakeResearch ability

    private Geolocator(Context context)
    {
        this.context = context;

        //starting localization
        locator = new Locator(context);
        requestParam = new RequestParam(RequestParam.PRIORITY_LOW_POWER,0,0);
        locatorCallback = new MyLocatorCallback();

        locator.startLocating(requestParam, locatorCallback);
    }

    public static Geolocator getGeolocator(Context context) //singleton pattern
    {
        if(instance == null)
            instance = new Geolocator(context);

        return instance;
    }

    public static Geolocator getGeolocator() throws NullPointerException //to obtein instance out of EarthquakeResearch context
    {                                                                    //nullpointerexception if the object doesn't exist
        if(instance == null) throw new NullPointerException();
        else return instance;
    }

    public Earthquake getLastRelevantEarthquake()
    {
        return lastRelevantEarthquake;
    } //get method

    public double getDistance(Earthquake earthquake) //compute distance between device and earthquake epicenter
    {
        //distance formula *111km/°
        return 111*Math.sqrt(Math.pow((deviceLatitude - earthquake.getLatitude()), 2) + Math.pow((deviceLongitude - earthquake.getLongitude()), 2));
    }

    public void geolocate(Earthquake earthquake) //start NotificationManager the object earthquake is within 300km of the device
    {
        double distance = getDistance(earthquake);
        if(distance <= maxDistance)
        {
            lastRelevantEarthquake = earthquake;
            NotificationManager notificationManager = NotificationManager.getNotificationManager(context);
            notificationManager.launchNotification();
        }
    }

    //Inner class
    public class MyLocatorCallback implements LocatorCallback
    {
        @Override
        public void onLocationReport(Location location) //save device position obteined by locator object
        {
            deviceLatitude = location.getLatitude();
            deviceLongitude = location.getLongitude();
        }

        @Override
        public void onStatusChanged(int type)
        {
        }

        @Override
        public void onErrorReport(int type)
        {
        }
    }

}
