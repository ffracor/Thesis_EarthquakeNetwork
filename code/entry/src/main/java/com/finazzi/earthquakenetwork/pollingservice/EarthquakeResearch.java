package com.finazzi.earthquakenetwork.pollingservice;

import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.app.dispatcher.TaskDispatcher;
import ohos.app.dispatcher.task.Revocable;
import ohos.app.dispatcher.task.TaskPriority;
import ohos.event.notification.NotificationRequest;

public class EarthquakeResearch extends Ability
{
    @Override
    public void onStart(Intent intent)
    {
        setBackground();
        //starting new thread which do the server request
        TaskDispatcher globalTaskDispatcher = getGlobalTaskDispatcher(TaskPriority.DEFAULT);
        Revocable revocable = globalTaskDispatcher.asyncDispatch(new Polling(this, globalTaskDispatcher));
    }

    private void setBackground()    //set background running of the service
    {
        NotificationRequest request = new NotificationRequest(1005);
        NotificationRequest.NotificationNormalContent content = new NotificationRequest.NotificationNormalContent();
        content.setTitle("Earthquake Network").setText("");
        NotificationRequest.NotificationContent notificationContent = new NotificationRequest.NotificationContent(content);
        request.setContent(notificationContent);

        keepBackgroundRunning(1005, request);
    }
}
