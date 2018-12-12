package com.Nflicks;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.multidex.MultiDex;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.Nflicks.custom.FontFamily;
import com.Nflicks.model.UserChannelListModel;
import com.gdacciaro.iOSDialog.iOSDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by CRAFT BOX on 7/27/2017.
 */

public class GlobalElements extends Application {

    public static String message_pack_name = "ROYALA";

    public static String directory = "notiflick";

    //public static boolean redirectFlag_bottomsheet = true;
    public static BottomSheetDialogFragment Flick_bottomSheetDialogFragment;

    public static boolean flag_onresume = false;
    public static String fileprovider_path = "com.Nflicks.fileprovider";
    public static int on_resume_flag = 100; // todo on_resume_flag = 101 (following activity refresh) ,on_resume_flag = 102 (channel list activity refresh)
    public static int follow_unfollow_flag_position = 0;
    public static int follow_unfollow_flag = 0;
    public static String channel_id = "0";

    public static ArrayList<UserChannelListModel> userChannelListModels;

    public static int ll = 20; // todo ll mins lower limit

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public interface Intercommunication_open {
        public void open();
    }

    public interface Intercommunication_close {
        public void close(String type);
    }

    public static void overrideFonts_roboto_regular(final Context context, final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts_roboto_regular(context, child);
                }
            } else if (v instanceof EditText) {
                ((EditText) v).setTypeface(FontFamily.process(context, R.raw.roboto_regular));
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(FontFamily.process(context, R.raw.roboto_regular));
            }
        } catch (Exception e) {
        }
    }

    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (info != null) {
                if (info.isConnected()) {
                    return true;
                } else {
                    NetworkInfo info1 = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    if (info1.isConnected()) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    public static void showDialog(Context context) {
        final iOSDialog iOSDialog = new iOSDialog(context);
        iOSDialog.setTitle("Internet");
        iOSDialog.setSubtitle("Please check your internet connection... ");
        iOSDialog.setPositiveLabel("Ok");
        iOSDialog.setBoldPositiveLabel(true);

        iOSDialog.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this,"OK clicked",Toast.LENGTH_SHORT).show();
                iOSDialog.dismiss();
            }
        });
        iOSDialog.show();
    }

    public static String getDateFrom_DD_MM_YYYY(String get_date) {

        if (get_date.equals("")) {
            return "";
        } else {
            DateFormat outputFormat = null;
            Date date = null;
            try {
                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                date = dateFormatter.parse("" + get_date);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return outputFormat.format(date);
        }
    }

    public static String getDateFrom_YYYY_MM_DD(String get_date) {

        if (get_date.equals("")) {
            return "";
        } else {
            DateFormat outputFormat = null;
            Date date = null;
            try {
                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                date = dateFormatter.parse("" + get_date);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return outputFormat.format(date);
        }
    }

    public static String getCapitalized(String name) {
        StringBuilder sb = new StringBuilder(name);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    public static boolean getVersionCheck() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    public static String getRemoveLastComma(String text) {
        String refine_txt = null;
        try {
            refine_txt = text.substring(0, text.lastIndexOf(","));
            return refine_txt;
        } catch (Exception e) {
            e.printStackTrace();
            return refine_txt = "";
        }
    }

    public static GradientDrawable RoundedButtion_White_redies_10(Context context) {
        float radii = context.getResources().getDimension(R.dimen.linear_redius_0);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[]{radii, radii, radii, radii, radii, radii, radii, radii});
        shape.setStroke(2, ContextCompat.getColor(context, R.color.divider));
        shape.setColor(ContextCompat.getColor(context, R.color.white));
        return shape;
    }

    public static GradientDrawable FollowButtion(Context context) {
        float radii = context.getResources().getDimension(R.dimen.linear_redius_10);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[]{radii, radii, radii, radii, radii, radii, radii, radii});
        shape.setStroke(1, ContextCompat.getColor(context, R.color.black));
        shape.setColor(ContextCompat.getColor(context, R.color.black));
        return shape;
    }

    public static GradientDrawable UnFollowButtion(Context context) {
        float radii = context.getResources().getDimension(R.dimen.linear_redius_10);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[]{radii, radii, radii, radii, radii, radii, radii, radii});
        shape.setStroke(2, ContextCompat.getColor(context, R.color.divider));
        shape.setColor(ContextCompat.getColor(context, R.color.lighter_gray));
        return shape;
    }

    public static GradientDrawable RoundedButtion_White(Context context) {
        float radii = context.getResources().getDimension(R.dimen.linear_redius_25);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[]{radii, radii, radii, radii, radii, radii, radii, radii});
        shape.setStroke(2, ContextCompat.getColor(context, R.color.divider));
        shape.setColor(ContextCompat.getColor(context, R.color.white));
        return shape;
    }

    public static GradientDrawable RoundedButtion_Lighter_gray(Context context) {
        float radii = context.getResources().getDimension(R.dimen.linear_redius_25);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[]{radii, radii, radii, radii, radii, radii, radii, radii});
        shape.setStroke(2, ContextCompat.getColor(context, R.color.divider));
        shape.setColor(ContextCompat.getColor(context, R.color.lighter_gray));
        return shape;
    }

    public static boolean versionCheck() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }
}
