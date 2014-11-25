package com.idr.trvlr.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.format.DateFormat;
import android.widget.TextView;

import com.idr.trvlr.R;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by indroniel on 7/18/14.
 */
public class TimeTickReceiver extends BroadcastReceiver {
    private final WeakReference<TextView> mTimeView;

    public TimeTickReceiver(TextView timeView,Context ctx) {
        mTimeView = new WeakReference<TextView>(timeView);
        mTimeView.get().setText(DateFormat.getTimeFormat(ctx).format(new Date()));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
            final TextView timeView = mTimeView.get();
            if (timeView != null) {
                timeView.setText(DateFormat.getTimeFormat(context).format(new Date()));
            }
        }
    }

    public static void registerInstance(Context ctx,ITimeTickReceiverSetter timeTickReceiver,TextView timeView){
        if(timeTickReceiver.getTimeTickerInstance()==null){
            timeTickReceiver.setTimeTickerInstance(new TimeTickReceiver(timeView,ctx));
            ctx.registerReceiver(timeTickReceiver.getTimeTickerInstance(),
                    new IntentFilter(Intent.ACTION_TIME_TICK));
        }
    }

    public static void unResisterInstance(Context ctx,ITimeTickReceiverSetter timeTickReceiver){
        if (timeTickReceiver.getTimeTickerInstance() != null) {
            ctx.unregisterReceiver(timeTickReceiver.getTimeTickerInstance());
            timeTickReceiver.setTimeTickerInstance(null);
        }
    }


    public interface ITimeTickReceiverSetter{
        public void setTimeTickerInstance(TimeTickReceiver instance);
        public TimeTickReceiver getTimeTickerInstance();
    }
};
