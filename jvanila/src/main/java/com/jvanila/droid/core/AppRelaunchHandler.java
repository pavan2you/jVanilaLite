/*
 * Copyright (C) 2015 - 2018 The jVanila Open Source Project
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * author - pavan.jvanila@gmail.com
 */

package com.jvanila.droid.core;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;

/**
 * Created by pavan on 12/01/16.
 *
 */
public class AppRelaunchHandler extends BroadcastReceiver {

    public static void relauchAfter(Context context, int afterMillis) {

        Intent relaunchListener = new Intent(context, AppRelaunchHandler.class);
        PendingIntent sender = PendingIntent.getBroadcast(
                context.getApplicationContext(), 1000, relaunchListener,
                PendingIntent.FLAG_CANCEL_CURRENT);

        long triggerAtMillis = System.currentTimeMillis() + afterMillis;
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis + afterMillis,
                    sender);
        }
        else {
            am.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, sender);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        System.out.println("AppRelaunchHandler # onReceive... "
                + context.getApplicationContext());
        launchPackage(context);
    }

    private void launchPackage(Context context) {
        Intent intent = getPackageIntent(context);
        try {
            context.startActivity(intent);
        }
        catch (Exception e) {
            try {
                PendingIntent sender = PendingIntent.getActivity(context, 1001,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);
                sender.send();
            }
            catch (PendingIntent.CanceledException ce) {
                ce.printStackTrace();
            }
        }
    }

    private Intent getPackageIntent(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(
                context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        return intent;
    }
}
