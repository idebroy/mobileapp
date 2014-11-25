package com.idr.trvlr;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
	private LinearLayout bottomLayout;
	private TextView bottomStationName;
	private TextView bottomArrivalTime;
	private String sourceStation;
	private ImageView shareIcon;
	private LocationManager locationManager;
	double lat = -1;
	double lon = -1;
	int count = 0;
	private Location currentLocation;
	private double averageSpeedOfTrain;
	private double totalDistanceCoveredByTrain;
	private double totalTimeTaken;
	private long MIN_TIME = 5000;
	private int distanceBetweenSourceDestination;
	private long currentDateandTime;
	private long trainArrivalTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_destination);

		getActionBar().hide();

		// Initialization
		selectDestinationListView = (ListView) findViewById(R.id.activity_destination_listview);
		trainName = (TextView) findViewById(R.id.activity_select_destination_train_name);
		trainNumber = (TextView) findViewById(R.id.activity_select_destination_train_number);

		bottomLayout = (LinearLayout) findViewById(R.id.activity_destination_bottom_layout);
		bottomStationName = (TextView) findViewById(R.id.activity_destination_bottom_sation_name);
		bottomArrivalTime = (TextView) findViewById(R.id.activity_destination_bottom_estimated_time);
		shareIcon = (ImageView) findViewById(R.id.activity_destination_share);


		locationManager =(LocationManager)SelectDestinationActivity.this.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, 0, this);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME, 0, this);

		// getting intent
		Intent intent = getIntent();
		
		routePointArray = new ArrayList<RoutePoint>();
		routePointArray.clear();
		routePointArray.addAll((ArrayList<RoutePoint>) getIntent().getSerializableExtra("array"));

		sourceStation = getIntent().getStringExtra("sourceStation");

		adapter = new SelectDestinationAdapter(SelectDestinationActivity.this, routePointArray,sourceStation);
		selectDestinationListView.setAdapter(adapter);


		// set values
		trainName.setText(""+getIntent().getStringExtra("TrainName"));
		trainNumber.setText(""+getIntent().getIntExtra("TrainNumber",1)+ " To "+routePointArray.get(routePointArray.size()-1).getDescription());

		// get current time
		Calendar calender = Calendar.getInstance();
		currentDateandTime = calender.getTimeInMillis();
		
		// get  scheduled arrival time on source station
		for(int i=0;i<routePointArray.size();i++)
		{
			if(routePointArray.get(i).getDescription().equalsIgnoreCase(sourceStation))
			{
				trainArrivalTime = routePointArray.get(i).getScheduleTime();
			}
		}



		
		/*
		 * listner on share icon*/
		shareIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
				sharingIntent.setType("text/plain");
				startActivity(Intent.createChooser(sharingIntent, "Share via"));

			}
		});




		// listview on item select
		selectDestinationListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, final View view, final int position,
					long arg3) {
				// TODO Auto-generated method stub
				if(routePointArray.get(position).getDescription().equalsIgnoreCase(sourceStation))
				{
					Toast.makeText(SelectDestinationActivity.this, "Sorry your origin cannot be your destination",Toast.LENGTH_LONG).show();
				}
				else
				{
					AlertDialog.Builder builder = new Builder(SelectDestinationActivity.this);
					builder.setMessage("Set Destination : "+ routePointArray.get(position).getDescription());

					distanceBetweenSourceDestination = calculateDistBetnSrcDest(sourceStation, routePointArray.get(position).getDescription());
					builder.setNegativeButton("Done", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub


							bottomLayout.setVisibility(View.VISIBLE);
							SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss aa");
							Calendar calendar = Calendar.getInstance();
							calendar.setTimeInMillis(routePointArray.get(position).getScheduleTime());

							String time =  dateFormat.format(calendar.getTime());

							adapter.setDestinationPosition(position);
							adapter.notifyDataSetChanged();
							bottomStationName.setText("Destination " +routePointArray.get(position).getDescription());

							bottomArrivalTime.setText("Estimated Arrival "+ ""+time);
						}
					});

					builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

						}
					});

					AlertDialog dialog = builder.create();
					dialog.show();
				}

			}
		});




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
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

		// get current location for the first time
		if(count==0)
		{

			Log.d("SelectDestinationActivity","OnLocationChanged");
			count++;
			lat = location.getLatitude();
			lon = location.getLongitude();

			currentLocation = new Location(LocationManager.GPS_PROVIDER);
			currentLocation.setLatitude(lat);
			currentLocation.setLongitude(lon);
		}


		else
		{

			if(currentDateandTime > trainArrivalTime)
			{

				//double distance =  Utils.calculateDistanceInFLoat(lat, lon, location.getLatitude(), location.getLongitude());
				double distanceBetweenTwoLocation =  currentLocation.distanceTo(location);

				currentLocation = location;

				//bottomLayout.setVisibility(View.VISIBLE);
			//	bottomStationName.setText(""+distanceBetweenTwoLocation + "meter"+"  "+ location.getLatitude()+","+location.getLongitude());


				// calculate speed of train

				// test - let distance between 
				distanceBetweenTwoLocation = 50;
				totalDistanceCoveredByTrain = totalDistanceCoveredByTrain+distanceBetweenTwoLocation;
				totalTimeTaken = totalTimeTaken + 5;
				averageSpeedOfTrain = totalDistanceCoveredByTrain/totalTimeTaken;

				// convert it into km/hr
				averageSpeedOfTrain = averageSpeedOfTrain * 18/5;

				// test - let speed of train be 40 km/hr
				//	averageSpeedOfTrain = 40;




				// estimated time

				if(distanceBetweenSourceDestination!=0)
				{
					double timeToReach = distanceBetweenSourceDestination/averageSpeedOfTrain;  // In hours
					timeToReach = timeToReach*60*60*1000;  // In milliseconds

					// get time of source station
					for(int i=0;i<routePointArray.size();i++)
					{
						if(routePointArray.get(i).getDescription().equalsIgnoreCase(sourceStation))

						{
							timeToReach = timeToReach + routePointArray.get(i).getScheduleTime();
						}
					}

					SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss aa");
					Calendar calendar = Calendar.getInstance();


					//	calendar.setTime(date)
					calendar.setTimeInMillis((long) timeToReach);
					String time =  dateFormat.format(calendar.getTime());

					bottomArrivalTime.setText("Expected Arrival : "+ time);
					
					Log.d("SelectDestinationActivity", "distance between source and location : "+ distanceBetweenSourceDestination);

				}


				Log.d("SelectDestinationActivity", "distance between source and location : "+ distanceBetweenSourceDestination);
				Log.d("SelectDestinationActivity", "speed of train : "+averageSpeedOfTrain);


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
}
