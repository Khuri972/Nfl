package com.Nflicks;

import android.*;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.Nflicks.custom.Toaster;
import com.Nflicks.netUtils.MyPreferences;
import com.Nflicks.netUtils.RequestInterface;
import com.Nflicks.netUtils.RetrofitClient;

import org.json.JSONObject;

import me.ydcool.lib.qrmodule.activity.QrScannerActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QrCodeActivity extends AppCompatActivity {

    public static final int PERMISSIONS_REQUEST_CODE = 0;
    ProgressDialog pd;
    MyPreferences myPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        myPreferences = new MyPreferences(QrCodeActivity.this);
        checkPermissionsAndOpenFilePicker();
    }

    private void checkPermissionsAndOpenFilePicker() {
        String permission = android.Manifest.permission.CAMERA;
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                Toaster.show(QrCodeActivity.this, "Allow external storage reading", false, Toaster.DANGER);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSIONS_REQUEST_CODE);
            }
        } else {
            startActivityForResult(
                    new Intent(QrCodeActivity.this, QrScannerActivity.class),
                    QrScannerActivity.QR_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QrScannerActivity.QR_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                SearchWithBarcode("" + data.getExtras().getString(QrScannerActivity.QR_RESULT_STR));
            } else {
                Intent intent=new Intent();
                setResult(120,intent);
                finish();
            }
        }
        else if (resultCode == 120)
        {
            Intent intent=new Intent();
            setResult(120,intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent();
        setResult(120,intent);
        finish();
    }

    private void SearchWithBarcode(String barcode_code) {
        try {
            pd = new ProgressDialog(QrCodeActivity.this);
            pd.setTitle("Please Wait");
            pd.setMessage("Loading");
            pd.setCancelable(false);
            pd.show();
            RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
            Call<ResponseBody> call = request.getChannelFromBarcode(myPreferences.getPreferences(MyPreferences.ID), barcode_code);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        pd.dismiss();
                        String json_response = response.body().string();
                        JSONObject json = new JSONObject(json_response);
                        if (json.getInt("ack") == 1) {
                            JSONObject result_obj = json.getJSONObject("result");
                            Intent i1 = new Intent(QrCodeActivity.this, ChannelDescriptionActivity.class);
                            Bundle b = new Bundle();
                            b.putSerializable("channel_id", result_obj.getString("id"));
                            b.putSerializable("channel_name", result_obj.getString("channel_name"));
                            b.putSerializable("channel_follower", result_obj.getString("follower"));
                            b.putSerializable("channel_details", result_obj.getString("details"));
                            b.putSerializable("channel_image_path", result_obj.getString("image_path"));
                            b.putSerializable("channel_sharable_url", result_obj.getString("shareable_url"));
                            b.putSerializable("channel_qr_code", result_obj.getString("qr_code_path"));
                            b.putSerializable("channel_privacy", result_obj.getString("channel_privacy"));
                            b.putString("activity_type", "2");
                            i1.putExtras(b);
                            startActivityForResult(i1, 0);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        } else {
                            Toaster.show(QrCodeActivity.this, "" + json.getString("ack_msg"), false, Toaster.DANGER);
                            Intent intent=new Intent();
                            setResult(120,intent);
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Intent intent=new Intent();
                        setResult(120,intent);
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    pd.dismiss();
                    Intent intent=new Intent();
                    setResult(120,intent);
                    finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Intent intent=new Intent();
            setResult(120,intent);
            finish();
        }
    }
}
