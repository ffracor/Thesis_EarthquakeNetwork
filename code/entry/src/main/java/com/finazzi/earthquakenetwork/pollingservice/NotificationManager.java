package com.finazzi.earthquakenetwork.pollingservice;

import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.app.Context;
import ohos.event.intentagent.IntentAgent;
import ohos.event.intentagent.IntentAgentConstant;
import ohos.event.intentagent.IntentAgentHelper;
import ohos.event.intentagent.IntentAgentInfo;
import ohos.event.notification.NotificationHelper;
import ohos.event.notification.NotificationRequest;
import ohos.event.notification.NotificationSlot;
import ohos.rpc.RemoteException;
import ohos.vibrator.agent.VibratorAgent;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationManager
{
    private double waveSpeed = 4.5; //km/s
    private double timeMax = 5; //seconds
    private Geolocator geolocator;
    private Context context;                //context of EarthquakeResearch ability
    private NotificationRequest request;
    private static NotificationManager instance = null; //singleton pattern

    private NotificationManager(Context context)
    {
        this.context = context;

        request = new NotificationRequest(1001);
        request.setSlotId(setNotificationSlot());
    }

    public static NotificationManager getNotificationManager(Context context) //singleton pattern
    {
        if(instance == null)
            instance = new NotificationManager(context);

        return instance;
    }

    private int getTime() //compute time (in seconds) between earthquake start and the actual time
    {
        geolocator = Geolocator.getGeolocator(context);
        ZonedDateTime deviceTime = ZonedDateTime.now();
        ZonedDateTime serverTime =  deviceTime.withZoneSameInstant(ZoneId.of("Europe/Rome"));
        LocalDateTime actualTime = serverTime.toLocalDateTime();

        Duration duration = Duration.between(geolocator.getLastRelevantEarthquake().getEarthquakeTime(), actualTime);

        double time = geolocator.getDistance(geolocator.getLastRelevantEarthquake())/waveSpeed - duration.getSeconds();

        if(time > timeMax)
            return (int)time;
        else
            return -1;
    }

    private IntentAgent createIntentAgent() //create agent to open EarthquakeInformation page ability when notification is opened
    {
        Intent in = new Intent();
        Operation operation = new Intent.OperationBuilder()
                .withDeviceId("")
                .withBundleName("com.finazzi.earthquakenetwork")
                .withAbilityName("com.finazzi.earthquakenetwork.userinterface.EarthquakeInformation")
                .build();
        in.setOperation(operation);

        List<Intent> intentList = new ArrayList<>();
        intentList.add(in);

        List<IntentAgentConstant.Flags> flags = new ArrayList<>();
        flags.add(IntentAgentConstant.Flags.UPDATE_PRESENT_FLAG);

        IntentAgentInfo paramsInfo = new IntentAgentInfo(200, IntentAgentConstant.OperationType.START_ABILITY, flags, intentList, null);

        IntentAgent agent = IntentAgentHelper.getIntentAgent(context, paramsInfo);

        return agent;
    }

    private String setNotificationSlot() //set a notification slot
    {
        NotificationSlot slot;
        slot = new NotificationSlot("1", "123", NotificationSlot.LEVEL_HIGH); // Create a NotificationSlot object.
        slot.setDescription("alarm notification slot");
        slot.setEnableVibration(false); // disable vibration when a notification is received.

        try
        {
            NotificationHelper.addNotificationSlot(slot);
        }
        catch (RemoteException ex)
        {}

        return slot.getId();
    }

    private void setNotification(String title, String text) //publish a notification
    {
        geolocator = Geolocator.getGeolocator(context);

        NotificationRequest.NotificationNormalContent content = new NotificationRequest.NotificationNormalContent();
        content.setTitle(title).setText(text);
        NotificationRequest.NotificationContent notificationContent = new NotificationRequest.NotificationContent(content);
        request.setContent(notificationContent);

        try
        {
            NotificationHelper.publishNotification(request);
        }
        catch(RemoteException e)
        {}
    }

    public void launchNotification() //launch notification based on time left
    {
        request.setIntentAgent(createIntentAgent());    //set intent agent
        //vibrazione
        VibratorAgent vibratorAgent = new VibratorAgent();  //create a "manual" vibration of 3 seconds
        List<Integer> vibratorList = vibratorAgent.getVibratorIdList();

        if(!vibratorList.isEmpty())
        {
            int vibratorId = vibratorList.get(0);
            boolean vibrateEffectResult = vibratorAgent.startOnce(vibratorId, 3000);
        }

        int time = getTime();

        if(time == -1) //publis "earthquake appened" notification"
            setNotification("Earthquake appened", "tap for info");
        else
            //publish notification with a time left countdown
            for(int i = time; i >= 0; i--)
            {
                setNotification("EARTHQUAKE COMING IN", Integer.toString(i) + "s");

                try
                {
                    Thread.sleep(1000);
                }
                catch(InterruptedException e){}
            }
    }
}
