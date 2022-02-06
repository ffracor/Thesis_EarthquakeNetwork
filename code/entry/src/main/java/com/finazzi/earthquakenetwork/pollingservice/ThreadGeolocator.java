package com.finazzi.earthquakenetwork.pollingservice;

import ohos.app.Context;

public class ThreadGeolocator implements Runnable
{
    private Geolocator geolocator;
    private Earthquake earthquake;
    private Context context;

    public ThreadGeolocator(Earthquake earthquake, Context context)
    {
        this.earthquake = earthquake;
        geolocator = Geolocator.getGeolocator(context);
        this.context = context;
    }

    @Override
    public void run()
    {
        geolocator.geolocate(earthquake);
    }
}
