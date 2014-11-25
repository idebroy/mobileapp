package com.idr.trvlr;

import java.util.ArrayList;
import java.util.List;

import com.idr.trvlr.sqlite.RouteDataSource;
import com.idr.trvlr.sqlite.RouteDataSource.Train;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TrainListAdapter extends ArrayAdapter<RouteDataSource.Train>{
	 private ArrayList<RouteDataSource.Train> allTrainPassingTroughStations;
	 private Context context;
	 
	 
	 /*Viewholder class*/
		static class ViewHolder {

	
			private TextView TrainName;
			private TextView TrainNumber;


		}

	public TrainListAdapter(Context context,  ArrayList<RouteDataSource.Train> allTrainPassingTroughStations) {
		super(context,android.R.layout.simple_list_item_1);
		this.context = context;
		this.allTrainPassingTroughStations = allTrainPassingTroughStations;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return allTrainPassingTroughStations.size();
		
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		//LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View  trainItemView = convertView ; 
		if(convertView == null)
		{

			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
			trainItemView = inflater.inflate(R.layout.train_list_item,null) ;
			
			//holder object Initialization
			ViewHolder	holder = new ViewHolder() ;
			
			holder.TrainName = (TextView) trainItemView.findViewById(R.id.train_name);
			holder.TrainNumber = (TextView) trainItemView.findViewById(R.id.train_number);
			
			trainItemView.setTag(holder);

		}
		
		// holder object regained from existing view
		ViewHolder viewHolder = (ViewHolder) trainItemView.getTag();
		
		//setting values
		viewHolder.TrainName.setText(""+allTrainPassingTroughStations.get(position).getTrainName());
		viewHolder.TrainNumber.setText(""+allTrainPassingTroughStations.get(position).getTrainNo());
		
		return trainItemView;
	}

	public ArrayList<RouteDataSource.Train> getAllTrainPassingTroughStations() {
		return allTrainPassingTroughStations;
	}

	public void setAllTrainPassingTroughStations(
			ArrayList<RouteDataSource.Train> allTrainPassingTroughStations) {
		this.allTrainPassingTroughStations = allTrainPassingTroughStations;
	}
	
	
}
