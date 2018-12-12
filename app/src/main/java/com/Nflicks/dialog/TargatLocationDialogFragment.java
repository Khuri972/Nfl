package com.Nflicks.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.Nflicks.GlobalElements;
import com.Nflicks.R;
import com.Nflicks.adapter.GeneralAdapter;
import com.Nflicks.custom.Toaster;
import com.Nflicks.model.GeneralModel;
import com.Nflicks.netUtils.MyPreferences;
import com.Nflicks.netUtils.RequestInterface;
import com.Nflicks.netUtils.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by CRAFT BOX on 9/12/2017.
 */

public class TargatLocationDialogFragment extends DialogFragment {
    //Button btn;
    ListView lv;
    SearchView sv;
    ArrayList<GeneralModel> data = new ArrayList<>();
    ArrayList<GeneralModel> selected_data = new ArrayList<>();
    GeneralAdapter adapter;
    ProgressDialog pd;
    MyPreferences myPreferences;
    ImageView submit;


    public void TargetLocationCheck() {
        boolean valid = false;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).isChecked()) {
                valid = true;
                break;
            }
        }
        if (valid) {
            submit.setVisibility(View.VISIBLE);
        } else {
            submit.setVisibility(View.GONE);
        }
    }

    public interface TargateLocationDialogIntercommunication {
        public void SearchLocationData(ArrayList<GeneralModel> search_data, String type);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View rootView = inflater.inflate(R.layout.dialog_general, null);
        myPreferences = new MyPreferences(getActivity());

        lv = (ListView) rootView.findViewById(R.id.listView1);
        sv = (SearchView) rootView.findViewById(R.id.searchView1);
        //btn=(Button) rootView.findViewById(R.id.dismiss);
        submit = (ImageView) rootView.findViewById(R.id.search_submit);

        //SEARCH
        sv.setQueryHint("Search..");
        sv.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        sv.setIconifiedByDefault(true);
        sv.setFocusable(true);
        sv.setIconified(false);

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                try {

                    try {
                        if (!data.isEmpty()) {
                            selected_data.clear();
                            for (int i = 0; i < data.size(); i++) {
                                if (data.get(i).isChecked()) {
                                    GeneralModel da = new GeneralModel();
                                    da.setId("" + data.get(i).getId());
                                    da.setName("" + data.get(i).getName());
                                    selected_data.add(da);
                                }
                            }

                            try {
                                TargateLocationDialogIntercommunication intercommunication = (TargateLocationDialogIntercommunication) getActivity();
                                intercommunication.SearchLocationData(selected_data, "location");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (!s.toString().equals("")) {
                        if (GlobalElements.isConnectingToInternet(getActivity())) {
                            GeneralSearch(s.toString());
                        } else {
                            GlobalElements.showDialog(getActivity());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        //BUTTON
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                try {
                    if (!data.isEmpty()) {
                        selected_data.clear();
                        for (int i = 0; i < data.size(); i++) {
                            if (data.get(i).isChecked()) {
                                GeneralModel da = new GeneralModel();
                                da.setId("" + data.get(i).getId());
                                da.setName("" + data.get(i).getName());
                                selected_data.add(da);
                            }
                        }
                        try {
                            TargateLocationDialogIntercommunication intercommunication = (TargateLocationDialogIntercommunication) getActivity();
                            intercommunication.SearchLocationData(selected_data, "location");
                            dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return rootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return dialog;
    }

    private void GeneralSearch(String query) {
        try {
            pd = new ProgressDialog(getActivity());
            pd.setTitle("Please Wait");
            pd.setMessage("Loading");
            pd.setCancelable(true);
            pd.show();
            RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
            Call<ResponseBody> call = request.getLocation(myPreferences.getPreferences(MyPreferences.ID), query);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        pd.dismiss();
                        String json_response = response.body().string();
                        JSONObject json = new JSONObject(json_response);
                        data.clear();
                        if (json.getInt("ack") == 1) {
                            JSONArray result = json.getJSONArray("result");
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject c = result.getJSONObject(i);
                                GeneralModel da = new GeneralModel();
                                da.setId("" + c.getString("id"));
                                da.setName("" + c.getString("name"));
                                da.setChecked(false);
                                data.add(da);
                            }
                            adapter = new GeneralAdapter(getActivity(), data, "target_location");
                            lv.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } else {
                            adapter = new GeneralAdapter(getActivity(), data, "target_location");
                            lv.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            Toaster.show(getActivity(), "" + json.getString("ack_msg"), true, Toaster.DANGER);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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
