package com.Nflicks.netUtils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by CRAFT BOX on 1/23/2017.
 */

public class MyPreferences {

    Context context;

    public static String PreferenceName="notiflick";
    public static String EncraptionKey="notflick@2442";

    public static String ID="id",refreshedtoken="refreshedtoken",status="status",Intrest="intrest",sharable_url="sharable_url",imei="imei",lowest_age="lowest_age",highest_age="highest_age";


    public MyPreferences(Context context)
    {
        this.context=context;
    }

    public String getPreferences(String key)
    {
        String value= null;
        try {
            SharedPreferences channel=context.getSharedPreferences(""+ PreferenceName, Context.MODE_PRIVATE);
            value = AESCrypt.decrypt(""+EncraptionKey,channel.getString(""+key,"").toString());
        } catch (Exception  e) {
            e.printStackTrace();
            value = "";
            return value;
        }

        return value;
    }

    public void setPreferences(String key, String value)
    {
        try {
            SharedPreferences sharedpreferences = context.getSharedPreferences(""+PreferenceName, Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = sharedpreferences.edit();
            ed.putString(""+key,AESCrypt.encrypt(""+EncraptionKey, ""+value));
            ed.commit();
        } catch (Exception  e) {
            e.printStackTrace();
        }
    }

    public  boolean clearPreferences() {
        try {
            SharedPreferences settings = context.getSharedPreferences("" + PreferenceName, Context.MODE_PRIVATE);
            return settings.edit().clear().commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /* todo tutoral praferance */

    public static String PreferenceTutoralName="notiflick_tutoral";

    public static String main_activity_tutorial="main_activity_tutorial";

    public String getTutoralPreferences(String key)
    {
        String value= null;
        try {
            SharedPreferences channel=context.getSharedPreferences(""+ PreferenceTutoralName, Context.MODE_PRIVATE);
            value = AESCrypt.decrypt(""+EncraptionKey,channel.getString(""+key,"").toString());
        } catch (Exception  e) {
            e.printStackTrace();
            value = "";
            return value;
        }

        return value;
    }

    public void setTutoralPreferences(String key, String value)
    {
        try {
            SharedPreferences sharedpreferences = context.getSharedPreferences(""+PreferenceTutoralName, Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = sharedpreferences.edit();
            ed.putString(""+key,AESCrypt.encrypt(""+EncraptionKey, ""+value));
            ed.commit();
        } catch (Exception  e) {
            e.printStackTrace();
        }
    }

    public  boolean clearTutoralPreferences() {
        try {
            SharedPreferences settings = context.getSharedPreferences("" + PreferenceTutoralName, Context.MODE_PRIVATE);
            return settings.edit().clear().commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
