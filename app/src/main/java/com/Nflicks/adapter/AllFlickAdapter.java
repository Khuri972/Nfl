package com.Nflicks.adapter;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Nflicks.ChannelDescriptionActivity;
import com.Nflicks.GlobalElements;
import com.Nflicks.R;
import com.Nflicks.SavedFlickActivity;
import com.Nflicks.custom.ExpandableTextView;
import com.Nflicks.custom.FontFamily;
import com.Nflicks.custom.ScaleImageView;
import com.Nflicks.custom.Toaster;
import com.Nflicks.dialog.FlickBottomSheetDialogFragment;
import com.Nflicks.model.ChannelDescriptionModel;
import com.Nflicks.netUtils.MyPreferences;
import com.Nflicks.netUtils.RequestInterface;
import com.Nflicks.netUtils.RetrofitClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by CRAFT BOX on 10/10/2016.
 */
public class AllFlickAdapter extends RecyclerView.Adapter<AllFlickAdapter.ViewHolder> {
    private ArrayList<ChannelDescriptionModel> data = new ArrayList<>();
    private Context context;
    AlertDialog buildInfosDialog;
    String filename, file_success = "fail";
    static long img_name;
    private DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    MyPreferences myPreferences;

    public interface ChannelDescriptionNotifidata {
        public void notifidata(int position);
    }

