package com.idr.trvlr;

import java.util.ArrayList;
import java.util.Collections;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.idr.trvlr.sqlite.RouteDataSource;
import com.idr.trvlr.sqlite.RouteDataSource.TrainStation;
import com.idr.trvlr.sqlite.TrainDbOpenHelper;
import com.idr.trvlr.util.Utils;

public class HomeFragment extends Fragment implements LocationListener{
	private TextView boardingStationText;
	private LinearLayout boardingStationLayout;
	private LinearLayout findTrainsLayout;
	private LocationManager locationManager;
	private Location location;
	private RouteDataSource mDataSource;
	private int count=0;
	private HomeActivity activity;
	private Animation translateOutOfScreenAnimation;
	private Animation translateIntoScreenAnimation;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_home, null);
	

		
		/*Initialization*/
		activity = (HomeActivity) getActivity();
		activity.actionBarTitle.setText("Trvlr");
		
		
		//Animation initilize
		 translateOutOfScreenAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_left_out_of_screen);
		 translateIntoScreenAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_into_screnn_from_left);



		boardingStationText = (TextView) v.findViewById(R.id.activity_home_boarding_statn_text);
		findTrainsLayout = (LinearLayout) v.findViewById(R.id.activity_home_select_train_button);
		
		boardingStationText.setText("Fetching Location...");
		
	//	findTrainsLayout.setVisibility(View.VISIBLE);
		locationManager = (LocationManager)getActivity(). getSystemService(Context.LOCATION_SERVICE);
		boardingStationLayout = (LinearLayout) v.findViewById(R.id.boarding_station_layout);
		
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		
		// push button out of screen
		findTrainsLayout.setAnimation(translateOutOfScreenAnimation);
		findTrainsLayout.startAnimation(translateOutOfScreenAnimation);

		if(locationManager!=null)
		{
			if(locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ))
			{
			location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			}
			else
			{
				location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}
		}
		
		if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
	        buildAlertMessageNoGps();
	    }


		mDataSource = RouteDataSource.getRouteDataSource(getActivity());

		//Utils.getAllTrainStations(HomeActivity.this, mDataSource.getDbHelper());

		// setting data to auto complete textview
		final RouteDataSource dataSource = mDataSource;
		TrainDbOpenHelper trainDbOpenHelper = dataSource.getDbHelper();

		// listner to boarding station layout
		boardingStationLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				activity.replceFragment();
				findTrainsLayout.setVisibility(View.INVISIBLE);
			}
		});

		// listener to select train button
		findTrainsLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),TrainListActivity.class);
				intent.putExtra("SRC_STATION",boardingStationText.getText().toString());
				startActivity(intent);


			}
		});

		//displayCurrentStation();

		// method to check whether google play services enabled or not
		//	servicesConnected();



		return v;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(activity.getOriginStationName()!=null)
		{
			boardingStationText.setText(activity.getOriginStationName());
			findTrainsLayout.setVisibility(View.VISIBLE);
			findTrainsLayout.setAnimation(translateIntoScreenAnimation);
			findTrainsLayout.startAnimation(translateIntoScreenAnimation);
			
		}

	}
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub


		Log.d("HomeFragment", "location changed");

		this.location = location;
		

		displayCurrentStation();
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
		Log.d("HomeFragment", "provider disbled : "+provider);

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
		Log.d("HomeFragment", "provider enabled : "+provider);

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

		Log.d("HomeFragment", "statis changed : status : "+status);
	}
	
	  private void buildAlertMessageNoGps() {
		    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		    builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
		           .setCancelable(false)
		           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		               public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
		                   startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		               }
		           })
		           .setNegativeButton("No", new DialogInterface.OnClickListener() {
		               public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
		                    dialog.cancel();
		               }
		           });
		    final AlertDialog alert = builder.create();
		    alert.show();
		}

	void displayCurrentStation()
	{
		if(count==0)
		{
		count++;

		if(location!=null)
		{

			ArrayList<RouteDataSource.TrainStation> originStationArray;
			originStationArray = (ArrayList<RouteDataSource.TrainStation>) mDataSource.getStationsByGeoDistance(100, location.getLatitude(),location.getLongitude());
			//	34.0900¡ N, 74.7900¡ E
			//18.5289 73.8744
			if(!originStationArray.isEmpty())
			{
				for(int i =0; i<originStationArray.size();i++)
				{

//					Location originLocation = new Location(LocationManager.GPS_PROVIDER);
//					originLocation.setLatitude(20.7233);
//					originLocation.setLongitude(77.0057);


					Location locationDestination = new Location(LocationManager.GPS_PROVIDER);
					locationDestination.setLatitude(Double.parseDouble(originStationArray.get(i).getLatitude()));
					locationDestination.setLongitude(Double.parseDouble(originStationArray.get(i).getLongitude()));

					// calculating distance from current origin
					float distanceFromCurrentLocation = location.distanceTo(locationDestination);
					distanceFromCurrentLocation = distanceFromCurrentLocation/1000;
					originStationArray.get(i).setDistanceFromOrigin(distanceFromCurrentLocation);
				}

				// sorting array list as per distance from current location
				Collections.sort(originStationArray, TrainStation.distanceComparator);

				activity.setOriginStationName(originStationArray.get(0).getStationName()) ;
				
				// translate "Select train"
				findTrainsLayout.setVisibility(View.VISIBLE);
				findTrainsLayout.setAnimation(translateIntoScreenAnimation);
				findTrainsLayout.startAnimation(translateIntoScreenAnimation);
				
				boardingStationText.setText(""+originStationArray.get(0).getStationName());
				Log.d("HomeFragment","Latitude :  "+originStationArray.get(0).getLatitude()+ "Longitude : "+originStationArray.get(0).getLongitude()+"Distance : "+originStationArray.get(0).getDistanceFromOrigin());
			}

		}
	}
	}
	
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		locationManager.removeUpdates(HomeFragment.this);
	}
	
	
	// Get location from services
		public class CurrentSationReceiver extends BroadcastReceiver
		{

			@Override
			public void onReceive(Context arg0, Intent intentReceived) {
				// TODO Auto-generated method stub

				Log.d("MyREceiver","In My own receiver");
				Location locationRecieved = new Location(LocationManager.GPS_PROVIDER);

				locationRecieved.setLatitude(intentReceived.getDoubleExtra("Latitude", 1));
				locationRecieved.setLongitude(intentReceived.getDoubleExtra("Longitude", 1));

			
				

			}

		}

}
