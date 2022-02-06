package com.finazzi.earthquakenetwork.pollingservice;

import ohos.app.Context;
import ohos.app.dispatcher.TaskDispatcher;
import ohos.app.dispatcher.task.Revocable;
import ohos.net.NetHandle;
import ohos.net.NetManager;
import ohos.net.NetStatusCallback;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class Polling implements Runnable
{
    private String line = null;
    private final String link = "https://www.earthquakenetwork.it/mysql/distquake_huawei_smartwatch.php";
    private Earthquake lastEarthquake = null;
    HttpURLConnection connection = null;
    private Earthquake earthquake = null;
    private Context context;
    private TaskDispatcher globalTaskDispatcher;

    public Polling(Context context, TaskDispatcher globalTaskDispatcher)
    {
        this.context = context;
        this.globalTaskDispatcher = globalTaskDispatcher;
    }

    @Override
    public void run()
    {
        while(true)
        {
            //request to server
            try
            {
                int responseCode = setConnection(); //set connection

                if(responseCode == -1){}    //do nothing, there is no connection
                else if (responseCode == 200)
                    getLine(connection.getInputStream());
                else
                    getLine(connection.getErrorStream());
            }
            catch(IOException e) {}
            finally
            {
                connection.disconnect();
            }

            try     //watch if earthquake is a new earthquake
            {
                earthquake = new Earthquake(line);
                if(lastEarthquake != null && earthquake.getId() != lastEarthquake.getId())
                {
                    //starting geolocation system
                    Revocable revocable = globalTaskDispatcher.asyncDispatch(new ThreadGeolocator(earthquake, context));
                }
            }
            catch(JSONException e) {}

            try     //wait for 15 seconds
            {
                Thread.sleep(15000);
            }
            catch(InterruptedException e) {}

            lastEarthquake = earthquake;
        }
    }

    private int setConnection() throws IOException  //establish connection
    {
        NetManager netManager = NetManager.getInstance(context);
        if(!netManager.hasDefaultNet())     //verifing if there is a network
            return -1;


        NetHandle netHandle = netManager.getDefaultNet();

        NetStatusCallback callback = new NetStatusCallback()
        {
            // Override the callback for network state changes.
        };
        netManager.addDefaultNetStatusCallback(callback);

        URL url = new URL(link);
        URLConnection urlConnection = netHandle.openConnection(url, java.net.Proxy.NO_PROXY);

        if(urlConnection instanceof HttpURLConnection)
            connection = (HttpURLConnection) urlConnection;

        connection.setRequestMethod("GET");
        connection.connect();

        return connection.getResponseCode();

    }

    private void getLine(InputStream inputStream) throws IOException //get data stream
    {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        line = bufferedReader.readLine();
        bufferedReader.close();
    }
}
