package com.Nflicks.fragment;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Nflicks.ContactSyncActvity;
import com.Nflicks.GlobalElements;
import com.Nflicks.MainActivity;
import com.Nflicks.R;
import com.Nflicks.adapter.FollowingVerticalAdapter;
import com.Nflicks.adapter.MyPagerAdapter;
import com.Nflicks.custom.Toaster;
import com.Nflicks.model.ContactVO;
import com.Nflicks.model.FollowingCategoryModel;
import com.Nflicks.model.FollowingModel;
import com.Nflicks.netUtils.MyPreferences;
import com.Nflicks.netUtils.RequestInterface;
import com.Nflicks.netUtils.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by CRAFT BOX on 7/29/2017.
 */

public class ContactsFragment extends Fragment {

    @BindView(R.id.contact_recycleview)
    RecyclerView mRecyclerView;
    @BindView(R.id.contact_empty)
    TextView mEmpty;
    @BindView(R.id.contact_search)
    EditText search;

    FollowingVerticalAdapter snapAdapter;
    ArrayList<FollowingModel> section = new ArrayList<>();
    ArrayList<FollowingCategoryModel> followingCategoryModel = new ArrayList<>();
    MyPreferences myPreferences;
    JSONArray contact_sync_array = new JSONArray();

    public interface ContactFragmentIntercommunication {
        public void contactSyncSuccess();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contacts, container, false);
        ButterKnife.bind(this, rootView);
        myPreferences = new MyPreferences(getActivity());
        search.setBackground(GlobalElements.RoundedButtion_White_redies_10(getActivity()));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(30, 25, 30, 5);
        search.setLayoutParams(params);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (search.getText().toString().equals("")) {
                        snapAdapter.filter("");
                    } else {
                        snapAdapter.filter(search.getText().toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        snapAdapter = new FollowingVerticalAdapter(getActivity(), followingCategoryModel, ContactsFragment.this);
        mRecyclerView.setAdapter(snapAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);

        if (!myPreferences.getPreferences(MyPreferences.status).equals("5")) {
            mRecyclerView.setVisibility(View.GONE);
            search.setVisibility(View.GONE);
            mEmpty.setVisibility(View.VISIBLE);
        } else {
            search.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmpty.setVisibility(View.GONE);
            if (GlobalElements.isConnectingToInternet(getActivity())) {
                GetUserChannel();
            } else {
                GlobalElements.showDialog(getActivity());
            }

        }


        return rootView;
    }

    public void RefereshData(int position, ArrayList<FollowingModel> da) {
        try {
            if (da == null) {
                System.out.print("empty");
            } else {
                followingCategoryModel.get(position).setFollowingModels(da);
                snapAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void RefereshData(int section_position, int position, FollowingModel da) {
        try {
            if (da == null) {
                System.out.print("empty");
            } else {
                followingCategoryModel.get(section_position).getFollowingModels().set(position, da);
                snapAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void GetUserChannel() {
        try {

            RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
            Call<ResponseBody> call = request.getAllContactChannel(myPreferences.getPreferences(MyPreferences.ID), myPreferences.getPreferences(MyPreferences.imei));

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {

                        String json_response = response.body().string();
                        JSONObject json = new JSONObject(json_response);
                        if (json.getInt("ack") == 1) {
                            followingCategoryModel.clear();
                            JSONArray result = json.getJSONArray("result");
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject result_obj = result.getJSONObject(i);
                                FollowingCategoryModel da = new FollowingCategoryModel();
                                da.setmGravity(Gravity.CENTER_HORIZONTAL);
                                da.setmText("" + result_obj.getString("contact_name"));
                                da.setChannel_owner_id("" + result_obj.getString("contact_id"));
                                da.setContact_no("" + result_obj.getString("contact_no"));

                                JSONArray channel_array = result_obj.getJSONArray("channels");
                                section = new ArrayList<>();
                                for (int j = 0; j < channel_array.length(); j++) {
                                    JSONObject channel_obj = channel_array.getJSONObject(j);
                                    FollowingModel da1 = new FollowingModel();
                                    da1.setId("" + channel_obj.getString("id"));
                                    da1.setName("" + channel_obj.getString("channel_name"));
                                    da1.setImage_path(channel_obj.getString("image_path"));
                                    da1.setDetails(channel_obj.getString("details"));
                                    da1.setQr_code_path(channel_obj.getString("qr_code_path"));
                                    da1.setShareable_url(channel_obj.getString("shareable_url"));
                                    da1.setIsFollowing(channel_obj.getInt("isFollowing"));
                                    da1.setFollower(channel_obj.getString("follower"));
                                    da1.setChannel_privacy(channel_obj.getString("channel_privacy"));
                                    da1.setChannel_owner_id(channel_obj.getString("user_id"));
                                    JSONObject last_seen_detail_obj = channel_obj.getJSONObject("last_seen_detail");
                                    da1.setCount(last_seen_detail_obj.getInt("flickCount"));
                                    section.add(da1);
                                }
                                if (channel_array.length() > 0) {
                                    da.setFollowingModels(section);
                                    followingCategoryModel.add(da);
                                }
                            }

                            snapAdapter = new FollowingVerticalAdapter(getActivity(), followingCategoryModel, ContactsFragment.this);
                            mRecyclerView.setAdapter(snapAdapter);
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            mRecyclerView.setHasFixedSize(true);
                            search.setVisibility(View.VISIBLE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            mEmpty.setVisibility(View.GONE);
                        } else {
                            search.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.GONE);
                            mEmpty.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    System.out.print("error" + t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ContatSync() {
        if (GlobalElements.isConnectingToInternet(getActivity())) {
            new GetAll().execute("");
        } else {
            GlobalElements.showDialog(getActivity());
        }
    }

    /* todo get fetch all contact from device contact */
    public class GetAll extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {

            try {
                ArrayList contactVOList = new ArrayList();
                contact_sync_array = new JSONArray();
                ContactVO contactVO;
                ContentResolver contentResolver = getActivity().getContentResolver();
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
                                contactVO.setContactNumber(phoneNumber);
                                try {
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
            if (contact_sync_array.length() > 0) {
                ContactSync();
            } else {
                try {
                    ContactFragmentIntercommunication intercommunication = (ContactFragmentIntercommunication) getActivity();
                    intercommunication.contactSyncSuccess();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void ContactSync() {
        try {
            RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
            Call<ResponseBody> call = request.ContactSync(myPreferences.getPreferences(MyPreferences.ID), myPreferences.getPreferences(MyPreferences.imei), contact_sync_array);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {

                        String json_response = response.body().string();
                        JSONObject json = new JSONObject(json_response);
                        if (json.getInt("ack") == 1) {
                            myPreferences.setPreferences(MyPreferences.status, json.getString("status"));
                            Toaster.show(getActivity(), "" + json.getString("ack_msg"), true, Toaster.DANGER);
                            GetUserChannel();
                        } else {
                            Toaster.show(getActivity(), "" + json.getString("ack_msg"), true, Toaster.DANGER);
                        }

                        try {
                            ContactFragmentIntercommunication intercommunication = (ContactFragmentIntercommunication) getActivity();
                            intercommunication.contactSyncSuccess();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            ContactFragmentIntercommunication intercommunication = (ContactFragmentIntercommunication) getActivity();
                            intercommunication.contactSyncSuccess();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    System.out.print("error" + t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
