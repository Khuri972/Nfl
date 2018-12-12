package com.Nflicks;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.Nflicks.adapter.AllContactsAdapter;
import com.Nflicks.custom.DividerItemDecoration;
import com.Nflicks.custom.FontFamily;
import com.Nflicks.custom.Toaster;
import com.Nflicks.model.ContactVO;
import com.Nflicks.netUtils.MyPreferences;
import com.Nflicks.netUtils.RequestInterface;
import com.Nflicks.netUtils.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactSyncActvity extends AppCompatActivity {

    @BindView(R.id.rvContacts)
    RecyclerView rvContacts;
    @BindView(R.id.main_title)
    TextView mainTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.sync)
    TextView sync;
    @BindView(R.id.skype)
    TextView skype;
    @BindView(R.id.sync_contact)
    TextView sync_contact_txt;
    @BindView(R.id.channel_list_back)
    ImageView back;

    JSONArray contact_sync_array = new JSONArray();
    List<ContactVO> contactVOList = new ArrayList();
    ProgressDialog pd;
    MyPreferences myPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_sync_actvity);
        ButterKnife.bind(this);
        hideSystemUI();
        myPreferences = new MyPreferences(this);
        mainTitle.setTypeface(FontFamily.process(ContactSyncActvity.this, R.raw.sqaure721), Typeface.BOLD);
        sync_contact_txt.setTypeface(FontFamily.process(ContactSyncActvity.this, R.raw.roboto_regular));
        //getAllContacts();

        sync.setBackground(GlobalElements.RoundedButtion_White(ContactSyncActvity.this));
        skype.setBackground(GlobalElements.RoundedButtion_White(ContactSyncActvity.this));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        skype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // myPreferences.setPreferences(MyPreferences.status,json.getString("status"));
                Intent i = new Intent(ContactSyncActvity.this, MainActivity.class);
                i.putExtra("type", "0");
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                //finish();
            }
        });


        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (GlobalElements.isConnectingToInternet(ContactSyncActvity.this)) {
                        new GetAll().execute();
                    } else {
                        GlobalElements.showDialog(ContactSyncActvity.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }

    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    public class GetAll extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(ContactSyncActvity.this);
            pd.setTitle("Please Wait");
            pd.setMessage("Loading");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                contactVOList = new ArrayList();
                contact_sync_array = new JSONArray();
                ContactVO contactVO;
                ContentResolver contentResolver = getContentResolver();
                Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

                int count = cursor.getCount();
                if (count > 0) {
                    while (cursor.moveToNext()) {

                        int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                        if (hasPhoneNumber > 0) {
                            JSONObject contact_obj = new JSONObject();
                            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                            contactVO = new ContactVO();
                            contactVO.setContactName(name);


                            Cursor phoneCursor = contentResolver.query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                    new String[]{id},
                                    null);
                            if (phoneCursor.moveToNext()) {
                                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                try {
                                    contactVO.setContactNumber(phoneNumber);
                                    contact_obj.put("name", name);
                                    contact_obj.put("mobile_no", phoneNumber);
                                    contactVOList.add(contactVO);
                                    contact_sync_array.put(contact_obj);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            phoneCursor.close();
                        }
                    }

                }
                return "";
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                pd.dismiss();
                hideSystemUI();
                ContactSync();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void ContactSync() {
        try {
            pd = new ProgressDialog(ContactSyncActvity.this);
            pd.setTitle("Please Wait");
            pd.setMessage("Loading");
            pd.setCancelable(true);
            pd.show();
            RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
            Call<ResponseBody> call = request.ContactSync(myPreferences.getPreferences(MyPreferences.ID), myPreferences.getPreferences(MyPreferences.imei), contact_sync_array);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        pd.dismiss();
                        String json_response = response.body().string();
                        JSONObject json = new JSONObject(json_response);
                        if (json.getInt("ack") == 1) {
                            Toaster.show(ContactSyncActvity.this, "" + json.getString("ack_msg"), true, Toaster.SUCCESS);
                            myPreferences.setPreferences(MyPreferences.status, json.getString("status"));
                            Intent i = new Intent(ContactSyncActvity.this, MainActivity.class);
                            i.putExtra("type", "0");
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        } else {
                            Toaster.show(ContactSyncActvity.this, "" + json.getString("ack_msg"), true, Toaster.DANGER);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        pd.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    pd.dismiss();
                    System.out.print("error" + t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
