package com.Nflicks;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.Nflicks.adapter.GeneralAdapter;
import com.Nflicks.custom.CustomEditText;
import com.Nflicks.custom.FlowLayout;
import com.Nflicks.custom.FontFamily;
import com.Nflicks.custom.RangeSeekBar;
import com.Nflicks.custom.Toaster;
import com.Nflicks.custom.Validation;
import com.Nflicks.dialog.ChannelBottomSheetDialogFragment;
import com.Nflicks.dialog.FlickBottomSheetDialogFragment;
import com.Nflicks.dialog.TargatAudienceDialogFragment;
import com.Nflicks.dialog.TargatLocationDialogFragment;
import com.Nflicks.interfacess.DrawableClickListener;
import com.Nflicks.model.GeneralModel;
import com.Nflicks.model.UserChannelListModel;
import com.Nflicks.netUtils.MyPreferences;
import com.Nflicks.netUtils.RequestInterface;
import com.Nflicks.netUtils.RetrofitClient;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.roughike.bottombar.BottomBar;
import com.soundcloud.android.crop.Crop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateChannelActivity extends AppCompatActivity implements GlobalElements.Intercommunication_open, GlobalElements.Intercommunication_close, FlickBottomSheetDialogFragment.Intercommunication, ChannelBottomSheetDialogFragment.ChannelDismissEvent, TargatLocationDialogFragment.TargateLocationDialogIntercommunication, TargatAudienceDialogFragment.TargatAudanceDialogIntercommunication, GeneralAdapter.Intercommunication {

    @BindView(R.id.channel_list_back)
    ImageView back;
    @BindView(R.id.main_title)
    TextView mainTitle;
    @BindView(R.id.channel_list_save)
    ImageView save;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.create_channel_image)
    CircleImageView channel_image;
    @BindView(R.id.create_channel_name_edt)
    EditText name_edt;
    @BindView(R.id.create_channel_public_radio)
    RadioButton channel_public;
    @BindView(R.id.create_channel_private_radio)
    RadioButton channel_private;
    @BindView(R.id.create_channel_contact_edt)
    EditText contact_edt;
    @BindView(R.id.create_channel_email_edt)
    EditText email_edt;
    @BindView(R.id.create_channel_website_edt)
    EditText website_edt;
    @BindView(R.id.create_channel_address_edt)
    EditText address_edt;
    @BindView(R.id.create_channel_detail_edt)
    EditText detail_edt;
    @BindView(R.id.create_channel_contact_txt)
    TextView txt;
    @BindView(R.id.min_age)
    TextView min_age;
    @BindView(R.id.max_age)
    TextView max_age;
    @BindView(R.id.slider_age)
    RangeSeekBar slider_age;
    @BindView(R.id.create_channel_target)
    FlowLayout target_location;
    @BindView(R.id.create_channel_audience)
    FlowLayout target_audience;

    @BindView(R.id.create_channel_main_linear)
    LinearLayout main_layout;
    @BindView(R.id.channel_delete_linear)
    LinearLayout channel_linear_delete;
    @BindView(R.id.channel_delete)
    TextView channel_delete;
    @BindView(R.id.channel_active_deactive)
    TextView channel_active_deactive;

    File image_file = null;
    @BindView(R.id.bottomBar)
    BottomBar bottomBar;
    MyPreferences myPreferences;
    UserChannelListModel userChannelListModel;

    String type = "0"; // 0 = create flick or 1 = update flick
    JSONArray json_array;
    private DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    ArrayList<GeneralModel> targetLocationModels = new ArrayList<>();
    ArrayList<GeneralModel> targetAudienceModels = new ArrayList<>();

    TargatLocationDialogFragment target_location_dialog;
    TargatAudienceDialogFragment target_audience_dialog;

    String channel_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_channel);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        myPreferences = new MyPreferences(CreateChannelActivity.this);

        mainTitle.setTypeface(FontFamily.process(CreateChannelActivity.this, R.raw.sqaure721), Typeface.BOLD);
        GlobalElements.overrideFonts_roboto_regular(CreateChannelActivity.this, main_layout);
        contact_edt.setTypeface(FontFamily.process(CreateChannelActivity.this, R.raw.roboto_regular), Typeface.BOLD);
        detail_edt.setTypeface(FontFamily.process(CreateChannelActivity.this, R.raw.roboto_regular), Typeface.BOLD);
        address_edt.setTypeface(FontFamily.process(CreateChannelActivity.this, R.raw.roboto_regular), Typeface.BOLD);
        email_edt.setTypeface(FontFamily.process(CreateChannelActivity.this, R.raw.roboto_regular), Typeface.BOLD);
        website_edt.setTypeface(FontFamily.process(CreateChannelActivity.this, R.raw.roboto_regular), Typeface.BOLD);
        min_age.setTypeface(FontFamily.process(CreateChannelActivity.this, R.raw.roboto_regular), Typeface.BOLD);
        max_age.setTypeface(FontFamily.process(CreateChannelActivity.this, R.raw.roboto_regular), Typeface.BOLD);


        min_age.setText("" + myPreferences.getPreferences(MyPreferences.lowest_age));
        max_age.setText("" + myPreferences.getPreferences(MyPreferences.highest_age));

        channel_public.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                channel_private.setChecked(false);
            }
        });

        channel_private.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                channel_public.setChecked(false);
            }
        });

        channel_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Crop.pickImage(CreateChannelActivity.this);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                GlobalElements.flag_onresume = true;
                finish();
            }
        });

        slider_age.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                min_age.setText("" + String.valueOf(minValue));
                max_age.setText("" + String.valueOf(maxValue));
            }
        });

        channel_delete.setBackground(GlobalElements.RoundedButtion_White(CreateChannelActivity.this));
        channel_active_deactive.setBackground(GlobalElements.RoundedButtion_White(CreateChannelActivity.this));

        try {
            bottomBar.selectTabAtPosition(2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!Validation.isValid(Validation.BLANK_CHECK, name_edt.getText().toString())) {
                        Toaster.show(CreateChannelActivity.this, "" + getResources().getString(R.string.channel_name), false, Toaster.DANGER);
                    } else if (!Validation.isValid(Validation.BLANK_CHECK, detail_edt.getText().toString())) {
                        Toaster.show(CreateChannelActivity.this, "" + getResources().getString(R.string.channel_detail), false, Toaster.DANGER);
                    } else if (!email_edt.getText().toString().equals("") && !Validation.isValid(Validation.EMAIL, email_edt.getText().toString())) {
                        Toaster.show(CreateChannelActivity.this, "" + getResources().getString(R.string.email), false, Toaster.DANGER);
                    } else if (!website_edt.getText().toString().equals("") && !Validation.isValid(Validation.WEBSITE, website_edt.getText().toString())) {
                        Toaster.show(CreateChannelActivity.this, "" + getResources().getString(R.string.website), false, Toaster.DANGER);
                    } else if (!channel_public.isChecked() && !channel_private.isChecked()) {
                        Toaster.show(CreateChannelActivity.this, "" + getResources().getString(R.string.channel_option), false, Toaster.DANGER);
                    }
                    /*else if(targetLocationModels.size()<2)
                    {
                        Toaster.show(CreateChannelActivity.this,""+getResources().getString(R.string.targate_location),false,Toaster.DANGER);
                    }
                    else if(targetAudienceModels.size()<2 || targetAudienceModels.size()>2)
                    {
                        Toaster.show(CreateChannelActivity.this,""+getResources().getString(R.string.targate_audience),false,Toaster.DANGER);
                    }*/
                    else {
                        boolean flag = false;
                        if (channel_public.isChecked()) {
                            if (targetAudienceModels.size() < 2 || targetAudienceModels.size() > 2) {
                                Toaster.show(CreateChannelActivity.this, "" + getResources().getString(R.string.targate_audience), false, Toaster.DANGER);
                            } else {
                                flag = true;
                            }
                        } else if (channel_private.isChecked()) {
                            if (targetAudienceModels.size() < 2 || targetAudienceModels.size() > 2) {
                                Toaster.show(CreateChannelActivity.this, "" + getResources().getString(R.string.targate_audience), false, Toaster.DANGER);
                            } else {
                                flag = true;
                            }
                        }

                        if (flag) {
                            if (type.equals("0")) {
                                if (image_file == null) {
                                    Toaster.show(CreateChannelActivity.this, "" + getResources().getString(R.string.channel_image), false, Toaster.DANGER);
                                } else {
                                    try {
                                        json_array = new JSONArray();
                                        JSONObject json_obj = new JSONObject();
                                        JSONArray target_location_array = new JSONArray();
                                        for (int i = 0; i < targetLocationModels.size(); i++) {
                                            JSONObject target_location_obj = new JSONObject();
                                            if (!targetLocationModels.get(i).getId().equals("0")) {
                                                target_location_obj.put("tag_id", targetLocationModels.get(i).getId());
                                                target_location_obj.put("tag_value", targetLocationModels.get(i).getName());
                                                target_location_array.put(target_location_obj);
                                            }
                                        }

                                        json_obj.put("type", "location");
                                        json_obj.put("values", target_location_array);
                                        json_array.put(json_obj);

                                        json_obj = new JSONObject();
                                        JSONArray target_audience_array = new JSONArray();
                                        for (int i = 0; i < targetAudienceModels.size(); i++) {
                                            if (!targetAudienceModels.get(i).getId().equals("0")) {
                                                JSONObject target_location_obj = new JSONObject();
                                                target_location_obj.put("tag_id", targetAudienceModels.get(i).getId());
                                                target_location_obj.put("tag_value", targetAudienceModels.get(i).getName());
                                                target_audience_array.put(target_location_obj);
                                            }
                                        }
                                        json_obj.put("type", "interest");
                                        json_obj.put("values", target_audience_array);
                                        json_array.put(json_obj);
                                        System.out.print("" + json_array.toString());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    if (type.equals("0")) {
                                        CreateUserChannel();
                                    } else if (type.equals("1")) {
                                        UpdateUserChannel(channel_id);
                                    }
                                }
                            } else {

                                try {
                                    json_array = new JSONArray();
                                    JSONObject json_obj = new JSONObject();
                                    JSONArray target_location_array = new JSONArray();
                                    for (int i = 0; i < targetLocationModels.size(); i++) {
                                        JSONObject target_location_obj = new JSONObject();
                                        if (!targetLocationModels.get(i).getId().equals("0")) {
                                            target_location_obj.put("tag_id", targetLocationModels.get(i).getId());
                                            target_location_obj.put("tag_value", targetLocationModels.get(i).getName());
                                            target_location_array.put(target_location_obj);
                                        }
                                    }

                                    json_obj.put("type", "location");
                                    json_obj.put("values", target_location_array);
                                    json_array.put(json_obj);

                                    json_obj = new JSONObject();
                                    JSONArray target_audience_array = new JSONArray();
                                    for (int i = 0; i < targetAudienceModels.size(); i++) {
                                        if (!targetAudienceModels.get(i).getId().equals("0")) {
                                            JSONObject target_location_obj = new JSONObject();
                                            target_location_obj.put("tag_id", targetAudienceModels.get(i).getId());
                                            target_location_obj.put("tag_value", targetAudienceModels.get(i).getName());
                                            target_audience_array.put(target_location_obj);
                                        }
                                    }
                                    json_obj.put("type", "interest");
                                    json_obj.put("values", target_audience_array);
                                    json_array.put(json_obj);
                                    System.out.print("" + json_array.toString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                if (type.equals("0")) {
                                    CreateUserChannel();
                                } else if (type.equals("1")) {
                                    UpdateUserChannel(channel_id);
                                } else if (type.equals("2")) {
                                    UpdateUserChannel(channel_id);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        channel_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toaster.show(CreateChannelActivity.this,"okk",false,Toaster.DANGER);

                final iOSDialog iOSDialog = new iOSDialog(CreateChannelActivity.this);
                iOSDialog.setTitle("" + getResources().getString(R.string.delete));
                iOSDialog.setSubtitle("" + getResources().getString(R.string.delete_channel_msg));
                iOSDialog.setPositiveLabel("" + getResources().getString(R.string.ok));
                iOSDialog.setNegativeLabel("" + getResources().getString(R.string.cancel));
                iOSDialog.setBoldPositiveLabel(true);

                iOSDialog.setPositiveListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        iOSDialog.dismiss();
                        if (GlobalElements.isConnectingToInternet(CreateChannelActivity.this)) {
                            DeleteChannel(userChannelListModel.getId(), "delete", 0);
                        } else {
                            GlobalElements.showDialog(CreateChannelActivity.this);
                        }
                    }
                });

                iOSDialog.setNegativeListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(MainActivity.this,"OK clicked",Toast.LENGTH_SHORT).show();
                        iOSDialog.dismiss();
                    }
                });
                iOSDialog.show();
            }
        });

        channel_active_deactive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final iOSDialog iOSDialog = new iOSDialog(CreateChannelActivity.this);
                if (userChannelListModel.getUserActive() == 1) {
                    iOSDialog.setTitle("" + getResources().getString(R.string.active_channel));
                    iOSDialog.setSubtitle("" + getResources().getString(R.string.active_channel_msg));
                } else {
                    iOSDialog.setTitle("" + getResources().getString(R.string.deactive_channel));
                    iOSDialog.setSubtitle("" + getResources().getString(R.string.deactive_channel_msg));
                }

                iOSDialog.setPositiveLabel("" + getResources().getString(R.string.ok));
                iOSDialog.setNegativeLabel("" + getResources().getString(R.string.cancel));
                iOSDialog.setBoldPositiveLabel(true);

                iOSDialog.setPositiveListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        iOSDialog.dismiss();
                        if (GlobalElements.isConnectingToInternet(CreateChannelActivity.this)) {
                            if (userChannelListModel.getUserActive() == 1) {
                                DeleteChannel(userChannelListModel.getId(), "", 0);
                            } else {
                                DeleteChannel(userChannelListModel.getId(), "", 1);
                            }
                        } else {
                            GlobalElements.showDialog(CreateChannelActivity.this);
                        }
                    }
                });

                iOSDialog.setNegativeListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(MainActivity.this,"OK clicked",Toast.LENGTH_SHORT).show();
                        iOSDialog.dismiss();
                    }
                });
                iOSDialog.show();
            }
        });

        /* todo targate location */
        try {

            target_location.removeAllViews();
            GeneralModel da = new GeneralModel();
            da.setId("0");
            da.setName(" ");
            targetLocationModels.add(da);

            for (int i = 0; i < targetLocationModels.size(); i++) {
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View addView = layoutInflater.inflate(R.layout.tag_view, null);

                final CustomEditText tag = (CustomEditText) addView.findViewById(R.id.textout);
                tag.setFocusable(false);
                tag.setClickable(true);
                tag.setText("" + targetLocationModels.get(i).getName());

                final CustomEditText add_tag = (CustomEditText) addView.findViewById(R.id.tag_view_add);
                add_tag.setFocusable(false);
                add_tag.setClickable(true);
                if (i != targetLocationModels.size() - 1) {
                    add_tag.setVisibility(View.GONE);
                    tag.setVisibility(View.VISIBLE);
                    tag.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close_black_24dp, 0);
                } else {
                    add_tag.setVisibility(View.VISIBLE);
                    tag.setVisibility(View.GONE);
                }

                tag.setDrawableClickListener(new DrawableClickListener() {
                    @Override
                    public void onClick(DrawablePosition target_evt) {
                        switch (target_evt) {
                            case LEFT:
                                //Do something here
                                break;
                            case RIGHT:
                                try {
                                    for (int i = 0; i < targetLocationModels.size(); i++) {
                                        if (targetLocationModels.get(i).getName().equals("" + tag.getText().toString())) {
                                            //Toaster.show(CreateChannelActivity.this,""+ targetLocationModels.get(i).getId(),false,Toaster.DANGER);
                                            targetLocationModels.remove(i);
                                            target_location.removeView(addView);
                                            break;
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            default:
                                System.out.print("");
                                break;
                        }
                    }
                });
                final FragmentManager fm = getFragmentManager();
                target_location_dialog = new TargatLocationDialogFragment();
                add_tag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            for (int i = 0; i < targetLocationModels.size(); i++) {
                                if (targetLocationModels.get(i).getId().equals("0")) {

                                    target_location_dialog.show(fm, "Best Players");
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                target_location.addView(addView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* todo targate audience */
        try {
            target_audience.removeAllViews();
            GeneralModel da = new GeneralModel();
            da.setId("0");
            da.setName(" ");
            targetAudienceModels.add(da);

            for (int i = 0; i < targetAudienceModels.size(); i++) {
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View addView = layoutInflater.inflate(R.layout.tag_view, null);
                final CustomEditText tag = (CustomEditText) addView.findViewById(R.id.textout);
                tag.setFocusable(false);
                tag.setClickable(true);
                tag.setText("" + targetAudienceModels.get(i).getName());

                final CustomEditText add_tag = (CustomEditText) addView.findViewById(R.id.tag_view_add);
                add_tag.setFocusable(false);
                add_tag.setClickable(true);
                if (i != targetAudienceModels.size() - 1) {
                    add_tag.setVisibility(View.GONE);
                    tag.setVisibility(View.VISIBLE);
                    tag.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close_black_24dp, 0);

                } else {
                    add_tag.setVisibility(View.VISIBLE);
                    tag.setVisibility(View.GONE);
                }

                tag.setDrawableClickListener(new DrawableClickListener() {
                    @Override
                    public void onClick(DrawablePosition target_evt) {
                        switch (target_evt) {
                            case LEFT:
                                //Do something here
                                break;
                            case RIGHT:
                                try {
                                    for (int i = 0; i < targetAudienceModels.size(); i++) {
                                        if (targetAudienceModels.get(i).getName().equals("" + tag.getText().toString())) {
                                            // Toaster.show(CreateChannelActivity.this,""+ targetAudienceModels.get(i).getId(),false,Toaster.DANGER);
                                            targetAudienceModels.remove(i);
                                            target_audience.removeView(addView);
                                            break;
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            default:
                                System.out.print("");
                                break;
                        }
                    }
                });
                final FragmentManager fm = getFragmentManager();
                target_audience_dialog = new TargatAudienceDialogFragment();
                add_tag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            for (int i = 0; i < targetAudienceModels.size(); i++) {
                                if (targetAudienceModels.get(i).getId().equals("0")) {
                                    target_audience_dialog.show(fm, "Best Players");
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                target_audience.addView(addView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Intent i = getIntent();
            type = i.getStringExtra("type");

            if (type.equals("1")) {
                mainTitle.setText("" + getResources().getString(R.string.update_channel));
                channel_linear_delete.setVisibility(View.VISIBLE);
                userChannelListModel = (UserChannelListModel) i.getSerializableExtra("data");
                channel_id = userChannelListModel.getId();

                if (userChannelListModel.getUserActive() == 1) {
                    channel_active_deactive.setText("" + getResources().getString(R.string.deactive_channel));
                } else {
                    channel_active_deactive.setText("" + getResources().getString(R.string.active_channel));
                }
                GetUserChannelDetail();
            } else {
                channel_linear_delete.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        GlobalElements.flag_onresume = true;
        /*Intent intent=new Intent();
        setResult(145,intent);*/
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(data.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        } else if (requestCode == 234) // todo FlickBottomSheetDialogFragment result
        {
            GlobalElements.Flick_bottomSheetDialogFragment.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == 23) // todo  FlickBottomSheetDialogFragment result
        {
            GlobalElements.Flick_bottomSheetDialogFragment.onActivityResult(requestCode, resultCode, data);
        } else if (resultCode == 248) // profile finish activity
        {
            bottomBar.selectTabAtPosition(2);
        } else if (resultCode == 250) {
            type = data.getStringExtra("type");
            channel_id = data.getStringExtra("channel_id");
        }
    }

    private void beginCrop(Uri source) {
        try {
            File tempFileFromSource = File.createTempFile("crop", ".png", CreateChannelActivity.this.getExternalCacheDir());
            image_file = tempFileFromSource;
            Uri destination = Uri.fromFile(tempFileFromSource);
            Crop.of(source, destination).asSquare().start(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            compressImage(image_file);
            channel_image.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void compressImage(File file) {
        if (file == null) {
            //showError("Please choose an image!");
        } else {
            try {
                File compressedImage = new Compressor(this)
                        .setQuality(75)
                        .setCompressFormat(Bitmap.CompressFormat.WEBP)
                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES).getAbsolutePath())
                        .compressToFile(file);
                image_file=compressedImage;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close(String type) {

    }

    @Override
    public void open() {

    }

    @Override
    public void ChannelDismissEvent() {

    }

    @Override
    public void FlickBackPress() {

    }

    public String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    /* todo get channel detail */
    public void GetUserChannelDetail() {
        final ProgressDialog pd;
        pd = new ProgressDialog(CreateChannelActivity.this);
        pd.setMessage("Loading");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        //todo call upload server client
        RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
        Call<ResponseBody> call = request.getChannelDetail(myPreferences.getPreferences(MyPreferences.ID), userChannelListModel.getId());

        call.enqueue(new Callback<ResponseBody>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pd.dismiss();
                try {

                    String json_responce = response.body().string();
                    JSONObject json = new JSONObject(json_responce);
                    if (json.getInt("ack") == 1) {
                        JSONObject result = json.getJSONObject("result");
                        name_edt.setText("" + result.getString("channel_name"));
                        if (result.getString("channel_privacy").equals("private")) {
                            channel_private.setChecked(true);
                        } else if (userChannelListModel.getChannel_privacy().equals("public")) {
                            channel_public.setChecked(true);
                        }

                        detail_edt.setText("" + result.getString("details"));
                        contact_edt.setText("" + result.getString("contact_no"));
                        address_edt.setText("" + result.getString("address"));
                        email_edt.setText("" + result.getString("email"));
                        website_edt.setText("" + result.getString("website"));

                        try {
                            if (!result.getString("channel_age_range").equals("")) {
                                String[] value = result.getString("channel_age_range").split("\\-");
                                slider_age.setSelectedMinValue(Integer.parseInt("" + value[0]));
                                slider_age.setSelectedMaxValue(Integer.parseInt("" + value[1]));
                                min_age.setText("" + value[0]);
                                max_age.setText("" + value[1]);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (!result.getString("image_path").equals("")) {
                            imageLoader.init(ImageLoaderConfiguration.createDefault(CreateChannelActivity.this));
                            options = new DisplayImageOptions.Builder()
                                    .showImageForEmptyUri(R.drawable.icon)
                                    .showImageOnFail(R.drawable.icon)
                                    .cacheInMemory(true)
                                    .cacheOnDisk(true)
                                    .build();
                            imageLoader.displayImage(userChannelListModel.getImage_path(), channel_image, options);
                        }

                        if (!result.getString("tags").equals("")) {
                            JSONObject tags = result.getJSONObject("tags");
                            JSONObject location = tags.getJSONObject("location");
                            JSONArray tag_values = location.getJSONArray("tag_values");

                            try {
                                targetLocationModels.remove(targetLocationModels.size() - 1);
                                for (int i = 0; i < tag_values.length(); i++) {
                                    JSONObject c = tag_values.getJSONObject(i);
                                    GeneralModel da = new GeneralModel();
                                    da.setId("" + c.get("tag_id"));
                                    da.setName("" + c.getString("tag_value"));
                                    targetLocationModels.add(da);
                                }
                                GeneralModel da = new GeneralModel();
                                da.setId("0");
                                da.setName("");
                                targetLocationModels.add(da);
                                target_location.removeAllViews();

                                for (int i = 0; i < targetLocationModels.size(); i++) {
                                    LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    final View addView = layoutInflater.inflate(R.layout.tag_view, null);
                                    final CustomEditText tag = (CustomEditText) addView.findViewById(R.id.textout);
                                    tag.setKeyListener(null);
                                    tag.setFocusable(false);
                                    tag.setClickable(true);
                                    tag.setText("" + targetLocationModels.get(i).getName());

                                    final CustomEditText add_tag = (CustomEditText) addView.findViewById(R.id.tag_view_add);
                                    add_tag.setFocusable(false);
                                    add_tag.setClickable(true);
                                    if (i != targetLocationModels.size() - 1) {
                                        add_tag.setVisibility(View.GONE);
                                        tag.setVisibility(View.VISIBLE);
                                        tag.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close_black_24dp, 0);
                                    } else {
                                        add_tag.setVisibility(View.VISIBLE);
                                        tag.setVisibility(View.GONE);
                                    }

                                    tag.setDrawableClickListener(new DrawableClickListener() {
                                        @Override
                                        public void onClick(DrawablePosition target_evt) {
                                            switch (target_evt) {
                                                case LEFT:
                                                    //Do something here
                                                    break;
                                                case RIGHT:
                                                    try {
                                                        for (int i = 0; i < targetLocationModels.size(); i++) {
                                                            if (targetLocationModels.get(i).getName().equals("" + tag.getText().toString())) {
                                                                targetLocationModels.remove(i);
                                                                target_location.removeView(addView);
                                                                break;
                                                            }
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    break;
                                                default:
                                                    System.out.print("");
                                                    break;
                                            }
                                        }
                                    });
                                    final FragmentManager fm = getFragmentManager();
                                    target_location_dialog = new TargatLocationDialogFragment();
                                    add_tag.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            try {
                                                for (int i = 0; i < targetLocationModels.size(); i++) {
                                                    if (targetLocationModels.get(i).getId().equals("0")) {

                                                        target_location_dialog.show(fm, "Best Players");
                                                        break;
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    target_location.addView(addView);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                JSONObject interest = tags.getJSONObject("interest");
                                tag_values = interest.getJSONArray("tag_values");
                                targetAudienceModels.remove(targetAudienceModels.size() - 1);
                                for (int i = 0; i < tag_values.length(); i++) {
                                    JSONObject c = tag_values.getJSONObject(i);
                                    GeneralModel da = new GeneralModel();
                                    da.setId("" + c.get("tag_id"));
                                    da.setName("" + c.getString("tag_value"));
                                    targetAudienceModels.add(da);
                                }
                                GeneralModel da = new GeneralModel();
                                da.setId("0");
                                da.setName("");
                                targetAudienceModels.add(da);
                                target_audience.removeAllViews();
                                for (int i = 0; i < targetAudienceModels.size(); i++) {
                                    LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    final View addView = layoutInflater.inflate(R.layout.tag_view, null);
                                    final CustomEditText tag = (CustomEditText) addView.findViewById(R.id.textout);
                                    tag.setKeyListener(null);
                                    tag.setFocusable(false);
                                    tag.setClickable(true);
                                    tag.setText("" + targetAudienceModels.get(i).getName());

                                    final CustomEditText add_tag = (CustomEditText) addView.findViewById(R.id.tag_view_add);
                                    add_tag.setFocusable(false);
                                    add_tag.setClickable(true);
                                    if (i != targetAudienceModels.size() - 1) {
                                        add_tag.setVisibility(View.GONE);
                                        tag.setVisibility(View.VISIBLE);
                                        tag.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close_black_24dp, 0);
                                    } else {
                                        add_tag.setVisibility(View.VISIBLE);
                                        tag.setVisibility(View.GONE);
                                    }

                                    tag.setDrawableClickListener(new DrawableClickListener() {
                                        @Override
                                        public void onClick(DrawablePosition target_evt) {
                                            switch (target_evt) {
                                                case LEFT:
                                                    break;
                                                case RIGHT:
                                                    try {
                                                        for (int i = 0; i < targetAudienceModels.size(); i++) {
                                                            if (targetAudienceModels.get(i).getName().equals("" + tag.getText().toString())) {
                                                                targetAudienceModels.remove(i);
                                                                target_audience.removeView(addView);
                                                                break;
                                                            }
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    break;
                                                default:
                                                    System.out.print("");
                                                    break;
                                            }
                                        }
                                    });
                                    final FragmentManager fm = getFragmentManager();
                                    target_audience_dialog = new TargatAudienceDialogFragment();
                                    add_tag.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            try {
                                                for (int i = 0; i < targetAudienceModels.size(); i++) {
                                                    if (targetAudienceModels.get(i).getId().equals("0")) {
                                                        target_audience_dialog.show(fm, "Best Players");
                                                        break;
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    target_audience.addView(addView);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            System.out.print("");
                        }
                    } else {
                        Toaster.show(CreateChannelActivity.this, "" + json.getString("ack_msg"), false, Toaster.DANGER);
                    }
                } catch (Exception e) {
                    pd.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pd.dismiss();
            }
        });
    }

    /* todo create channel */
    public void CreateUserChannel() {
        final ProgressDialog pd;
        pd = new ProgressDialog(CreateChannelActivity.this);
        pd.setMessage("Loading");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        //todo call upload server client
        RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
        //todo request body
        RequestBody requestfile;
        MultipartBody.Part body;
        Call<ResponseBody> call;
        if (image_file != null) {
            requestfile = RequestBody.create(MediaType.parse("image/*"), image_file);
            body = MultipartBody.Part.createFormData("file", image_file.getName(), requestfile);
            String channel_privacy = "";
            if (channel_public.isChecked()) {
                channel_privacy = "public";
            } else if (channel_private.isChecked()) {
                channel_privacy = "private";
            }
            call = request.Create_channel(myPreferences.getPreferences(MyPreferences.ID), name_edt.getText().toString(), channel_privacy, detail_edt.getText().toString(), contact_edt.getText().toString(), address_edt.getText().toString(), email_edt.getText().toString(), website_edt.getText().toString(), "" + min_age.getText().toString() + "-" + max_age.getText().toString(), json_array.toString(), body);
        } else {

            String channel_privacy = "";
            if (channel_public.isChecked()) {
                channel_privacy = "public";
            } else if (channel_private.isChecked()) {
                channel_privacy = "private";
            }
            call = request.Create_channel(myPreferences.getPreferences(MyPreferences.ID), name_edt.getText().toString(), channel_privacy, detail_edt.getText().toString(), contact_edt.getText().toString(), address_edt.getText().toString(), email_edt.getText().toString(), website_edt.getText().toString(), "" + min_age.getText().toString() + "-" + max_age.getText().toString(), json_array.toString());
        }
        call.enqueue(new Callback<ResponseBody>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pd.dismiss();
                try {
                    String json_responce = response.body().string();
                    JSONObject json = new JSONObject(json_responce);
                    if (json.getInt("ack") == 1) {
                        Toaster.show(CreateChannelActivity.this, "" + json.getString("ack_msg"), false, Toaster.SUCCESS);
                        GlobalElements.flag_onresume = true;
                        JSONObject result = json.getJSONObject("result");
                        Intent i = new Intent(CreateChannelActivity.this, ChannelUrlAvailablityActivity.class);
                        i.putExtra("channel_id", "" + result.getString("id"));
                        i.putExtra("channel_name", "" + result.getString("shareable_url"));
                        startActivityForResult(i, 0);
                    } else {
                        Toaster.show(CreateChannelActivity.this, "" + json.getString("ack_msg"), false, Toaster.DANGER);
                    }
                } catch (Exception e) {
                    pd.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pd.dismiss();
            }
        });
    }

    /* todo update channel */
    public void UpdateUserChannel(final String channel_id) {
        final ProgressDialog pd;
        pd = new ProgressDialog(CreateChannelActivity.this);
        pd.setMessage("Loading");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        //todo call upload server client
        RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
        //todo request body
        RequestBody requestfile;
        MultipartBody.Part body;
        Call<ResponseBody> call;
        if (image_file != null) {
            requestfile = RequestBody.create(MediaType.parse("image/*"), image_file);
            body = MultipartBody.Part.createFormData("file", image_file.getName(), requestfile);
            String channel_privacy = "";
            if (channel_public.isChecked()) {
                channel_privacy = "public";
            } else if (channel_private.isChecked()) {
                channel_privacy = "private";
            }

            call = request.Update_channel(myPreferences.getPreferences(MyPreferences.ID), channel_id, name_edt.getText().toString(), channel_privacy, detail_edt.getText().toString(), contact_edt.getText().toString(), address_edt.getText().toString(), email_edt.getText().toString(), website_edt.getText().toString(), "" + min_age.getText().toString() + "-" + max_age.getText().toString(), json_array.toString(), body);
        } else {

            String channel_privacy = "";
            if (channel_public.isChecked()) {
                channel_privacy = "public";
            } else if (channel_private.isChecked()) {
                channel_privacy = "private";
            }
            call = request.Update_channel(myPreferences.getPreferences(MyPreferences.ID), channel_id, name_edt.getText().toString(), channel_privacy, detail_edt.getText().toString(), contact_edt.getText().toString(), address_edt.getText().toString(), email_edt.getText().toString(), website_edt.getText().toString(), "" + min_age.getText().toString() + "-" + max_age.getText().toString(), json_array.toString());
        }
        call.enqueue(new Callback<ResponseBody>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pd.dismiss();
                try {
                    String json_responce = response.body().string();
                    JSONObject json = new JSONObject(json_responce);
                    if (json.getInt("ack") == 1) {
                        Toaster.show(CreateChannelActivity.this, "" + json.getString("ack_msg"), false, Toaster.SUCCESS);
                        // GlobalElements.flag_onresume = true;
                        if (type.equals("1")) {

                            try {
                                JSONObject c = json.getJSONObject("result");

                                userChannelListModel.setId("" + c.getString("id"));
                                userChannelListModel.setName("" + c.getString("channel_name"));
                                userChannelListModel.setImage_path(c.getString("image_path"));
                                userChannelListModel.setFollower(c.getString("follower"));
                                userChannelListModel.setName(c.getString("channel_name"));
                                userChannelListModel.setChannel_privacy(c.getString("channel_privacy"));
                                userChannelListModel.setDetail(c.getString("details"));
                                userChannelListModel.setContact_no(c.getString("contact_no"));
                                userChannelListModel.setAddress(c.getString("address"));
                                userChannelListModel.setEmail(c.getString("email"));
                                userChannelListModel.setWebsite(c.getString("website"));
                                userChannelListModel.setQr_code_path(c.getString("qr_code_path"));
                                userChannelListModel.setShareable_url(c.getString("shareable_url"));
                                userChannelListModel.setUserActive(c.getInt("UserActive"));

                                Intent i = getIntent();
                                Bundle b = new Bundle();
                                b.putSerializable("data", userChannelListModel);
                                setResult(450, i);
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if (type.equals("2")) {
                            JSONObject result = json.getJSONObject("result");
                            Intent i = new Intent(CreateChannelActivity.this, ChannelUrlAvailablityActivity.class);
                            i.putExtra("channel_id", "" + channel_id);
                            i.putExtra("channel_name", "" + result.getString("shareable_url"));
                            startActivityForResult(i, 0);
                        } else {
                            finish();
                        }
                    } else {
                        Toaster.show(CreateChannelActivity.this, "" + json.getString("ack_msg"), false, Toaster.DANGER);
                    }
                } catch (Exception e) {
                    pd.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pd.dismiss();
            }
        });
    }

    /* todo implements method */
    @Override
    public void SearchLocationData(ArrayList<GeneralModel> search_data, String type) {

        try {
            targetLocationModels.remove(targetLocationModels.size() - 1);
            for (int i = 0; i < search_data.size(); i++) {
                GeneralModel da = new GeneralModel();
                da.setId("" + search_data.get(i).getId());
                da.setName("" + search_data.get(i).getName());

                try {
                    boolean isValidProduct = true;
                    for (int j = 0; j < targetLocationModels.size(); j++) {
                        if (targetLocationModels.get(j).getId().equals(search_data.get(i).getId())) {
                            isValidProduct = false;
                        }
                    }
                    if (isValidProduct) {
                        targetLocationModels.add(da);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            GeneralModel da = new GeneralModel();
            da.setId("0");
            da.setName(" ");
            targetLocationModels.add(da);

            target_location.removeAllViews();

            for (int i = 0; i < targetLocationModels.size(); i++) {
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View addView = layoutInflater.inflate(R.layout.tag_view, null);
                final CustomEditText tag = (CustomEditText) addView.findViewById(R.id.textout);
                tag.setKeyListener(null);
                tag.setFocusable(false);
                tag.setClickable(true);
                tag.setText("" + targetLocationModels.get(i).getName());

                final CustomEditText add_tag = (CustomEditText) addView.findViewById(R.id.tag_view_add);
                add_tag.setFocusable(false);
                add_tag.setClickable(true);
                if (i != targetLocationModels.size() - 1) {
                    add_tag.setVisibility(View.GONE);
                    tag.setVisibility(View.VISIBLE);
                    tag.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close_black_24dp, 0);
                } else {
                    add_tag.setVisibility(View.VISIBLE);
                    tag.setVisibility(View.GONE);
                }

                tag.setDrawableClickListener(new DrawableClickListener() {
                    @Override
                    public void onClick(DrawablePosition target_evt) {
                        switch (target_evt) {
                            case LEFT:
                                //Do something here
                                break;
                            case RIGHT:
                                try {
                                    for (int i = 0; i < targetLocationModels.size(); i++) {
                                        if (targetLocationModels.get(i).getName().equals("" + tag.getText().toString())) {
                                            //Toaster.show(CreateChannelActivity.this,""+ targetLocationModels.get(i).getId(),false,Toaster.DANGER);
                                            targetLocationModels.remove(i);
                                            target_location.removeView(addView);
                                            break;
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            default:
                                System.out.print("");
                                break;
                        }
                    }
                });
                final FragmentManager fm = getFragmentManager();
                target_location_dialog = new TargatLocationDialogFragment();
                add_tag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            for (int i = 0; i < targetLocationModels.size(); i++) {
                                if (targetLocationModels.get(i).getId().equals("0")) {
                                    target_location_dialog.show(fm, "Best Players");
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                target_location.addView(addView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* todo implements method */
    @Override
    public void searchAudianceData(ArrayList<GeneralModel> search_data, String type) {
        try {
            targetAudienceModels.remove(targetAudienceModels.size() - 1);
            for (int i = 0; i < search_data.size(); i++) {
                GeneralModel da = new GeneralModel();
                da.setId("" + search_data.get(i).getId());
                da.setName("" + search_data.get(i).getName());

                try {
                    boolean isValidProduct = true;
                    for (int j = 0; j < targetAudienceModels.size(); j++) {
                        if (targetAudienceModels.get(j).getId().equals(search_data.get(i).getId())) {
                            isValidProduct = false;
                        }
                    }
                    if (isValidProduct) {
                        targetAudienceModels.add(da);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            GeneralModel da = new GeneralModel();
            da.setId("0");
            da.setName(" ");
            targetAudienceModels.add(da);

            target_audience.removeAllViews();

            for (int i = 0; i < targetAudienceModels.size(); i++) {
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View addView = layoutInflater.inflate(R.layout.tag_view, null);
                final CustomEditText tag = (CustomEditText) addView.findViewById(R.id.textout);
                tag.setKeyListener(null);
                tag.setFocusable(false);
                tag.setClickable(true);
                tag.setText("" + targetAudienceModels.get(i).getName());

                final CustomEditText add_tag = (CustomEditText) addView.findViewById(R.id.tag_view_add);
                add_tag.setFocusable(false);
                add_tag.setClickable(true);
                if (i != targetAudienceModels.size() - 1) {
                    add_tag.setVisibility(View.GONE);
                    tag.setVisibility(View.VISIBLE);
                    tag.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close_black_24dp, 0);
                } else {
                    add_tag.setVisibility(View.VISIBLE);
                    tag.setVisibility(View.GONE);
                }

                tag.setDrawableClickListener(new DrawableClickListener() {
                    @Override
                    public void onClick(DrawablePosition target_evt) {
                        switch (target_evt) {
                            case LEFT:
                                //Do something here
                                break;
                            case RIGHT:
                                try {
                                    for (int i = 0; i < targetAudienceModels.size(); i++) {
                                        if (targetAudienceModels.get(i).getName().equals("" + tag.getText().toString())) {
                                            //Toaster.show(CreateChannelActivity.this,""+ targetAudienceModels.get(i).getId(),false,Toaster.DANGER);
                                            targetAudienceModels.remove(i);
                                            target_audience.removeView(addView);
                                            break;
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            default:
                                System.out.print("");
                                break;
                        }
                    }
                });
                final FragmentManager fm = getFragmentManager();
                target_audience_dialog = new TargatAudienceDialogFragment();
                add_tag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            for (int i = 0; i < targetAudienceModels.size(); i++) {
                                if (targetAudienceModels.get(i).getId().equals("0")) {
                                    target_audience_dialog.show(fm, "Best Players");
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                target_audience.addView(addView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void Check(String type) {
        if (type.equals("target_location")) {
            target_location_dialog.TargetLocationCheck();
        } else {
            target_audience_dialog.TargetAudiance();
        }
    }

    /* todo channel delete  */
    public void DeleteChannel(String cid, final String delete_flag, final int isActive) {
        final ProgressDialog pd;
        pd = new ProgressDialog(CreateChannelActivity.this);
        pd.setMessage("Loading");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        //todo call upload server client
        RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
        Call<ResponseBody> call;

        if (delete_flag.equals("delete")) {
            call = request.channelDelete(myPreferences.getPreferences(MyPreferences.ID), cid);
        } else {
            call = request.channel_isActive(myPreferences.getPreferences(MyPreferences.ID), cid, isActive);
        }

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pd.dismiss();
                try {
                    String json_responce = response.body().string();
                    JSONObject json = new JSONObject(json_responce);
                    if (json.getInt("ack") == 1) {

                        if (delete_flag.equals("delete")) {
                            Toaster.show(CreateChannelActivity.this, "" + json.getString("ack_msg"), false, Toaster.SUCCESS);
                            //GlobalElements.flag_onresume = true;
                            /*if (type.equals("1")) {
                                Intent i = getIntent();
                                setResult(350, i);
                            }
                            finish();*/

                            Intent i = new Intent(CreateChannelActivity.this, MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();
                        } else {
                            userChannelListModel.setUserActive(isActive);

                            Intent i = new Intent(CreateChannelActivity.this, MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();
                        }
                    } else {
                        Toaster.show(CreateChannelActivity.this, "" + json.getString("ack_msg"), false, Toaster.DANGER);
                    }
                } catch (Exception e) {
                    pd.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pd.dismiss();
            }
        });
    }
}
