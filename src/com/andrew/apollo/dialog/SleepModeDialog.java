package com.andrew.apollo.dialog;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.andrew.apollo.MusicPlaybackService;
import com.andrew.apollo.R;
import com.andrew.apollo.utils.MusicUtils;

public class SleepModeDialog extends DialogFragment {

    private static String TAG = "SleepTimerDialog";
    private static final long mMill = 60 * 1000;
    private static AlarmManager alarmManager;
    private static Context context;
    private static PendingIntent pendingIntent;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int minutes = Integer.valueOf(getString(R.string.default_interval));
        context = getActivity().getBaseContext();
        Intent action = new Intent(MusicPlaybackService.SLEEP_MODE_STOP_ACTION);
        ComponentName serviceName = new ComponentName(context, MusicPlaybackService.class);
        action.setComponent(serviceName);
        pendingIntent =  PendingIntent.getService(context, 4, action, 0);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        AlertDialog.Builder builder =new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.sleep_mode_time_selector, null);
        final TextView tvPopUpTime = (TextView)view.findViewById(R.id.pop_up_time);
        tvPopUpTime.setText(String.valueOf(minutes));
        final SeekBar sBar = (SeekBar)view.findViewById(R.id.seekbar);
        sBar.setProgress(minutes-1);
        sBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                tvPopUpTime.setText(String.valueOf(arg1+1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
            }
        });
        builder.setTitle(R.string.select_quit_time);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                long timeLeft = (sBar.getProgress() + 1) * mMill;
                alarmManager.set(AlarmManager.RTC_WAKEUP, timeLeft + System.currentTimeMillis(), pendingIntent);
                MusicUtils.setAlarmSet(true);
                Toast.makeText(context, String.format(getString(R.string.quit_warining), sBar.getProgress() + 1), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.dismiss();
            }
        });
        builder.setView(view);
        return builder.create();
    }

    public static void show(FragmentManager parent) {
        final SleepModeDialog dialog = new SleepModeDialog();
        if(MusicUtils.getAlarmSet()) {
            alarmManager.cancel(pendingIntent);
            Toast.makeText(context, R.string.cancel_sleep_mode, Toast.LENGTH_SHORT).show();
            MusicUtils.setAlarmSet(false);
        } else {
            dialog.show(parent, TAG);
        }
    }
}
