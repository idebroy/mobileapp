package com.idr.trvlr;

import com.crashlytics.android.Crashlytics;
import java.util.List;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;


import com.idr.trvlr.sqlite.RouteDataSource;
import com.idr.trvlr.sqlite.RoutePoint;
import com.idr.trvlr.sqlite.TrainDbOpenHelper;
import com.idr.trvlr.util.SuggestionAdapter;
import com.idr.trvlr.util.Utils;

public class HomeActivity extends FragmentActivity {

	private TextView boardingStationText;
	private LocationManager locationManager;

	//private Location location;
	// flag for GPS status
	boolean isGPSEnabled = false;
	// flag for network status
	boolean isNetworkEnabled = false;
	// flag for GPS status
	boolean canGetLocation = false;

	int count = 0;
	double latitude; // latitude
	double longitude; // longitude
	private static RouteDataSource mDataSource;
	public String originStationName;
	public TextView actionBarTitle;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		Crashlytics.start(this);

	
		// Service to send data to server
		Intent intent = new Intent(this,GlobalService.class);
		//startService(intent);
		

		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActionBar().setCustomView(R.layout.action_bar_view);
		getActionBar().hide();

		actionBarTitle = (TextView) findViewById(R.id.action_bar_home_title);
		getSupportFragmentManager().beginTransaction().replace(R.id.activity_home_frame, new HomeFragment()).commit();
		
	
		Log.d("HomeActivity",""+TrainDbOpenHelper.SYNC_DATA_TABLE_CREATE);
	//	RouteDataSource.getRouteDataSource(HomeActivity.this).insertValuesSyncTable();
	//	RouteDataSource.getRouteDataSource(HomeActivity.this).getValuesSyncTable();
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		actionBarTitle.setText("Trvlr");
	}



	public static int findIndex(List<RoutePoint> routePoints, RoutePoint routePoint){
		int idx=0;
		for(RoutePoint p : routePoints){
			if( p.equals(routePoint)){
				return idx;
			}
			idx++;
		}
		return -1;
	}





	/**
	 * Verify that Google Play services is available before making a request.
	 *
	 * @return true if Google Play services is available, otherwise false
	 */
	
	/*
	private boolean servicesConnected() {

		// Check that Google Play services is available
		int resultCode =
				GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			//   Log.d(Utils.class.getName(), getString(R.string.play_services_available));
			//Toast.makeText(getApplicationContext(), "Google PLay Service Available", Toast.LENGTH_SHORT).show();
			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			// Display an error dialog
			//   Toast.makeText(getApplicationContext(), "No Google PLay Service", Toast.LENGTH_SHORT).show();
		}
		return false;
	}


*/

	/*
	 * Class for dialog fragment showing stations list
	 */
	public class TrainSrcDestFragment extends DialogFragment {

		public TrainSrcDestFragment() {
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);


		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			final AutoCompleteTextView srcStation = (AutoCompleteTextView)rootView.findViewById(R.id.srcStation);
			Button doneButton = (Button) rootView.findViewById(R.id.fragment_main_done);


			final RouteDataSource dataSource = mDataSource;
			TrainDbOpenHelper trainDbOpenHelper = dataSource.getDbHelper();

			SuggestionAdapter suggestionAdapter = new SuggestionAdapter(this.getActivity().getApplicationContext(),
					Utils.getAllTrainStationsCursor(this.getActivity().getApplicationContext(),trainDbOpenHelper),
					trainDbOpenHelper);

			//	Utils.getAllTrainStations(ctx, trainDbOpenHelper)

			srcStation.setAdapter(suggestionAdapter);
			setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
			WindowManager.LayoutParams wmlp = getDialog().getWindow().getAttributes();

			//	wmlp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;


			// listener to done button
			doneButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					boardingStationText.setText(srcStation.getText().toString());
					getFragmentManager().beginTransaction().remove(TrainSrcDestFragment.this).addToBackStack(null).commit();
					getFragmentManager().popBackStack();
				}
			});


			return rootView;
		}
	}

	// replace "select origin screen"
	public void replceFragment()
	{
		actionBarTitle.setText("Select Origin");
		getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.entry, R.anim.exit,R.anim.pop_enter,R.anim.pop_exit).replace(R.id.activity_home_frame, new ChooseOriginFragment()).addToBackStack(null).commit();
	}

	public String getOriginStationName() {
		return originStationName;
	}


	public void setOriginStationName(String originStationName) {
		this.originStationName = originStationName;
	}


}
