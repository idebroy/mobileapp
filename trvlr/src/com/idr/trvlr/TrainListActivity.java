package com.idr.trvlr;

import java.io.Serializable;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.idr.trvlr.sqlite.RouteDataSource;
import com.idr.trvlr.sqlite.RoutePoint;
import com.idr.trvlr.util.Utils;

public class TrainListActivity extends Activity implements Serializable{



	private RouteDataSource mDataSource;
	private ArrayList<RouteDataSource.Train> allTrainPassingTroughStations;
	private ArrayList<RouteDataSource.Train> searchArray;
	private TrainListAdapter mAdapter;
	private TrainListAdapter searchAdapter;
	private ListView listview;
	private String srcStation;
	private ProgressDialog progDialog;
	private EditText searchEditText;
	protected ArrayList<RoutePoint> destinationArray;
	private Intent toDestinationIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_train_list);
		getActionBar().hide();

		//Initialization
		listview = (ListView) findViewById(R.id.activity_train_listview);
		searchEditText = (EditText) findViewById(R.id.activity_train_list_search);
		searchArray = new ArrayList<RouteDataSource.Train>();
		mDataSource = RouteDataSource.getRouteDataSource(this);
		progDialog = new ProgressDialog(TrainListActivity.this);

		toDestinationIntent = new Intent(TrainListActivity.this,SelectDestinationActivity.class);

		srcStation = getIntent().getStringExtra("SRC_STATION");
		allTrainPassingTroughStations = new ArrayList<RouteDataSource.Train>();

		mAdapter = new TrainListAdapter(this,allTrainPassingTroughStations);


		listview.setAdapter(mAdapter);

		// progress dialog
		progDialog.setTitle("Please Wait...");
		progDialog.setCancelable(false);
		progDialog.show();


		searchEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence newText, int start, int before, int count) {
				// TODO Auto-generated method stub


				searchArray.clear() ;


				//iterating in contacts list
				for(int i = 0;i<allTrainPassingTroughStations.size() ; i++) 
				{

					// if  search found , add in search array
					if(allTrainPassingTroughStations.get(i).getTrainName().toLowerCase().contains(newText))  
					{
						searchArray.add(allTrainPassingTroughStations.get(i)) ;

					}//end of if

				} // end of for loop


				searchAdapter = new TrainListAdapter(getApplicationContext(), searchArray) ;
				listview.setAdapter(searchAdapter) ;






			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		new ReadDataInBackground().execute();

		/*OnItem Click listener*/
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {






				// asynch task to get data
				new GetStationsInBackground().execute(position);
			}
		});




	}

	
	/*
	 * get all trains 
	 * */
	public class ReadDataInBackground extends AsyncTask<Void, Void, Void>
	{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			allTrainPassingTroughStations = mDataSource.getAllPassingTrains(srcStation);

			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mAdapter.setAllTrainPassingTroughStations(allTrainPassingTroughStations);
			mAdapter.notifyDataSetChanged();

			progDialog.dismiss();
		}

	}

	public class GetStationsInBackground extends AsyncTask<Integer, Void, Void>
	{
		ProgressDialog dialog;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			dialog = new ProgressDialog(TrainListActivity.this);
			dialog.setTitle("Calculating...");
			dialog.setMessage("Please wait...");
			dialog.setIndeterminate(true);
			dialog.show();
		}

		@Override
		protected Void doInBackground(Integer... params) {
			// TODO Auto-generated method stub

			int position = params[0];
			Bundle bundle = new Bundle();
			// TODO Auto-generated method stub
			if(searchArray.isEmpty())
			{
				//Utils.populateRouteData(TrainListActivity.this,allTrainPassingTroughStations.get(position).getId() ,mDataSource);
				destinationArray = (ArrayList<RoutePoint>) Utils.getStationsFromTrain(TrainListActivity.this,allTrainPassingTroughStations.get(position).getId(), mDataSource);
				
				
				// trimming array
				int arraySize = destinationArray.size();

				for (int i=0;i<arraySize;i++)
				{

					if(destinationArray.get(0).getDescription().equalsIgnoreCase(srcStation))
					{
						break;
					}
					destinationArray.remove(0);
				}


				toDestinationIntent.putExtra("TrainName",allTrainPassingTroughStations.get(position).getTrainName());
				toDestinationIntent.putExtra("TrainNumber", allTrainPassingTroughStations.get(position).getTrainNo());
				//	bundle.putSerializable("RoutDataSourceObject", mDataSource);
				//bundle.putInt("TrainId",allTrainPassingTroughStations.get(position).getId());
			}
			else
			{
				//	Utils.populateRouteData(TrainListActivity.this,searchArray.get(position).getId() , mDataSource);
				destinationArray = (ArrayList<RoutePoint>) Utils.getStationsFromTrain(TrainListActivity.this,searchArray.get(position).getId() , mDataSource);
				
				
				// trimming array
				int arraySize = destinationArray.size();

				for (int i=0;i<arraySize;i++)
				{

					if(destinationArray.get(0).getDescription().equalsIgnoreCase(srcStation))
					{
						break;
					}
					destinationArray.remove(0);
				}


				toDestinationIntent.putExtra("TrainName",searchArray.get(position).getTrainName());
				toDestinationIntent.putExtra("TrainNumber", searchArray.get(position).getTrainNo());


				//	bundle.putSerializable("RoutDataSourceObject", mDataSource);
				//	bundle.putInt("TrainId",searchArray.get(position).getId());
			}


			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			//	intent.putExtras(bundle);
			toDestinationIntent.putExtra("sourceStation", srcStation);
			toDestinationIntent.putExtra("array", destinationArray);

			dialog.dismiss();
			startActivity(toDestinationIntent);
		}

	}
}
