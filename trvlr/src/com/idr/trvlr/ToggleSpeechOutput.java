package com.idr.trvlr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.WebView.FindListener;
import android.widget.ImageView;
import android.widget.RemoteViews;

public class ToggleSpeechOutput extends BroadcastReceiver{

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		Log.d("ToggleSpeechOutput","SpeechOutput");
		
		//RemoteViews smallViews = new RemoteViews(g, R.layout.notification_layout_big);
	}

}
