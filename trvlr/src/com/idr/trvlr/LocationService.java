/*Service to track next station and give voice output*/


package com.idr.trvlr;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


import com.idr.trvlr.sqlite.RoutePoint;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.StaticLayout;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.ToggleButton;

public class LocationService extends Service implements LocationListener {

	private static ArrayList<RoutePoint> routePointArray;
	private int count;
	private double lat;
	private double lon;
	private Location currentLocation;
	private double totalDistanceCoveredByTrain;
	private double totalTimeTaken;
	private double averageSpeedOfTrain;
	private static String sourceStation;
	private int j=1;
	private double distanceFromStartToNextStation =11;
	private static String nextStation;
	private static TextToSpeech textToSpeechInstance;
	private LocationManager locationManager;
	private static double distnceToNextStation;
	private boolean trainCrossedFirstStation = false;
	static boolean speechOutput = false;
	private int nextStationIndex = 0;
	private static NotificationManager notificationManager;
	private Notification myNotification;
	private static NotificationCompat.Builder mBuilder;
	private static Intent resultIntent;
 static int destinationStationPosition =1;
 public static Context context;
 private static Intent myIntent;
 final static String MY_ACTION = "MY_ACTION";
 public static long MIN_TIME = 5000;




	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int trainCrossedFirstStations, int startId) {
		// TODO Auto-generated method stub
		myIntent = intent;
		Log.d("LocationService", "OnStartCommand");
		context = getApplicationContext();
		routePointArray = new ArrayList<RoutePoint>();
		routePointArray.clear();
		locationManager =(LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, 0, this);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME, 0, this);
		
		
		
		routePointArray.addAll((ArrayList<RoutePoint>) intent.getSerializableExtra("array"));
		
		if (intent.hasExtra("sourceStation")) {
			sourceStation = intent.getStringExtra("sourceStation");
		}

		for(int i=0;i<routePointArray.size();i++)
		{
			if(routePointArray.get(i).getDescription().equalsIgnoreCase(sourceStation))
			{
				nextStation = routePointArray.get(i+1).getDescription();

				if(i+1<=routePointArray.size())
				{
					nextStationIndex = i+1;
				}

				else
				{
					// TODO ....it is last station
				}


			}
		}

		textToSpeechInstance = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {

			@Override
			public void onInit(int status) {
				// TODO Auto-generated method stub
				textToSpeechInstance.setLanguage(Locale.ENGLISH);

			}
		});
		
		
		
		/*Notification settings*/
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
	 mBuilder =
			    new NotificationCompat.Builder(this)
			    .setSmallIcon(R.drawable.loading)
			    .setContentTitle("Travlr")
			    .setContentText("Searching Next Station");
	 
	 mBuilder.setSmallIcon(R.drawable.loading, 10);
			    
		
		//mBuilder.add
		
	//	mBuilder.setContent(notificationView);
		
		
	 resultIntent = new Intent(getApplicationContext(), SelectDestinationActivity.class);
		
		resultIntent.putExtra("array", routePointArray);
		resultIntent.putExtra("sourceStation", sourceStation);
		
		resultIntent.putExtra("TrainName", intent.getStringExtra("TrainName"));
		resultIntent.putExtra("TrainNumber", intent.getIntExtra("TrainNumber",1));
		resultIntent.putExtra("ActionFromService", "ActionFromService");
		
		if(getDestinationStationPosition()!=0)
		{
			resultIntent.putExtra("destinationPosition", LocationService.destinationStationPosition);
		}
		
		PendingIntent resultPendingIntent =
		    PendingIntent.getActivity(
		    this,
		    0,
		    resultIntent,
		    PendingIntent.FLAG_UPDATE_CURRENT
		);
		
		mBuilder.setContentIntent(resultPendingIntent);
		//mBuilder.
		
		
		Intent speechIntent = new Intent(getApplicationContext(),ToggleSpeechOutput.class);
		CharSequence charSeq = "zxcd";
		
		PendingIntent speechPendingIntent =
			    PendingIntent.getBroadcast(
			    this,
			    0,
			    speechIntent,
			    PendingIntent.FLAG_UPDATE_CURRENT
			);
		//mBuilder.addAction(R.drawable.ic_launcher, charSeq, speechPendingIntent);
		
		//notificationView.get
	//	notificationManager.notify(001, mBuilder.build());


		return Service.START_NOT_STICKY;
		
		
		
		
	}

	private void setListener(RemoteViews notificationView) {
		// TODO Auto-generated method stub
	
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		Log.d("Service","OnLocationChanged");

		
		Intent intent = new Intent();
	       intent.setAction(MY_ACTION);
	    intent.putExtra("Latitude", location.getLatitude());
	    intent.putExtra("Longitude", location.getLongitude());
	       sendBroadcast(intent);



		//	 textToSpeechInstance.speak("Hello rupam, Hi Puja,Hi ARun, Hi ankit", TextToSpeech.QUEUE_FLUSH, null);
		// get current location for the first time
		if(count==0)
		{

			Log.d("SelectDestinationActivity","count : "+count);
			count++;
			lat = location.getLatitude();
			lon = location.getLongitude();

			currentLocation = new Location(LocationManager.GPS_PROVIDER);
			currentLocation.setLatitude(lat);
			currentLocation.setLongitude(lon);
		}


		else
		{

			//	Log.d("SelectDestinationActivity", "currentDateandTime : "+currentDateandTime+" trainArrivalTime  : "+trainArrivalTime);


			SimpleDateFormat dateFormatOld = new SimpleDateFormat("dd MM yyyy hh:mm:ss aa");
			Calendar calendarOld = Calendar.getInstance();

			double distanceBetweenTwoLocation =  currentLocation.distanceTo(location);

			currentLocation = location;



			// calculate speed of train

			// test - let distance between two location is 10 km
			distanceBetweenTwoLocation = 10;
			totalDistanceCoveredByTrain = totalDistanceCoveredByTrain + distanceBetweenTwoLocation;
			totalTimeTaken = totalTimeTaken + 1;
			averageSpeedOfTrain = totalDistanceCoveredByTrain/totalTimeTaken;

			// convert it into km/hr
			averageSpeedOfTrain = averageSpeedOfTrain * 18/5;

			// test - let speed of train be 100 km/hr
			//	averageSpeedOfTrain = 100;




			//getNextStaion();

			//currentLocation.setLatitude(19.1724);
			//	currentLocation.setLongitude(72.9570);

			//	getGeoNextStation(currentLocation);

			getGeoNextNewStation(currentLocation);

			// estimated time




			Log.d("LocationService", "speed of train : "+averageSpeedOfTrain);


			//	}

		}
	}

	private void getGeoNextNewStation(Location currentLocation2) {

		Location nextStationLocation = new Location(LocationManager.GPS_PROVIDER);
		nextStationLocation.setLatitude(routePointArray.get(nextStationIndex).getLatitude());
		nextStationLocation.setLongitude(routePointArray.get(nextStationIndex).getLongitude());

		
		distnceToNextStation = currentLocation2.distanceTo(nextStationLocation);
		distnceToNextStation = Math.round(distnceToNextStation/1000);

		CharSequence charSeq1 = "Next station : "+nextStation;
		CharSequence charSeq2 = distnceToNextStation+ " Kms";
		mBuilder.setContentText(charSeq2);
		mBuilder.setContentTitle(charSeq1);
		notificationManager.notify(002, mBuilder.build());
		
		// if distance is less then 5km ...speech op enables
		if(distnceToNextStation<5 && distnceToNextStation>0)
		{
			
			
			if(speechOutput)
			{
			textToSpeechInstance.speak("Next station is "+nextStation+"and distance is : "+distnceToNextStation+"Kilometers", TextToSpeech.QUEUE_FLUSH, null);
			}
		}
		else
		{
			textToSpeechInstance.stop();
		}

		// train arrived at station , change the next station 
		if(distnceToNextStation==0 || distnceToNextStation<1)
		{
			if(routePointArray.size()<=nextStationIndex+1)
			{
				nextStationIndex = nextStationIndex+1;
			}
			// arrived at last station
			else
			{
				stopSelf();
			}
		}


	}


	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void onTaskRemoved(Intent rootIntent) {
		// TODO Auto-generated method stub
		super.onTaskRemoved(rootIntent);
		Log.d("Service", "On TaskRemoved");
		stopSelf();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d("Service", "On Destroy");
		locationManager.removeUpdates(this);
		notificationManager.cancel(002);
		stopSelf();
	}

	public static void stopSpeechOutput()
	{
		speechOutput = false;
	}
	public static void startSpeechOutput()
	{
		speechOutput = true;
	}

	void getNextStaion ()
	{
		// get the next station
		for(int i=0;i<routePointArray.size();i++)
		{
			if(routePointArray.get(i).getDescription().equalsIgnoreCase(sourceStation))

			{


				if(i+j+1<routePointArray.size())
				{

					if(totalDistanceCoveredByTrain>distanceFromStartToNextStation)
					{
						j++;
						distanceFromStartToNextStation = distanceFromStartToNextStation +routePointArray.get(i+j).getDistance();
						nextStation = routePointArray.get(i+j).getDescription();

						trainCrossedFirstStation = true;
						Log.d("SelectDestinationActivity if", "nextStation :"+nextStation);
						Log.d("SelectDestinationActivity if", "distnceToNextStation :"+ ""+Math.abs((distanceFromStartToNextStation-totalDistanceCoveredByTrain)/1000));

						if(speechOutput)
						{
							textToSpeechInstance.speak("Next station is "+nextStation+"and distance is : "+(distanceFromStartToNextStation-totalDistanceCoveredByTrain)+"Kilometers", TextToSpeech.QUEUE_FLUSH, null);
						}
					}
					else
					{

						// train has not crossed first station
						if(!trainCrossedFirstStation)
						{
							distnceToNextStation = routePointArray.get(i+j).getDistance();
							distanceFromStartToNextStation = distnceToNextStation;
						}
						else
						{
							distnceToNextStation = distanceFromStartToNextStation;
						}

						nextStation = routePointArray.get(i+j).getDescription();
						distnceToNextStation = distnceToNextStation - totalDistanceCoveredByTrain;

						Log.d("SelectDestinationActivity else ", "nextStation :"+nextStation);
						Log.d("SelectDestinationActivity else", "distnceToNextStation :"+distnceToNextStation/1000);

						if(speechOutput)
						{
							textToSpeechInstance.speak("Next station is "+nextStation+"and distance is : "+distnceToNextStation+"Kilometers", TextToSpeech.QUEUE_FLUSH, null);
						}
					}
				}
				//		


			}
		}

	}

	public static int getDestinationStationPosition() {
		
		return LocationService.destinationStationPosition;
		
	}

	public static void setDestinationStationPosition(int destinationStation) {
		LocationService.destinationStationPosition = destinationStation;
	}
	
	
	public static  void updateNotification()
	{
		
		Log.d("LocationService","updateNotification");
		Intent newIntent = new Intent(context,SelectDestinationActivity.class);
		newIntent.putExtra("array", routePointArray);
		newIntent.putExtra("sourceStation", sourceStation);
		
		newIntent.putExtra("TrainName", myIntent.getStringExtra("TrainName"));
		newIntent.putExtra("TrainNumber", myIntent.getIntExtra("TrainNumber",1));
		newIntent.putExtra("ActionFromService", "ActionFromService");
		newIntent.putExtra("destinationPosition", LocationService.destinationStationPosition);
		
		if(getDestinationStationPosition()!=0)
		{
			newIntent.putExtra("destinationPosition", LocationService.destinationStationPosition);
		}
		
		PendingIntent resultPendingIntent =
		    PendingIntent.getActivity(
		    context,
		    0,
		    newIntent,
		    PendingIntent.FLAG_UPDATE_CURRENT
		);
		
		mBuilder.setContentIntent(resultPendingIntent);
		
		notificationManager.cancel(001);
		notificationManager.cancelAll();
		
		notificationManager.notify(002, mBuilder.build());
	}
	

	
}