    public AllFlickAdapter(Context context, ArrayList<ChannelDescriptionModel> data) {
        this.data = data;
        this.context = context;
        myPreferences = new MyPreferences(context);
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_saved_flick, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        GlobalElements.overrideFonts_roboto_regular(context, viewHolder.layout);
        viewHolder.title.setTypeface(FontFamily.process(context, R.raw.roboto_regular), Typeface.BOLD);

        if (i == 0) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(30, 30, 30, 15);
            viewHolder.cardView.setLayoutParams(params);
        } else if (data.size() == i + 1) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(30, 15, 30, 30);
            viewHolder.cardView.setLayoutParams(params);
        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(30, 15, 30, 15);
            viewHolder.cardView.setLayoutParams(params);
        }

        viewHolder.channel_name.setTypeface(FontFamily.process(context, R.raw.roboto_regular));
        viewHolder.detail.setTypeface(FontFamily.process(context, R.raw.roboto_regular));

        viewHolder.channel_name.setText("" + data.get(i).getChannel_name());
        viewHolder.title.setText("" + data.get(i).getTitle());
        try {
            viewHolder.detail.setText("" + GlobalElements.fromHtml(data.get(i).getDetail()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (data.get(i).getChannel_image_path().equals("")) {

        } else {

            options = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.default_image)
                    .showImageOnFail(R.drawable.default_image)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();
            imageLoader.displayImage(data.get(i).getChannel_image_path(), viewHolder.channel_img, options);
        }

        if (data.get(i).getImage().equals("")) {
            viewHolder.imageView.setVisibility(View.GONE);
        } else {

            options = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.default_image)
                    .showImageOnFail(R.drawable.default_image)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();
            imageLoader.displayImage(data.get(i).getImage(), viewHolder.imageView, options);
            viewHolder.imageView.setVisibility(View.VISIBLE);
        }

        viewHolder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(context);
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View dialogView = inflater.inflate(R.layout.dialog_flick_more, null);
                    LinearLayout layout = (LinearLayout) dialogView.findViewById(R.id.dialog_flick_main_layout);
                    GlobalElements.overrideFonts_roboto_regular(context, layout);
                    TextView copy_txt = (TextView) dialogView.findViewById(R.id.dialog_flick_copy);
                    LinearLayout linear_spam = (LinearLayout) dialogView.findViewById(R.id.dialog_flick_spam_linear);
                    LinearLayout linear_inappropriate = (LinearLayout) dialogView.findViewById(R.id.dialog_flick_inappropriate_linear);
                    LinearLayout linear_delete_flick = (LinearLayout) dialogView.findViewById(R.id.dialog_flick_delete_linear);
                    LinearLayout linear_update_flick = (LinearLayout) dialogView.findViewById(R.id.dialog_flick_update_linear);
                    TextView spam = (TextView) dialogView.findViewById(R.id.dialog_flick_spam);
                    TextView inappropriate = (TextView) dialogView.findViewById(R.id.dialog_flick_inappropriate);
                    TextView whatsapp = (TextView) dialogView.findViewById(R.id.dialog_flick_whatsapp);

                    if (data.get(i).getIsSpamed() == 1) {
                        linear_spam.setVisibility(View.GONE);
                    }

                    if (data.get(i).getIsInAppropriate() == 1) {
                        linear_inappropriate.setVisibility(View.GONE);
                    }

                    try {
                        if (data.get(i).getChannel_owner_id().equals(myPreferences.getPreferences(MyPreferences.ID))) {
                            linear_spam.setVisibility(View.GONE);
                            linear_inappropriate.setVisibility(View.GONE);
                            linear_delete_flick.setVisibility(View.VISIBLE);
                            linear_update_flick.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    linear_delete_flick.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DeleteFlick(i, data.get(i).getId());
                        }
                    });

                    linear_update_flick.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                buildInfosDialog.dismiss();
                                GlobalElements.Flick_bottomSheetDialogFragment = new FlickBottomSheetDialogFragment();
                                Bundle b = new Bundle();
                                b.putString("channel_id", data.get(i).getChannel_owner_id());
                                b.putString("channel_name", data.get(i).getChannel_name());
                                b.putString("channel_image_path", data.get(i).getChannel_image_path());
                                b.putString("follower", data.get(i).getC_follower());
                                b.putString("type", "1");
                                b.putString("flick_title", data.get(i).getTitle());
                                b.putString("flick_details", data.get(i).getDetail());
                                b.putString("fid", data.get(i).getId());
                                b.putInt("position", i);
                                GlobalElements.Flick_bottomSheetDialogFragment.setArguments(b);
                                GlobalElements.Flick_bottomSheetDialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), GlobalElements.Flick_bottomSheetDialogFragment.getTag());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    spam.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (GlobalElements.isConnectingToInternet(context)) {
                                SpamOrInappropriate(data.get(i).getId(), i, "Spam");
                            } else {
                                GlobalElements.showDialog(context);
                            }
                        }
                    });


                    inappropriate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (GlobalElements.isConnectingToInternet(context)) {
                                SpamOrInappropriate(data.get(i).getId(), i, "");
                            } else {
                                GlobalElements.showDialog(context);
                            }
                        }
                    });

                    whatsapp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                if (!data.get(i).getImage().equals("")) {
                                    Canvas c = null;
                                    File root = Environment.getExternalStorageDirectory();
                                    img_name = System.currentTimeMillis();
                                    viewHolder.imageView.setDrawingCacheEnabled(true);
                                    Bitmap bitmap = viewHolder.imageView.getDrawingCache();
                                    int w = bitmap.getWidth();
                                    int h = bitmap.getHeight() - 95; // -100
                                    Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.imag);
                                    Canvas canvas = new Canvas(bitmap);
                                    Paint paint = new Paint();
                                    paint.setColor(Color.WHITE); // Text Color
                                    paint.setTextSize(70); // Text Size
                                    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)); // Text Overlapping Pattern
                                    // some more settings...

                                    int ic = icon.getWidth();

                                    canvas.drawBitmap(icon, w - ic, h, paint);
                                    // canvas.drawText("Notiflick", 10, h, paint);
                                    //canvas.setBitmap(icon);
                                    File cachePath = new File(root.getAbsolutePath() + "/DCIM/Camera/" + img_name + ".jpg");
                                    try {
                                        cachePath.createNewFile();
                                        FileOutputStream ostream = new FileOutputStream(cachePath);
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                                        ostream.close();

                                        Intent share = new Intent(Intent.ACTION_SEND);
                                        share.setType("image/*");
                                        Uri contentUri = null;
                                        if (GlobalElements.versionCheck()) {
                                            contentUri = FileProvider.getUriForFile(context, "" + GlobalElements.fileprovider_path, cachePath);
                                        } else {
                                            contentUri = Uri.fromFile(cachePath);
                                        }
                                        share.putExtra(Intent.EXTRA_STREAM, contentUri);
                                        share.putExtra(Intent.EXTRA_TEXT, "" + myPreferences.getPreferences(MyPreferences.sharable_url) + data.get(i).getShareable_url());
                                        ((Activity) context).startActivityForResult(share, 1);
                                        buildInfosDialog.dismiss();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    try {
                                        Intent i1 = new Intent(Intent.ACTION_SEND);
                                        i1.setType("text/plain");
                                        i1.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                                        i1.putExtra(Intent.EXTRA_TEXT, "" + myPreferences.getPreferences(MyPreferences.sharable_url) + data.get(i).getShareable_url());
                                        context.startActivity(Intent.createChooser(i1, "Share URL"));
                                        buildInfosDialog.dismiss();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    copy_txt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                    clipboard.setText("" + data.get(i).getShareable_url());
                                    Toaster.show(context, "" + context.getResources().getString(R.string.copy_url), false, Toaster.SUCCESS);
                                } else {
                                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("Copied Text", "" + myPreferences.getPreferences(MyPreferences.sharable_url) + data.get(i).getShareable_url());
                                    clipboard.setPrimaryClip(clip);
                                    Toaster.show(context, "" + context.getResources().getString(R.string.copy_url), false, Toaster.SUCCESS);
                                }
                                buildInfosDialog.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    alertDialog2.setView(dialogView);
                    buildInfosDialog = alertDialog2.create();
                    buildInfosDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        viewHolder.share_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!data.get(i).getImage().equals("")) {
                        Canvas c = null;
                        File root = Environment.getExternalStorageDirectory();
                        img_name = System.currentTimeMillis();
                        viewHolder.imageView.setDrawingCacheEnabled(true);
                        Bitmap bitmap = viewHolder.imageView.getDrawingCache();
                        int w = bitmap.getWidth();
                        int h = bitmap.getHeight() - 95; // -100
                        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.imag);
                        Canvas canvas = new Canvas(bitmap);
                        Paint paint = new Paint();
                        paint.setColor(Color.WHITE); // Text Color
                        paint.setTextSize(70); // Text Size
                        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)); // Text Overlapping Pattern
                        // some more settings...

                        int ic = icon.getWidth();

                        canvas.drawBitmap(icon, w - ic, h, paint);
                        File cachePath = new File(root.getAbsolutePath() + "/DCIM/Camera/" + img_name + ".jpg");
                        try {
                            cachePath.createNewFile();
                            FileOutputStream ostream = new FileOutputStream(cachePath);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                            ostream.close();
                            Intent share = new Intent(Intent.ACTION_SEND);
                            share.setType("image/*");
                            Uri contentUri = null;
                            if (GlobalElements.versionCheck()) {
                                contentUri = FileProvider.getUriForFile(context, "" + GlobalElements.fileprovider_path, cachePath);
                            } else {
                                contentUri = Uri.fromFile(cachePath);
                            }
                            share.putExtra(Intent.EXTRA_STREAM, contentUri);
                            share.putExtra(Intent.EXTRA_TEXT, "" + myPreferences.getPreferences(MyPreferences.sharable_url) + data.get(i).getShareable_url());
                            ((Activity) context).startActivityForResult(share, 1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            Intent i1 = new Intent(Intent.ACTION_SEND);
                            i1.setType("text/plain");
                            i1.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                            i1.putExtra(Intent.EXTRA_TEXT, "" + myPreferences.getPreferences(MyPreferences.sharable_url) + data.get(i).getShareable_url());
                            context.startActivity(Intent.createChooser(i1, "Share URL"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if (data.get(i).getFile_path().equals("")) {
            viewHolder.download_img.setVisibility(View.GONE);
        } else {
            viewHolder.download_img.setVisibility(View.VISIBLE);
        }

        viewHolder.download_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!data.get(i).getFile_path().equals("")) {
                    try {
                        filename = data.get(i).getFile_path().substring(data.get(i).getFile_path().lastIndexOf("/") + 1);
                        new DownloadFileFromURL(data.get(i).getFile_path(), filename).execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        viewHolder.save_flick.setImageResource(R.drawable.save);
        if (myPreferences.getPreferences(MyPreferences.ID).equals("" + data.get(i).getChannel_owner_id())) {
            viewHolder.save_flick.setVisibility(View.GONE);
        } else {
            if (data.get(i).getIsBookmark() == 0) {
                viewHolder.save_flick.setVisibility(View.VISIBLE);
            } else {
                viewHolder.save_flick.setVisibility(View.GONE);
            }
        }

        viewHolder.save_flick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GlobalElements.isConnectingToInternet(context)) {
                    try {
                        SaveFlick(data.get(i).getId(), i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    GlobalElements.showDialog(context);
                }
            }
        });

        if (data.get(i).getCreated_date_string().equals("")) {
            viewHolder.adate_string.setVisibility(View.GONE);
        } else {
            viewHolder.adate_string.setText("" + data.get(i).getCreated_date_string());
            viewHolder.adate_string.setVisibility(View.VISIBLE);
        }

        viewHolder.channel_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i1 = new Intent(context, ChannelDescriptionActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("channel_id", data.get(i).getChannel_id());
                    b.putSerializable("channel_name", data.get(i).getChannel_name());
                    b.putSerializable("channel_follower", data.get(i).getC_follower());
                    b.putSerializable("channel_details", data.get(i).getDetail());
                    b.putSerializable("channel_image_path", data.get(i).getChannel_image_path());
                    b.putSerializable("channel_sharable_url", data.get(i).getShareable_url());
                    b.putSerializable("channel_qr_code", data.get(i).getQr_code_path());
                    b.putSerializable("channel_privacy", data.get(i).getChannel_privacy());
                    b.putString("activity_type", "0");
                    i1.putExtras(b);
                    ((Activity) context).startActivityForResult(i1, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.list_channel_desc_image)
        ScaleImageView imageView;
        @BindView(R.id.list_channel_desc_more)
        ImageView more;
        @BindView(R.id.list_channel_desc_detail)
        ExpandableTextView detail;
        @BindView(R.id.list_channel_desc_main)
        LinearLayout layout;
        @BindView(R.id.list_channel_desc_title)
        TextView title;
        @BindView(R.id.cardview)
        CardView cardView;
        @BindView(R.id.list_channel_desc_share)
        ImageView share_img;
        @BindView(R.id.list_channel_desc_download)
        ImageView download_img;
        @BindView(R.id.list_channel_desc_save_flick)
        ImageView save_flick;
        @BindView(R.id.list_channel_desc_adate_string)
        TextView adate_string;
        @BindView(R.id.list_channel_img)
        CircleImageView channel_img;
        @BindView(R.id.list_channel_title)
        TextView channel_name;
        @BindView(R.id.channel_layout) LinearLayout channel_layout;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class DownloadFileFromURL extends AsyncTask<String, Integer, Integer> {
        int notificationId;
        private NotificationManager mNotifyManager;
        private NotificationCompat.Builder mBuilder;
        String file_success = "";

        private String file_url, file_name;

        public DownloadFileFromURL(String file_url, String file_name) {
            super();
            this.file_url = file_url;
            this.file_name = file_name;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Random r = new Random();
            notificationId = r.nextInt(80 - 65) + 65;
            Intent viewIntent = new Intent();
            viewIntent.setAction(Intent.ACTION_VIEW);
            File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "" + GlobalElements.directory);
            String file_path = folder + "/" + file_name;
            String extension_file = "jpg";
            try {
                String[] extension = file_name.split("\\.");
                extension_file = extension[1];
            } catch (Exception e) {
                e.printStackTrace();
            }
            Uri contentUri = null;

            if (GlobalElements.getVersionCheck()) {
                viewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                contentUri = FileProvider.getUriForFile(context, "" + GlobalElements.fileprovider_path, new File("" + file_path));
            } else {
                contentUri = Uri.fromFile(new File("" + file_path));
            }
            viewIntent.setDataAndType(contentUri, MimeTypeMap.getSingleton().getMimeTypeFromExtension("" + extension_file));  // you can also change jpeg to other types
            PendingIntent viewPendingIntent = PendingIntent.getActivity(context, notificationId, viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder = new NotificationCompat.Builder(context);
            mBuilder.setContentTitle("Download")
                    .setContentText("Download in progress")
                    .setContentIntent(viewPendingIntent)
                    .setSmallIcon(R.drawable.ic_file_download_black_24dp);
            mBuilder.setProgress(100, 0, false);
            mBuilder.setAutoCancel(true);
            mNotifyManager.notify(notificationId, mBuilder.build());
        }

        @Override
        protected Integer doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(file_url);
                URLConnection conection = url.openConnection();
                conection.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "" + GlobalElements.directory);
                if (!folder.exists()) {
                    folder.mkdir();
                }
                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream
                OutputStream output = new FileOutputStream(folder + "/" + file_name);

                byte data[] = new byte[1024];

                long total = 0;
                for (int i = 0; i <= 100; i += 5) {
                    // Sets the progress indicator completion percentage
                    publishProgress(Math.min(i, 100));
                    try {
                        // Sleep for 5 seconds
                        Thread.sleep(2 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                while ((count = input.read(data)) != -1) {
                    total += count;
                    output.write(data, 0, count);
                }
                // flushing output
                output.flush();
                // closing streams
                output.close();
                input.close();
                file_success = "success";
            } catch (Exception e) {
                e.printStackTrace();
                file_success = "error";
            }
            return null;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(Integer... values) {
            // setting progress percentage
            mBuilder.setProgress(100, values[0], false);
            mNotifyManager.notify(notificationId, mBuilder.build());
            super.onProgressUpdate(values);
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(Integer file_url) {
            try {
                mBuilder.setContentText("Download complete");
                mBuilder.setProgress(0, 0, false);
                mNotifyManager.notify(notificationId, mBuilder.build());
                if (file_success.equals("success")) {
                    Toaster.show(context, "Download Successfully In Your " + GlobalElements.directory + " Folder In Sdcard", true, Toaster.SUCCESS);
                } else {
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(notificationId);
                    Toaster.show(context, "" + context.getResources().getString(R.string.file_not_found), false, Toaster.DANGER);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void SaveFlick(String fid, final int position) {
        try {

            RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
            Call<ResponseBody> call = request.SaveFlick(myPreferences.getPreferences(MyPreferences.ID), fid);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {

                        String json_response = response.body().string();
                        JSONObject json = new JSONObject(json_response);
                        if (json.getInt("ack") == 1) {

                            try {
                                ChannelDescriptionModel da = data.get(position);
                                da.setIsBookmark(1);
                                notifyItemChanged(position);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toaster.show(context, "" + json.getString("ack_msg"), true, Toaster.DANGER);
                        }
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

    private void SpamOrInappropriate(String fid, final int position, final String Spam_flag) {
        try {

            RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
            Call<ResponseBody> call;
            if (Spam_flag.equals("Spam")) {
                call = request.ReportSpam(myPreferences.getPreferences(MyPreferences.ID), fid);
            } else {
                call = request.ReportInappropriate(myPreferences.getPreferences(MyPreferences.ID), fid);
            }

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {

                        String json_response = response.body().string();
                        JSONObject json = new JSONObject(json_response);
                        if (Spam_flag.equals("Spam")) {
                            if (json.getInt("ack") == 1) {
                                ChannelDescriptionNotifidata notifidata = (ChannelDescriptionNotifidata) context;
                                notifidata.notifidata(position);
                                buildInfosDialog.dismiss();
                            } else {
                                Toaster.show(context, "" + json.getString("ack_msg"), true, Toaster.DANGER);
                            }
                        } else {
                            ChannelDescriptionNotifidata notifidata = (ChannelDescriptionNotifidata) context;
                            notifidata.notifidata(position);
                            buildInfosDialog.dismiss();
                        }
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

    private void DeleteFlick(final int position, String fid) {
        try {

            RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
            Call<ResponseBody> call = request.DeleteFlick(myPreferences.getPreferences(MyPreferences.ID), fid);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {

                        String json_response = response.body().string();
                        JSONObject json = new JSONObject(json_response);
                        if (json.getInt("ack") == 1) {
                            ChannelDescriptionNotifidata notifidata = (ChannelDescriptionNotifidata) context;
                            notifidata.notifidata(position);
                            buildInfosDialog.dismiss();
                        } else {
                            Toaster.show(context, "" + json.getString("ack_msg"), true, Toaster.DANGER);
                        }
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
