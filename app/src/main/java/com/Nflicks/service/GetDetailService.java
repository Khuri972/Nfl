package com.Nflicks.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.Nflicks.GlobalElements;
import com.Nflicks.netUtils.MyPreferences;
import com.Nflicks.netUtils.RequestInterface;
import com.Nflicks.netUtils.RetrofitClient;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by CRAFT BOX on 9/15/2017.
 */

public class GetDetailService extends Service {

    MyPreferences myPreferences;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        myPreferences = new MyPreferences(this);
        //getting systems default ringtone
        if(GlobalElements.isConnectingToInternet(GetDetailService.this)) {
            if(!myPreferences.getPreferences(MyPreferences.ID).equals(""))
            {
                getDetail();
            }
        }
        return START_STICKY;
    }

    private void getDetail(){
        try {

            RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
            Call<ResponseBody> call = request.getUserdetail(myPreferences.getPreferences(MyPreferences.ID));

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String json_response = response.body().string();
                        JSONObject json=new JSONObject(json_response);
                        JSONObject extra_obj=json.getJSONObject("extra");
                        myPreferences.setPreferences(MyPreferences.sharable_url,""+extra_obj.get("sharable_url"));
                        myPreferences.setPreferences(MyPreferences.lowest_age,""+extra_obj.get("lowest_age"));
                        myPreferences.setPreferences(MyPreferences.highest_age,""+extra_obj.get("highest_age"));
                        stopService(new Intent(GetDetailService.this, GetDetailService.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
