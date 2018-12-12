/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.Nflicks.firabase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.RemoteViews;

import com.Nflicks.ChannelDescriptionActivity;
import com.Nflicks.GlobalElements;
import com.Nflicks.MainActivity;
import com.Nflicks.NotificationActivity;
import com.Nflicks.R;
import com.Nflicks.netUtils.MyPreferences;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    MyPreferences myPreferences;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        try {

            // todo 0=View notihing
            // todo 1=Follow Request : channel_id
            // todo 2=>Following : channel_id
            // todo 3=>New Flick Available : channel_id
            // todo 4=>System : hide
            // todo 5=>Follow Request Accepted : channel_id

            myPreferences = new MyPreferences(MyFirebaseMessagingService.this);
            if (!myPreferences.getPreferences(MyPreferences.ID).equals("")) {
                if (myPreferences.getPreferences(MyPreferences.ID).equals("" + remoteMessage.getData().get("user_id")) || remoteMessage.getData().get("user_id").equals("0")) {
                    String notification_title = "", notification_desc = "";
                    notification_title = remoteMessage.getData().get("notification_title");
                    notification_desc = remoteMessage.getData().get("notification_description");
                    String ref_id = remoteMessage.getData().get("ref_id");
                    String type = remoteMessage.getData().get("notification_type");

                    NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                    String[] items = notification_desc.split("\\<br />");

                    for (int i = 0; i < items.length; i++) {
                        inboxStyle.addLine(items[i]);
                    }

                    /*for (String item : items) {
                        inboxStyle.addLine(item);
                    }*/


                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);


                    if (type.equals("0") || type.equals("1")) {
                        Random r = new Random();
                        int notificationId = r.nextInt(80 - 65) + 65;
                        Intent viewIntent = new Intent(MyFirebaseMessagingService.this, NotificationActivity.class);
                        viewIntent.putExtra("ref_id", "" + ref_id);
                        viewIntent.putExtra("notification_title", "" + notification_title);
                        PendingIntent viewPendingIntent = PendingIntent.getActivity(MyFirebaseMessagingService.this, notificationId, viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                        /*Notification notif = new Notification.Builder(MyFirebaseMessagingService.this)
                                .setContentTitle(""+GlobalElements.fromHtml(notification_title))
                                .setContentText(""+GlobalElements.fromHtml(notification_desc))
                                .setContentIntent(viewPendingIntent)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setLargeIcon(largeIcon)
                                .setSound(defaultSoundUri)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(GlobalElements.fromHtml(notification_desc)))
                                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                                //.setStyle(new Notification.BigTextStyle().bigText(GlobalElements.fromHtml(notification_title)))
                                .setAutoCancel(true)
                                .build();*/

                        Notification notif = mBuilder.setSmallIcon(R.mipmap.ic_launcher).setTicker("").setWhen(0)
                                .setContentTitle("" + GlobalElements.fromHtml(notification_title))
                                .setContentText("" + GlobalElements.fromHtml(notification_desc))
                                .setContentIntent(viewPendingIntent)
                                .setLargeIcon(largeIcon)
                                .setSound(defaultSoundUri)
                                .setStyle(inboxStyle)
                                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                                .setAutoCancel(true)
                                .build();

                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MyFirebaseMessagingService.this);
                        notificationManager.notify(notificationId, notif);
                    } else if (type.equals("2") || type.equals("3") || type.equals("5")) {
                        Random r = new Random();
                        int notificationId = r.nextInt(80 - 65) + 65;
                        Intent viewIntent = new Intent(MyFirebaseMessagingService.this, ChannelDescriptionActivity.class);
                        viewIntent.putExtra("channel_id", "" + ref_id);
                        viewIntent.putExtra("notification_title", "" + notification_title);
                        viewIntent.putExtra("activity_type", "1");
                        PendingIntent viewPendingIntent = PendingIntent.getActivity(MyFirebaseMessagingService.this, notificationId, viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Notification notif = new Notification.Builder(MyFirebaseMessagingService.this)
                                .setContentTitle("" + GlobalElements.fromHtml(notification_title))
                                .setContentText("" + GlobalElements.fromHtml(notification_desc))
                                .setContentIntent(viewPendingIntent)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setLargeIcon(largeIcon)
                                .setSound(defaultSoundUri)
                                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                                .setAutoCancel(true)
                                .build();
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MyFirebaseMessagingService.this);
                        notificationManager.notify(notificationId, notif);

                        if (type.equals("5") || type.equals("3")) {
                            Intent intent = new Intent();
                            intent.putExtra("channel_id", "" + ref_id);
                            intent.setAction("com.Notiflick.onMessageReceived");
                            sendOrderedBroadcast(intent, null);
                        }
                    }
                } else {
                    Log.e(">>>", "ok");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}