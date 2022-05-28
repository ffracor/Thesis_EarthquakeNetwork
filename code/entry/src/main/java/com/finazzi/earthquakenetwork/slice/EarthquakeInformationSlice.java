package com.finazzi.earthquakenetwork.slice;

import com.finazzi.earthquakenetwork.ResourceTable;
import com.finazzi.earthquakenetwork.pollingservice.Geolocator;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Text;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class EarthquakeInformationSlice extends AbilitySlice
{
    private Text text;
    private Geolocator geolocator;

    @Override
    public void onStart(Intent intent)
    {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_alarm_layout);

        text = (Text)findComponentById(ResourceTable.Id_text_alarm);
    }

    public void onActive()
    {
        super.onActive();
        geolocator = null;
        try
        {
            geolocator = Geolocator.getGeolocator();
        }
        catch(Exception e) {geolocator = null;}

        ZonedDateTime zonedDateTime = geolocator.getLastRelevantEarthquake().getEarthquakeTime().atZone(ZoneId.of("Europe/Rome"));
        LocalDateTime actualTime = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();

        DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
        text.setText("\nLocation:\n" + geolocator.getLastRelevantEarthquake().getLocation() + "\n\nDate:\n" + actualTime.getDayOfMonth() + "-" + actualTime.getMonthValue() + "-" + actualTime.getYear() + "\n" +dtf.format(actualTime));
    }
}
