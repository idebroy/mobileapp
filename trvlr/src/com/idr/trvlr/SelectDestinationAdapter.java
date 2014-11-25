package com.idr.trvlr;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.idr.trvlr.TrainListAdapter.ViewHolder;
import com.idr.trvlr.sqlite.RouteDataSource;
import com.idr.trvlr.sqlite.RoutePoint;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SelectDestinationAdapter extends ArrayAdapter<RoutePoint> {
	private ArrayList<RoutePoint> allTrainPassingTroughStations;
	private Context context;

	private int destinationPosition = -1;
	private String sourceStation;


	/*Viewholder class*/
	static class ViewHolder {


		private TextView DestinationName;
		private TextView destinationTime;
		private TextView destinationDistance;
		private ImageView destinationDot;


	}

	public SelectDestinationAdapter(Context context,  ArrayList<RoutePoint> allTrainPassingTroughStations,String sourceStation) {
		super(context,android.R.layout.simple_list_item_1);
		this.context = context;
		this.allTrainPassingTroughStations = allTrainPassingTroughStations;
		this.sourceStation = sourceStation;
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
			trainItemView = inflater.inflate(R.layout.list_item_select_destination,null) ;

			//holder object Initialization
			ViewHolder	holder = new ViewHolder() ;

			holder.DestinationName = (TextView) trainItemView.findViewById(R.id.destination_station_name);
			holder.destinationTime = (TextView) trainItemView.findViewById(R.id.destination_time);
			holder.destinationDistance = (TextView) trainItemView.findViewById(R.id.destination_distance);
			holder.destinationDot = (ImageView) trainItemView.findViewById(R.id.destination_dot);
			//	holder.TrainNumber = (TextView) trainItemView.findViewById(R.id.train_number);

			trainItemView.setTag(holder);

		}

		// holder object regained from existing view
		ViewHolder viewHolder = (ViewHolder) trainItemView.getTag();

		//setting values
		viewHolder.DestinationName.setText(""+allTrainPassingTroughStations.get(position).getDescription());

		//	viewHolder.TrainNumber.setText(""+allTrainPassingTroughStations.get(position).getTrainNo());


		// String longV = "1343805819061";
		long millisecond = allTrainPassingTroughStations.get(position).getScheduleTime();
		// DateFormat formater = new DateFormat("dd/MM/yyyy hh:mm:ss.SSS");
		SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss aa");
		// String dateString= formater.format("dd/MM/yyyy hh:mm:ss.SSS", new Date(millisecond)).toString();
		//	 dateString =  formater.getTimeFormat(context).toString();

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millisecond);
		String time =  dateFormat.format(calendar.getTime());

		viewHolder.destinationTime.setText(""+time);

		viewHolder.destinationDistance.setText(""+allTrainPassingTroughStations.get(position).getDistance()+" Kms");

		viewHolder.destinationDot.setImageDrawable(context.getResources().getDrawable(R.drawable.prev_next_station));

		// source station
		if(allTrainPassingTroughStations.get(position).getDescription().equalsIgnoreCase(sourceStation))
		{
			viewHolder.destinationDot.setImageDrawable(context.getResources().getDrawable(R.drawable.current_station));
		}

		if(destinationPosition != -1)
		{
			if(destinationPosition == position)
			{
				if(allTrainPassingTroughStations.get(destinationPosition).getDescription().equalsIgnoreCase(sourceStation))
				{
					Toast.makeText(context, "Sorry your origin cannot be your destination",Toast.LENGTH_LONG).show();

				}
				else
				{
					viewHolder.destinationDot.setImageDrawable(context.getResources().getDrawable(R.drawable.current_destination));
				}
			}

		}


		return trainItemView;
	}

	public ArrayList<RoutePoint> getAllTrainPassingTroughStations() {
		return allTrainPassingTroughStations;
	}

	public void setAllTrainPassingTroughStations(
			ArrayList<RoutePoint> allTrainPassingTroughStations) {
		this.allTrainPassingTroughStations = allTrainPassingTroughStations;
	}

	public int getDestinationPosition() {
		return destinationPosition;
	}

	public void setDestinationPosition(int destinationPosition) {
		this.destinationPosition = destinationPosition;
	}






}
