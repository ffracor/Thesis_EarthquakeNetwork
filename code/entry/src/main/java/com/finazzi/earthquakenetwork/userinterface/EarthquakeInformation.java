package com.finazzi.earthquakenetwork.userinterface;

import com.finazzi.earthquakenetwork.slice.EarthquakeInformationSlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class EarthquakeInformation extends Ability
{
    @Override
    public void onStart(Intent intent)
    {
        super.onStart(intent);
        super.setMainRoute(EarthquakeInformationSlice.class.getName());
        setSwipeToDismiss(true);
    }
}
