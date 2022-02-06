package com.finazzi.earthquakenetwork.slice;

import com.finazzi.earthquakenetwork.ResourceTable;
import com.finazzi.earthquakenetwork.pollingservice.Geolocator;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.agp.components.Text;
import ohos.agp.components.element.ShapeElement;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class HomeSlice extends AbilitySlice
{
    private Button btn1;
    private Text text;
    private Geolocator geolocator;

    @Override
    public void onStart(Intent intent)
    {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_menu);

        btn1 = (Button) findComponentById(ResourceTable.Id_button_menu1);

        btn1.setClickedListener(new Component.ClickedListener()
        {
            @Override
            public void onClick(Component component)
            {
                startEarthquakeInformation();
            }
        });
        text = (Text)findComponentById(ResourceTable.Id_text_menu);
    }

    @Override
    public void onActive()
    {
        super.onActive();
        geolocator = Geolocator.getGeolocator();

        double time;

        ZonedDateTime deviceTime = ZonedDateTime.now();
        ZonedDateTime serverTime =  deviceTime.withZoneSameInstant(ZoneId.of("Europe/Rome"));
        LocalDateTime actualTime = serverTime.toLocalDateTime();

        try
        {
            Duration duration = Duration.between(geolocator.getLastRelevantEarthquake().getEarthquakeTime(), actualTime);
            time = duration.getSeconds();
        }
        catch(NullPointerException e)
        {
            time = 21601;
        }

        ShapeElement red = new ShapeElement(getContext(), ResourceTable.Graphic_red);
        ShapeElement green = new ShapeElement(getContext(), ResourceTable.Graphic_green);

        if(time < 21600)
        {
            btn1.setBackground(red);
            btn1.setText("EARTHQUAKE INFO");
            btn1.setEnabled(true);
            text.setText("Push the red button to have information about last earthquake in your area");
        }
        else
        {
            btn1.setBackground(green);
            btn1.setText("NO EARTHQUAKE AROUND YOU");
            btn1.setEnabled(false);
            text.setText("you will be notified if there is an earthquake nearby");
        };
    }

    public void startEarthquakeInformation()
    {
        Intent in = new Intent();
        Operation operation = new Intent.OperationBuilder()
                .withDeviceId("")
                .withBundleName("com.finazzi.earthquakenetwork")
                .withAbilityName("com.finazzi.earthquakenetwork.userinterface.EarthquakeInformation")
                .build();
        in.setOperation(operation);
        startAbility(in);
    }
}
