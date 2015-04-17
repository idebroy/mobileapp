package com.idr.trvlr;

import java.io.Serializable;
import java.util.ArrayList;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

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
	private ImageView trainAnimatingImage;
	private ImageView trainAnimatingImage2;
	private LinearLayout animationLayout;
	private Animator animationOne;
	private Animator animationTwo;
	ObjectAnimator animatorOne;
	ObjectAnimator animatorTwo;
	private AnimatorSet animatorSet;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_train_list);
		getActionBar().hide();
		getActionBar().setCustomView(R.layout.action_bar_view);
		//Initialization
		animatorOne = new ObjectAnimator();
		listview = (ListView) findViewById(R.id.activity_train_listview);
		searchEditText = (EditText) findViewById(R.id.activity_train_list_search);
		searchArray = new ArrayList<RouteDataSource.Train>();
		mDataSource = RouteDataSource.getRouteDataSource(this);
		progDialog = new ProgressDialog(TrainListActivity.this);
		trainAnimatingImage = (ImageView) findViewById(R.id.activity_train_list_animation);
		trainAnimatingImage2 = (ImageView) findViewById(R.id.activity_train_list_animation2);
		animatorSet = new AnimatorSet();

		toDestinationIntent = new Intent(TrainListActivity.this,SelectDestinationActivity.class);
		animationLayout = (LinearLayout) findViewById(R.id.activity_train_list_animation_layout);

		srcStation = getIntent().getStringExtra("SRC_STATION");
		allTrainPassingTroughStations = new ArrayList<RouteDataSource.Train>();

		mAdapter = new TrainListAdapter(this,allTrainPassingTroughStations);

		// forcefully hiding keyboard
		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);

		listview.setAdapter(mAdapter);

		// progress dialog
		progDialog.setTitle("Please Wait...");
		progDialog.setCancelable(false);
		//	progDialog.show();




//		BitmapFactory.Options options = new BitmapFactory.Options();
//		options.inJustDecodeBounds = true;
//		options.inSampleSize = 256;
//		Bitmap bp = BitmapFactory.decodeResource(getResources(), R.drawable.train_animation,options);
		

		//
		//		
	
		

		searchEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence newText, int start, int before, int count) {
				// TODO Auto-generated method stub


				searchArray.clear() ;


				//iterating in contacts list
				for(int i = 0;i<allTrainPassingTroughStations.size() ; i++) 
				{

					// if  search found , add in search array
					if(allTrainPassingTroughStations.get(i).getTrainName().toLowerCase().contains(newText.toString().toLowerCase()))  
					{
						searchArray.add(allTrainPassingTroughStations.get(i)) ;

					}//end of if

				} // end of for loop


			//	searchAdapter = new TrainListAdapter(getApplicationContext(), searchArray) ;
			//	listview.setAdapter(searchAdapter) ;
				
				
				mAdapter.setAllTrainPassingTroughStations(searchArray);
				mAdapter.notifyDataSetChanged();






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
		
		// go button listner on keyboard
		searchEditText.setOnEditorActionListener(new OnEditorActionListener() {
		    @Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        boolean handled = false;
		        if (actionId == EditorInfo.IME_ACTION_GO) {
		          
		        	
		        	InputMethodManager imm = (InputMethodManager) TrainListActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
		    		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
		        	
		            handled = true;
		        }
		        return handled;
		    }

			
		});

		
		new ReadDataInBackground().execute();
		animationOne =AnimatorInflater.loadAnimator(TrainListActivity.this, R.anim.loading_one);// AnimationUtils.loadAnimation(TrainListActivity.this, R.anim.loading_one);
		animationTwo = AnimatorInflater.loadAnimator(TrainListActivity.this, R.anim.loading_two);
		//trainAnimatingImage.startAnimation(animation);
		animationOne.setTarget(trainAnimatingImage);

		animationTwo.setTarget(trainAnimatingImage2);
		animationOne.start();
	//	animationTwo.start();
		
	//	animatorSet.playSequentially(animationOne,animationTwo);
		
//		animatorSet.addListener(new AnimatorListenerAdapter() {
//			@Override
//			public void onAnimationEnd(Animator animation) {
//				// TODO Auto-generated method stub
//			
//			//	animatorSet.start();
//				Log.d("TrainListActicity","Animation Ends");
//			}
//		});
	//	animatorSet.start();

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

			trainAnimatingImage.clearAnimation();
			animationLayout.setVisibility(View.GONE);
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

					if(destinationArray.get(i).getDescription().equalsIgnoreCase(srcStation))
					{
						break;
					}
					
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
//				int arraySize = destinationArray.size();
//
//				for (int i=0;i<arraySize;i++)
//				{
//
//					if(destinationArray.get(0).getDescription().equalsIgnoreCase(srcStation))
//					{
//						break;
//					}
//					destinationArray.remove(0);
//				}


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
