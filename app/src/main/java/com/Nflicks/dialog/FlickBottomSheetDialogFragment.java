package com.Nflicks.dialog;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.Nflicks.GlobalElements;
import com.Nflicks.MainActivity;
import com.Nflicks.ProfileActivity;
import com.Nflicks.QrCodeActivity;
import com.Nflicks.R;
import com.Nflicks.SearchActivity;
import com.Nflicks.custom.ImageInputHelper;
import com.Nflicks.custom.ScaleImageView;
import com.Nflicks.custom.Toaster;
import com.Nflicks.custom.Validation;
import com.Nflicks.netUtils.MyPreferences;
import com.Nflicks.netUtils.RequestInterface;
import com.Nflicks.netUtils.RetrofitClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by CRAFT BOX on 8/18/2017.
 */

public class FlickBottomSheetDialogFragment extends BottomSheetDialogFragment implements ImageInputHelper.ImageActionListener {

    boolean flag = false;  // reopen channel dialog dont remove this flag
    ScaleImageView flick_img;
    Button select_image, attachment, flick_send, flick_send_sms;
    EditText detail_edt, flick_name_edt;
    TextView detail_character_txt;
    File image_file = null, document_file = null;
    private ImageInputHelper imageInputHelper;
    View contentView;

    String file_path = "";
    String type = ""; // todo  type 0 = add flick or 1 = update flick
    public static final int PERMISSIONS_REQUEST_CODE = 0;
    private ArrayList<String> docPaths = new ArrayList<>();


    /* channel variable */
    CircleImageView channel_img;
    TextView channel_name_txt, channel_follower_txt;
    String channel_id, fid;  // todo  fid = flick id
    int flick_position;
    private DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    MyPreferences myPreferences;
    BottomBar bottomBar;
    String flick_upload_flag="";
    boolean temp_flag = false;
    public interface Intercommunication {
        public void FlickBackPress();
    }

    public interface FlickUpdateIntercommunication {
        public void Update_flick(int position, String title, String details);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                flag = true;
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        contentView = View.inflate(getContext(), R.layout.dialog_flick, null);
        dialog.setContentView(contentView);
        dialog.setCanceledOnTouchOutside(true);
        myPreferences = new MyPreferences(getActivity());
        flick_upload_flag="";

        ImageView back = (ImageView) contentView.findViewById(R.id.dialog_flick_back);
        flick_img = (ScaleImageView) contentView.findViewById(R.id.dialog_flick_image);
        select_image = (Button) contentView.findViewById(R.id.dialog_flick_upload_image);
        attachment = (Button) contentView.findViewById(R.id.dialog_flick_attachment);
        flick_send = (Button) contentView.findViewById(R.id.dialog_flick_send);
        flick_send_sms = (Button) contentView.findViewById(R.id.dialog_flick_send_sms);

        flick_name_edt = (EditText) contentView.findViewById(R.id.create_flick_name_edt);
        detail_edt = (EditText) contentView.findViewById(R.id.dialog_flick_detail_edt);
        detail_character_txt = (TextView) contentView.findViewById(R.id.dialog_flick_detail_character_txt);

        channel_img = (CircleImageView) contentView.findViewById(R.id.channel_img);
        channel_name_txt = (TextView) contentView.findViewById(R.id.channel_name);
        channel_follower_txt = (TextView) contentView.findViewById(R.id.channel_follower);

