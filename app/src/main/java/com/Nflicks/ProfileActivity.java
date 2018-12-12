package com.Nflicks;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.Nflicks.custom.CustomEditText;
import com.Nflicks.custom.FlowLayout;
import com.Nflicks.custom.FontFamily;
import com.Nflicks.custom.Toaster;
import com.Nflicks.custom.Validation;
import com.Nflicks.dialog.ChannelBottomSheetDialogFragment;
import com.Nflicks.dialog.FlickBottomSheetDialogFragment;
import com.Nflicks.dialog.TargatAudienceDialogFragment;
import com.Nflicks.dialog.TargatLocationDialogFragment;
import com.Nflicks.interfacess.DrawableClickListener;
import com.Nflicks.model.GeneralModel;
import com.Nflicks.netUtils.MyPreferences;
import com.Nflicks.netUtils.RequestInterface;
import com.Nflicks.netUtils.RetrofitClient;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;
import com.soundcloud.android.crop.Crop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

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

public class ProfileActivity extends AppCompatActivity implements FlickBottomSheetDialogFragment.Intercommunication, ChannelBottomSheetDialogFragment.ChannelDismissEvent, TargatAudienceDialogFragment.TargatAudanceDialogIntercommunication {

    @BindView(R.id.bottomBar)
    BottomBar bottomBar;
    @BindView(R.id.channel_list_back)
    ImageView back;
    @BindView(R.id.main_title)
    TextView mainTitle;
    @BindView(R.id.channel_list_save)
    ImageView save;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.input_birthday)
    TextView birthdate;
    @BindView(R.id.input_email)
    EditText email;
    @BindView(R.id.input_contact)
    EditText contact;
    @BindView(R.id.input_website)
    EditText website;
    @BindView(R.id.input_male_radio)
    RadioButton male_radio;
    @BindView(R.id.input_female_radio)
    RadioButton female_radio;
    @BindView(R.id.profile_main_linear)
    LinearLayout layout;
    @BindView(R.id.create_channel_image)
    CircleImageView user_image;
    @BindView(R.id.create_channel_name_edt)
    EditText user_name;
    @BindView(R.id.create_channel_detail_edt)
    EditText user_detail;
    @BindView(R.id.profile_intrest_tag)
    FlowLayout intrest_tag;
    @BindView(R.id.profile_logout)
    ImageView logout;

    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    File file1 = null, imageDir;
    Uri outputFileUri;
    String user_img1;

    ArrayList<GeneralModel> targetAudienceModels = new ArrayList<>();
    JSONArray json_array = new JSONArray();

    BottomSheetDialogFragment bottomSheetDialogFragment;
    ProgressDialog pd;
    MyPreferences myPreferences;
    private DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        mainTitle.setTypeface(FontFamily.process(ProfileActivity.this, R.raw.sqaure721), Typeface.BOLD);
        GlobalElements.overrideFonts_roboto_regular(ProfileActivity.this, layout);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        myPreferences = new MyPreferences(ProfileActivity.this);
        contact.setEnabled(false);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    final iOSDialog iOSDialog = new iOSDialog(ProfileActivity.this);
                    iOSDialog.setTitle("" + getResources().getString(R.string.logout_title));
                    iOSDialog.setSubtitle("" + getResources().getString(R.string.logout_msg));
                    iOSDialog.setPositiveLabel("" + getResources().getString(R.string.ok));
                    iOSDialog.setNegativeLabel("" + getResources().getString(R.string.cancel));
                    iOSDialog.setBoldPositiveLabel(true);

                    iOSDialog.setPositiveListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Toast.makeText(MainActivity.this,"OK clicked",Toast.LENGTH_SHORT).show();
                            iOSDialog.dismiss();
                            myPreferences.clearPreferences();
                            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();
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
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        imageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "" + GlobalElements.directory + "");
        if (!imageDir.exists()) {
            imageDir.mkdir();
        }

        male_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                female_radio.setChecked(false);
            }
        });

        female_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                male_radio.setChecked(false);
            }
        });

        birthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });

        user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    file1 = null;
                    final Dialog dialog = new Dialog(ProfileActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_gallery);

                    LinearLayout camera = (LinearLayout) dialog.findViewById(R.id.dialog_camera);
                    LinearLayout gallery = (LinearLayout) dialog.findViewById(R.id.dialog_gallery);
                    camera.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            // imageInputHelper.takePhotoWithCamera();
                            try {
                                user_img1 = imageDir + "/test.jpg";
                                file1 = new File(user_img1);
                                try {
                                    file1.createNewFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                if (GlobalElements.versionCheck()) {
                                    outputFileUri = FileProvider.getUriForFile(ProfileActivity.this, "" + GlobalElements.fileprovider_path, file1);
                                } else {
                                    outputFileUri = Uri.fromFile(file1);
                                }
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                                startActivityForResult(cameraIntent, 180);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    gallery.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Crop.pickImage(ProfileActivity.this);
                        }
                    });
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        try {
            bottomSheetDialogFragment = new ChannelBottomSheetDialogFragment();
            GlobalElements.Flick_bottomSheetDialogFragment = new FlickBottomSheetDialogFragment();
            bottomBar = (BottomBar) findViewById(R.id.bottomBar);
            //GlobalElements.setBottomBarListner(ProfileActivity.this, "ProfileActivity");
            bottomBar.selectTabAtPosition(4);

            bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
                @Override
                public void onTabSelected(@IdRes int tabId) {
                    try {
                        if (tabId == R.id.tab_main) {
                            Intent i = new Intent(ProfileActivity.this, MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        } else if (tabId == R.id.tab_search) {
                            Intent i = new Intent(ProfileActivity.this, SearchActivity.class);
                            startActivityForResult(i, 0);
                        } else if (tabId == R.id.tab_create_channel) {
                            bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                        } else if (tabId == R.id.tab_qrcode) {
                            Intent i = new Intent(ProfileActivity.this, QrCodeActivity.class);
                            startActivityForResult(i, 0);
                        } else if (tabId == R.id.tab_profile) {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
                @Override
                public void onTabReSelected(@IdRes int tabId) {
                    try {
                        if (tabId == R.id.tab_main) {
                            Intent i = new Intent(ProfileActivity.this, MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        } else if (tabId == R.id.tab_search) {
                            Intent i = new Intent(ProfileActivity.this, SearchActivity.class);
                            startActivityForResult(i, 0);
                        } else if (tabId == R.id.tab_create_channel) {
                            bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                        } else if (tabId == R.id.tab_qrcode) {
                            Intent i = new Intent(ProfileActivity.this, QrCodeActivity.class);
                            startActivityForResult(i, 0);
                        } else if (tabId == R.id.tab_profile) {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(248, intent);
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!Validation.isValid(Validation.BLANK_CHECK, user_name.getText().toString())) {
                        Toaster.show(ProfileActivity.this, "" + getResources().getString(R.string.user_name), false, Toaster.DANGER);
                    } else if (!email.getText().toString().equals("") && !Validation.isValid(Validation.EMAIL, email.getText().toString())) {
                        Toaster.show(ProfileActivity.this, "" + getResources().getString(R.string.email), false, Toaster.DANGER);
                    } else if (!website.getText().toString().equals("") && !Validation.isValid(Validation.WEBSITE, website.getText().toString())) {
                        Toaster.show(ProfileActivity.this, "" + getResources().getString(R.string.website), false, Toaster.DANGER);
                    } else if (!female_radio.isChecked() && !male_radio.isChecked()) {
                        Toaster.show(ProfileActivity.this, "" + getResources().getString(R.string.gender), false, Toaster.DANGER);
                    } else if (targetAudienceModels.isEmpty()) {
                        Toaster.show(ProfileActivity.this, "" + getResources().getString(R.string.intrest_category), false, Toaster.DANGER);
                    } else {
                        try {

                            json_array = new JSONArray();
                            for (int i = 0; i < targetAudienceModels.size(); i++) {
                                if (!targetAudienceModels.get(i).getId().equals("0")) {
                                    JSONObject target_location_obj = new JSONObject();
                                    target_location_obj.put("id", targetAudienceModels.get(i).getId());
                                    json_array.put(target_location_obj);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (GlobalElements.isConnectingToInternet(ProfileActivity.this)) {
                            UpdateUserProfile();
                        } else {
                            GlobalElements.showDialog(ProfileActivity.this);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if (GlobalElements.isConnectingToInternet(ProfileActivity.this)) {
            GetProfile();
        } else {
            GlobalElements.showDialog(ProfileActivity.this);
        }

        try {
            intrest_tag.removeAllViews();
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

                                            targetAudienceModels.remove(i);
                                            intrest_tag.removeView(addView);
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
                add_tag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            for (int i = 0; i < targetAudienceModels.size(); i++) {
                                if (targetAudienceModels.get(i).getId().equals("0")) {
                                    Intent i1 = new Intent(ProfileActivity.this, CategoryChooesActivity.class);
                                    i1.putExtra("type", "1");
                                    i1.putExtra("jsonArray", json_array.toString());
                                    startActivityForResult(i1, 0);
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                intrest_tag.addView(addView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            bottomBar.selectTabAtPosition(4);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(248, intent);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

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
            bottomBar.selectTabAtPosition(4);
        } else if (resultCode == 250) // CategoryChooesActivity  finish activity
        {

            try {
                targetAudienceModels.clear();
                intrest_tag.removeAllViews();

                JSONArray demo = new JSONArray("" + myPreferences.getPreferences(MyPreferences.Intrest));
                json_array = demo;
                for (int j = 0; j < demo.length(); j++) {
                    JSONObject a = demo.getJSONObject(j);
                    GeneralModel da = new GeneralModel();
                    da.setId("" + a.getString("id"));
                    da.setName("" + a.getString("interest_name"));
                    targetAudienceModels.add(da);
                }

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

                                                targetAudienceModels.remove(i);
                                                intrest_tag.removeView(addView);
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
                    add_tag.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                for (int i = 0; i < targetAudienceModels.size(); i++) {
                                    if (targetAudienceModels.get(i).getId().equals("0")) {
                                        Intent i1 = new Intent(ProfileActivity.this, CategoryChooesActivity.class);
                                        i1.putExtra("type", "1");
                                        i1.putExtra("jsonArray", json_array.toString());
                                        startActivityForResult(i1, 0);
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    intrest_tag.addView(addView);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (resultCode == 350) {
            bottomSheetDialogFragment.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == 180) {
            cropImage(outputFileUri);
        } else if (requestCode == 181) {
            try {
                Bitmap largeIcon = MediaStore.Images.Media.getBitmap(getContentResolver(), outputFileUri);
                user_image.setImageBitmap(largeIcon);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (resultCode == 120) {
            bottomBar.selectTabAtPosition(4);
        }
    }

    private void cropImage(Uri uri) {
        // Use existing crop activity.
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(uri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            if (GlobalElements.versionCheck()) {
                cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            }
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, 181);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDateTimePicker() {
        Calendar newCalendar = Calendar.getInstance();
        if (!birthdate.getText().toString().equals("")) {
            String[] date = birthdate.getText().toString().split("\\-");
            String year = date[2];
            String month = date[1];
            String day = date[0];
            newCalendar.set(Calendar.YEAR, Integer.parseInt(year));
            newCalendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);
            newCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
        } else {
            //newCalendar.add(Calendar.YEAR, -18);
        }

        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                birthdate.setText(dateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toDatePickerDialog.getDatePicker().setLayoutMode(1);
        }
        newCalendar = Calendar.getInstance();

        newCalendar.add(Calendar.YEAR, -18);
        toDatePickerDialog.getDatePicker().setMaxDate(newCalendar.getTimeInMillis() - 1000);
        toDatePickerDialog.show();
    }

    private void beginCrop(Uri source) {
        try {
            File tempFileFromSource = File.createTempFile("crop", ".png", ProfileActivity.this.getExternalCacheDir());
            file1 = tempFileFromSource;
            Uri destination = Uri.fromFile(tempFileFromSource);
            Crop.of(source, destination).asSquare().start(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            compressImage(file1);
            user_image.setImageURI(Crop.getOutput(result));
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
                file1 = compressedImage;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void FlickBackPress() {
        try {
            //GlobalElements.redirectFlag_bottomsheet = false;
            bottomSheetDialogFragment = new ChannelBottomSheetDialogFragment();
            bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ChannelDismissEvent() {
        try {
            bottomBar.selectTabAtPosition(4);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* todo get profile */
    private void GetProfile() {
        try {
            pd = new ProgressDialog(ProfileActivity.this);
            pd.setTitle("Please Wait");
            pd.setMessage("Loading");
            pd.setCancelable(true);
            pd.show();
            RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
            Call<ResponseBody> call = request.Getprofile(myPreferences.getPreferences(MyPreferences.ID));

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        pd.dismiss();
                        String json_response = response.body().string();
                        JSONObject json = new JSONObject(json_response);
                        if (json.getInt("ack") == 1) {
                            JSONObject result_obj = json.getJSONObject("result");
                            user_name.setText("" + result_obj.getString("firstname") + " " + result_obj.getString("lastname"));
                            if (!result_obj.getString("birthdate").equals("0000-00-00")) {
                                birthdate.setText("" + GlobalElements.getDateFrom_DD_MM_YYYY(result_obj.getString("birthdate")));
                            }

                            email.setText("" + result_obj.getString("email"));
                            website.setText("" + result_obj.getString("website"));
                            contact.setText("" + result_obj.getString("mobile_no"));

                            if (result_obj.getString("gender").equals("1")) {
                                female_radio.setChecked(true);
                                male_radio.setChecked(false);
                            } else if (result_obj.getString("gender").equals("2")) {
                                female_radio.setChecked(false);
                                male_radio.setChecked(true);
                            }

                            user_detail.setText("" + result_obj.getString("details"));

                            if (!result_obj.getString("user_image_full_path").equals("")) {
                                imageLoader.init(ImageLoaderConfiguration.createDefault(ProfileActivity.this));
                                options = new DisplayImageOptions.Builder()
                                        .showImageForEmptyUri(R.drawable.icon)
                                        .showImageOnFail(R.drawable.icon)
                                        .cacheInMemory(true)
                                        .cacheOnDisk(true)
                                        .build();
                                imageLoader.displayImage(result_obj.getString("user_image_full_path"), user_image, options);
                            }

                            try {
                                JSONArray interest = result_obj.getJSONArray("interest");
                                myPreferences.setPreferences(MyPreferences.Intrest, interest.toString());

                                targetAudienceModels.remove(targetAudienceModels.size() - 1);
                                for (int i = 0; i < interest.length(); i++) {
                                    JSONObject c = interest.getJSONObject(i);
                                    GeneralModel da = new GeneralModel();
                                    da.setId("" + c.get("id"));
                                    da.setName("" + c.getString("interest_name"));
                                    targetAudienceModels.add(da);
                                }
                                GeneralModel da = new GeneralModel();
                                da.setId("0");
                                da.setName("");
                                targetAudienceModels.add(da);
                                intrest_tag.removeAllViews();

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
                                                                intrest_tag.removeView(addView);
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
                                    final TargatAudienceDialogFragment p = new TargatAudienceDialogFragment();
                                    add_tag.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            try {
                                                for (int i = 0; i < targetAudienceModels.size(); i++) {
                                                    if (targetAudienceModels.get(i).getId().equals("0")) {
                                                        //  p.show(fm, "Best Players");
                                                        Intent i1 = new Intent(ProfileActivity.this, CategoryChooesActivity.class);
                                                        i1.putExtra("type", "1");
                                                        i1.putExtra("jsonArray", json_array.toString());
                                                        startActivityForResult(i1, 0);
                                                        break;
                                                    }

                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    intrest_tag.addView(addView);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toaster.show(ProfileActivity.this, "" + json.getString("ack_msg"), true, Toaster.DANGER);
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

    public void UpdateUserProfile() {
        final ProgressDialog pd;
        pd = new ProgressDialog(ProfileActivity.this);
        pd.setMessage("Loading");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        //todo call upload server client
        RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
        //todo request body
        RequestBody requestfile;
        MultipartBody.Part body;
        Call<ResponseBody> call;
        if (file1 != null) {
            requestfile = RequestBody.create(MediaType.parse("image/*"), file1);
            body = MultipartBody.Part.createFormData("file", file1.getName(), requestfile);
            String gender = "", first_name = "", last_name = "";
            if (female_radio.isChecked()) {
                gender = "1";
            } else if (male_radio.isChecked()) {
                gender = "2";
            } else {
                gender = "0";
            }
            String[] parts = user_name.getText().toString().split(" ");
            first_name = parts[0];
            if (parts.length == 2) {
                last_name = parts[1];
            }
            call = request.RegUser(myPreferences.getPreferences(MyPreferences.ID), first_name, last_name, GlobalElements.getDateFrom_YYYY_MM_DD(birthdate.getText().toString()), email.getText().toString(), contact.getText().toString(), website.getText().toString(), gender, user_detail.getText().toString(), json_array.toString(), body);
        } else {
            String gender = "", first_name = "", last_name = "";
            if (female_radio.isChecked()) {
                gender = "1";
            } else if (male_radio.isChecked()) {
                gender = "2";
            } else {
                gender = "0";
            }

            String[] parts = user_name.getText().toString().split(" ");
            first_name = parts[0];
            if (parts.length == 2) {
                last_name = parts[1];
            }
            call = request.RegUser(myPreferences.getPreferences(MyPreferences.ID), first_name, last_name, GlobalElements.getDateFrom_YYYY_MM_DD(birthdate.getText().toString()), email.getText().toString(), contact.getText().toString(), website.getText().toString(), gender, user_detail.getText().toString(), json_array.toString());
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
                        myPreferences.setPreferences(MyPreferences.Intrest, json_array.toString());
                        Toaster.show(ProfileActivity.this, "" + json.getString("ack_msg"), false, Toaster.SUCCESS);
                        finish();
                    } else {
                        Toaster.show(ProfileActivity.this, "" + json.getString("ack_msg"), false, Toaster.DANGER);
                    }
                } catch (IOException e) {
                    pd.dismiss();
                    e.printStackTrace();
                } catch (JSONException e) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            File fdelete = new File("" + imageDir + "/test.jpg");
            boolean deleted = fdelete.delete();
            if (deleted) {
                Log.e("delete", "done");
            } else {
                Log.e("delete", "some error");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
                        if (targetAudienceModels.get(j).getId().equals(da.getId())) {
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
            intrest_tag.removeAllViews();

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

                                            targetAudienceModels.remove(i);
                                            intrest_tag.removeView(addView);
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
                final TargatAudienceDialogFragment p = new TargatAudienceDialogFragment();
                add_tag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            for (int i = 0; i < targetAudienceModels.size(); i++) {
                                if (targetAudienceModels.get(i).getId().equals("0")) {
                                    // p.show(fm, "Best Players");
                                    Intent i1 = new Intent(ProfileActivity.this, CategoryChooesActivity.class);
                                    i1.putExtra("type", "1");
                                    i1.putExtra("jsonArray", json_array.toString());
                                    startActivityForResult(i1, 0);
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                intrest_tag.addView(addView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
