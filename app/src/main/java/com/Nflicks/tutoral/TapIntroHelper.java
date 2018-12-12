package com.Nflicks.tutoral;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.Nflicks.R;
import com.Nflicks.netUtils.MyPreferences;
import com.danimahardhika.android.helpers.core.ColorHelper;
import com.danimahardhika.android.helpers.core.UnitHelper;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

/*
 * Wallpaper Board
 *
 * Copyright (c) 2017 Dani Mahardhika
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class TapIntroHelper {

    /* todo dashboard tutoral */
    public static void showDashboardIntro(@NonNull final Context context, @ColorInt final int color) {
        final MyPreferences myPreferences = new MyPreferences(context);
        try {
            if (myPreferences.getTutoralPreferences(MyPreferences.main_activity_tutorial).equals("")) {
                final AppCompatActivity activity = (AppCompatActivity) context;
                final Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int primary = ColorHelper.getTitleTextColor(color);
                        int secondary = ColorHelper.setColorAlpha(primary, 0.7f);
                        TapTargetSequence tapTargetSequence = new TapTargetSequence(activity);
                        tapTargetSequence.continueOnCancel(true);

                        View tab_main = activity.findViewById(R.id.tab_main); //home
                        if (tab_main != null) {
                            TapTarget tapTarget = TapTarget.forView(tab_main,
                                    context.getResources().getString(R.string.tap_intro_main_tab_home_apply),
                                    context.getResources().getString(R.string.tap_intro_main_tab_home_desc_apply))
                                    .titleTextColorInt(primary)
                                    .descriptionTextColorInt(secondary)
                                    .targetCircleColorInt(primary)
                                    .outerCircleColorInt(color)
                                    .drawShadow(true);
                            tapTargetSequence.target(tapTarget);
                        }

                        View tab_search = activity.findViewById(R.id.tab_search); //search
                        if (tab_search != null) {
                            TapTarget tapTarget = TapTarget.forView(tab_search,
                                    context.getResources().getString(R.string.tap_intro_main_tab_search_apply),
                                    context.getResources().getString(R.string.tap_intro_main_tab_search_desc_apply))
                                    .titleTextColorInt(primary)
                                    .descriptionTextColorInt(secondary)
                                    .targetCircleColorInt(primary)
                                    .outerCircleColorInt(color)
                                    .drawShadow(true);
                            tapTargetSequence.target(tapTarget);
                        }

                        View tab_create_channel = activity.findViewById(R.id.tab_create_channel); //create channel
                        if (tab_create_channel != null) {
                            TapTarget tapTarget = TapTarget.forView(tab_create_channel,
                                    context.getResources().getString(R.string.tap_intro_main_tab_create_channel_apply),
                                    context.getResources().getString(R.string.tap_intro_main_tab_create_channel_desc_apply))
                                    .titleTextColorInt(primary)
                                    .descriptionTextColorInt(secondary)
                                    .targetCircleColorInt(primary)
                                    .outerCircleColorInt(color)
                                    .drawShadow(true);
                            tapTargetSequence.target(tapTarget);
                        }

                        View tab_qrcode = activity.findViewById(R.id.tab_qrcode); // qr code
                        if (tab_qrcode != null) {
                            TapTarget tapTarget = TapTarget.forView(tab_qrcode,
                                    context.getResources().getString(R.string.tap_intro_main_tab_qr_code_apply),
                                    context.getResources().getString(R.string.tap_intro_main_tab_qr_code_desc_apply))
                                    .titleTextColorInt(primary)
                                    .descriptionTextColorInt(secondary)
                                    .targetCircleColorInt(primary)
                                    .outerCircleColorInt(color)
                                    .drawShadow(true);
                            tapTargetSequence.target(tapTarget);
                        }

                        View tab_profile = activity.findViewById(R.id.tab_profile); //setting
                        if (tab_profile != null) {
                            TapTarget tapTarget = TapTarget.forView(tab_profile,
                                    context.getResources().getString(R.string.tap_intro_main_tab_profile_apply),
                                    context.getResources().getString(R.string.tap_intro_main_tab_profile_desc_apply))
                                    .titleTextColorInt(primary)
                                    .descriptionTextColorInt(secondary)
                                    .targetCircleColorInt(primary)
                                    .outerCircleColorInt(color)
                                    .drawShadow(true);
                            tapTargetSequence.target(tapTarget);
                        }


                        if (toolbar != null) {
                            //toolbar.inflateMenu(R.menu.dashboard);

                            TapTarget tapTarget_more = TapTarget.forToolbarMenuItem(toolbar, R.id.main_more,
                                    context.getResources().getString(R.string.tap_intro_main_more_apply),
                                    context.getResources().getString(R.string.tap_intro_main_more_desc_apply))
                                    .titleTextColorInt(primary)
                                    .descriptionTextColorInt(secondary)
                                    .targetCircleColorInt(primary)
                                    .outerCircleColorInt(color)
                                    .drawShadow(true);
                            tapTargetSequence.target(tapTarget_more);

                            TapTarget tapTarget_notification = TapTarget.forToolbarMenuItem(toolbar, R.id.main_save_notification,
                                    context.getResources().getString(R.string.tap_intro_main_notification_apply),
                                    context.getResources().getString(R.string.tap_intro_main_notification_desc_apply))
                                    .titleTextColorInt(primary)
                                    .descriptionTextColorInt(secondary)
                                    .targetCircleColorInt(primary)
                                    .outerCircleColorInt(color)
                                    .drawShadow(true);
                            tapTargetSequence.target(tapTarget_notification);

                            TapTarget tapTarget = TapTarget.forToolbarMenuItem(toolbar, R.id.main_save_flick,
                                    context.getResources().getString(R.string.tap_intro_main_saved_apply),
                                    context.getResources().getString(R.string.tap_intro_main_saved_desc_apply))
                                    .titleTextColorInt(primary)
                                    .descriptionTextColorInt(secondary)
                                    .targetCircleColorInt(primary)
                                    .outerCircleColorInt(color)
                                    .drawShadow(true);
                            tapTargetSequence.target(tapTarget);

                        }

                        tapTargetSequence.listener(new TapTargetSequence.Listener() {
                            @Override
                            public void onSequenceFinish() {
                                myPreferences.setTutoralPreferences(MyPreferences.main_activity_tutorial, "1");
                            }

                            @Override
                            public void onSequenceStep(TapTarget tapTarget, boolean b) {

                            }

                            @Override
                            public void onSequenceCanceled(TapTarget tapTarget) {

                            }
                        });
                        tapTargetSequence.start();
                    }
                }, 100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