        try {
            temp_flag = false;
            bottomBar = (BottomBar) contentView.findViewById(R.id.bottomBar);
            //GlobalElements.setBottomBarListner(getActivity(),"ChannelBottomSheetDialogFragment");
            bottomBar.selectTabAtPosition(2);
            bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
                @Override
                public void onTabSelected(@IdRes int tabId) {
                    try {
                        if (tabId == R.id.tab_main) {
                            Intent i = new Intent(getActivity(), MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        } else if (tabId == R.id.tab_search) {
                            Intent i = new Intent(getActivity(), SearchActivity.class);
                            startActivityForResult(i, 0);
                        } else if (tabId == R.id.tab_create_channel) {

                        } else if (tabId == R.id.tab_qrcode) {
                            Intent i = new Intent(getActivity(), QrCodeActivity.class);
                            startActivityForResult(i, 0);
                        } else if (tabId == R.id.tab_profile) {
                            Intent i = new Intent(getActivity(), ProfileActivity.class);
                            startActivityForResult(i, 0);
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
                        if (tabId == R.id.tab_create_channel) {
                            if (tabId == R.id.tab_main) {
                                Intent i = new Intent(getActivity(), MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            } else if (tabId == R.id.tab_search) {
                                Intent i = new Intent(getActivity(), SearchActivity.class);
                                startActivityForResult(i, 0);
                            } else if (tabId == R.id.tab_create_channel) {

                            } else if (tabId == R.id.tab_qrcode) {
                                Intent i = new Intent(getActivity(), QrCodeActivity.class);
                                startActivityForResult(i, 0);
                            } else if (tabId == R.id.tab_profile) {
                                Intent i = new Intent(getActivity(), ProfileActivity.class);
                                startActivityForResult(i, 0);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


        final View touchOutsideView = getDialog().getWindow().getDecorView().findViewById(android.support.design.R.id.touch_outside);
        touchOutsideView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (type.equals("0")) {
                    flag = true;
                    dismiss();
                } else {
                    dismiss();
                }
            }
        });

        try {
            Bundle b = getArguments();
            channel_id = b.getString("channel_id");
            type = b.getString("type");

            channel_name_txt.setText("" + b.getString("channel_name"));
            channel_follower_txt.setText("" + b.getString("follower") + "\n" + getResources().getString(R.string.followers));

            if (!b.getString("channel_image_path").equals("")) {
                imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
                options = new DisplayImageOptions.Builder()
                        .showImageForEmptyUri(R.drawable.icon)
                        .showImageOnFail(R.drawable.icon)
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .build();
                imageLoader.displayImage(b.getString("channel_image_path"), channel_img, options);
            }


            if (type.equals("1")) {
                flick_position = b.getInt("position", 0);
                fid = b.getString("fid");
                flick_send.setText("" + getResources().getString(R.string.update_flicks));
                flick_send_sms.setText("" + getResources().getString(R.string.update_flick_sms));

                flick_name_edt.setText("" + b.getString("flick_title"));
                detail_edt.setText("" + b.getString("flick_details"));

                if (detail_edt.length() == 280) {
                    detail_character_txt.setText(Html.fromHtml("<font color='" + ContextCompat.getColor(getActivity(), R.color.red) + "' >" + detail_edt.length() + "</font> <font color='" + ContextCompat.getColor(getActivity(), R.color.light_gray) + "'> of 280</font>"));
                } else {
                    detail_character_txt.setText(Html.fromHtml("<font color='" + ContextCompat.getColor(getActivity(), R.color.green) + "' >" + detail_edt.length() + "</font> <font color='" + ContextCompat.getColor(getActivity(), R.color.light_gray) + "'> of 280</font>"));
                }

                select_image.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.green));
                attachment.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.green));
            } else {
                flick_send.setText("" + getResources().getString(R.string.send_flick));
                flick_send_sms.setText("" + getResources().getString(R.string.send_flick_sms));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        flick_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Validation.isValid(Validation.BLANK_CHECK, flick_name_edt.getText().toString())) {
                    Toaster.show(getActivity(), "" + getActivity().getResources().getString(R.string.flick_title), false, Toaster.DANGER);
                } else if (!Validation.isValid(Validation.BLANK_CHECK, detail_edt.getText().toString())) {
                    Toaster.show(getActivity(), "" + getActivity().getResources().getString(R.string.flick_detail), false, Toaster.DANGER);
                } else {
                    if(flick_upload_flag.equals(""))
                    {
                        flick_upload_flag="1";
                        if (type.equals("0")) {
                            CreateFlick("0");
                        } else {
                            UpdateFlick(fid, "0");
                        }
                    }
                }
            }
        });
        flick_send_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Validation.isValid(Validation.BLANK_CHECK, flick_name_edt.getText().toString())) {
                    Toaster.show(getActivity(), "" + getActivity().getResources().getString(R.string.flick_title), false, Toaster.DANGER);
                } else if (!Validation.isValid(Validation.BLANK_CHECK, detail_edt.getText().toString())) {
                    Toaster.show(getActivity(), "" + getActivity().getResources().getString(R.string.flick_detail), false, Toaster.DANGER);
                } else {
                    if(flick_upload_flag.equals(""))
                    {
                        flick_upload_flag="1";
                        if (type.equals("0")) {
                            CreateFlick("1");
                        } else {
                            UpdateFlick(fid, "1");
                        }
                    }
                }
            }
        });

        imageInputHelper = new ImageInputHelper(this);
        imageInputHelper.setImageActionListener(this);
        attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("0")) {
                    try {
                        if (file_path.equals("")) {
                            checkPermissionsAndOpenFilePicker();
                        } else {
                            AlertDialog buildInfosDialog;
                            AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(getActivity());
                            alertDialog2.setMessage("Are you sure want change file");

                            alertDialog2.setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Write your code here to execute after dialog
                                            checkPermissionsAndOpenFilePicker();
                                        }
                                    });

                            alertDialog2.setNegativeButton("NO",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Write your code here to execute after dialog

                                        }
                                    });

                            alertDialog2.setNeutralButton("Remove File",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Write your code here to execute after dialog
                                            document_file = null;
                                            file_path = "";
                                            attachment.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.lighter_gray));
                                        }
                                    });

                            buildInfosDialog = alertDialog2.create();
                            buildInfosDialog.show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        detail_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    if (charSequence.length() == 280) {
                        detail_character_txt.setText(Html.fromHtml("<font color='" + ContextCompat.getColor(getActivity(), R.color.red) + "' >" + charSequence.length() + "</font> <font color='" + ContextCompat.getColor(getActivity(), R.color.light_gray) + "'> of 280</font>"));
                    } else {
                        detail_character_txt.setText(Html.fromHtml("<font color='" + ContextCompat.getColor(getActivity(), R.color.green) + "' >" + charSequence.length() + "</font> <font color='" + ContextCompat.getColor(getActivity(), R.color.light_gray) + "'> of 280</font>"));
                    }
                } else {
                    detail_character_txt.setText("0 of 280");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals("0")) {
                    try {
                        if (image_file == null) {
                            imageInputHelper.selectImageFromGallery();
                        } else {
                            AlertDialog buildInfosDialog;
                            AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(getActivity());
                            alertDialog2.setMessage("Are you sure want change image");

                            alertDialog2.setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Write your code here to execute after dialog
                                            imageInputHelper.selectImageFromGallery();
                                        }
                                    });

                            alertDialog2.setNegativeButton("NO",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Write your code here to execute after dialog

                                        }
                                    });

                            alertDialog2.setNeutralButton("Remove Image",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Write your code here to execute after dialog
                                            image_file = null;
                                            select_image.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.lighter_gray));
                                        }
                                    });

                            buildInfosDialog = alertDialog2.create();
                            buildInfosDialog.show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals("0")) {
                    flag = true;
                    dismiss();
                } else {
                    dismiss();
                }
            }
        });

        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(android.content.DialogInterface dialog,
                                 int keyCode, android.view.KeyEvent event) {
                if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
                    if (type.equals("0")) {
                        flag = true;
                        dismiss();
                    } else {
                        dismiss();
                    }
                    return true;
                } else return false;
            }
        });


        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int height = size.y;
            ((BottomSheetBehavior) behavior).setPeekHeight((height / 2) + 400);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        bottomBar.selectTabAtPosition(2);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {

            if (type.equals("0")) {
                if (flag) {
                    flag = false;
                    dismiss();
                    Intercommunication intercommunication = (Intercommunication) getActivity();
                    intercommunication.FlickBackPress();
                } else {
                    System.out.print("");
                }
            } else {
                dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* choose image part*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 23) {
            imageInputHelper.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == 234) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                docPaths = new ArrayList<>();
                docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));

                for (String s : docPaths) {
                    file_path = s;
                }
                document_file = new File(file_path);
                // Get length of file in bytes
                long fileSizeInBytes = document_file.length();
                // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
                long fileSizeInKB = fileSizeInBytes / 1024;
                // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
                long fileSizeInMB = fileSizeInKB / 1024;
                System.out.print("" + fileSizeInMB);
                if (fileSizeInMB > 25) {
                    Toast.makeText(getActivity(), "" + getResources().getString(R.string.file_size), Toast.LENGTH_LONG).show();
                } else {
                    attachment.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.green));
                }
            }
        }
    }

    @Override
    public void onImageSelectedFromGallery(Uri uri, File imageFile) {
        try {
            if (GlobalElements.versionCheck()) {
                this.image_file = new File(getPath(uri));
                compressImage(this.image_file);
            } else {
                this.image_file = new File(uri.getPath());
                compressImage(this.image_file);
            }
            if (this.image_file != null) {
                select_image.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.green));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compressImage(File file) {
        if (file == null) {
            //showError("Please choose an image!");
        } else {
            try {
                File compressedImage = new Compressor(getActivity())
                        .setQuality(75)
                        .setCompressFormat(Bitmap.CompressFormat.WEBP)
                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES).getAbsolutePath())
                        .compressToFile(file);
                image_file = compressedImage;
                //flick_name_edt.setText(String.format("Size : %s", getReadableFileSize(image_file.length())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(column_index);
        cursor.close();
        return s;
    }

    @Override
    public void onImageTakenFromCamera(Uri uri, File imageFile) {

    }

    @Override
    public void onImageCropped(Uri uri, File imageFile) {

    }

    private void checkPermissionsAndOpenFilePicker() {
        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
                Toast.makeText(getActivity(), "Allow external storage reading", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, PERMISSIONS_REQUEST_CODE);
            }
        } else {
            FilePickerBuilder.getInstance().setMaxCount(1)
                    .setSelectedFiles(docPaths)
                    .setActivityTheme(R.style.FilePickerTheme)
                    .pickDocument(getActivity());
        }
    }

    public void CreateFlick(String flick_send_flag) {
        final ProgressDialog pd;
        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading");
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        pd.show();

        //todo call upload server client
        RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
        //todo request body
        RequestBody requestfile_image, requestfile_document;
        MultipartBody.Part body_image, body_document;
        Call<ResponseBody> call;
        if (image_file != null && document_file != null) {
            requestfile_image = RequestBody.create(MediaType.parse("image/*"), image_file);
            body_image = MultipartBody.Part.createFormData("image", image_file.getName(), requestfile_image);

            requestfile_document = RequestBody.create(MediaType.parse("*/*"), document_file);
            body_document = MultipartBody.Part.createFormData("document", document_file.getName(), requestfile_document);
            call = request.CreateFlick(myPreferences.getPreferences(MyPreferences.ID), channel_id, flick_name_edt.getText().toString(), detail_edt.getText().toString(), flick_send_flag, body_image, body_document);
        } else if (image_file != null) {
            requestfile_image = RequestBody.create(MediaType.parse("image/*"), image_file);
            body_image = MultipartBody.Part.createFormData("image", image_file.getName(), requestfile_image);
            call = request.CreateFlick(myPreferences.getPreferences(MyPreferences.ID), channel_id, flick_name_edt.getText().toString(), detail_edt.getText().toString(), flick_send_flag, body_image);
        } else if (document_file != null) {
            requestfile_document = RequestBody.create(MediaType.parse("*/*"), document_file);
            body_document = MultipartBody.Part.createFormData("document", document_file.getName(), requestfile_document);
            call = request.CreateFlick(myPreferences.getPreferences(MyPreferences.ID), channel_id, flick_name_edt.getText().toString(), detail_edt.getText().toString(), flick_send_flag, body_document);
        } else {
            call = request.CreateFlick(myPreferences.getPreferences(MyPreferences.ID), channel_id, flick_name_edt.getText().toString(), detail_edt.getText().toString(), flick_send_flag);
        }

        call.enqueue(new Callback<ResponseBody>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pd.dismiss();
                flick_upload_flag="";
                try {
                    String json_responce = response.body().string();
                    JSONObject json = new JSONObject(json_responce);
                    if (json.getInt("ack") == 1) {
                        Toaster.show(getActivity(), "" + json.getString("ack_msg"), false, Toaster.SUCCESS);
                        flag = true;
                        dismiss();
                    } else {
                        Toaster.show(getActivity(), "" + json.getString("ack_msg"), false, Toaster.DANGER);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    flick_upload_flag="";
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pd.dismiss();
                flick_upload_flag="";
            }
        });
    }

    public void UpdateFlick(String fid, String flick_send_flag) {
        final ProgressDialog pd;
        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        //todo call upload server client
        RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
        //todo request body
        Call<ResponseBody> call;
        call = request.UpdateFlick(myPreferences.getPreferences(MyPreferences.ID), fid, flick_name_edt.getText().toString(), detail_edt.getText().toString(), flick_send_flag);

        call.enqueue(new Callback<ResponseBody>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pd.dismiss();
                flick_upload_flag="";
                try {
                    String json_responce = response.body().string();
                    JSONObject json = new JSONObject(json_responce);
                    if (json.getInt("ack") == 1) {
                        Toaster.show(getActivity(), "" + json.getString("ack_msg"), false, Toaster.SUCCESS);
                        FlickUpdateIntercommunication intercommunication = (FlickUpdateIntercommunication) getActivity();
                        intercommunication.Update_flick(flick_position, flick_name_edt.getText().toString(), detail_edt.getText().toString());
                        dismiss();
                    } else {
                        Toaster.show(getActivity(), "" + json.getString("ack_msg"), false, Toaster.DANGER);
                    }
                } catch (Exception e) {
                    pd.dismiss();
                    flick_upload_flag="";
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pd.dismiss();
                flick_upload_flag="";
            }
        });
    }
}
