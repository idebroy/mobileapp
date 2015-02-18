package com.idr.trvlr;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.idr.trvlr.sqlite.RouteDataSource;
import com.idr.trvlr.sqlite.RoutePoint;
import com.idr.trvlr.util.Utils;

public class SelectDestinationActivity extends Activity  implements LocationListener{

	private ListView selectDestinationListView;
	private SelectDestinationAdapter adapter;
	private RouteDataSource mDatasource;
	private int trainlId;
	private ArrayList<RoutePoint> routePointArray;
	private TextView trainName;
	private TextView trainNumber;
	private TextView sourceStationText;
	private TextView destStationText;
	private LinearLayout bottomLayout;
	private TextView bottomStationName;
	private TextView bottomArrivalTime;
	private String sourceStation;
	private String nextStation;
	private ImageView shareIcon;
	private LocationManager locationManager;
	double lat = -1;   
	double lon = -1;
	int j=1;
	int count = 0;
	private Location currentLocation;
	private double averageSpeedOfTrain;
	private double totalDistanceCoveredByTrain;
	private double distnceToNextStation;
	private double totalTimeTaken;
	private int distanceBetweenSourceDestination;
	private long currentDateandTime;
	private long trainArrivalTime;
	private LinearLayout enterCoachLayout;
	private TextView coachNoView;
	private int sourceStationPosition;
	private TextToSpeech textToSpeechInstance;
	private double camparableDistance;
	private Intent intentService;
	private ImageView optionsMenu;
	private Location destinationLocation;
	private TextView durationText;
	private ImageView destinationPullDrawerImage;
	private ImageView destinationPushDrawerImage;
	private LinearLayout destinationDrawer;
	private TextView trainNumberText;
	private LinearLayout actionBarTopLayout;
	ObjectAnimator onjectAnimator;
	private MyReceiver myReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_destination);

		// action bar settings
		final ActionBar actionBar = getActionBar();
		actionBar.hide();



		// Initialization
		onjectAnimator = new ObjectAnimator();
		selectDestinationListView = (ListView) findViewById(R.id.activity_destination_listview);
		trainName = (TextView) findViewById(R.id.activity_select_destination_train_name);
		trainNumber = (TextView) findViewById(R.id.activity_select_destination_train_number);
		trainNumberText = (TextView) findViewById(R.id.destination_train_number);
		//sourceStationText = (TextView) findViewById(R.id.activity_select_destination_origin_station);
		destStationText = (TextView) findViewById(R.id.activity_select_destination_name);
		destinationPullDrawerImage = (ImageView) findViewById(R.id.destination_pull_drawer_image);
		destinationPushDrawerImage = (ImageView) findViewById(R.id.destination_push_drawer_image);
		enterCoachLayout =  (LinearLayout) findViewById(R.id.activity_destination_enter_coach_layout);
		destinationDrawer = (LinearLayout) findViewById(R.id.destination_drawer);
		bottomLayout = (LinearLayout) findViewById(R.id.activity_destination_bottom_layout);
		bottomStationName = (TextView) findViewById(R.id.activity_destination_bottom_sation_name);
		bottomArrivalTime = (TextView) findViewById(R.id.activity_destination_bottom_estimated_time);
		durationText = (TextView) findViewById(R.id.activity_destination_bottom_time);
		shareIcon = (ImageView) findViewById(R.id.activity_destination_share);
		optionsMenu = (ImageView) findViewById(R.id.activity_destination_options_menu);
		final EditText selectCoach = (EditText)findViewById(R.id.select_coach);
		final EditText selectBerth = (EditText)findViewById(R.id.select_berth);
		final Spinner coachTypeSpinner = (Spinner)findViewById(R.id.select_coach_type);
		actionBarTopLayout = (LinearLayout) findViewById(R.id.destination_top_layout);
		ArrayList<String> coachSpinnerArray = new ArrayList<String>();
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(SelectDestinationActivity.this, R.layout.destination_spinner_item,coachSpinnerArray);
		final HashMap<String, String> hashmap = new HashMap<String, String>();



		//Register BroadcastReceiver
		//to receive event from our service
		myReceiver = new MyReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(LocationService.MY_ACTION);
		registerReceiver(myReceiver, intentFilter);






		// adding data
		coachSpinnerArray.add("Coach Type");coachSpinnerArray.add("Sleeper Class (S)");coachSpinnerArray.add("AC I (H)");coachSpinnerArray.add("AC II (A)");
		coachSpinnerArray.add("First Class (F)");coachSpinnerArray.add("AC chair car (C)");coachSpinnerArray.add("AC III (B)");
		coachSpinnerArray.add("Second class chair car (D)");coachSpinnerArray.add("Executive class (E)");coachSpinnerArray.add("Garib rath chair car (J)");
		coachSpinnerArray.add("Garib rath AC III (G)");coachSpinnerArray.add("AC I & AC II combiled (HA)");coachSpinnerArray.add("AC II & AC III combined (AB)");	
		coachSpinnerArray.add("High capacity AC III (L)");coachSpinnerArray.add("High capacity Chair car (M)");coachSpinnerArray.add("High capacity Sleeper (N)");





		// mapping coach with their symbols
		hashmap.put(coachSpinnerArray.get(0), "S");hashmap.put(coachSpinnerArray.get(1), "H");hashmap.put(coachSpinnerArray.get(2), "A");hashmap.put(coachSpinnerArray.get(3) ,"F");
		hashmap.put(coachSpinnerArray.get(4), "C");hashmap.put(coachSpinnerArray.get(5), "B");hashmap.put(coachSpinnerArray.get(6), "D");hashmap.put(coachSpinnerArray.get(7), "E");
		hashmap.put(coachSpinnerArray.get(8), "J");hashmap.put(coachSpinnerArray.get(9), "G");hashmap.put(coachSpinnerArray.get(10), "HA");hashmap.put(coachSpinnerArray.get(11), "AB");
		hashmap.put(coachSpinnerArray.get(12), "L");hashmap.put(coachSpinnerArray.get(13), "M");hashmap.put(coachSpinnerArray.get(14), "N");

		coachTypeSpinner.setAdapter(spinnerAdapter);


		coachNoView = (TextView) findViewById(R.id.activity_destination_coach_number);


		locationManager =(LocationManager)SelectDestinationActivity.this.getSystemService(Context.LOCATION_SERVICE);
		// get last known location
		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if (location != null) {
			//  onLocationChanged(location);
			Log.d("SelectDestinationActivity", "GetLastKnownLOcation");
		}


		textToSpeechInstance = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {

			@Override
			public void onInit(int status) {
				// TODO Auto-generated method stub
				textToSpeechInstance.setLanguage(Locale.ENGLISH);

			}
		});








		//		adapter.setSourceStationPosition(5);

		// getting intent
		Intent intent = getIntent();

		routePointArray = new ArrayList<RoutePoint>();
		routePointArray.clear();
		routePointArray.addAll((ArrayList<RoutePoint>) getIntent().getSerializableExtra("array"));
		sourceStation = getIntent().getStringExtra("sourceStation");
		adapter = new SelectDestinationAdapter(SelectDestinationActivity.this, routePointArray,sourceStation);
		selectDestinationListView.setAdapter(adapter);



		/*Restore state from */
		if(getIntent().hasExtra("destinationPosition"))
		{
			if(getIntent().getIntExtra("destinationPosition",1)!=0)
			{

				int positionOfDestination = getIntent().getIntExtra("destinationPosition",1);
				bottomLayout.setVisibility(View.VISIBLE);


				SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(routePointArray.get(positionOfDestination).getScheduleTime());

				String time =  dateFormat.format(calendar.getTime());

				adapter.setDestinationPosition(positionOfDestination);
				adapter.notifyDataSetChanged();
				bottomStationName.setText(routePointArray.get(positionOfDestination).getDescription());
				setDestinationLocation(routePointArray.get(positionOfDestination).getLatitude(),routePointArray.get(positionOfDestination).getLongitude());
				bottomArrivalTime.setText(""+time);
				
				
				distanceBetweenSourceDestination = calculateDistBetnSrcDest(sourceStation, routePointArray.get(positionOfDestination).getDescription());

			}
		}



		// set values
		trainName.setText(""+getIntent().getStringExtra("TrainName"));
		trainNumber.setText(" To "+routePointArray.get(routePointArray.size()-1).getDescription());
		trainNumberText.setText("Train#"+getIntent().getIntExtra("TrainNumber",1));
		//trainNumber.setText(""+getIntent().getIntExtra("TrainNumber",1));

		destStationText.setText(routePointArray.get(routePointArray.size()-1).getDescription());
		//sourceStationText.setText(""+sourceStation);


		// get current time
		Calendar calender = Calendar.getInstance();
		currentDateandTime = calender.getTimeInMillis();

		// get  scheduled arrival time on source station and cell position of source station
		for(int i=0;i<routePointArray.size();i++)
		{
			if(routePointArray.get(i).getDescription().equalsIgnoreCase(sourceStation))
			{
				trainArrivalTime = routePointArray.get(i).getScheduleTime();
				setSourceStationPosition(i);

				Log.d("SelectDestination","calculate train arrival time :"+trainArrivalTime);
			}
		}

		// scroll listview to source station
		selectDestinationListView.post(new Runnable() {
			@Override
			public void run() {
				selectDestinationListView.smoothScrollToPositionFromTop(getSourceStationPosition(), 0, 500);
			}
		});


		/*listner to pull drawr icon*/
		destinationPullDrawerImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				ImageView image = (ImageView) view;
				image.setVisibility(View.GONE);
				destinationDrawer.setVisibility(View.VISIBLE);
				ScaleAnimation scaleAnimation = new ScaleAnimation(0, 0, 0, 300, Animation.RELATIVE_TO_SELF,  Animation.RELATIVE_TO_SELF, 50, 0);
				scaleAnimation.setDuration(1000);
				//destinationDrawer.setAnimation(scaleAnimation);
				//destinationDrawer.startAnimation(scaleAnimation);
			}
		});

		/**/
		actionBarTopLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				destinationPullDrawerImage.setVisibility(View.GONE);
				destinationDrawer.setVisibility(View.VISIBLE);
			}
		});
		/*
		 * listner tp push drawer*/
		destinationPushDrawerImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				destinationPullDrawerImage.setVisibility(View.VISIBLE);
				destinationDrawer.setVisibility(View.GONE);
				//coachNoView.setVisibility(View.VISIBLE);
				//coachNoView.setText(""+hashmap.get(coachTypeSpinner.getSelectedItem())+selectCoach.getText().toString() + " | "+selectBerth.getText().toString());
			}
		});

		/*
		 * listner on share icon*/
		shareIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {


			}
		});

		/*listner on menu*/
		optionsMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
				sharingIntent.setType("text/plain");
				sharingIntent.putExtra(Intent.EXTRA_TEXT, "Using Trvlr App");
				startActivity(Intent.createChooser(sharingIntent, "Share via"));

				// TODO Auto-generated method stub
				PopupMenu popupMenu = new PopupMenu(SelectDestinationActivity.this, view);
				popupMenu.inflate(R.menu.pop_up_menu_destination);
				popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						// TODO Auto-generated method stub

						switch (item.getItemId()) {
						case R.id.switch_on:
							//startService(intentService);
							LocationService.startSpeechOutput();
							break;
						case R.id.switch_off:
							//stopService(intentService);
							LocationService.stopSpeechOutput();
							break;

						case R.id.destination_share: 
							Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
							sharingIntent.setType("text/plain");
							sharingIntent.putExtra(Intent.EXTRA_TEXT, "Using Trvlr App");
							startActivity(Intent.createChooser(sharingIntent, "Share via"));

						default:
							break;
						}
						return false;
					}
				});

				//	popupMenu.show();
			}
		});



		// listview on item select
		selectDestinationListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, final View view, final int position,
					long arg3) {
				// TODO Auto-generated method stub
				//	enterCoachLayout.setVisibility(View.VISIBLE);

				if(routePointArray.get(position).getDescription().equalsIgnoreCase(sourceStation))
				{
					Toast.makeText(SelectDestinationActivity.this, "Sorry your origin cannot be your destination",Toast.LENGTH_LONG).show();
				}
				else
				{

					//  your destination cannot be before your boarding station
					if(position>sourceStationPosition)
					{

						AlertDialog.Builder builder = new Builder(SelectDestinationActivity.this);
						//	builder.setMessage("Set Destination : "+ routePointArray.get(position).getDescription());
						builder.setTitle(routePointArray.get(position).getDescription());

						LayoutInflater inflater = getLayoutInflater();
						View dialoglayout = inflater.inflate(R.layout.select_destination_dialog_view, null);

						final Switch setDestinationSwitch = (Switch) dialoglayout.findViewById(R.id.destination_dialog_set_dest_switch);
						Switch setALertSwitch = (Switch) dialoglayout.findViewById(R.id.destination_dialog_set_alert_switch);

						/*Listner to set ALarm switch*/
						setALertSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
								// TODO Auto-generated method stub
								if(isChecked)
								{

								}
							}
						});

						builder.setView(dialoglayout);



						distanceBetweenSourceDestination = calculateDistBetnSrcDest(sourceStation, routePointArray.get(position).getDescription());



						AlertDialog dialog = builder.create();
						//	dialog.show();

						LocationService.startSpeechOutput();
						bottomLayout.setVisibility(View.VISIBLE);
						SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
						Calendar calendar = Calendar.getInstance();
						calendar.setTimeInMillis(routePointArray.get(position).getScheduleTime());

						String time =  dateFormat.format(calendar.getTime());

						adapter.setDestinationPosition(position);
						LocationService.setDestinationStationPosition(position);
						LocationService.updateNotification();
						adapter.notifyDataSetChanged();
						bottomStationName.setText(routePointArray.get(position).getDescription());
						setDestinationLocation(routePointArray.get(position).getLatitude(),routePointArray.get(position).getLongitude());
						bottomArrivalTime.setText(""+time);
					}
					else
					{
						Toast.makeText(SelectDestinationActivity.this, "Sorry , your destination cannot be before your boarding station", Toast.LENGTH_LONG).show();
					}
				}
			}
		});


		// start service to get next station speech output
		intentService = new Intent(SelectDestinationActivity.this,LocationService.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("array", routePointArray);
		intentService.putExtras(bundle);
		intentService.putExtra("sourceStation", sourceStation);
		intentService.putExtra("TrainName", getIntent().getStringExtra("TrainName"));
		intentService.putExtra("TrainNumber", getIntent().getIntExtra("TrainNumber",1));


		// prevents reseting service 
		if(!getIntent().hasExtra("ActionFromService"))
		{
			stopService(intentService);
			SelectDestinationActivity.this.startService(intentService);
		}


	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d("OnResume", "");


		//locationManager.getBestProvider(criteria, enabledOnly)
		//	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, 0, this);
		//locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME, 0, this);
	}

	protected int calculateDistBetnSrcDest(String src,String dest) {

		double srcLat = 0;
		double srcLong= 0;
		double destLat = 0;
		double destLong = 0;
		for(int i=0;i<routePointArray.size();i++)
		{
			if(routePointArray.get(i).getDescription().equalsIgnoreCase(src))
			{
				srcLat = routePointArray.get(i).getLatitude();
				srcLong = routePointArray.get(i).getLongitude();
			}
			if(routePointArray.get(i).getDescription().equalsIgnoreCase(dest))
			{
				destLat = routePointArray.get(i).getLatitude();
				destLong = routePointArray.get(i).getLongitude();
			}


		}

		return(Utils.calculateDistance(srcLat, srcLong, destLat, destLong));

	}


	@Override
	public void onLocationChanged(Location location) {}



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
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		locationManager.removeUpdates(this);



	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.d("SelectDestinationActivity", "On Stop");
		locationManager.removeUpdates(this);
		//	stopService(intentService);

	}


	public int getSourceStationPosition() {
		return sourceStationPosition;
	}

	public void setSourceStationPosition(int sourceStationPosition) {
		this.sourceStationPosition = sourceStationPosition;
	}



	private void setDestinationLocation(double latitude,
			double longitude) {
		destinationLocation = new Location(LocationManager.GPS_PROVIDER);
		destinationLocation.setLatitude(latitude);
		destinationLocation.setLongitude(longitude);

	}

	private Location getDestinationLocation()
	{
		return destinationLocation;


	}

	public void tracETA(Location location)
	{

		// TODO Auto-generated method stub
		Log.d("SelectDestinationActivity","OnLocationChanged");

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

			SimpleDateFormat dateFormatOld = new SimpleDateFormat("dd MM yyyy hh:mm:ss aa");
			Calendar calendarOld = Calendar.getInstance();


			//	calendar.setTime(date)
			calendarOld.setTimeInMillis((long) currentDateandTime);
			String timeCurrent =  dateFormatOld.format(calendarOld.getTime());

			calendarOld.setTimeInMillis((long) trainArrivalTime);
			String arrivalTime =  dateFormatOld.format(calendarOld.getTime());


			//	Log.d("SelectDestinationActivity", "currentDateandTime : "+timeCurrent+" trainArrivalTime  : "+arrivalTime);


			/*Condition to start ETA*/

			//	if(currentDateandTime > trainArrivalTime)
			//	{


			//double distance =  Utils.calculateDistanceInFLoat(lat, lon, location.getLatitude(), location.getLongitude());
			double distanceBetweenTwoLocation =  currentLocation.distanceTo(location);

			currentLocation = location;

			//bottomLayout.setVisibility(View.VISIBLE);
			//	bottomStationName.setText(""+distanceBetweenTwoLocation + "meter"+"  "+ location.getLatitude()+","+location.getLongitude());


			// calculate speed of train

			// testing - let distance between two location is 50 m
			//distanceBetweenTwoLocation = 50;
			//totalDistanceCoveredByTrain = totalDistanceCoveredByTrain+distanceBetweenTwoLocation;

			averageSpeedOfTrain = distanceBetweenTwoLocation / (LocationService.MIN_TIME/1000);
			// convert it into km/hr
			averageSpeedOfTrain = averageSpeedOfTrain * 18/5;

			// test - let speed of train be 100 km/hr
			//averageSpeedOfTrain = 100; 





			// estimated time

			if(distanceBetweenSourceDestination!=0 && currentLocation!=null)
			{
				//double timeToReach = distanceBetweenSourceDestination/averageSpeedOfTrain;  // In hours
				double distanceFromCurrentToDestination = Math.round((currentLocation.distanceTo(destinationLocation))/1000); // in km
				double timeToReach = distanceFromCurrentToDestination/averageSpeedOfTrain;  // In hours
				timeToReach = timeToReach*60*60*1000;  // In milliseconds


				/*Duration Time*/
				SimpleDateFormat dateFormatTest = new SimpleDateFormat("hh:mm ");
				Calendar calendarTest = Calendar.getInstance();
				calendarTest.setTimeInMillis((long) timeToReach);
				String durationTime =  dateFormatTest.format(calendarTest.getTime());

				durationText.setText(""+durationTime);
				Log.d("SelectDestinationActivity"," time to reach "+durationTime);


				// get time of source station
				for(int i=0;i<routePointArray.size();i++)
				{
					if(routePointArray.get(i).getDescription().equalsIgnoreCase(sourceStation))

					{
						timeToReach = timeToReach + routePointArray.get(i).getScheduleTime(); // add time to origin sceduled arrival time
					}
				}

				SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm aa");
				Calendar calendar = Calendar.getInstance();


				//	calendar.setTime(date)
				calendar.setTimeInMillis((long) timeToReach);
				String time =  dateFormat.format(calendar.getTime());

				bottomArrivalTime.setText(""+ time);

				Log.d("SelectDestinationActivity", "distance between source and location : "+ distanceBetweenSourceDestination);

			}


			Log.d("SelectDestinationActivity", "distance between source and location : "+ distanceBetweenSourceDestination);
			Log.d("SelectDestinationActivity", "speed of train : "+averageSpeedOfTrain+"Distance between two location : "+distanceBetweenTwoLocation);


			//	}

		}


	}

	// Get location from services
	public class MyReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context arg0, Intent intentReceived) {
			// TODO Auto-generated method stub

			Log.d("MyREceiver","In My own receiver");
			Location locationRecieved = new Location(LocationManager.GPS_PROVIDER);

			locationRecieved.setLatitude(intentReceived.getDoubleExtra("Latitude", 1));
			locationRecieved.setLongitude(intentReceived.getDoubleExtra("Longitude", 1));

		
			
			tracETA(locationRecieved);

		}

	}
}
