package com.Nflicks;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Nflicks.custom.FontFamily;
import com.Nflicks.custom.Toaster;
import com.Nflicks.custom.Validation;
import com.Nflicks.netUtils.MyPreferences;
import com.Nflicks.netUtils.RequestInterface;
import com.Nflicks.netUtils.RetrofitClient;
import com.soundcloud.android.crop.Crop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

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

public class RegistrationActivity extends AppCompatActivity {

    @BindView(R.id.reg_header)
    TextView regHeader;
    @BindView(R.id.reg_title)
    TextView regTitle;
    @BindView(R.id.reg_user_img)
    CircleImageView regUserImg;
    @BindView(R.id.reg_name)
    EditText regName;
    @BindView(R.id.reg_last_name)
    EditText regLastName;
    @BindView(R.id.reg_desc)
    TextView regDesc;
    @BindView(R.id.reg_submit)
    TextView regSubmit;

    File file1 = null, imageDir;
    Uri outputFileUri;
    String user_img1;
    MyPreferences myPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
        setContentView(R.layout.activity_registration_activiy);
        ButterKnife.bind(this);
        myPreferences = new MyPreferences(RegistrationActivity.this);

        imageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "" + GlobalElements.directory + "");
        if (!imageDir.exists()) {
            imageDir.mkdir();
        }

        regHeader.setTypeface(FontFamily.process(RegistrationActivity.this, R.raw.sqaure721), Typeface.BOLD);
        regTitle.setTypeface(FontFamily.process(RegistrationActivity.this, R.raw.roboto_regular));
        regName.setTypeface(FontFamily.process(RegistrationActivity.this, R.raw.roboto_regular));
        regLastName.setTypeface(FontFamily.process(RegistrationActivity.this, R.raw.roboto_regular));
        regDesc.setTypeface(FontFamily.process(RegistrationActivity.this, R.raw.roboto_regular));
        regSubmit.setTypeface(FontFamily.process(RegistrationActivity.this, R.raw.sqaure721), Typeface.BOLD);

        regUserImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Crop.pickImage(RegistrationActivity.this);
                file1 = null;
                final Dialog dialog = new Dialog(RegistrationActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_gallery);

                LinearLayout camera = (LinearLayout) dialog.findViewById(R.id.dialog_camera);
                LinearLayout gallery = (LinearLayout) dialog.findViewById(R.id.dialog_gallery);
                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        try {
                            user_img1 = imageDir + "/test.jpg";
                            file1 = new File(user_img1);
                            try {
                                file1.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (GlobalElements.versionCheck()) {
                                outputFileUri = FileProvider.getUriForFile(RegistrationActivity.this, "" + GlobalElements.fileprovider_path, file1);
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
                        Crop.pickImage(RegistrationActivity.this);
                    }
                });
                dialog.show();
            }
        });

        regSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Validation.isValid(Validation.BLANK_CHECK, regName.getText().toString())) {
                    Toaster.show(RegistrationActivity.this, "" + getResources().getString(R.string.user_name), false, Toaster.DANGER);
                } else if (!Validation.isValid(Validation.BLANK_CHECK, regLastName.getText().toString())) {
                    Toaster.show(RegistrationActivity.this, "" + getResources().getString(R.string.user_last_name), false, Toaster.DANGER);
                } else {
                    RegUser();
                }
            }
        });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(result.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        } else if (requestCode == 180) {
            cropImage(outputFileUri);
        } else if (requestCode == 181) {
            try {
                Bitmap largeIcon = MediaStore.Images.Media.getBitmap(getContentResolver(), outputFileUri);
                regUserImg.setImageBitmap(largeIcon);
            } catch (IOException e) {
                e.printStackTrace();
            }

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


    private void beginCrop(Uri source) {
        try {
            File tempFileFromSource = File.createTempFile("crop", ".png", RegistrationActivity.this.getExternalCacheDir());
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
            regUserImg.setImageURI(Crop.getOutput(result));
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


    public void RegUser() {
        final ProgressDialog pd;
        pd = new ProgressDialog(RegistrationActivity.this);
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
            call = request.RegUser(myPreferences.getPreferences(MyPreferences.ID), regName.getText().toString(), regLastName.getText().toString(), body);
        } else {
            call = request.RegUser(myPreferences.getPreferences(MyPreferences.ID), regName.getText().toString(), regLastName.getText().toString());
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
                        //Toaster.show(RegistrationActivity.this, "" + json.getString("ack_msg"), false, Toaster.SUCCESS);
                        myPreferences.setPreferences(MyPreferences.status, json.getString("status"));
                        Intent i = new Intent(RegistrationActivity.this, CategoryChooesActivity.class);
                        i.putExtra("type", "0");
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                    } else {
                        Toaster.show(RegistrationActivity.this, "" + json.getString("ack_msg"), false, Toaster.DANGER);
                    }
                } catch (Exception e) {
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
