package com.finazzi.earthquakenetwork;

import com.finazzi.earthquakenetwork.slice.HomeSlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;

import java.util.ArrayList;
import java.util.List;

public class MainAbility extends Ability
{
    @Override
    public void onStart(Intent intent)
    {
        super.onStart(intent);
        super.setMainRoute(HomeSlice.class.getName());

        reqPermission();
        startEarthquakeResearch();
    }

    public void reqPermission()
    {
        final String[] permission = {"ohos.permission.LOCATION","ohos.permission.LOCATION_IN_BACKGROUND","ohos.permission.KEEP_BACKGROUND_RUNNING","ohos.permission.INTERNET","ohos.permission.GET_NETWORK_INFO", "ohos.permission.VIBRATE"};
        final List<String> permissionList = new ArrayList<>();
        for (String s : permission)
        {
            if (verifySelfPermission(s) != 0 && canRequestPermission(s))
            {
                permissionList.add(s);
            }
        }
        if (permissionList.size() > 0)
        {
            requestPermissionsFromUser(permissionList.toArray(new String[0]), 0);
        }
    }

    public void startEarthquakeResearch()
    {
        Intent in = new Intent();
        Operation operation = new Intent.OperationBuilder()
                .withDeviceId("")
                .withBundleName("com.finazzi.earthquakenetwork")
                .withAbilityName("com.finazzi.earthquakenetwork.pollingservice.EarthquakeResearch")
                .build();
        in.setOperation(operation);
        startAbility(in);
    }
}
