package com.Nflicks.reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by CRAFT BOX on 1/25/2017.
 */

public class SmsBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "MyBroadCastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"OnReceive ++>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        try {
            Bundle bndl = intent.getExtras();
            SmsMessage[] msg = null;
            String sender = "",message="";
            if (null != bndl)
            {
                //---retrieve the SMS message received---
                Object[] pdus = (Object[]) bndl.get("pdus");
                msg = new SmsMessage[pdus.length];
                for (int i  = 0; i<msg.length; i++){
                    msg[i]  = SmsMessage.createFromPdu((byte[])pdus[i]);
                    sender  = msg[i].getOriginatingAddress();
                    message = msg[i].getMessageBody().toString();

                }
                //---display incoming SMS as a Android Toast---

                Intent intent1 = new Intent();
                intent1.putExtra("sender", ""+sender);
                intent1.putExtra("message", ""+message);
                intent1.setAction("com.Nflicks.reciver.onMessageReceived");
                context.sendOrderedBroadcast(intent1, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
