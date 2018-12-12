package com.Nflicks.custom;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by CRAFT BOX on 7/10/2017.
 */

public class FontFamily {
    private static final String TAG = "FontSourceProcessor";
    /**
     * parse raw font resource path
     */
    public static Typeface process(Context context,int resource){
        Typeface sResTypeface;
        InputStream sInputStream = null;

        String sOutPath = context.getCacheDir() + "/tmp" + System.currentTimeMillis() + ".raw";

        try{
            sInputStream = context.getResources().openRawResource(resource);
        }catch (Resources.NotFoundException e){
            Log.e(TAG, "Could not find font in Resources!");
        }

        try{
            byte[] sBuffer = new byte[sInputStream.available()];
            BufferedOutputStream sBOutStream = new BufferedOutputStream(new FileOutputStream(sOutPath));
            int l;
            while ((l = sInputStream.read(sBuffer)) > 0){
                sBOutStream.write(sBuffer, 0, l);
            }
            sBOutStream.close();

            sResTypeface = Typeface.createFromFile(sOutPath);

            new File(sOutPath).delete();
        }catch (IOException e){
            Log.e(TAG, "Error reading in fonts!");
            return null;
        }
        Log.d(TAG, "Successfully loaded font.");
        return sResTypeface;
    }
}
