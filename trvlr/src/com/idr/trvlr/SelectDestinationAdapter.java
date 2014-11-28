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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SelectDestinationAdapter extends ArrayAdapter<RoutePoint> {
	private ArrayList<RoutePoint> stationsList;
	private Context context;


	private int destinationPosition = -1;
	private String sourceStation;


	/*Viewholder class*/
	static class ViewHolder {


		private TextView DestinationName;
		private TextView destinationTime;
		private TextView destinationDistance;
		private ImageView destinationDot;
		private ImageView prevNextSTation;


	}

	public SelectDestinationAdapter(Context context,  ArrayList<RoutePoint> stationsList,String sourceStation) {
		super(context,android.R.layout.simple_list_item_1);
		this.context = context;
		this.stationsList = stationsList;
		this.sourceStation = sourceStation;
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return stationsList.size();

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
			holder.prevNextSTation = (ImageView) trainItemView.findViewById(R.id.prev_next_station);
			//	holder.TrainNumber = (TextView) trainItemView.findViewById(R.id.train_number);

			trainItemView.setTag(holder);

		}

		// holder object regained from existing view
		ViewHolder viewHolder = (ViewHolder) trainItemView.getTag();

		//setting values
		viewHolder.DestinationName.setText(""+stationsList.get(position).getDescription());

		//	viewHolder.TrainNumber.setText(""+stationsList.get(position).getTrainNo());


		// String longV = "1343805819061";
		long millisecond = stationsList.get(position).getScheduleTime();
		// DateFormat formater = new DateFormat("dd/MM/yyyy hh:mm:ss.SSS");
		SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss aa");
		// String dateString= formater.format("dd/MM/yyyy hh:mm:ss.SSS", new Date(millisecond)).toString();
		//	 dateString =  formater.getTimeFormat(context).toString();

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millisecond);
		String time =  dateFormat.format(calendar.getTime());

		viewHolder.destinationTime.setText(""+time);

		// first station is 0kms from source
		if(position==0)
		{
			viewHolder.destinationDistance.setText(""+"0 Kms");
		}
		else
		{
		viewHolder.destinationDistance.setText(""+stationsList.get(position).getDistance()+" Kms");
		}

		viewHolder.destinationDot.setVisibility(View.GONE);
		viewHolder.prevNextSTation.setVisibility(View.VISIBLE);

		// source station
		if(stationsList.get(position).getDescription().equalsIgnoreCase(sourceStation))
		{
		//	viewHolder.destinationDot.setImageDrawable(context.getResources().getDrawable(R.drawable.current_station));
		}

		if(destinationPosition != -1)
		{
			if(destinationPosition == position)
			{
				if(stationsList.get(destinationPosition).getDescription().equalsIgnoreCase(sourceStation))
				{
					Toast.makeText(context, "Sorry your origin cannot be your destination",Toast.LENGTH_LONG).show();

				}
				else
				{
					//viewHolder.destinationDot.setImageDrawable(context.getResources().getDrawable(R.drawable.current_destination));
					Log.d("Adaoter","inside else");
					viewHolder.destinationDot.setVisibility(View.VISIBLE);
					viewHolder.prevNextSTation.setVisibility(View.GONE);
				}
			}

		}


		return trainItemView;
	}

	public ArrayList<RoutePoint> getstationsList() {
		return stationsList;
	}

	public void setstationsList(
			ArrayList<RoutePoint> stationsList) {
		this.stationsList = stationsList;
	}

	public int getDestinationPosition() {
		return destinationPosition;
	}

	public void setDestinationPosition(int destinationPosition) {
		this.destinationPosition = destinationPosition;
	}






}
