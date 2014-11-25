package com.idr.trvlr;

import java.util.ArrayList;
import java.util.List;

import com.idr.trvlr.sqlite.RouteDataSource;
import com.idr.trvlr.sqlite.TrainDbOpenHelper;
import com.idr.trvlr.sqlite.RouteDataSource.TrainStation;
import com.idr.trvlr.util.SuggestionAdapter;
import com.idr.trvlr.util.Utils;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView.FindListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

public class ChooseOriginFragment extends Fragment {
	
	ArrayList<RouteDataSource.TrainStation> stationsList;
	private RouteDataSource mDataSource;

	private HomeActivity activity;
	private SearchView changeOriginListview;
	private AutoCompleteTextView chooseOriginTextview;
	private Button doneButton;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_choose_origin, null);
		//Initialization
		activity = (HomeActivity) getActivity();
		mDataSource = RouteDataSource.getRouteDataSource(getActivity());
		chooseOriginTextview = (AutoCompleteTextView) v.findViewById(R.id.fragment_choose_origin_srcStation);
		doneButton = (Button) v.findViewById(R.id.fragment_choose_origin_done);
		
		final RouteDataSource dataSource = mDataSource;
		TrainDbOpenHelper trainDbOpenHelper = dataSource.getDbHelper();

		SuggestionAdapter suggestionAdapter = new SuggestionAdapter(this.getActivity().getApplicationContext(),
				Utils.getAllTrainStationsCursor(this.getActivity().getApplicationContext(),trainDbOpenHelper),
				trainDbOpenHelper);
		
	//	Utils.getAllTrainStations(ctx, trainDbOpenHelper)

		chooseOriginTextview.setAdapter(suggestionAdapter);
	
		
		/*
		 * listner to doneBotton*/
		doneButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				if(chooseOriginTextview.getText().toString()!=null )
				{
					if(!chooseOriginTextview.getText().toString().isEmpty())
					{
						activity.setOriginStationName(chooseOriginTextview.getText().toString());
					}
				}
				
				InputMethodManager mgr =      (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				mgr.hideSoftInputFromWindow(arg0.getWindowToken(), 0);
				activity.onBackPressed();
			}
		});
		
		return v;
	}

}
